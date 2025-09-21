import logging
import os
import time
import joblib
import mysql.connector
import pandas as pd
from flask import Flask, jsonify, request
from mysql_config import DB_CONFIG
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

# =================================================================
# 1. CẤU HÌNH CƠ BẢN
# =================================================================

# Cấu hình logging để hiển thị thông tin chi tiết khi chạy
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False
CACHE_FILE = 'recommendation_cache.pkl' # File để lưu cache model

# --- Các biến toàn cục để lưu trữ model và dữ liệu đã xử lý ---
df = None
tfidf = None
cosine_sim = None
movie_index = None


# =================================================================
# 2. CÁC HÀM XỬ LÝ DỮ LIỆU VÀ MODEL (CORE LOGIC)
# =================================================================

def load_data_and_model():
    """
    Tải dữ liệu từ MySQL, xử lý, huấn luyện model gợi ý và lưu vào cache.
    Đây là hàm tốn nhiều tài nguyên nhất và sẽ được gọi khi khởi động hoặc refresh.
    """
    logging.info("🔄 Bắt đầu quá trình tải dữ liệu và huấn luyện model...")
    
    # Kết nối MySQL để lấy dữ liệu thô
    conn = mysql.connector.connect(**DB_CONFIG)
    
    # Câu lệnh SQL để tổng hợp thông tin phim từ nhiều bảng
    query = """
    SELECT 
        f.id,
        f.title,
        f.poster_url,
        f.release_year,
        GROUP_CONCAT(DISTINCT g.name) as genres,
        GROUP_CONCAT(DISTINCT a.name) as actors,
        d.name as director,
        c.name as country
    FROM film f
    LEFT JOIN movie_genres mg ON f.id = mg.movie_id
    LEFT JOIN genre g ON mg.genre_id = g.id
    LEFT JOIN movie_actors ma ON f.id = ma.movie_id
    LEFT JOIN actor a ON ma.actor_id = a.id
    LEFT JOIN director d ON f.director_id = d.id
    LEFT JOIN country c ON f.country_id = c.id
    GROUP BY f.id, f.title, d.name, c.name
    """
    
    temp_df = pd.read_sql(query, conn)
    conn.close()
    
    if temp_df.empty:
        raise Exception("Không có dữ liệu trong bảng 'film' để huấn luyện.")
    
    logging.info(f"✅ Đã tải thành công {len(temp_df)} phim từ database.")
    
    # Hàm helper để kết hợp các đặc trưng văn bản thành một chuỗi duy nhất
    def combine_features(row):
        # Lấy các đặc trưng: thể loại, diễn viên, đạo diễn, quốc gia
        features = [
            str(row.get('genres', '')),
            str(row.get('actors', '')),
            str(row.get('director', '')),
            str(row.get('country', ''))
        ]
        # Nối chúng lại và chuẩn hóa (viết thường, loại bỏ dấu phẩy)
        combined = ' '.join(filter(None, features)) # Lọc bỏ các giá trị None
        combined = combined.lower().replace(',', ' ').replace(';', ' ').replace(':', ' ')
        return ' '.join(combined.split()) # Xóa các khoảng trắng thừa
    
    # Áp dụng hàm trên để tạo cột 'combined_features'
    temp_df['combined_features'] = temp_df.apply(combine_features, axis=1)
    logging.info("⚙️  Đã kết hợp xong các đặc trưng (features) cho mỗi phim.")
    
    # Huấn luyện model TF-IDF
    logging.info("🔄 Bắt đầu huấn luyện model TF-IDF...")
    temp_tfidf = TfidfVectorizer(
        max_features=2000,      # Chỉ giữ lại 2000 từ/cụm từ phổ biến nhất
        ngram_range=(1, 2),     # Xem xét cả từ đơn (unigram) và cụm 2 từ (bigram)
        min_df=1,               # Một từ phải xuất hiện ít nhất 1 lần
        max_df=0.95,            # Loại bỏ các từ xuất hiện trong hơn 95% tài liệu (quá phổ biến)
        stop_words=None         # Không dùng stop words có sẵn, vì tên riêng rất quan trọng
    )
    
    # Tạo ma trận TF-IDF: mỗi hàng là một phim, mỗi cột là một từ/cụm từ
    tfidf_matrix = temp_tfidf.fit_transform(temp_df['combined_features'])
    logging.info(f"📊 Ma trận TF-IDF đã được tạo với kích thước: {tfidf_matrix.shape}")
    
    # Tính toán ma trận tương đồng Cosine
    # So sánh vector của mỗi phim với tất cả các phim khác
    temp_cosine_sim = cosine_similarity(tfidf_matrix, tfidf_matrix)
    logging.info(f"📐 Ma trận tương đồng Cosine đã được tính toán với kích thước: {temp_cosine_sim.shape}")

    # Tạo một từ điển để tra cứu index của phim từ ID
    temp_movie_index = dict(zip(temp_df['id'], temp_df.index))
    
    # Đóng gói tất cả vào một đối tượng để lưu cache
    cache_data = {
        'df': temp_df,
        'tfidf': temp_tfidf,
        'cosine_sim': temp_cosine_sim,
        'movie_index': temp_movie_index,
        'timestamp': time.time()
    }
    
    # Lưu cache xuống file
    joblib.dump(cache_data, CACHE_FILE)
    logging.info(f"✅ Đã lưu cache model và dữ liệu vào file '{CACHE_FILE}'")
    
    return cache_data

def _apply_cache_data(cache_data):
    """Hàm helper để gán dữ liệu từ cache vào các biến toàn cục."""
    global df, tfidf, cosine_sim, movie_index
    df = cache_data['df']
    tfidf = cache_data['tfidf']
    cosine_sim = cache_data['cosine_sim']
    movie_index = cache_data['movie_index']
    logging.info("✔️  Đã áp dụng dữ liệu từ cache vào các biến toàn cục.")

def refresh_cache():
    """Refresh cache thủ công, được gọi bởi webhook."""
    try:
        new_cache_data = load_data_and_model()
        _apply_cache_data(new_cache_data)
        return True
    except Exception:
        logging.exception("❌ Lỗi nghiêm trọng khi đang refresh cache.")
        return False

# =================================================================
# 3. KHỞI ĐỘNG ỨNG DỤNG
# =================================================================
try:
    logging.info("🚀 Khởi động server, thực hiện làm mới cache từ database...")
    initial_cache_data = load_data_and_model()
    _apply_cache_data(initial_cache_data)
    logging.info("✅ Hệ thống đã sẵn sàng nhận request!")
except Exception:
    logging.exception("❌ Lỗi nghiêm trọng khi khởi động, không thể tải dữ liệu. Server sẽ thoát.")
    exit(1)


# =================================================================
# 4. CÁC API ENDPOINTS
# =================================================================

@app.route('/recommend/content', methods=['GET'])
def content_based_recommend():
    """API gợi ý các phim liên quan (content-based) cho một phim cụ thể."""
    logging.info(f"▶️  Nhận request GET /recommend/content với params: {request.args}")
    try:
        movie_id = request.args.get('movie_id', type=int)
        if not movie_id or movie_id not in movie_index:
            logging.warning(f"⚠️ Yêu cầu phim không hợp lệ hoặc không tồn tại: movie_id={movie_id}")
            return jsonify({"error": "Không tìm thấy phim với ID đã cho"}), 404

        # Lấy index của phim từ ID
        idx = movie_index[movie_id]
        
        # Lấy điểm tương đồng của phim này với tất cả các phim khác
        sim_scores = list(enumerate(cosine_sim[idx]))
        
        # Sắp xếp và lấy 4 phim có điểm cao nhất (bỏ qua phim đầu tiên vì đó là chính nó)
        sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)[1:5]
        
        movie_indices = [i[0] for i in sim_scores]
        recommendations = []
        
        for i in movie_indices:
            movie = df.iloc[i].to_dict()
            recommendations.append({
                'id': int(movie.get('id')),
                'title': movie.get('title'),
                'poster_url': movie.get('poster_url', ''),
                'release_year': movie.get('release_year', ''),
                'genres': movie.get('genres', ''),
                'similarity_score': float(sim_scores[movie_indices.index(i)][1])
            })
        
        logging.info(f"✅ Trả về {len(recommendations)} phim liên quan cho movie_id={movie_id}")
        return jsonify({
            "movie_id": movie_id,
            "type": "content-based",
            "recommended_movies": recommendations
        })
        
    except Exception:
        logging.exception(f"❌ Lỗi server khi xử lý /recommend/content")
        return jsonify({"error": "Lỗi hệ thống, vui lòng thử lại sau."}), 500

@app.route('/recommend/personalized', methods=['GET'])
def personalized_recommend():
    """API gợi ý phim cá nhân hóa dựa trên lịch sử xem của người dùng."""
    logging.info(f"▶️  Nhận request GET /recommend/personalized với params: {request.args}")
    try:
        user_id = request.args.get('user_id', type=int)
        if not user_id:
            logging.warning("⚠️ Yêu cầu thiếu tham số 'user_id'")
            return jsonify({"error": "Thiếu user_id"}), 400

        # Lấy lịch sử xem của user từ DB
        conn = mysql.connector.connect(**DB_CONFIG)
        query = "SELECT f.id, f.title, f.poster_url, f.release_year, GROUP_CONCAT(DISTINCT g.name) as genres, GROUP_CONCAT(DISTINCT a.name) as actors, d.name as director, c.name as country FROM user_history uh JOIN film f ON uh.movie_id = f.id LEFT JOIN movie_genres mg ON f.id = mg.movie_id LEFT JOIN genre g ON mg.genre_id = g.id LEFT JOIN movie_actors ma ON f.id = ma.movie_id LEFT JOIN actor a ON ma.actor_id = a.id LEFT JOIN director d ON f.director_id = d.id LEFT JOIN country c ON f.country_id = c.id WHERE uh.user_id = %s GROUP BY f.id, f.title, d.name, c.name LIMIT 20"
        user_history = pd.read_sql(query, conn, params=(user_id,))
        conn.close()
        
        logging.info(f"🔎 Tìm thấy {len(user_history)} phim trong lịch sử của user_id={user_id}")

        # Trường hợp user chưa có lịch sử xem -> trả về phim thịnh hành (fallback)
        if user_history.empty:
            logging.info(f"🤷‍♂️ User_id={user_id} chưa có lịch sử. Sử dụng fallback (phim thịnh hành).")
            sample_movies = df.head(4).to_dict('records')
            recommendations = []
            for movie in sample_movies:
                recommendations.append({
                    'id': int(movie.get('id')),
                    'title': movie.get('title'),
                    'poster_url': movie.get('poster_url', ''),
                    'release_year': movie.get('release_year', ''),
                    'genres': movie.get('genres', ''),
                    'similarity_score': 0.8 # Điểm giả lập
                })
            return jsonify({
                "user_id": user_id,
                "type": "fallback",
                "recommended_movies": recommendations
            })

        # Tạo profile của user bằng cách kết hợp đặc trưng của các phim đã xem
        def combine_features(row):
            features = [ str(row.get(f, '')) for f in ['genres', 'actors', 'director', 'country'] ]
            combined = ' '.join(filter(None, features))
            return ' '.join(combined.lower().replace(',', ' ').split())
        
        user_history['combined_features'] = user_history.apply(combine_features, axis=1)
        user_profile = ' '.join(user_history['combined_features'].tolist())

        # Vector hóa profile user và tính toán độ tương đồng
        user_vector = tfidf.transform([user_profile])
        all_movie_vectors = tfidf.transform(df['combined_features'])
        similarity_scores = cosine_similarity(user_vector, all_movie_vectors).flatten()

        # Lọc ra các phim user đã xem và lấy top 4 phim gợi ý
        watched_movie_ids = set(user_history['id'].tolist())
        recommendations = []
        movie_scores = sorted(list(enumerate(similarity_scores)), key=lambda x: x[1], reverse=True)

        for idx, score in movie_scores:
            movie_id = int(df.iloc[idx]['id'])
            if movie_id not in watched_movie_ids and score > 0.01:
                movie = df.iloc[idx].to_dict()
                recommendations.append({
                    'id': movie_id,
                    'title': movie.get('title'),
                    'poster_url': movie.get('poster_url', ''),
                    'release_year': movie.get('release_year', ''),
                    'genres': movie.get('genres', ''),
                    'similarity_score': float(score)
                })
                if len(recommendations) >= 4:
                    break

        logging.info(f"✅ Trả về {len(recommendations)} phim cá nhân hóa cho user_id={user_id}")
        return jsonify({
            "user_id": user_id,
            "type": "personalized",
            "recommended_movies": recommendations
        })

    except Exception:
        logging.exception(f"❌ Lỗi server khi xử lý /recommend/personalized cho user_id={request.args.get('user_id')}")
        return jsonify({"error": "Lỗi hệ thống, vui lòng thử lại sau."}), 500


@app.route('/webhook/data-changed', methods=['POST'])
def webhook_data_changed():
    """
    Webhook chung, nhận thông báo về bất kỳ thay đổi dữ liệu nào
    (tạo, sửa, xóa) và kích hoạt việc làm mới cache.
    """
    try:
        logging.info("▶️  Nhận request POST /webhook/data-changed")
        data = request.get_json()

        entity_id = data.get('entity_id') if data else "Không rõ"
        event_type = data.get('event_type') if data else "Không rõ"
        
        logging.info(f"🎬 Webhook được kích hoạt. Sự kiện: '{event_type}', ID thực thể: {entity_id}. Bắt đầu refresh cache.")
        
        if refresh_cache():
            return jsonify({
                "status": "success",
                "message": f"Cache đã được refresh thành công do sự kiện '{event_type}' trên thực thể ID: {entity_id}"
            })
        else:
            return jsonify({
                "status": "error",
                "message": "Lỗi khi đang refresh cache"
            }), 500
            
    except Exception as e:
        logging.exception("❌ Lỗi nghiêm trọng khi xử lý webhook.")
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500

@app.route('/health', methods=['GET'])
def health_check():
    """API kiểm tra trạng thái của hệ thống."""
    return jsonify({
        "status": "healthy",
        "movies_in_cache": len(df) if df is not None else 0,
        "tfidf_features": tfidf.max_features if tfidf is not None else 0,
        "cache_timestamp": time.ctime(os.path.getmtime(CACHE_FILE)) if os.path.exists(CACHE_FILE) else "N/A"
    })

if __name__ == '__main__':
    # Chạy server Flask ở chế độ debug
    logging.info("Flask server đang khởi động ở chế độ debug...")
    app.run(host='0.0.0.0', port=5000, debug=True)


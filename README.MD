# MovieFlix - Web Xem Phim với Hệ thống Gợi ý Thông minh

MovieFlix là một dự án ứng dụng web xem phim hoàn chỉnh, được xây dựng với kiến trúc microservice, bao gồm một ứng dụng web chính và một API gợi ý phim riêng biệt.

- **Ứng dụng Web chính (Spring Boot):** Cung cấp giao diện người dùng để xem phim, tìm kiếm, lọc, đăng nhập/đăng ký. Tích hợp một trang quản trị mạnh mẽ để quản lý tất cả các tài nguyên như phim, thể loại, diễn viên, đạo diễn, và người dùng.

- **API Gợi ý (Python Flask):** Một microservice độc lập sử dụng Machine Learning (TF-IDF và Cosine Similarity) để cung cấp các gợi ý phim (phim liên quan, phim cá nhân hóa) dựa trên nội dung và lịch sử xem của người dùng.


## 🏛️ Kiến trúc Hệ thống

Dự án bao gồm 2 thành phần chính hoạt động độc lập nhưng giao tiếp với nhau qua API và Webhook:

1. **Ứng dụng Web (Java Spring Boot):**

    - Là "bộ não" chính của hệ thống.

    - Xử lý xác thực người dùng (login/register) và phân quyền (user/admin).

    - Cung cấp các trang giao diện cho người dùng (trang chủ, xem phim, tìm kiếm).

    - Cung cấp trang quản trị (Admin Panel) với đầy đủ các chức năng CRUD.

    - Giao tiếp với Flask API để lấy danh sách phim gợi ý.

    - **Gửi Webhook** đến Flask API mỗi khi có sự thay đổi dữ liệu (thêm/sửa/xóa phim, diễn viên,...) để kích hoạt việc làm mới cache.

2. **Hệ thống Gợi ý (Python Flask API):**

    - Là dịch vụ chuyên biệt cho Machine Learning.

    - Khi khởi động, nó tải toàn bộ dữ liệu phim từ database, xử lý và huấn luyện một model TF-IDF.

    - Lưu trữ model và dữ liệu đã xử lý vào một file cache (`.pkl`) để tăng tốc độ phản hồi.

    - Cung cấp các endpoint API để trả về danh sách phim gợi ý.

    - Có một endpoint Webhook để lắng nghe thông báo từ ứng dụng Spring Boot và thực hiện làm mới cache.


## 🛠️ Công nghệ sử dụng

### Backend (Ứng dụng Web chính)

- **Ngôn ngữ:** Java 17+

- **Framework:** Spring Boot 3+

- **Thành phần:** Spring Web, Spring Security, Spring Data JPA, Thymeleaf

- **Database:** MySQL

- **Build Tool:** Maven hoặc Gradle


### Backend (Hệ thống Gợi ý)

- **Ngôn ngữ:** Python 3.11.9

- **Framework:** Flask

- **Thư viện:** Pandas, Scikit-learn, Joblib, mysql-connector-python


### Frontend

- HTML5

- CSS3 & Bootstrap 5

- Thymeleaf (Server-Side Rendering)


## 🚀 Hướng dẫn Cài đặt và Chạy dự án

Để chạy dự án này, bạn cần khởi động cả hai server: Spring Boot và Flask.

### Yêu cầu

- JDK 17 hoặc mới hơn

- Python 3.9 hoặc mới hơn

- MySQL Server

- Maven

- Một IDE như IntelliJ (cho Java) và VS Code (cho Python)


### Bước 1: Cài đặt Cơ sở dữ liệu

1. Tạo một database mới trong MySQL, ví dụ tên là `movie_db`.

2. Chạy các file SQL để tạo bảng và nạp dữ liệu mẫu.


### Bước 2: Chạy Hệ thống Gợi ý (Flask API)

1. **Di chuyển đến thư mục của Flask API** (ví dụ: `.../ml/`).

2. **Tạo và kích hoạt môi trường ảo:**

    ```
    python -m venv venv
    # Windows
    venv\Scripts\activate
    # macOS/Linux
    source venv/bin/activate
    ```

3. **Cài đặt các thư viện cần thiết:**

    ```
    pip install -r requirements.txt
    ```

4. **Cấu hình kết nối database:**

    - Mở file `mysql_config.py`.

    - Cập nhật các thông tin `host`, `user`, `password`, `database` cho đúng với cấu hình MySQL của bạn.

5. **Chạy server:**

    ```
    python app.py
    ```

   Server sẽ khởi động tại `http://localhost:5000`. Nó sẽ bắt đầu tải dữ liệu từ DB và huấn luyện model.


### Bước 3: Chạy Ứng dụng Web chính (Spring Boot)

1. **Di chuyển đến thư mục gốc của dự án Spring Boot.**

2. **Cấu hình kết nối:**

    - Mở file `src/main/resources/application.properties`.

    - Cập nhật các thuộc tính `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password` để kết nối đến database `movie_db`.

    - Đảm bảo thuộc tính `recommendation.api.base-url` trỏ đúng đến Flask API:

        ```
        recommendation.api.base-url=http://localhost:5000
        ```

3. **Chạy ứng dụng:**

    - Sử dụng IDE của bạn (IntelliJ/Eclipse) để chạy lớp `MovieFlixApplication`.

    - Hoặc dùng Maven/Gradle trong terminal:

        ```
        # Maven
        ./mvnw spring-boot:run
        # Gradle
        ./gradlew bootRun
        ```


    Server Spring Boot sẽ khởi động tại `http://localhost:8080`.


### Bước 4: Truy cập ứng dụng

- **Trang người dùng:** Mở trình duyệt và truy cập `http://localhost:8080`

- **Trang quản trị:** Truy cập `http://localhost:8080/admin`
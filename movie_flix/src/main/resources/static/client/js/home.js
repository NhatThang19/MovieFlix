// Alpine.js component for Home page
function movieApp() {
    return {
        currentSlide: 0,
        slideInterval: null,
        currentPage: 1,
        moviesPerPage: 8,

        slides: [{
                title: "Hành Động Siêu Việt",
                description: "Một bộ phim hành động đỉnh cao với những pha hành động mãn nhãn, xoay quanh câu chuyện của một đặc vụ bí ẩn phải đối mặt với tổ chức tội phạm quốc tế để cứu lấy người mình yêu thương.",
                image: "https://placehold.co/1200x600/333/fff?text=Hành+Động+Siêu+Việt"
            },
            {
                title: "Vũ Trụ Marvel",
                description: "Khám phá vũ trụ rộng lớn của Marvel với những siêu anh hùng huyền thoại trong cuộc chiến chống lại cái ác.",
                image: "https://placehold.co/1200x600/444/fff?text=Vũ+Trụ+Marvel"
            },
            {
                title: "Thế Giới Tương Lai",
                description: "Một hành trình kỳ thú đến tương lai nơi công nghệ và con người hòa làm một.",
                image: "https://placehold.co/1200x600/555/fff?text=Thế+Giới+Tương+Lai"
            }
        ],

        featuredMovies: [{
                id: 1,
                title: "Siêu Anh Hùng",
                poster: "https://placehold.co/300x450/444/fff?text=Siêu+Anh+Hùng",
                year: "2022",
                genres: ["Hành Động", "Khoa Học"]
            },
            {
                id: 2,
                title: "Vũ Trụ Marvel",
                poster: "https://placehold.co/300x450/444/fff?text=Vũ+Trụ+Marvel",
                year: "2023",
                genres: ["Hành Động", "Phiêu Lưu"]
            },
            {
                id: 3,
                title: "Chiến Binh Tương Lai",
                poster: "https://placehold.co/300x450/444/fff?text=Chiến+Binh+Tương+Lai",
                year: "2021",
                genres: ["Hành Động", "Khoa Học"]
            },
            {
                id: 4,
                title: "Bí Mật Vũ Trụ",
                poster: "https://placehold.co/300x450/444/fff?text=Bí+Mật+Vũ+Trụ",
                year: "2023",
                genres: ["Khoa Học", "Hồi Hộp"]
            }
        ],

        allMovies: [{
                id: 5,
                title: "Thám Tử Siêu Đẳng",
                poster: "https://placehold.co/300x450/555/fff?text=Thám+Tử+Siêu+Đẳng",
                year: "2023",
                genres: ["Hành Động", "Hài Hước"]
            },
            {
                id: 6,
                title: "Cuộc Chiến Giữa Các Vì Sao",
                poster: "https://placehold.co/300x450/555/fff?text=Cuộc+Chiến+Giữa+Các+Vì+Sao",
                year: "2023",
                genres: ["Khoa Học", "Phiêu Lưu"]
            },
            {
                id: 7,
                title: "Tình Yêu Thời Đại",
                poster: "https://placehold.co/300x450/555/fff?text=Tình+Yêu+Thời+Đại",
                year: "2023",
                genres: ["Tình Cảm", "Cổ Trang"]
            },
            {
                id: 8,
                title: "Nhà Ma Ma Cà Rồng",
                poster: "https://placehold.co/300x450/555/fff?text=Nhà+Ma+Ma+Cà+Rồng",
                year: "2023",
                genres: ["Kinh Dị", "Hài Hước"]
            },
            {
                id: 9,
                title: "Vua Hải Tặc",
                poster: "https://placehold.co/300x450/666/fff?text=Vua+Hải+Tặc",
                year: "2022",
                genres: ["Phiêu Lưu", "Hành Động"]
            },
            {
                id: 10,
                title: "Chiến Binh Rồng",
                poster: "https://placehold.co/300x450/666/fff?text=Chiến+Binh+Rồng",
                year: "2023",
                genres: ["Hoạt Hình", "Phiêu Lưu"]
            },
            {
                id: 11,
                title: "Bí Mật Của Gió",
                poster: "https://placehold.co/300x450/666/fff?text=Bí+Mật+Của+Gió",
                year: "2021",
                genres: ["Tình Cảm", "Cổ Trang"]
            },
            {
                id: 12,
                title: "Thế Giới Ảo",
                poster: "https://placehold.co/300x450/666/fff?text=Thế+Giới+Ảo",
                year: "2023",
                genres: ["Khoa Học", "Hành Động"]
            },
            {
                id: 13,
                title: "Đại Dương Xanh",
                poster: "https://placehold.co/300x450/777/fff?text=Đại+Dương+Xanh",
                year: "2022",
                genres: ["Phiêu Lưu", "Tình Cảm"]
            },
            {
                id: 14,
                title: "Thành Phố Bóng Tối",
                poster: "https://placehold.co/300x450/777/fff?text=Thành+Phố+Bóng+Tối",
                year: "2023",
                genres: ["Hành Động", "Hồi Hộp"]
            },
            {
                id: 15,
                title: "Vương Quốc Cổ Đại",
                poster: "https://placehold.co/300x450/777/fff?text=Vương+Quốc+Cổ+Đại",
                year: "2021",
                genres: ["Cổ Trang", "Tình Cảm"]
            },
            {
                id: 16,
                title: "Robot Tương Lai",
                poster: "https://placehold.co/300x450/777/fff?text=Robot+Tương+Lai",
                year: "2023",
                genres: ["Khoa Học", "Hành Động"]
            }
        ],

        get totalPages() {
            return Math.ceil(this.allMovies.length / this.moviesPerPage);
        },

        get paginatedMovies() {
            const start = (this.currentPage - 1) * this.moviesPerPage;
            const end = start + this.moviesPerPage;
            return this.allMovies.slice(start, end);
        },

        init() {
            this.startSlider();
        },

        startSlider() {
            this.slideInterval = setInterval(() => {
                this.nextSlide();
            }, 5000);
        },

        nextSlide() {
            this.currentSlide = (this.currentSlide + 1) % this.slides.length;
            this.updateSlider();
        },

        prevSlide() {
            this.currentSlide = (this.currentSlide - 1 + this.slides.length) % this.slides.length;
            this.updateSlider();
        },

        goToSlide(index) {
            this.currentSlide = index;
            this.updateSlider();
        },

        updateSlider() {
            const translateX = -this.currentSlide * 100;
            if (this.$refs.slider) {
                this.$refs.slider.style.transform = `translateX(${translateX}%)`;
            }

            // Reset interval
            clearInterval(this.slideInterval);
            this.startSlider();
        }
    }
}
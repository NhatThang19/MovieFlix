function searchApp() {
    return {
        searchQuery: 'Hành động',
        currentPage: 1,
        moviesPerPage: 8,

        // Mock data
        allMovies: [
            {
                id: 1,
                title: "Hành Động Siêu Việt",
                poster: "https://placehold.co/300x450/444/fff?text=Hành+Động+Siêu+Việt",
                year: "2023",
                genres: ["Hành Động", "Phiêu Lưu"]
            },
            {
                id: 2,
                title: "Siêu Anh Hùng",
                poster: "https://placehold.co/300x450/444/fff?text=Siêu+Anh+Hùng",
                year: "2022",
                genres: ["Hành Động", "Khoa Học"]
            },
            {
                id: 3,
                title: "Vũ Trụ Marvel",
                poster: "https://placehold.co/300x450/444/fff?text=Vũ+Trụ+Marvel",
                year: "2023",
                genres: ["Hành Động", "Phiêu Lưu"]
            },
            {
                id: 4,
                title: "Chiến Binh Tương Lai",
                poster: "https://placehold.co/300x450/444/fff?text=Chiến+Binh+Tương+Lai",
                year: "2021",
                genres: ["Hành Động", "Khoa Học"]
            },
            {
                id: 5,
                title: "Bí Mật Vũ Trụ",
                poster: "https://placehold.co/300x450/444/fff?text=Bí+Mật+Vũ+Trụ",
                year: "2023",
                genres: ["Khoa Học", "Hồi Hộp"]
            },
            {
                id: 6,
                title: "Thám Tử Siêu Đẳng",
                poster: "https://placehold.co/300x450/555/fff?text=Thám+Tử+Siêu+Đẳng",
                year: "2023",
                genres: ["Hành Động", "Hài Hước"]
            },
            {
                id: 7,
                title: "Cuộc Chiến Giữa Các Vì Sao",
                poster: "https://placehold.co/300x450/555/fff?text=Cuộc+Chiến+Giữa+Các+Vì+Sao",
                year: "2023",
                genres: ["Khoa Học", "Phiêu Lưu"]
            },
            {
                id: 8,
                title: "Tình Yêu Thời Đại",
                poster: "https://placehold.co/300x450/555/fff?text=Tình+Yêu+Thời+Đại",
                year: "2023",
                genres: ["Tình Cảm", "Cổ Trang"]
            },
            {
                id: 9,
                title: "Nhà Ma Ma Cà Rồng",
                poster: "https://placehold.co/300x450/555/fff?text=Nhà+Ma+Ma+Cà+Rồng",
                year: "2023",
                genres: ["Kinh Dị", "Hài Hước"]
            },
            {
                id: 10,
                title: "Vua Hải Tặc",
                poster: "https://placehold.co/300x450/666/fff?text=Vua+Hải+Tặc",
                year: "2022",
                genres: ["Phiêu Lưu", "Hành Động"]
            },
            {
                id: 11,
                title: "Chiến Binh Rồng",
                poster: "https://placehold.co/300x450/666/fff?text=Chiến+Binh+Rồng",
                year: "2023",
                genres: ["Hoạt Hình", "Phiêu Lưu"]
            },
            {
                id: 12,
                title: "Bí Mật Của Gió",
                poster: "https://placehold.co/300x450/666/fff?text=Bí+Mật+Của+Gió",
                year: "2021",
                genres: ["Tình Cảm", "Cổ Trang"]
            }
        ],

        get filteredMovies() {
            return this.allMovies;
        },

        get totalPages() {
            return Math.ceil(this.filteredMovies.length / this.moviesPerPage);
        },

        get paginatedMovies() {
            const start = (this.currentPage - 1) * this.moviesPerPage;
            const end = start + this.moviesPerPage;
            return this.filteredMovies.slice(start, end);
        }
    }
}
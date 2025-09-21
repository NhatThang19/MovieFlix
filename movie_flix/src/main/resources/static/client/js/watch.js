// Alpine.js component for Watch page
function movieApp() {
    return {
        relatedMovies: [{
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
        init() {
            // Initialize Video.js player
            var player = videojs('my-video', {
                fluid: true,
                responsive: true,
                playbackRates: [0.5, 1, 1.5, 2],
                controls: true,
                preload: 'auto',
                controlBar: {
                    fullscreenToggle: true
                }
            });

            // Handle fullscreen events
            player.on('fullscreenchange', function () {
                const videoContainer = document.querySelector('.video-js');
                if (player.isFullscreen()) {
                    // Hide cursor after 3 seconds of inactivity
                    let cursorTimeout;

                    const hideCursor = () => {
                        videoContainer.style.cursor = 'none';
                    };

                    const showCursor = () => {
                        videoContainer.style.cursor = 'default';
                        clearTimeout(cursorTimeout);
                        cursorTimeout = setTimeout(hideCursor, 3000);
                    };

                    // Show cursor on mouse move
                    videoContainer.addEventListener('mousemove', showCursor);

                    // Hide cursor initially after 3 seconds
                    cursorTimeout = setTimeout(hideCursor, 3000);

                    // Show cursor on play/pause
                    player.on('play', showCursor);
                    player.on('pause', showCursor);
                } else {
                    // Reset cursor when exiting fullscreen
                    videoContainer.style.cursor = 'default';
                }
            });
        }
    }
}
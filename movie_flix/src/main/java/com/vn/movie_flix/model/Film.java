package com.vn.movie_flix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên phim không được để trống")
    private String title;

    @NotBlank(message = "Mô tả phim không được để trống")
    @Column(length = 2000)
    private String description;

    @Min(value = 1888, message = "Năm phát hành không hợp lệ")
    private int releaseYear;

    @NotBlank(message = "URL poster không được để trống")
    private String posterUrl;

    @NotBlank(message = "URL phim không được để trống")
    private String filmUrl;

    @Min(value = 1, message = "Thời lượng phim phải lớn hơn 0")
    private int duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id")
    private Director director;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> actors;

    @ManyToMany(mappedBy = "history")
    private Set<User> watchedByUsers;
}

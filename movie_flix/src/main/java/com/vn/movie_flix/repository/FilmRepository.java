package com.vn.movie_flix.repository;

import com.vn.movie_flix.model.Actor;
import com.vn.movie_flix.model.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    Page<Film> findByTitleContainingIgnoreCase(String name, Pageable pageable);

    Page<Film> findAll(Specification<Film> spec, Pageable pageable);

    List<Film> findByGenres_Id(Long genreId);

    List<Film> findByActors_Id(Long actorId);

    List<Film> findByCountry_Id(Long countryId);

    List<Film> findByDirector_Id(Long directorId);
}

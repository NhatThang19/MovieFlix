package com.vn.movie_flix.repository;

import com.vn.movie_flix.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Page<Genre> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

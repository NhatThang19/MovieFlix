package com.vn.movie_flix.repository;

import com.vn.movie_flix.model.Director;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    Page<Director> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

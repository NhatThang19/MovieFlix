package com.vn.movie_flix.repository;

import com.vn.movie_flix.model.Actor;
import com.vn.movie_flix.model.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Page<Country> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

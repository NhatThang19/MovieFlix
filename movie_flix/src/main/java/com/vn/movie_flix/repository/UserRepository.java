package com.vn.movie_flix.repository;

import com.vn.movie_flix.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String username);

    List<User> findByHistory_Id(Long filmId);

    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<User> findAll(Pageable pageable);
}
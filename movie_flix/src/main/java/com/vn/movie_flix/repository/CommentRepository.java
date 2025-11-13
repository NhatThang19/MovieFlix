package com.vn.movie_flix.repository;

import com.vn.movie_flix.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByFilmIdOrderByCreatedAtDesc(Long filmId);

    List<Comment> findByUserId(Long userId);
}
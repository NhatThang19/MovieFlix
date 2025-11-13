package com.vn.movie_flix.service;

import com.vn.movie_flix.model.Comment;
import com.vn.movie_flix.model.Film;
import com.vn.movie_flix.model.User;
import com.vn.movie_flix.repository.CommentRepository;
import com.vn.movie_flix.repository.FilmRepository;
import com.vn.movie_flix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    public List<Comment> getCommentsByFilmId(Long filmId) {
        return commentRepository.findByFilmIdOrderByCreatedAtDesc(filmId);
    }

    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    @Transactional
    public void saveComment(String content, Long filmId, Long userId) {
        Film film = filmRepository.getReferenceById(filmId);
        User user = userRepository.getReferenceById(userId);

        Comment comment = Comment.builder()
                .content(content)
                .film(film)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
    }
}
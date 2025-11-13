package com.vn.movie_flix.service;

import com.vn.movie_flix.model.Film;
import com.vn.movie_flix.model.User;
import com.vn.movie_flix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FilmService filmService;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User findByEmail(String username) {
        return userRepository.findByEmail(username).orElse(null);
    }

    public Page<User> getUsers(String keyword, Pageable pageable) {
        if (StringUtils.hasText(keyword)) {
            return userRepository.findByEmailContainingIgnoreCase(keyword, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean isFilmInFavorites(Long userId, Long filmId) {
        User user = findById(userId);
        if (user == null) {
            return false;
        }

        return user.getFavorites().stream().anyMatch(film -> film.getId().equals(filmId));
    }

    @Transactional
    public void addFilmToFavorites(Long userId, Long filmId) {
        User user = findById(userId);
        Film film = filmService.findById(filmId);

        if (user != null && film != null) {
            user.getFavorites().add(film);
            userRepository.save(user);
        }
    }

    @Transactional
    public void removeFilmFromFavorites(Long userId, Long filmId) {
        User user = findById(userId);
        Film film = filmService.findById(filmId);

        if (user != null && film != null) {
            user.getFavorites().remove(film);
            userRepository.save(user);
        }
    }

    @Transactional(readOnly = true)
    public User getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.getFavorites().size();
            user.getHistory().size();
        }
        return user;
    }
}

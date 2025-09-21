package com.vn.movie_flix.service;

import com.vn.movie_flix.model.User;
import com.vn.movie_flix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User findByEmail(String username) {
        return userRepository.findByEmail(username).orElse(null);
    }


}

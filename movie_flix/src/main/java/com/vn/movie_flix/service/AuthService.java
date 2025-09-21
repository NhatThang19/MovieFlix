package com.vn.movie_flix.service;

import com.vn.movie_flix.dto.request.UserRegisterReq;
import com.vn.movie_flix.model.Role;
import com.vn.movie_flix.model.User;
import com.vn.movie_flix.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserRegisterReq req) {

        Role clientRole = roleRepository.findRoleByName("viewer");

        User newUser = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(clientRole)
                .build();

        userService.save(newUser);
    }
}

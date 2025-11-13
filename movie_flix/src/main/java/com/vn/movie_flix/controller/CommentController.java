package com.vn.movie_flix.controller;

import com.vn.movie_flix.config.UserDetailsCustom;
import com.vn.movie_flix.model.User;
import com.vn.movie_flix.repository.UserRepository;
import com.vn.movie_flix.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    @PostMapping("/comment/add")
    public String addComment(@RequestParam("content") String content,
                             @RequestParam("filmId") Long filmId,
                             Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findUserByEmail(principal.getName());

        Long userId = user.getId();

        commentService.saveComment(content, filmId, userId);

        return "redirect:/movie/" + filmId;
    }
}
package com.vn.movie_flix.controller;

import com.vn.movie_flix.config.UserDetailsCustom;
import com.vn.movie_flix.model.User;
import com.vn.movie_flix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class FavoritesController {

    private final UserService userService;


    @PostMapping("/favorites/toggle")
    public String toggleFavorite(@RequestParam("filmId") Long filmId,
                                 Principal principal,
                                 @RequestParam("returnUrl") String returnUrl) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());

        Long userId = user.getId();

        if (userService.isFilmInFavorites(userId, filmId)) {
            userService.removeFilmFromFavorites(userId, filmId);
        } else {
            userService.addFilmToFavorites(userId, filmId);
        }

        return "redirect:" + returnUrl;
    }
}
package com.vn.movie_flix.controller;

import com.vn.movie_flix.model.Comment;
import com.vn.movie_flix.model.Film;
import com.vn.movie_flix.model.User;
import com.vn.movie_flix.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ClientController {
    private final FilmService filmService;
    private final MovieRecommendationService recommendationService;
    private final GenreService genreService;
    private final ActorService actorService;
    private final DirectorService directorService;
    private final CountryService countryService;
    private final UserService userService;
    private final CommentService commentService;

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size,
            Model model, @AuthenticationPrincipal UserDetails userDetails) {

        List<Film> allMovies = filmService.getAllFilms();
        int totalPages = (int) Math.ceil((double) allMovies.size() / size);
        int currentPage = Math.max(1, Math.min(page, totalPages));

        int start = (currentPage - 1) * size;
        int end = Math.min(start + size, allMovies.size());
        List<Film> paginatedMovies = allMovies.subList(start, end);

        Long userId = null;

        if (userDetails != null) {
            userId = userService.findByEmail(userDetails.getUsername()).getId();
        }

        List<Map<String, Object>> personalizedMovies = List.of();
        if (userId != null && userId > 0) {
            personalizedMovies = recommendationService.getPersonalizedRecommendations(userId);
        }

        model.addAttribute("paginatedMovies", paginatedMovies);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalMovies", allMovies.size());
        model.addAttribute("personalizedMovies", personalizedMovies);

        return "client/index";
    }

    @GetMapping("/movie/{id}")
    public String watchMovie(@PathVariable Long id,
                             Model model, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {

        Film movie = filmService.findById(id);
        if (movie == null) {
            return "redirect:/";
        }

        boolean isFavorited = false;
        Long userId = null;

        if (userDetails != null) {
            User user = userService.findByEmail(userDetails.getUsername());
            userId = user.getId();
            Set<Film> userHistory = user.getHistory();
            userHistory.add(movie);
            userService.save(user);

            isFavorited = userService.isFilmInFavorites(user.getId(), id);
        }


        List<Map<String, Object>> relatedMovies =
                recommendationService.getContentBasedRecommendations(id);

        model.addAttribute("movie", movie);
        model.addAttribute("relatedMovies", relatedMovies);

        List<Comment> comments = commentService.getCommentsByFilmId(id);

        model.addAttribute("comments", comments);
        model.addAttribute("isFavorited", isFavorited);

        model.addAttribute("currentUri", request.getRequestURI());

        return "client/watch";
    }


    @GetMapping("/search")
    public String search(
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "genreId", required = false) Long genreId,
            @RequestParam(name = "actorId", required = false) Long actorId,
            @RequestParam(name = "directorId", required = false) Long directorId,
            @RequestParam(name = "countryId", required = false) Long countryId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "12") int size,
            Model model
    ) {

        Page<Film> filmPage = filmService.searchAndFilter(
                query, genreId, actorId, directorId, countryId, PageRequest.of(page - 1, size)
        );

        model.addAttribute("genres", genreService.getAll());
        model.addAttribute("actors", actorService.getAll());
        model.addAttribute("directors", directorService.getAll());
        model.addAttribute("countries", countryService.getAll());

        model.addAttribute("filmPage", filmPage);
        model.addAttribute("searchQuery", query);

        model.addAttribute("selectedGenreId", genreId);
        model.addAttribute("selectedActorId", actorId);
        model.addAttribute("selectedDirectorId", directorId);
        model.addAttribute("selectedCountryId", countryId);

        return "client/search";
    }

    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());

        Long userId = user.getId();

        user = userService.getUserProfile(userId);

        if (user == null) {
            return "redirect:/logout";
        }

        model.addAttribute("user", user);
        model.addAttribute("title", "Hồ sơ của tôi");

        return "client/profile";
    }
}

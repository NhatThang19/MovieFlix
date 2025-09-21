package com.vn.movie_flix.service;

import com.vn.movie_flix.model.Film;
import com.vn.movie_flix.model.Genre;
import com.vn.movie_flix.model.User;
import com.vn.movie_flix.repository.FilmRepository;
import com.vn.movie_flix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final WebhookService webhookService;
    private final UserRepository userRepository;

    public void save(Film film) {
        Film newFilm = filmRepository.save(film);
        webhookService.notifyDataChanged("film_created", film.getId());
    }

    public Film findById(Long id) {
        return filmRepository.findById(id).orElse(null);
    }

    public Page<Film> findAll(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return filmRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else {
            return filmRepository.findAll(pageable);
        }
    }

    public void deleteById(Long id) {
        Film filmToDelete = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim với ID: " + id));

        List<User> usersWithThisFilmInHistory = userRepository.findByHistory_Id(id);

        for (User user : usersWithThisFilmInHistory) {
            user.getHistory().remove(filmToDelete);
        }

        filmToDelete.getGenres().clear();
        filmToDelete.getActors().clear();

        filmRepository.deleteById(id);

        webhookService.notifyDataChanged("film_deleted", filmToDelete.getId());
    }


    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Page<Film> searchAndFilter(String query, Long genreId, Long actorId, Long directorId, Long countryId, Pageable pageable) {

        Specification<Film> spec = (root, criteriaQuery, criteriaBuilder) -> null;

        if (query != null && !query.isBlank()) {
            spec = spec.and(titleContains(query));
        }

        if (directorId != null) {
            spec = spec.and(hasDirector(directorId));
        }

        if (countryId != null) {
            spec = spec.and(hasCountry(countryId));
        }

        if (genreId != null) {
            spec = spec.and(hasGenre(genreId));
        }

        if (actorId != null) {
            spec = spec.and(hasActor(actorId));
        }

        return filmRepository.findAll(spec, pageable);
    }

    private Specification<Film> titleContains(String query) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + query.toLowerCase() + "%");
    }

    private Specification<Film> hasDirector(Long directorId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("director").get("id"), directorId);
    }

    private Specification<Film> hasCountry(Long countryId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("country").get("id"), countryId);
    }

    private Specification<Film> hasGenre(Long genreId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.join("genres").get("id"), genreId);
        };
    }

    private Specification<Film> hasActor(Long actorId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.join("actors").get("id"), actorId);
        };
    }
}

package com.vn.movie_flix.service;

import com.vn.movie_flix.model.Film;
import com.vn.movie_flix.model.Genre;
import com.vn.movie_flix.repository.FilmRepository;
import com.vn.movie_flix.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final FilmRepository filmRepository;
    private final WebhookService webhookService;

    public void save(Genre genre) {
        genreRepository.save(genre);
        webhookService.notifyDataChanged("genre_created", genre.getId());

    }

    public Genre findById(Long id) {
        return genreRepository.findById(id).orElse(null);
    }

    public Page<Genre> findAll(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return genreRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            return genreRepository.findAll(pageable);
        }
    }

    public void deleteById(Long id) {
        Genre genreToDelete = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại với ID: " + id));

        List<Film> filmsWithThisGenre = filmRepository.findByGenres_Id(id);

        for (Film film : filmsWithThisGenre) {
            film.getGenres().remove(genreToDelete);
        }

        genreRepository.deleteById(id);

        webhookService.notifyDataChanged("genre_deleted", genreToDelete.getId());
    }

    public List<Genre> getAll() {
        return genreRepository.findAll();
    }

    public Map<Long, String> getGenresAsMap() {
        return genreRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Genre::getId,
                        Genre::getName,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }
}

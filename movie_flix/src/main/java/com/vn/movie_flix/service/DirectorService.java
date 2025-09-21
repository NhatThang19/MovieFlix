package com.vn.movie_flix.service;

import com.vn.movie_flix.model.Actor;
import com.vn.movie_flix.model.Director;
import com.vn.movie_flix.model.Director;
import com.vn.movie_flix.model.Film;
import com.vn.movie_flix.repository.DirectorRepository;
import com.vn.movie_flix.repository.FilmRepository;
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
public class DirectorService {
    private final DirectorRepository directorRepository;
    private final FilmRepository filmRepository;
    private final WebhookService webhookService;

    public void save(Director director) {
        directorRepository.save(director);
        webhookService.notifyDataChanged("director_created", director.getId());
    }

    public Director findById(Long id) {
        return directorRepository.findById(id).orElse(null);
    }

    public Page<Director> findAll(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return directorRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            return directorRepository.findAll(pageable);
        }
    }

    public void deleteById(Long id) {
        Director directorToDelete = directorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đạo diễn với ID: " + id));

        List<Film> filmsByThisDirector = filmRepository.findByDirector_Id(id);

        for (Film film : filmsByThisDirector) {
            film.setDirector(null);
        }

        directorRepository.deleteById(id);

        webhookService.notifyDataChanged("director_deleted", directorToDelete.getId());

    }

    public Map<Long, String> getDirectorsAsMap() {
        return directorRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Director::getId,
                        Director::getName,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    public List<Director> getAll() {
        return directorRepository.findAll();
    }
}

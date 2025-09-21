package com.vn.movie_flix.service;

import com.vn.movie_flix.model.Actor;
import com.vn.movie_flix.model.Film;
import com.vn.movie_flix.model.Genre;
import com.vn.movie_flix.repository.ActorRepository;
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
public class ActorService {
    private final ActorRepository actorRepository;
    private final FilmRepository filmRepository;
    private final WebhookService webhookService;

    public void save(Actor actor) {
        actorRepository.save(actor);
        webhookService.notifyDataChanged("actor_created", actor.getId());
    }

    public Actor findById(Long id) {
        return actorRepository.findById(id).orElse(null);
    }

    public Page<Actor> findAll(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return actorRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            return actorRepository.findAll(pageable);
        }
    }

    public void deleteById(Long id) {
        Actor actorToDelete = actorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy diễn viên với ID: " + id));

        List<Film> filmsWithThisActor = filmRepository.findByActors_Id(id);

        for (Film film : filmsWithThisActor) {
            film.getActors().remove(actorToDelete);
        }
        actorRepository.deleteById(id);

        webhookService.notifyDataChanged("actor_deleted", actorToDelete.getId());
    }

    public Map<Long, String> getActorsAsMap() {
        return actorRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Actor::getId,
                        Actor::getName,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    public List<Actor> getAll() {
        return actorRepository.findAll();
    }
}

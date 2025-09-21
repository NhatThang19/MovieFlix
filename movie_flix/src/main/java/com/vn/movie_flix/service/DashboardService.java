package com.vn.movie_flix.service;

import com.vn.movie_flix.dto.response.DashboardStatsDto;
import com.vn.movie_flix.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final CountryRepository countryRepository;
    private final UserRepository userRepository;

    public DashboardStatsDto getDashboardStats() {
        return DashboardStatsDto.builder()
                .totalFilms(filmRepository.count())
                .totalGenres(genreRepository.count())
                .totalActors(actorRepository.count())
                .totalCountries(countryRepository.count())
                .totalUsers(userRepository.count())
                .build();
    }
}

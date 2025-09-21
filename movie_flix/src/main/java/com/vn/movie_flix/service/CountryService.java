package com.vn.movie_flix.service;

import com.vn.movie_flix.model.Country;
import com.vn.movie_flix.model.Film;
import com.vn.movie_flix.repository.CountryRepository;
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
public class CountryService {
    private final CountryRepository countryRepository;
    private final FilmRepository filmRepository;
    private final WebhookService webhookService;

    public void save(Country country) {
        countryRepository.save(country);
        webhookService.notifyDataChanged("country_created", country.getId());
    }

    public Country findById(Long id) {
        return countryRepository.findById(id).orElse(null);
    }

    public Page<Country> findAll(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return countryRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            return countryRepository.findAll(pageable);
        }
    }

    public void deleteById(Long id) {
        Country countryToDelete = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quốc gia với ID: " + id));

        List<Film> filmsFromThisCountry = filmRepository.findByCountry_Id(id);

        for (Film film : filmsFromThisCountry) {
            film.setCountry(null);
        }

        countryRepository.deleteById(id);

        webhookService.notifyDataChanged("Country_deleted", countryToDelete.getId());

    }

    public Map<Long, String> getCountriesAsMap() {
        return countryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Country::getId,
                        Country::getName,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    public List<Country> getAll() {
        return countryRepository.findAll();
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private static final Set<String> SORTED_BY = Set.of("likes", "year");
    private final FilmService service;

    @GetMapping
    public List<Film> getFilms() {
        return service.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return service.getFilmById(id);
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        log.info("Request received POST /films: '{}'", film);
        return service.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Request received PUT /films: '{}'", film);
        return service.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Request received PUT /films/{}/like/{}", id, userId);
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Request received DELETE /films/{}/like/{}", id, userId);
        service.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year
    ) {
        return service.getPopular(count, genreId, year);
    }

    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable Long filmId) {
        log.info("Request received DELETE /films/{}", filmId);
        service.removeFilm(filmId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(
            @PathVariable Long directorId,
            @RequestParam(name = "sortBy", value = "sortBy", defaultValue = "year") String sortBy) {
        log.info("Request received GET /films/director/{}?sortBy={}", directorId, sortBy);
        if (!SORTED_BY.contains(sortBy.toLowerCase())) {
            throw new ValidationException(String.format("Invalid request parameter sortBy='%s'", sortBy));
        }
        return service.getFilmsByDirector(directorId, sortBy.toLowerCase());
    }

    @GetMapping("/search")
    public List<Film> searchFilm(
            @RequestParam(name = "query", value = "query") String query,
            @RequestParam(name = "by", value = "by", defaultValue = "title", required = false) String... by) {
        log.info("Request received GET 'GET /films/search?query={}&by={}'", query, by);
        if (query.isBlank()) {
            throw new ValidationException("Request parameter 'query' should not be empty.");
        }
        return service.search(query, by);
    }
}

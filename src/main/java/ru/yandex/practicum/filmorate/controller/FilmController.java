package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        log.info("Request received PUT /films{}/like/{}", id, userId);
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Request received DELETE /films{}/like/{}", id, userId);
        service.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return service.getPopular(count);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsForDirector(
            @PathVariable Long directorId,
            @RequestParam(name = "sortBy", value = "sortBy", defaultValue = "year") String sortBy) {
        log.info("Request received GET /films/director/{}?sortBy={}", directorId, sortBy);
        if (!SORTED_BY.contains(sortBy.toLowerCase())) {
            throw new ValidationException(String.format("Invalid request parameter sortBy='%s'", sortBy));
        }
        return service.getFilmsForDirector(directorId, sortBy.toLowerCase());
    }
}
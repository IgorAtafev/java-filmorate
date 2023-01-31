package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    FilmService service = new FilmService();

    @GetMapping
    public List<Film> getFilms() {
        return service.getFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Request received POST /films: '{}'", film);
        return service.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получен запрос PUT /films: '{}'", film);
        return service.updateFilm(film);
    }
}
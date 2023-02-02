package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    /**
     * Returns a list of all films
     * @return list of all films
     */
    List<Film> getFilms();

    /**
     * Creates a new film
     * @param film
     * @return new film
     */
    Film createFilm(Film film);

    /**
     * Updates the film
     * @param film
     * @return updated film
     */
    Film updateFilm(Film film);
}
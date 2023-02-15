package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    /**
     * Returns a list of all films
     * @return list of all films
     */
    List<Film> getFilms();

    /**
     * Returns a film by id
     * @param id
     * @return film or null if there was no one
     */
    Optional<Film> getFilmById(Long id);

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
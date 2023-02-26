package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

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

    /**
     * Adds a user like to a film
     * @param film
     * @param user
     */
    void addLike(Film film, User user);

    /**
     * Removes a user like to a film
     * @param film
     * @param user
     */
    void removeLike(Film film, User user);
}
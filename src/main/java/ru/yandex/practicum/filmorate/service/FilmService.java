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
     * Returns film by id
     * If the film is not found throws NotFoundException
     * @param id
     * @return film by id
     */
    Film getFilmById(Long id);

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
     * If the film or user is not found throws NotFoundException
     * @param id
     * @param userId
     */
    void addLike(Long id, Long userId);

    /**
     * Removes a user like to a film
     * If the film or user is not found throws NotFoundException
     * @param id
     * @param userId
     */
    void removeLike(Long id, Long userId);

    /**
     * Returns a list of popular films by number of likes
     * The number of films is set by the parameter count
     * @param count
     * @return list of popular films
     */
    List<Film> getPopular(int count);

    /**
     * Removes a film
     * If the film is not found throws NotFoundException
     * @param id
     */
    void removeFilm(Long id);
}
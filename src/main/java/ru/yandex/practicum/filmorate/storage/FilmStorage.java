package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    /**
     * Returns a list of all films
     *
     * @return list of all films
     */
    List<Film> getFilms();

    /**
     * Returns a film by id
     *
     * @param id
     * @return film or null if there was no one
     */
    Optional<Film> getFilmById(Long id);

    /**
     * Creates a new film
     *
     * @param film
     * @return new film
     */
    Film createFilm(Film film);

    /**
     * Updates the film
     *
     * @param film
     * @return updated film
     */
    Film updateFilm(Film film);

    /**
     * Adds a user like to a film
     *
     * @param id
     * @param userId
     */
    void addLike(Long id, Long userId);

    /**
     * Removes a user like to a film
     *
     * @param id
     * @param userId
     */
    void removeLike(Long id, Long userId);

    /**
     * Returns a list of popular films by number of likes
     * The number of films is set by the parameter count
     *
     * @param count
     * @return list of popular films
     */
    List<Film> getPopular(int count);

    /**
     * Removes a film
     * @param id
     */
    void removeFilm(Long id);

    /**
     * Checks for the existence of Film by id
     *
     * @param id
     * @return true or false
     */
    boolean filmExists(Long id);

    /**
     * Removes a film from film_likes
     * @param id
     */
    void removeLikeFilm(Long id);

    /**
     * Removes a film from film_genres
     * @param id
     */
    void removeGenreFilm(Long id);

    /**
     * Returns a list of films for director, sorted by likes or year
     *
     * @param directorId director's id
     * @param sortBy     sorted type (likes or year)
     * @return list of films
     */
    List<Film> getFilmsForDirector(Long directorId, String sortBy);
}
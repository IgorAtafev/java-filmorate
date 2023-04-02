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
     * Filtering should be based on two parameters: by genre and for the year
     *
     * @param count
     * @param genreId
     * @param year
     * @return list of popular films
     */
    List<Film> getPopular(int count, Integer genreId, Integer year);

    /**
     * Removes a film
     *
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
     * Checks for the existence of Director by id Film
     *
     * @param id
     * @return true or false
     */
    boolean filmDirectorExists(Long id);

    /**
     * Returns a list of films related to users preferences
     * If the user is not found throws NotFoundException
     *
     * @param id
     * @return list of films recommended to user
     */
    List<Film> getRecommendations(Long id);

    /**
     * Removes a film from film_likes
     *
     * @param id
     */
    void removeLikeFilm(Long id);

    /**
     * Removes a film from film_genres
     *
     * @param id
     */
    void removeGenreFilm(Long id);

    /**
     * Removes a film from film_director
     *
     * @param id
     */
    void removeFilmDirector(long id);

    /**
     * Returns a list of films for director, sorted by likes or year
     *
     * @param directorId director's id
     * @param sortBy     sorted type (likes or year)
     * @return list of films
     */
    List<Film> getFilmsForDirector(Long directorId, String sortBy);

    /**
     * Checks for the existence of a film like
     *
     * @param id
     * @param userId
     * @return true or false
     */
    boolean likeExists(Long id, Long userId);
}
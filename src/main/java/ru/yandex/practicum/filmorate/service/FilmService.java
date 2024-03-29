package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    /**
     * Returns a list of all films
     *
     * @return list of all films
     */
    List<Film> getFilms();

    /**
     * Returns film by id
     * If the film is not found throws NotFoundException
     *
     * @param id
     * @return film by id
     */
    Film getFilmById(Long id);

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
     * If the film or user is not found throws NotFoundException
     *
     * @param id
     * @param userId
     */
    void addLike(Long id, Long userId);

    /**
     * Removes a user like to a film
     * If the film or user is not found throws NotFoundException
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
     * If the film is not found throws NotFoundException
     *
     * @param id
     */
    void removeFilm(Long id);

    /**
     * Returns a list of films for director, sorted by likes or year
     *
     * @param directorId director's id
     * @param sortBy sorted type (likes or year)
     * @return list of films
     */
    List<Film> getFilmsByDirector(Long directorId, String sortBy);

    /**
     * Returns a list of films for search substring by title or/and director's name
     *
     * @param query search substring
     * @param by search param title or/and director's name
     * @return list of films
     */
    List<Film> search(String query, String[] by);

    /**
     * Adds genres to the film list
     *
     * @param films
     */
    void addGenresToFilms(List<Film> films);

    /**
     * Adds directors to the film list
     *
     * @param films
     */
    void addDirectorsToFilms(List<Film> films);
}

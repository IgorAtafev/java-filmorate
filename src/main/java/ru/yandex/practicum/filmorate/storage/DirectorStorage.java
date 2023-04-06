package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DirectorStorage {

    /**
     * Returns a list of all directors
     *
     * @return list of all films
     */
    Collection<Director> getDirectors();

    /**
     * Creates a new director
     *
     * @param director
     * @return new director
     */
    Director create(Director director);

    /**
     * Updates the film
     *
     * @param director
     * @return updated film
     */
    Director update(Director director);

    /**
     * Returns a director by id
     *
     * @param id
     * @return director or null if there was no one
     */
    Director getDirectorById(long id);

    /**
     * Delete the director by id
     *
     * @param id
     * @return
     */
    int delete(long id);

    /**
     * Get collection of directors for film
     *
     * @param id film
     * @return collection of directors or empty collection
     */
    Collection<Director> getDirectorsByFilmId(Long id);

    /**
     * Checks for the existence of Director by id
     *
     * @param id
     * @return true or false
     */
    boolean directorExists(long id);

    /**
     * Returns a list of directors by film id list
     *
     * @param filmIds
     * @return list of directors by film id list
     */
    Map<Long, Set<Director>> getDirectorsByFilmIds(List<Long> filmIds);
}
package ru.yandex.practicum.filmorate.service;


import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {
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
}

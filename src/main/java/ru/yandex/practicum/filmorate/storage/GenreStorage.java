package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    /**
     * Returns a list of all genres
     * @return list of all genres
     */
    List<Genre> getGenres();

    /**
     * Returns genre by id
     * @param id
     * @return genre or null if there was no one
     */
    Optional<Genre> getGenreById(Integer id);

    /**
     * Returns a list of genres by id list
     * @param ids
     * @return list of genres by id list
     */
    List<Genre> getGenresByIds(List<Integer> ids);

    /**
     * Returns a list of genres by film id
     * @param filmId
     * @return list of genres by film id
     */
    List<Genre> getGenresByFilmId(Long filmId);
}
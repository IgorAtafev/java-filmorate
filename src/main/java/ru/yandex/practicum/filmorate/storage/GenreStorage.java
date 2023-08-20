package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    /**
     * Returns a list of all genres
     *
     * @return list of all genres
     */
    List<Genre> getGenres();

    /**
     * Returns genre by id
     *
     * @param id
     * @return genre or null if there was no one
     */
    Optional<Genre> getGenreById(Integer id);

    /**
     * Returns a list of genres by film id
     *
     * @param filmId
     * @return list of genres by film id
     */
    List<Genre> getGenresByFilmId(Long filmId);

    /**
     * Returns a list of genres by film id list
     *
     * @param filmIds
     * @return list of genres by film id list
     */
    Map<Long, Set<Genre>> getGenresByFilmIds(List<Long> filmIds);
}

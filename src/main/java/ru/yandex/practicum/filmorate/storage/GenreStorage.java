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
}
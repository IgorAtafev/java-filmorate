package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {

    /**
     * Returns a list of all genres
     *
     * @return list of all genres
     */
    List<Genre> getGenres();

    /**
     * Returns genre by id
     * If the genre is not found throws NotFoundException
     *
     * @param id
     * @return genre by id
     */
    Genre getGenreById(Integer id);
}

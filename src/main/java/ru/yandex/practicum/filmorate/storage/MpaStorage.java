package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    /**
     * Returns a list of all Mpa ratings
     * @return list of all Mpa ratings
     */
    List<Mpa> getMpaRatings();

    /**
     * Returns Mpa rating by id
     * @param id
     * @return Mpa rating or null if there was no one
     */
    Optional<Mpa> getMpaRatingById(Integer id);
}
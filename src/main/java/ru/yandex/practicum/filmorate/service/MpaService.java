package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {

    /**
     * Returns a list of all Mpa ratings
     * @return list of all Mpa ratings
     */
    List<Mpa> getMpaRatings();

    /**
     * Returns Mpa rating by id
     * If the rating is not found throws NotFoundException
     * @param id
     * @return Mpa rating by id
     */
    Mpa getMpaRatingById(Integer id);
}
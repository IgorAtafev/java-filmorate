package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmService {

    private final Map<Integer, Film> films = new HashMap<>();

    /**
     * Returns a list of all films
     * @return list of all films
     */
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    /**
     * Creates a new film
     * @param film
     * @return new film
     */
    public Film create(Film film) {
        return null;
    }

    /**
     * Updates the film
     * @param film
     * @return updated film
     */
    public Film update(Film film) {
        return null;
    }
}
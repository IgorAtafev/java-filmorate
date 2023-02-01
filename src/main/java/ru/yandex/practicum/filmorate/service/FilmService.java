package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmService {

    private int nextId = 0;
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
    public Film createFilm(Film film) {
        if (film.getId() != 0) {
            throw new ValidationException("The movie must have an empty ID when created");
        }

        film.setId(generateId());
        films.put(film.getId(), film);

        return film;
    }

    /**
     * Updates the film
     * @param film
     * @return updated film
     */
    public Film updateFilm(Film film) {
        if (film.getId() == 0) {
            throw new ValidationException("The movie must not have an empty ID when updating");
        }

        if (!films.containsKey(film.getId())) {
            throw new ValidationException("This movie does not exist");
        }

        films.put(film.getId(), film);

        return film;
    }

    private int generateId() {
        return ++nextId;
    }
}
package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.validator.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmServiceImpl implements FilmService {

    private int nextId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getId() != null && film.getId() != 0) {
            throw new ValidationException("The movie must have an empty ID when created");
        }

        film.setId(++nextId);
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null || film.getId() == 0) {
            throw new ValidationException("The movie must not have an empty ID when updating");
        }

        if (!films.containsKey(film.getId())) {
            throw new ValidationException("This movie does not exist");
        }

        films.put(film.getId(), film);

        return film;
    }
}
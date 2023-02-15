package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage storage;

    @Override
    public List<Film> getFilms() {
        return storage.getFilms();
    }

    @Override
    public Film createFilm(Film film) {
        if (!isIdValueNull(film)) {
            throw new ValidationException("The movie must have an empty ID when created");
        }

        return storage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (isIdValueNull(film)) {
            throw new ValidationException("The movie must not have an empty ID when updating");
        }

        if (storage.getFilmById(film.getId()).isEmpty()) {
            throw new ValidationException("This movie does not exist");
        }

        return storage.updateFilm(film);
    }

    private boolean isIdValueNull(Film film) {
        return film.getId() == null;
    }
}
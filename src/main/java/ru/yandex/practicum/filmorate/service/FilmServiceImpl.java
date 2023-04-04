package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private static final String FILM_DOES_NOT_EXIST = "Film width id %d does not exist";
    private static final String MPA_RATING_DOES_NOT_EXIST = "Mpa rating width id %d does not exist";
    private static final String USER_DOES_NOT_EXIST = "User width id %d does not exist";
    private static final String EMPTY_ID_ON_CREATION = "The film must have an empty ID when created";
    private static final String NOT_EMPTY_ID_ON_UPDATE = "The film must not have an empty ID when updating";
    private static final String DIRECTOR_DOSE_NOT_EXIST = "Director with id %d does not exist";

    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;

    private final EventStorage eventStorage;

    @Override
    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();

        directorStorage.addDirectorsToFilms(films);
        return films;
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id).orElseThrow(
                () -> new NotFoundException(String.format(FILM_DOES_NOT_EXIST, id))
        );

        film.addDirectors(directorStorage.getDirectorsByFilmId(film.getId()));
        return film;

    }

    @Override
    public Film createFilm(Film film) {
        if (!isIdValueNull(film)) {
            throw new ValidationException(EMPTY_ID_ON_CREATION);
        }

        if (!mpaStorage.mpaRatingExists(film.getMpa().getId())) {
            throw new NotFoundException(String.format(MPA_RATING_DOES_NOT_EXIST, film.getMpa().getId()));
        }

        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (isIdValueNull(film)) {
            throw new ValidationException(NOT_EMPTY_ID_ON_UPDATE);
        }

        if (!filmStorage.filmExists(film.getId())) {
            throw new NotFoundException(String.format(FILM_DOES_NOT_EXIST, film.getId()));
        }

        if (!mpaStorage.mpaRatingExists(film.getMpa().getId())) {
            throw new NotFoundException(String.format(MPA_RATING_DOES_NOT_EXIST, film.getMpa().getId()));
        }

        return filmStorage.updateFilm(film);
    }

    @Override
    public void addLike(Long id, Long userId) {
        if (!filmStorage.filmExists(id)) {
            throw new NotFoundException(String.format(FILM_DOES_NOT_EXIST, id));
        }

        if (!userStorage.userExists(userId)) {
            throw new NotFoundException(String.format(USER_DOES_NOT_EXIST, userId));
        }
        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .entityId(id)
                .eventType("LIKE")
                .operation("ADD")
                .timestamp(System.currentTimeMillis())
                .build());
        if (filmStorage.likeExists(id, userId)) {
            return;
        }
        filmStorage.addLike(id, userId);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        if (!filmStorage.filmExists(id)) {
            throw new NotFoundException(String.format(FILM_DOES_NOT_EXIST, id));
        }

        if (!userStorage.userExists(userId)) {
            throw new NotFoundException(String.format(USER_DOES_NOT_EXIST, userId));
        }

        filmStorage.removeLike(id, userId);
        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .entityId(id)
                .eventType("LIKE")
                .operation("REMOVE")
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @Override
    public List<Film> getPopular(int count, Integer genreId, Integer year) {
        List<Film> films = filmStorage.getPopular(count, genreId, year);
        directorStorage.addDirectorsToFilms(films);
        return films;
    }

    @Override
    public void removeFilm(Long id) {
        if (!filmStorage.filmExists(id)) {
            throw new NotFoundException(String.format(FILM_DOES_NOT_EXIST, id));
        }

        filmStorage.removeFilm(id);
    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        if (!directorStorage.directorExists(directorId)) {
            throw new NotFoundException(String.format(DIRECTOR_DOSE_NOT_EXIST, directorId));
        }
        List<Film> films = filmStorage.getFilmsByDirector(directorId, sortBy);
        directorStorage.addDirectorsToFilms(films);
        return films;
    }

    @Override
    public List<Film> search(String query, String[] by) {
        List<Film> films = filmStorage.search(query, by);
        directorStorage.addDirectorsToFilms(films);
        return films;
    }

    private boolean isIdValueNull(Film film) {
        return film.getId() == null;
    }
}
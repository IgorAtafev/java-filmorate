package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;

    @Override
    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        addGenresToFilms(films);
        addDirectorsToFilms(films);
        return films;
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id).orElseThrow(
                () -> new NotFoundException(String.format("Film width id %d does not exist", id))
        );

        film.addGenres(genreStorage.getGenresByFilmId(film.getId()));
        film.addDirectors(directorStorage.getDirectorsByFilmId(film.getId()));
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        if (!isIdValueNull(film)) {
            throw new ValidationException("The film must have an empty ID when created");
        }

        if (!mpaStorage.mpaRatingExists(film.getMpa().getId())) {
            throw new NotFoundException(String.format("Mpa rating width id %d does not exist", film.getMpa().getId()));
        }

        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (isIdValueNull(film)) {
            throw new ValidationException("The film must not have an empty ID when updating");
        }

        if (!filmStorage.filmExists(film.getId())) {
            throw new NotFoundException(String.format("Film width id %d does not exist", film.getId()));
        }

        if (!mpaStorage.mpaRatingExists(film.getMpa().getId())) {
            throw new NotFoundException(String.format("Mpa rating width id %d does not exist", film.getMpa().getId()));
        }

        return filmStorage.updateFilm(film);
    }

    @Override
    public void addLike(Long id, Long userId) {
        if (!filmStorage.filmExists(id)) {
            throw new NotFoundException(String.format("Film width id %d does not exist", id));
        }

        if (!userStorage.userExists(userId)) {
            throw new NotFoundException(String.format("User width id %d does not exist", userId));
        }

        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .entityId(id)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
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
            throw new NotFoundException(String.format("Film width id %d does not exist", id));
        }

        if (!userStorage.userExists(userId)) {
            throw new NotFoundException(String.format("User width id %d does not exist", userId));
        }

        filmStorage.removeLike(id, userId);

        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .entityId(id)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @Override
    public List<Film> getPopular(int count, Integer genreId, Integer year) {
        List<Film> films = filmStorage.getPopular(count, genreId, year);
        addGenresToFilms(films);
        addDirectorsToFilms(films);
        return films;
    }

    @Override
    public void removeFilm(Long id) {
        if (!filmStorage.filmExists(id)) {
            throw new NotFoundException(String.format("Film width id %d does not exist", id));
        }

        filmStorage.removeFilm(id);
    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        if (!directorStorage.directorExists(directorId)) {
            throw new NotFoundException(String.format("Director with id %d does not exist", directorId));
        }

        List<Film> films = filmStorage.getFilmsByDirector(directorId, sortBy);
        addGenresToFilms(films);
        addDirectorsToFilms(films);
        return films;
    }

    @Override
    public List<Film> search(String query, String[] by) {
        List<Film> films = filmStorage.search(query, by);
        addGenresToFilms(films);
        addDirectorsToFilms(films);
        return films;
    }

    @Override
    public void addGenresToFilms(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<Long, Set<Genre>> filmsGenres = genreStorage.getGenresByFilmIds(filmIds);

        if (filmsGenres.isEmpty()) {
            return;
        }

        for (Film film : films) {
            if (filmsGenres.containsKey(film.getId())) {
                film.addGenres(filmsGenres.get(film.getId()));
            }
        }
    }

    @Override
    public void addDirectorsToFilms(List<Film> films) {
        if (!films.isEmpty()) {
            directorStorage.addDirectorsToFilms(films);
        }
    }

    private boolean isIdValueNull(Film film) {
        return film.getId() == null;
    }
}
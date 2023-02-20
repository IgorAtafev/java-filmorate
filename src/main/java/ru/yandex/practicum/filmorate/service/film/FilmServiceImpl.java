package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage storage;
    private final UserService userService;

    @Override
    public List<Film> getFilms() {
        return storage.getFilms();
    }

    @Override
    public Film getFilmById(Long id) {
        return storage.getFilmById(id).orElseThrow(
                () -> new NotFoundException(String.format("Film width id %d does not exist", id))
        );
    }

    @Override
    public Film createFilm(Film film) {
        if (!isIdValueNull(film)) {
            throw new ValidationException("The film must have an empty ID when created");
        }

        return storage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (isIdValueNull(film)) {
            throw new ValidationException("The film must not have an empty ID when updating");
        }

        getFilmById(film.getId());

        return storage.updateFilm(film);
    }

    @Override
    public void addLike(Long id, Long userId) {
        Film film = getFilmById(id);
        User user = userService.getUserById(userId);

        storage.addLike(film, user);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        Film film = getFilmById(id);
        User user = userService.getUserById(userId);

        storage.removeLike(film, user);
    }

    @Override
    public List<Film> getPopular(int count) {
        Comparator<Film> comparator = Comparator.comparing(film -> film.getLikes().size());
        comparator = comparator.reversed();

        return storage.getFilms().stream()
                .sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean isIdValueNull(Film film) {
        return film.getId() == null;
    }
}
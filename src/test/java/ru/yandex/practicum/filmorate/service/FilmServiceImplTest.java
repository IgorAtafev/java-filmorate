package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.controller.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.controller.service.UserService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserService userService;

    @InjectMocks
    private FilmServiceImpl filmService;

    @Test
    void getFilms_shouldReturnEmptyListOfFilms() {
        when(filmStorage.getFilms()).thenReturn(Collections.emptyList());

        assertTrue(filmService.getFilms().isEmpty());
    }

    @Test
    void getFilms_shouldReturnListOfFilms() {
        Film film1 = initFilm();
        Film film2 = initFilm();

        List<Film> expected = List.of(film1, film2);

        when(filmStorage.getFilms()).thenReturn(expected);

        assertEquals(expected, filmService.getFilms());
    }

    @Test
    void getFilmById_shouldReturnFilmById() {
        Long filmId = 1L;
        Film film = initFilm();

        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));

        assertEquals(film, filmService.getFilmById(filmId));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getFilmById_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        when(filmStorage.getFilmById(filmId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.getFilmById(filmId)
        );
    }

    @Test
    void createFilm_shouldCreateAFilm() {
        Film film = initFilm();

        when(filmStorage.createFilm(film)).thenReturn(film);

        assertEquals(film, filmService.createFilm(film));
    }

    @Test
    void createFilm_shouldThrowAnException_ifFilmIdIsNotEmpty() {
        Long filmId = 1L;
        Film film = initFilm();
        film.setId(filmId);

        assertThrows(
                ValidationException.class,
                () -> filmService.createFilm(film)
        );

        verify(filmStorage, never()).createFilm(film);
    }

    @Test
    void updateFilm_shouldUpdateTheFilm() {
        Long filmId = 1L;
        Film film = initFilm();
        film.setId(filmId);

        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(filmStorage.updateFilm(film)).thenReturn(film);

        assertEquals(film, filmService.updateFilm(film));
    }

    @Test
    void updateFilm_shouldThrowAnException_ifFilmIdIsEmpty() {
        Film film = initFilm();

        assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(film)
        );

        verify(filmStorage, never()).updateFilm(film);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void updateFilm_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        Film film = initFilm();
        film.setId(filmId);

        when(filmStorage.getFilmById(filmId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.updateFilm(film)
        );

        verify(filmStorage, never()).updateFilm(film);
    }

    @Test
    void addLike_shouldAddTheUserLikeToAFilm() {
        Long filmId = 1L;
        Long userId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userService.getUserById(userId)).thenReturn(user);
        doNothing().when(filmStorage).addLike(film, user);

        filmService.addLike(filmId, userId);

        verify(filmStorage, times(1)).addLike(film, user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        Long userId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(filmStorage.getFilmById(filmId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.addLike(filmId, userId)
        );

        verify(filmStorage, never()).addLike(film, user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long filmId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userService.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.addLike(filmId, userId)
        );

        verify(filmStorage, never()).addLike(film, user);
    }

    @Test
    void removeLike_shouldRemoveTheUserLikeToAFilm() {
        Long filmId = 1L;
        Long userId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userService.getUserById(userId)).thenReturn(user);
        doNothing().when(filmStorage).removeLike(film, user);

        filmService.removeLike(filmId, userId);

        verify(filmStorage, times(1)).removeLike(film, user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        Long userId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(filmStorage.getFilmById(filmId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.removeLike(filmId, userId)
        );

        verify(filmStorage, never()).removeLike(film, user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long filmId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userService.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.removeLike(filmId, userId)
        );

        verify(filmStorage, never()).removeLike(film, user);
    }

    @Test
    void getPopular_shouldReturnEmptyListOfPopularFilms() {
        when(filmStorage.getFilms()).thenReturn(Collections.emptyList());

        assertTrue(filmService.getPopular(10).isEmpty());
    }

    @Test
    void getPopular_shouldReturnListOfPopularFilmsByNumberOfLikes() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        Film film1 = initFilm();
        Film film2 = initFilm();
        Film film3 = initFilm();

        film1.addLike(userId1);
        film2.addLike(userId1);
        film2.addLike(userId2);

        List<Film> expected = List.of(film2);

        when(filmStorage.getFilms()).thenReturn(List.of(film1, film2, film3));

        assertEquals(expected, filmService.getPopular(1));
    }

    private Film initFilm() {
        Film film = new Film();

        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);

        return film;
    }

    private User initUser() {
        User user = new User();

        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946, 8, 20));

        return user;
    }
}
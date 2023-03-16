package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {

    @Mock
    private FilmStorage storage;

    @Mock
    private UserService userService;

    @Mock
    private MpaService mpaService;

    @InjectMocks
    private FilmServiceImpl filmService;

    @Test
    void getFilms_shouldReturnEmptyListOfFilms() {
        when(storage.getFilms()).thenReturn(Collections.emptyList());

        assertTrue(filmService.getFilms().isEmpty());

        verify(storage, times(1)).getFilms();
    }

    @Test
    void getFilms_shouldReturnListOfFilms() {
        Film film1 = initFilm();
        Film film2 = initFilm();

        List<Film> expected = List.of(film1, film2);

        when(storage.getFilms()).thenReturn(expected);

        assertEquals(expected, filmService.getFilms());

        verify(storage, times(1)).getFilms();
    }

    @Test
    void getFilmById_shouldReturnFilmById() {
        Long filmId = 1L;
        Film film = initFilm();

        when(storage.getFilmById(filmId)).thenReturn(Optional.of(film));

        assertEquals(film, filmService.getFilmById(filmId));

        verify(storage, times(1)).getFilmById(filmId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getFilmById_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        when(storage.getFilmById(filmId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.getFilmById(filmId)
        );

        verify(storage, times(1)).getFilmById(filmId);
    }

    @Test
    void createFilm_shouldCreateAFilm() {
        Integer mpaId = 1;
        Film film = initFilm();
        film.getMpa().setId(mpaId);

        when(mpaService.getMpaRatingById(mpaId)).thenReturn(film.getMpa());
        when(storage.createFilm(film)).thenReturn(film);

        assertEquals(film, filmService.createFilm(film));

        verify(mpaService, times(1)).getMpaRatingById(mpaId);
        verify(storage, times(1)).createFilm(film);
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

        verify(storage, never()).createFilm(film);
    }

    @Test
    void updateFilm_shouldUpdateTheFilm() {
        Long filmId = 1L;
        Integer mpaId = 1;
        Film film = initFilm();
        film.setId(filmId);
        film.getMpa().setId(mpaId);

        when(storage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(mpaService.getMpaRatingById(mpaId)).thenReturn(film.getMpa());
        when(storage.updateFilm(film)).thenReturn(film);

        assertEquals(film, filmService.updateFilm(film));

        verify(storage, times(1)).getFilmById(filmId);
        verify(mpaService, times(1)).getMpaRatingById(mpaId);
        verify(storage, times(1)).updateFilm(film);
    }

    @Test
    void updateFilm_shouldThrowAnException_ifFilmIdIsEmpty() {
        Film film = initFilm();

        assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(film)
        );

        verify(storage, never()).updateFilm(film);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void updateFilm_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        Film film = initFilm();
        film.setId(filmId);

        when(storage.getFilmById(filmId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.updateFilm(film)
        );

        verify(storage, times(1)).getFilmById(filmId);
        verify(storage, never()).updateFilm(film);
    }

    @Test
    void addLike_shouldAddTheUserLikeToAFilm() {
        Long filmId = 1L;
        Long userId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(storage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userService.getUserById(userId)).thenReturn(user);

        filmService.addLike(filmId, userId);

        verify(storage, times(1)).getFilmById(filmId);
        verify(userService, times(1)).getUserById(userId);
        verify(storage, times(1)).addLike(film, user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        Long userId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(storage.getFilmById(filmId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.addLike(filmId, userId)
        );

        verify(storage, times(1)).getFilmById(filmId);
        verify(userService, never()).getUserById(userId);
        verify(storage, never()).addLike(film, user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long filmId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(storage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userService.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.addLike(filmId, userId)
        );

        verify(storage, times(1)).getFilmById(filmId);
        verify(userService, times(1)).getUserById(userId);
        verify(storage, never()).addLike(film, user);
    }

    @Test
    void removeLike_shouldRemoveTheUserLikeToAFilm() {
        Long filmId = 1L;
        Long userId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(storage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userService.getUserById(userId)).thenReturn(user);

        filmService.removeLike(filmId, userId);

        verify(storage, times(1)).getFilmById(filmId);
        verify(userService, times(1)).getUserById(userId);
        verify(storage, times(1)).removeLike(film, user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        Long userId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(storage.getFilmById(filmId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.removeLike(filmId, userId)
        );

        verify(storage, times(1)).getFilmById(filmId);
        verify(userService, never()).getUserById(userId);
        verify(storage, never()).removeLike(film, user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long filmId = 1L;
        Film film = initFilm();
        User user = initUser();

        when(storage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userService.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.removeLike(filmId, userId)
        );

        verify(storage, times(1)).getFilmById(filmId);
        verify(userService, times(1)).getUserById(userId);
        verify(storage, never()).removeLike(film, user);
    }

    @Test
    void getPopular_shouldReturnEmptyListOfPopularFilms() {
        int count = 10;

        when(storage.getPopular(count)).thenReturn(Collections.emptyList());

        assertTrue(filmService.getPopular(10).isEmpty());

        verify(storage, times(1)).getPopular(count);
    }

    @Test
    void getPopular_shouldReturnListOfPopularFilmsByNumberOfLikes() {
        int count = 2;
        Long userId1 = 1L;
        Long userId2 = 2L;
        Film film1 = initFilm();
        Film film2 = initFilm();
        Film film3 = initFilm();

        film1.addLike(userId1);
        film2.addLike(userId1);
        film2.addLike(userId2);

        List<Film> expected = List.of(film2, film1);

        when(storage.getPopular(count)).thenReturn(expected);

        assertEquals(expected, filmService.getPopular(count));

        verify(storage, times(1)).getPopular(count);
    }

    private Film initFilm() {
        Film film = new Film();

        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setMpa(new Mpa());

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
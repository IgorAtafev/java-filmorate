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
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
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
    private FilmStorage filmStorage;

    @Mock
    private MpaStorage mpaStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private GenreStorage genreStorage;

    @Mock
    private DirectorStorage directorStorage;

    @Mock
    private EventStorage eventStorage;

    @InjectMocks
    private FilmServiceImpl filmService;

    @Test
    void getFilms_shouldReturnEmptyListOfFilms() {
        when(filmStorage.getFilms()).thenReturn(Collections.emptyList());

        assertTrue(filmService.getFilms().isEmpty());

        verify(filmStorage, times(1)).getFilms();
    }

    @Test
    void getFilms_shouldReturnListOfFilms() {
        Film film1 = initFilm();
        Film film2 = initFilm();
        film1.setId(1L);
        film2.setId(2L);

        List<Film> expected = List.of(film1, film2);

        when(filmStorage.getFilms()).thenReturn(expected);

        assertEquals(expected, filmService.getFilms());

        verify(filmStorage, times(1)).getFilms();
    }

    @Test
    void getFilmById_shouldReturnFilmById() {
        Long filmId = 1L;
        Film film = initFilm();

        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));

        assertEquals(film, filmService.getFilmById(filmId));

        verify(filmStorage, times(1)).getFilmById(filmId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getFilmById_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        when(filmStorage.getFilmById(filmId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> filmService.getFilmById(filmId)
        );

        verify(filmStorage, times(1)).getFilmById(filmId);
    }

    @Test
    void createFilm_shouldCreateAFilm() {
        Integer mpaId = 1;
        Film film = initFilm();
        film.getMpa().setId(mpaId);

        when(mpaStorage.mpaRatingExists(mpaId)).thenReturn(true);
        when(filmStorage.createFilm(film)).thenReturn(film);

        assertEquals(film, filmService.createFilm(film));

        verify(mpaStorage, times(1)).mpaRatingExists(mpaId);
        verify(filmStorage, times(1)).createFilm(film);
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

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 999})
    void createFilm_shouldThrowAnException_ifRatingDoesNotExist(Integer mpaId) {
        Film film = initFilm();
        film.getMpa().setId(mpaId);

        when(mpaStorage.mpaRatingExists(mpaId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> filmService.createFilm(film)
        );

        verify(mpaStorage, times(1)).mpaRatingExists(mpaId);
        verify(filmStorage, never()).createFilm(film);
    }

    @Test
    void updateFilm_shouldUpdateTheFilm() {
        Long filmId = 1L;
        Integer mpaId = 1;
        Film film = initFilm();
        film.setId(filmId);
        film.getMpa().setId(mpaId);

        when(filmStorage.filmExists(filmId)).thenReturn(true);
        when(mpaStorage.mpaRatingExists(mpaId)).thenReturn(true);
        when(filmStorage.updateFilm(film)).thenReturn(film);

        assertEquals(film, filmService.updateFilm(film));

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(mpaStorage, times(1)).mpaRatingExists(mpaId);
        verify(filmStorage, times(1)).updateFilm(film);
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
        Integer mpaId = 1;
        Film film = initFilm();
        film.setId(filmId);

        when(filmStorage.filmExists(filmId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> filmService.updateFilm(film)
        );

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(mpaStorage, never()).mpaRatingExists(mpaId);
        verify(filmStorage, never()).updateFilm(film);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 999})
    void updateFilm_shouldThrowAnException_ifRatingDoesNotExist(Integer mpaId) {
        Long filmId = 1L;
        Film film = initFilm();
        film.setId(filmId);
        film.getMpa().setId(mpaId);

        when(filmStorage.filmExists(filmId)).thenReturn(true);
        when(mpaStorage.mpaRatingExists(mpaId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> filmService.updateFilm(film)
        );

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(mpaStorage, times(1)).mpaRatingExists(mpaId);
        verify(filmStorage, never()).updateFilm(film);
    }

    @Test
    void addLike_shouldAddTheUserLikeToAFilm() {
        Long filmId = 1L;
        Long userId = 1L;

        when(filmStorage.filmExists(filmId)).thenReturn(true);
        when(userStorage.userExists(userId)).thenReturn(true);

        filmService.addLike(filmId, userId);

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(userStorage, times(1)).userExists(userId);
        verify(filmStorage, times(1)).addLike(filmId, userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        Long userId = 1L;

        when(filmStorage.filmExists(filmId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> filmService.addLike(filmId, userId)
        );

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(userStorage, never()).userExists(userId);
        verify(filmStorage, never()).addLike(filmId, userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long filmId = 1L;

        when(filmStorage.filmExists(filmId)).thenReturn(true);
        when(userStorage.userExists(userId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> filmService.addLike(filmId, userId)
        );

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(userStorage, times(1)).userExists(userId);
        verify(filmStorage, never()).addLike(filmId, userId);
    }

    @Test
    void removeLike_shouldRemoveTheUserLikeToAFilm() {
        Long filmId = 1L;
        Long userId = 1L;

        when(filmStorage.filmExists(filmId)).thenReturn(true);
        when(userStorage.userExists(userId)).thenReturn(true);

        filmService.removeLike(filmId, userId);

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(userStorage, times(1)).userExists(userId);
        verify(filmStorage, times(1)).removeLike(filmId, userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        Long userId = 1L;

        when(filmStorage.filmExists(filmId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> filmService.removeLike(filmId, userId)
        );

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(userStorage, never()).userExists(userId);
        verify(filmStorage, never()).removeLike(filmId, userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long filmId = 1L;

        when(filmStorage.filmExists(filmId)).thenReturn(true);
        when(userStorage.userExists(userId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> filmService.removeLike(filmId, userId)
        );

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(userStorage, times(1)).userExists(userId);
        verify(filmStorage, never()).removeLike(filmId, userId);
    }

    @Test
    void getPopular_shouldReturnEmptyListOfPopularFilms() {
        int count = 10;
        Integer genreId = null;
        Integer year = null;

        when(filmStorage.getPopular(count, genreId, year)).thenReturn(Collections.emptyList());

        assertTrue(filmService.getPopular(10, genreId, year).isEmpty());

        verify(filmStorage, times(1)).getPopular(count, genreId, year);
    }

    @Test
    void getPopular_shouldReturnListOfPopularFilmsByNumberOfLikes() {
        int count = 2;
        Integer genreId = null;
        Integer year = null;
        Long userId1 = 1L;
        Long userId2 = 2L;
        Film film1 = initFilm();
        Film film2 = initFilm();

        film1.addLike(userId1);
        film2.addLike(userId1);
        film2.addLike(userId2);

        List<Film> expected = List.of(film2, film1);

        when(filmStorage.getPopular(count, genreId, year)).thenReturn(expected);

        assertEquals(expected, filmService.getPopular(count, genreId, year));

        verify(filmStorage, times(1)).getPopular(count, genreId, year);
    }

    @Test
    void removeUser_shouldRemoveTheUser() {
        Long userId = 1L;

        when(filmStorage.filmExists(userId)).thenReturn(true);

        filmService.removeFilm(userId);

        verify(filmStorage, times(1)).filmExists(userId);
        verify(filmStorage, times(1)).removeFilm(userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeUser_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        when(filmStorage.filmExists(userId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> filmService.removeFilm(userId)
        );

        verify(filmStorage, times(1)).filmExists(userId);
        verify(filmStorage, never()).removeFilm(userId);
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
}

package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmServiceTest {

    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserServiceImpl(userStorage);

    private final FilmStorage filmStorage = new InMemoryFilmStorage();
    private final FilmService filmService = new FilmServiceImpl(filmStorage, userService);

    @Test
    void getFilms_shouldCheckForNull() {
        assertNotNull(filmService.getFilms());
    }

    @Test
    void getFilms_shouldReturnEmptyListOfFilms() {
        assertTrue(filmService.getFilms().isEmpty());
    }

    @Test
    void getFilms_shouldReturnListOfFilms() {
        Film film1 = initFilm();
        filmService.createFilm(film1);
        Film film2 = initFilm();
        filmService.createFilm(film2);

        List<Film> expected = List.of(film1, film2);
        List<Film> actual = filmService.getFilms();

        assertEquals(expected, actual);
    }

    @Test
    void getFilmById_shouldReturnFilmById() {
        Film film1 = initFilm();
        filmService.createFilm(film1);

        Film film2 = filmService.getFilmById(film1.getId());
        assertEquals(film1, film2);
    }

    @Test
    void getFilmById_shouldThrowAnException_ifFilmDoesNotExist() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmService.getFilmById(0L)
        );
        assertEquals("Film width id 0 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> filmService.getFilmById(-1L)
        );
        assertEquals("Film width id -1 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> filmService.getFilmById(999L)
        );
        assertEquals("Film width id 999 does not exist", exception.getMessage());
    }

    @Test
    void createFilm_shouldCreateAFilm() {
        Film film = initFilm();
        filmService.createFilm(film);

        List<Film> expected = List.of(film);
        List<Film> actual = filmService.getFilms();

        assertEquals(expected, actual);
    }

    @Test
    void createFilm_shouldThrowAnException_ifFilmIdIsNotEmpty() {
        Film film = initFilm();
        film.setId(1L);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmService.createFilm(film)
        );
        assertEquals("The film must have an empty ID when created", exception.getMessage());
    }

    @Test
    void updateFilm_shouldUpdateTheFilm() {
        Film film = initFilm();
        filmService.createFilm(film);
        film.setId(1L);
        film.setName("Film Updated");
        film.setDescription("New film update decriptio");
        film.setReleaseDate(LocalDate.of(1989, 4, 17));
        film.setDuration(190);

        filmService.updateFilm(film);

        List<Film> expected = List.of(film);
        List<Film> actual = filmService.getFilms();

        assertEquals(expected, actual);
    }

    @Test
    void updateFilm_shouldThrowAnException_ifFilmIdIsEmpty() {
        Film film = initFilm();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(film)
        );
        assertEquals("The film must not have an empty ID when updating", exception.getMessage());
    }

    @Test
    void updateFilm_shouldThrowAnException_ifFilmDoesNotExist() {
        Film film1 = initFilm();
        filmService.createFilm(film1);
        Film film2 = initFilm();
        filmService.createFilm(film2);

        Film film3 = new Film();
        film3.setId(999L);
        film3.setName("nisi eiusmod3");
        film3.setDescription("adipisicing3");
        film3.setReleaseDate(LocalDate.of(2006, 2, 2));
        film3.setDuration(250);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmService.updateFilm(film3)
        );
        assertEquals("Film width id 999 does not exist", exception.getMessage());
    }

    @Test
    void addLike_shouldAddTheUserLikeToAFilm() {
        Film film = initFilm();
        filmService.createFilm(film);
        User user = initUser();
        userService.createUser(user);

        filmService.addLike(film.getId(), user.getId());

        List<Long> expected = List.of(user.getId());
        List<Long> actual = film.getLikes();

        assertEquals(expected, actual);
    }

    @Test
    void addLike_shouldThrowAnException_ifUserOrFilmDoesNotExist() {
        Film film = initFilm();
        filmService.createFilm(film);
        User user = initUser();
        userService.createUser(user);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmService.addLike(film.getId(), 999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> filmService.addLike(-1L, user.getId())
        );
        assertEquals("Film width id -1 does not exist", exception.getMessage());
    }

    @Test
    void removeLike_shouldRemoveTheUserLikeToAFilm() {
        Film film = initFilm();
        filmService.createFilm(film);
        User user1 = initUser();
        userService.createUser(user1);
        User user2 = initUser();
        userService.createUser(user2);

        filmService.addLike(film.getId(), user1.getId());
        filmService.addLike(film.getId(), user2.getId());

        filmService.removeLike(film.getId(), user1.getId());

        List<Long> expected = List.of(user2.getId());
        List<Long> actual = film.getLikes();

        assertEquals(expected, actual);

        filmService.removeLike(film.getId(), user2.getId());

        assertTrue(film.getLikes().isEmpty());
    }

    @Test
    void removeLike_shouldThrowAnException_ifUserOrFilmDoesNotExist() {
        Film film = initFilm();
        filmService.createFilm(film);
        User user = initUser();
        userService.createUser(user);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmService.removeLike(film.getId(), 999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> filmService.removeLike(-1L, user.getId())
        );
        assertEquals("Film width id -1 does not exist", exception.getMessage());
    }

    @Test
    void getPopular_shouldReturnEmptyListOfPopularFilms() {
        assertTrue(filmService.getPopular(10).isEmpty());
    }

    @Test
    void getPopular_shouldReturnListOfPopularFilmsByNumberOfLikes() {
        Film film1 = initFilm();
        filmService.createFilm(film1);
        Film film2 = initFilm();
        filmService.createFilm(film2);
        User user1 = initUser();
        userService.createUser(user1);
        User user2 = initUser();
        userService.createUser(user2);

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film2.getId(), user1.getId());

        List<Film> expected = List.of(film2);
        List<Film> actual = filmService.getPopular(1);

        assertEquals(expected, actual);
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
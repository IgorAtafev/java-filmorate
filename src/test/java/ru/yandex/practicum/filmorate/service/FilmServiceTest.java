package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmServiceTest {

    private Film film1;

    private final FilmService service = new FilmServiceImpl();

    @BeforeEach
    void setUp() {
        film1 = initFilm();
    }

    @Test
    void getFilms_shouldCheckForNull() {
        assertNotNull(service.getFilms());
    }

    @Test
    void getFilms_shouldReturnEmptyListOfFilms() {
        assertTrue(service.getFilms().isEmpty());
    }

    @Test
    void getFilms_shouldReturnListOfFilms() {
        service.createFilm(film1);
        Film film2 = initFilm();
        service.createFilm(film2);

        List<Film> expected = List.of(film1, film2);
        List<Film> actual = service.getFilms();

        assertEquals(expected, actual);
    }

    @Test
    void createFilm_shouldCreateAFilm() {
        service.createFilm(film1);

        List<Film> expected = List.of(film1);
        List<Film> actual = service.getFilms();

        assertEquals(expected, actual);
    }

    @Test
    void createFilm_shouldThrowAnException_ifTheFilmIdIsNotEmpty() {
        film1.setId(1);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createFilm(film1)
        );
        assertEquals("The movie must have an empty ID when created", exception.getMessage());
    }

    @Test
    void updateFilm_shouldUpdateTheFilm() {
        service.createFilm(film1);
        film1.setId(1);
        film1.setName("Film Updated");
        film1.setDescription("New film update decriptio");
        film1.setReleaseDate(LocalDate.of(1989, 4, 17));
        film1.setDuration(190);

        service.updateFilm(film1);

        List<Film> expected = List.of(film1);
        List<Film> actual = service.getFilms();

        assertEquals(expected, actual);
    }

    @Test
    void updateFilm_shouldThrowAnException_ifTheFilmIdIsEmpty() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.updateFilm(film1)
        );
        assertEquals("The movie must not have an empty ID when updating", exception.getMessage());
    }

    @Test
    void updateFilm_shouldThrowAnException_ifTheFilmDoesNotExist() {
        service.createFilm(film1);
        Film film2 = initFilm();
        service.createFilm(film2);

        Film film3 = new Film();
        film3.setId(999);
        film3.setName("nisi eiusmod3");
        film3.setDescription("adipisicing3");
        film3.setReleaseDate(LocalDate.of(2006, 2, 2));
        film3.setDuration(250);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.updateFilm(film3)
        );
        assertEquals("This movie does not exist", exception.getMessage());
    }

    private Film initFilm() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        return film;
    }
}
package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.validator.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmServiceTest {

    private Film film1;
    private Film film2;

    private final FilmService service = new FilmServiceImpl();

    @BeforeEach
    void setUp() {
        initFilms();
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
        film1 = new Film(1, "Film Updated", "New film update decriptio",
                LocalDate.of(1989, 4, 17), 190);

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
        service.createFilm(film2);

        Film film3 = new Film(999, "nisi eiusmod3", "adipisicing3",
                LocalDate.of(2006, 2, 2), 250);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.updateFilm(film3)
        );
        assertEquals("This movie does not exist", exception.getMessage());
    }

    private void initFilms() {
        film1 = new Film(null, "nisi eiusmod", "adipisicing",
                LocalDate.of(1967, 3, 25), 100);
        film2 = new Film(0, "nisi eiusmod2", "adipisicing2",
                LocalDate.of(1986, 1, 2), 200);
    }
}
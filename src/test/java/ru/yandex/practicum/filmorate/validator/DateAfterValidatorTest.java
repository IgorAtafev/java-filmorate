package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateAfterValidatorTest {

    private Film film;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        film = initFim();
    }

    @Test
    void isValid_shouldCheckTheReleaseDateIsAfter28121895() {
        film.setReleaseDate(LocalDate.of(1989, 4, 17));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    void isValid_shouldCheckTheReleaseDateIsBefore28121895() {
        film.setReleaseDate(LocalDate.of(1800, 1, 9));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());

        String message = "Release date must be no earlier than 28.12.1895";
        assertTrue(violations.stream()
                .map(ConstraintViolation::getMessage)
                .anyMatch(message::equals));
    }

    @Test
    void isValid_shouldCheckTheReleaseDateIsEqual28121895() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    private Film initFim() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        return film;
    }
}
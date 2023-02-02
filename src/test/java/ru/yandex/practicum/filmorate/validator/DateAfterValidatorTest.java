package ru.yandex.practicum.filmorate.validator;

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

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void isValid_shouldCheckTheReleaseDateIsAfter28121895() {
        Film film = new Film(1, "Film Updated", "New film update decriptio",
                LocalDate.of(1989, 4, 17), 190);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    void isValid_shouldCheckTheReleaseDateIsBefore28121895() {
        Film film = new Film(1, "Film Updated", "New film update decriptio",
                LocalDate.of(1800, 1, 9), 190);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());

        String message = "Release date must be no earlier than 28.12.1895";
        assertTrue(violations.stream()
                .map(ConstraintViolation::getMessage)
                .anyMatch(message::equals));
    }

    @Test
    void isValid_shouldCheckTheReleaseDateIsEqual28121895() {
        Film film = new Film(1, "Film Updated", "New film update decriptio",
                LocalDate.of(1895, 12, 28), 190);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }
}
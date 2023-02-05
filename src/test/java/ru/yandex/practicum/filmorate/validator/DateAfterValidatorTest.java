package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateAfterValidatorTest {

    private final TestData testData = new TestData();

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void isValid_shouldCheckTheDateIsAfter28121895() {
        testData.date = LocalDate.of(1989, 4, 17);
        Set<ConstraintViolation<TestData>> violations = validator.validate(testData);
        assertTrue(violations.isEmpty());
    }

    @Test
    void isValid_shouldCheckTheReleaseDateIsBefore28121895() {
        testData.date = LocalDate.of(1800, 1, 9);
        Set<ConstraintViolation<TestData>> violations = validator.validate(testData);

        assertFalse(violations.isEmpty());

        String message = "Date must be no earlier than 28.12.1895";
        assertTrue(violations.stream()
                .map(ConstraintViolation::getMessage)
                .anyMatch(message::equals));
    }

    @Test
    void isValid_shouldCheckTheReleaseDateIsEqual28121895() {
        testData.date = LocalDate.of(1895, 12, 28);
        Set<ConstraintViolation<TestData>> violations = validator.validate(testData);
        assertTrue(violations.isEmpty());
    }

    private static class TestData {
        @After(currentDate = "1895-12-28", pattern = "yyyy-MM-dd", message = "Date must be no earlier than 28.12.1895")
        LocalDate date;
    }
}
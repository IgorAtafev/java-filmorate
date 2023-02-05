package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateAfterValidator implements ConstraintValidator<After, LocalDate> {

    private String current;
    private String pattern;

    @Override
    public void initialize(After constraintAnnotation) {
        current = constraintAnnotation.currentDate();
        pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return true;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate currentDate = LocalDate.parse(current, formatter);

        return date.isAfter(currentDate) || date.isEqual(currentDate);
    }
}
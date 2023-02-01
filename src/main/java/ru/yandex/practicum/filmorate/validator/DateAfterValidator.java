package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateAfterValidator implements ConstraintValidator<After, LocalDate> {

    private String currentDate;
    private String pattern;

    @Override
    public void initialize(After constraintAnnotation) {
        currentDate = constraintAnnotation.currentDate();
        pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.isAfter(LocalDate.parse(currentDate, formatter));
    }
}
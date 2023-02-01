package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateAfterValidator implements ConstraintValidator<After, LocalDate> {

    private String currentDate;

    @Override
    public void initialize(After constraintAnnotation) {
        currentDate = constraintAnnotation.current();
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.isAfter(LocalDate.parse(currentDate, formatter));
    }
}
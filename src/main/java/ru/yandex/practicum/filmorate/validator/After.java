package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Checks that the annotated element must be a date no earlier than the current one.
 * Null elements are considered valid.
 * The currentDate parameter specifies the current date.
 * In the pattern parameter, the date pattern.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateAfterValidator.class)
@Documented
public @interface After {

    String message() default "invalid date";

    String currentDate();

    String pattern() default "yyyy-MM-dd";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
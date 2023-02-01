package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateAfterValidator.class)
@Documented
public @interface After {

    String message() default "{DateAfter.invalid}";

    String current();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validator.After;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Film {

    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 50, message = "Name must contain at least 3 and no more than 50 characters")
    private String name;

    @NotNull(message = "Description cannot be null")
    @Size(max = 200, message = "Description must be no more than 200 characters")
    private String description;

    @NotNull(message = "Release date cannot be null")
    @After(currentDate = "1895-12-28", pattern = "yyyy-MM-dd", message = "Release date must be no earlier than 28.12.1895")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be positive")
    private int duration;
}
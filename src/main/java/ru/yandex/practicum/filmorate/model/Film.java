package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Film {

    private int id;

    @NotBlank
    @Size(min=3, max=50)
    private String name;

    @NotNull
    @Size(max=200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Positive
    private int duration;
}
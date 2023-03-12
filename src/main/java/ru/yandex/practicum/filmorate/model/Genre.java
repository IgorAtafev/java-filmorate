package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class Genre {

    private Integer id;

    @NotBlank(message = "Genre cannot be empty")
    private String name;
}
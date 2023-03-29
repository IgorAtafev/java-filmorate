package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Review {

    private Long reviewId;

    @NotNull(message = "Content cannot be null")
    @Pattern(regexp = "^\\S{2,500}$", message = "Content must contain at least 2 and no more than 500 characters")
    private String content;

    @NotNull(message = "Review type cannot be null")
    private boolean isPositive;

    @NotNull(message = "Film id cannot be null")
    private Long filmId;

    @NotNull(message = "User id cannot be null")
    private Long userId;

    private int useful;
}
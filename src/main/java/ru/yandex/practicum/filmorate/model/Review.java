package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Review {

    @JsonProperty("reviewId")
    private Long id;

    @NotNull(message = "Content cannot be null")
    @Size(min = 2, max = 500, message = "Content must contain at least 2 and no more than 500 characters")
    private String content;

    @NotNull(message = "Review type cannot be null")
    @JsonProperty("isPositive")
    private Boolean positive;

    @NotNull(message = "Film id cannot be null")
    private Long filmId;

    @NotNull(message = "User id cannot be null")
    private Long userId;

    private int useful;
}
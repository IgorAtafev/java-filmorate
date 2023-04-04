package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
import java.util.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Film {

    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 100, message = "Name must be no more than 100 characters")
    private String name;

    @NotNull(message = "Description cannot be null")
    @Size(max = 200, message = "Description must be no more than 200 characters")
    private String description;

    @NotNull(message = "Release date cannot be null")
    @After(currentDate = "1895-12-28", pattern = "yyyy-MM-dd", message = "Release date must be no earlier than 28.12.1895")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be positive")
    private int duration;

    @NotNull
    private Mpa mpa;

    private final Set<Long> likes = new HashSet<>();

    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<Director> directors = new HashSet<>();

    @JsonDeserialize(as = LinkedHashSet.class)
    private final Set<Genre> genres = new HashSet<>();

    public Collection<Long> getLikes() {
        return Collections.unmodifiableSet(likes);
    }

    public void addLike(Long id) {
        likes.add(id);
    }

    public void removeLike(Long id) {
        likes.remove(id);
    }

    public Collection<Genre> getGenres() {
        return Collections.unmodifiableSet(genres);
    }

    public void addGenres(Collection<Genre> otherGenres) {
        genres.addAll(otherGenres);
    }

    public void addDirectors(Collection<Director> otherDirectors) {
        directors.addAll(otherDirectors);
    }
}
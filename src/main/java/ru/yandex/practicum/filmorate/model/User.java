package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {

    private Long id;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Login cannot be null")
    @Pattern(regexp = "^\\S{5,20}$", message = "Login must contain at least 5 and no more than 20 characters")
    private String login;

    @Size(max = 30, message = "Name must contain at least 3 and no more than 30 characters")
    private String name;

    @NotNull(message = "Birthday cannot be null")
    @PastOrPresent(message = "Birthday cannot be in the future")
    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();

    public void addFriend(Long id) {
        friends.add(id);
    }

    public void removeFriend(Long id) {
        friends.remove(id);
    }
}
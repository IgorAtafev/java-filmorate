package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class User {

    private int id;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Pattern(regexp="^\\S{5,20}$")
    private String login;

    @Size(min=3, max=30)
    private String name;

    @NotNull
    @Past
    private LocalDate birthday;
}
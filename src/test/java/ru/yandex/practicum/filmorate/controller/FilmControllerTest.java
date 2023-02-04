package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    @Mock
    private FilmService service;

    @InjectMocks
    private FilmController controller;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getFilms_shouldReturnEmptyListOfFilms() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getFilms_shouldReturnListOfFilms() throws Exception {
        Film film1 = initFilm();
        Film film2 = initFilm();
        List<Film> expected = List.of(film1, film2);
        when(service.getFilms()).thenReturn(expected);

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void createFilm_shouldCreateAFilm() throws Exception {
        Film film = initFilm();
        String json = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isOk());
        mockMvc.perform(put("/films").contentType("application/json").content(json))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFilms")
    void createFilm_shouldResponseWithBadRequest_ifFilmIsInvalid(Film film) throws Exception {
        String json = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidFilms() {
        return Stream.of(
                Arguments.of(initFilm(film -> film.setName(""))),
                Arguments.of(initFilm(film -> film.setDescription("a long string".repeat(20)))),
                Arguments.of(initFilm(film -> film.setReleaseDate(LocalDate.parse("1000-01-01")))),
                Arguments.of(initFilm(film -> film.setDuration(-100)))
        );
    }

    private static Film initFilm(Consumer<Film> consumer) {
        Film film = new Film();
        consumer.accept(film);
        return film;
    }

    private static Film initFilm() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        return film;
    }
}
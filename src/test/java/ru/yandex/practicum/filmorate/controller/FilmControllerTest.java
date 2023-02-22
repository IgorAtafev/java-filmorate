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
import ru.yandex.practicum.filmorate.validator.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
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
    void getFilmById_shouldReturnFilmById() throws Exception {
        Film film = initFilm();

        when(service.getFilmById(1L)).thenReturn(film);

        mockMvc.perform(get("/films/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentFilm")
    void getFilmById_shouldResponseWithNotFound_ifFilmDoesNotExist(Long id) throws Exception {
        when(service.getFilmById(id)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/films/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createFilm_shouldResponseWithOk() throws Exception {
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

    @Test
    void addLike_shouldResponseWithOk() throws Exception {
        mockMvc.perform(put("/films/{id}/like/{userId}", 1, 2))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentFilm")
    void addLike_shouldResponseWithNotFound_ifUserOrFilmDoesNotExist(Long id, Long userId) throws Exception {
        doThrow(NotFoundException.class).when(service).addLike(id, userId);

        mockMvc.perform(put("/films/{id}/like/{userId}", id, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeLike_shouldResponseWithOk() throws Exception {
        mockMvc.perform(delete("/films/{id}/like/{userId}", 1, 2))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentFilm")
    void removeLike_shouldResponseWithNotFound_ifUserOrFilmDoesNotExist(Long id, Long userId) throws Exception {
        doThrow(NotFoundException.class).when(service).removeLike(id, userId);

        mockMvc.perform(delete("/films/{id}/like/{userId}", id, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPopular_shouldReturnEmptyListOfPopularFilms() throws Exception {
        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getPopular_shouldReturnListOfPopularFilmsByNumberOfLikes() throws Exception {
        Film film1 = initFilm();
        Film film2 = initFilm();
        List<Film> expected = List.of(film1, film2);

        when(service.getPopular(2)).thenReturn(expected);

        mockMvc.perform(get("/films/popular?count={count}", 2))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    private static Stream<Arguments> provideInvalidFilms() {
        return Stream.of(
                Arguments.of(initFilm(film -> film.setName(null))),
                Arguments.of(initFilm(film -> film.setName(""))),
                Arguments.of(initFilm(film -> film.setName("ni"))),
                Arguments.of(initFilm(film -> film.setName("nisi eiusm".repeat(5) + "o"))),
                Arguments.of(initFilm(film -> film.setDescription(null))),
                Arguments.of(initFilm(film -> film.setDescription("long strin".repeat(20) + "g"))),
                Arguments.of(initFilm(film -> film.setReleaseDate(null))),
                Arguments.of(initFilm(film -> film.setReleaseDate(LocalDate.parse("1000-01-01")))),
                Arguments.of(initFilm(film -> film.setDuration(0))),
                Arguments.of(initFilm(film -> film.setDuration(-100)))
        );
    }

    private static Stream<Arguments> provideNonExistentFilm() {
        return Stream.of(
                Arguments.of(-1L, -1L),
                Arguments.of(0L, 0L),
                Arguments.of(999L, 999L)
        );
    }

    private static Film initFilm(Consumer<Film> consumer) {
        Film film = initFilm();
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
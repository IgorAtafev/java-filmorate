package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

        verify(service, times(1)).getFilms();
    }

    @Test
    void getFilms_shouldReturnListOfFilms() throws Exception {
        Film film1 = initFilm();
        Film film2 = initFilm();

        List<Film> expected = List.of(film1, film2);
        String json = objectMapper.writeValueAsString(expected);

        when(service.getFilms()).thenReturn(expected);

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getFilms();
    }

    @Test
    void getFilmById_shouldReturnFilmById() throws Exception {
        Long filmId = 1L;
        Film film = initFilm();
        String json = objectMapper.writeValueAsString(film);

        when(service.getFilmById(filmId)).thenReturn(film);

        mockMvc.perform(get("/films/{id}", filmId))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getFilmById(filmId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getFilmById_shouldResponseWithNotFound_ifFilmDoesNotExist(Long filmId) throws Exception {
        when(service.getFilmById(filmId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/films/{id}", filmId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getFilmById(filmId);
    }

    @Test
    void createFilm_shouldResponseWithOk() throws Exception {
        Film film = initFilm();
        String json = objectMapper.writeValueAsString(film);

        when(service.createFilm(film)).thenReturn(film);

        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isOk());

        verify(service, times(1)).createFilm(film);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFilms")
    void createFilm_shouldResponseWithBadRequest_ifFilmIsInvalid(Film film) throws Exception {
        String json = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).createFilm(film);
    }

    @Test
    void updateFilm_shouldResponseWithOk() throws Exception {
        Film film = initFilm();
        String json = objectMapper.writeValueAsString(film);

        when(service.updateFilm(film)).thenReturn(film);

        mockMvc.perform(put("/films").contentType("application/json").content(json))
                .andExpect(status().isOk());

        verify(service, times(1)).updateFilm(film);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFilms")
    void updateFilm_shouldResponseWithBadRequest_ifFilmIsInvalid(Film film) throws Exception {
        String json = objectMapper.writeValueAsString(film);

        mockMvc.perform(put("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).updateFilm(film);
    }

    @Test
    void addLike_shouldResponseWithOk() throws Exception {
        Long filmId = 1L;
        Long userId = 1L;

        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        verify(service, times(1)).addLike(filmId, userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldResponseWithNotFound_ifFilmDoesNotExist(Long filmId) throws Exception {
        Long userId = 1L;

        doThrow(NotFoundException.class).when(service).addLike(filmId, userId);

        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).addLike(filmId, userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        Long filmId = 1L;

        doThrow(NotFoundException.class).when(service).addLike(filmId, userId);

        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).addLike(filmId, userId);
    }

    @Test
    void removeLike_shouldResponseWithOk() throws Exception {
        Long filmId = 1L;
        Long userId = 1L;

        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        verify(service, times(1)).removeLike(filmId, userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldResponseWithNotFound_ifFilmDoesNotExist(Long filmId) throws Exception {
        Long userId = 1L;

        doThrow(NotFoundException.class).when(service).removeLike(filmId, userId);

        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).removeLike(filmId, userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        Long filmId = 1L;

        doThrow(NotFoundException.class).when(service).removeLike(filmId, userId);

        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).removeLike(filmId, userId);
    }

    @Test
    void getPopular_shouldReturnEmptyListOfPopularFilms() throws Exception {
        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1)).getPopular(10, null, null);
    }

    @Test
    void getPopular_shouldReturnListOfPopularFilmsByNumberOfLikes() throws Exception {
        int count = 2;
        Integer genreId = null;
        Integer year = null;
        Film film1 = initFilm();
        Film film2 = initFilm();

        List<Film> expected = List.of(film1, film2);
        String json = objectMapper.writeValueAsString(expected);

        when(service.getPopular(count, genreId, year)).thenReturn(expected);

        mockMvc.perform(get("/films/popular?count={count}", count))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getPopular(count, genreId, year);
    }

    private static Stream<Arguments> provideInvalidFilms() {
        return Stream.of(
                Arguments.of(initFilm(film -> film.setName(null))),
                Arguments.of(initFilm(film -> film.setName(""))),
                Arguments.of(initFilm(film -> film.setName("nisi eiusm".repeat(10) + "o"))),
                Arguments.of(initFilm(film -> film.setDescription(null))),
                Arguments.of(initFilm(film -> film.setDescription("long strin".repeat(20) + "g"))),
                Arguments.of(initFilm(film -> film.setReleaseDate(null))),
                Arguments.of(initFilm(film -> film.setReleaseDate(LocalDate.parse("1000-01-01")))),
                Arguments.of(initFilm(film -> film.setDuration(0))),
                Arguments.of(initFilm(film -> film.setDuration(-100))),
                Arguments.of(initFilm(film -> film.setMpa(null)))
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
        film.setMpa(new Mpa());

        return film;
    }
}
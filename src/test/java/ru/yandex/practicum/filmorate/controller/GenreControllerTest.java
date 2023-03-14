package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GenreControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private GenreService service;

    @InjectMocks
    private GenreController controller;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void getGenres_shouldReturnEmptyListOfGenres() throws Exception {
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1)).getGenres();
    }

    @Test
    void getGenres_shouldReturnListOfGenres() throws Exception {
        Genre genre1 = initGenre();
        Genre genre2 = initGenre();

        List<Genre> expected = List.of(genre1, genre2);
        String json = objectMapper.writeValueAsString(expected);

        when(service.getGenres()).thenReturn(expected);

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getGenres();
    }

    @Test
    void getGenreById_shouldReturnGenreById() throws Exception {
        Integer genreId = 1;
        Genre genre = initGenre();
        String json = objectMapper.writeValueAsString(genre);

        when(service.getGenreById(genreId)).thenReturn(genre);

        mockMvc.perform(get("/genres/{id}", genreId))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getGenreById(genreId);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 999})
    void getGenreById_shouldResponseWithNotFound_ifGenreDoesNotExist(Integer genreId) throws Exception {
        when(service.getGenreById(genreId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/genres/{id}", genreId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getGenreById(genreId);
    }

    private Genre initGenre() {
        Genre genre = new Genre();

        genre.setName("Комедия");

        return genre;
    }
}
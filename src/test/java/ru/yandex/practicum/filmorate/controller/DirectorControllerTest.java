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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DirectorControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    @Mock
    private DirectorService service;

    @InjectMocks
    private DirectorController controller;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void getDirectors_shouldReturnEmptyListOfFilms() throws Exception {
        mockMvc.perform(get("/directors"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1)).getDirectors();
    }

    @Test
    void getDirectors_shouldReturnListOfDirectors() throws Exception {
        Director director1 = initDirector(1L);
        Director director2 = initDirector(2L);

        List<Director> expected = List.of(director1, director2);
        String json = objectMapper.writeValueAsString(expected);

        when(service.getDirectors()).thenReturn(expected);

        mockMvc.perform(get("/directors"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getDirectors();
    }

    @Test
    void getDirectorById_shouldReturnDirectorById() throws Exception {
        Long directorId = 1L;
        Director director = initDirector(directorId);
        String json = objectMapper.writeValueAsString(director);

        when(service.getDirectorById(directorId)).thenReturn(director);

        mockMvc.perform(get("/directors/{id}", directorId))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getDirectorById(directorId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getDirectorById_shouldResponseWithNotFound_ifDirectorDoesNotExist(Long directorId) throws Exception {
        when(service.getDirectorById(directorId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/directors/{id}", directorId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getDirectorById(directorId);
    }

    @Test
    void createDirector_shouldResponseWithOk() throws Exception {
        Director director = initDirector(1L);
        String json = objectMapper.writeValueAsString(director);

        when(service.create(director)).thenReturn(director);

        mockMvc.perform(post("/directors").contentType("application/json").content(json))
                .andExpect(status().isCreated());

        verify(service, times(1)).create(director);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDirectors")
    void createDirector_shouldResponseWithBadRequest_ifDirectorIsInvalid(Director director) throws Exception {
        String json = objectMapper.writeValueAsString(director);

        mockMvc.perform(post("/directors").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(director);
    }

    @Test
    void updateDirector_shouldResponseWithOk() throws Exception {
        Director director = initDirector(1);
        String json = objectMapper.writeValueAsString(director);

        when(service.update(director)).thenReturn(director);

        mockMvc.perform(put("/directors").contentType("application/json").content(json))
                .andExpect(status().isOk());

        verify(service, times(1)).update(director);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDirectors")
    void updateDirector_shouldResponseWithBadRequest_ifDirectorIsInvalid(Director director) throws Exception {
        String json = objectMapper.writeValueAsString(director);

        mockMvc.perform(put("/directors").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).update(director);
    }


    @Test
    void deleteDirectorById_shouldReturnDirectorById() throws Exception {
        Long directorId = 1L;
        mockMvc.perform(delete("/directors/{id}", directorId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(directorId);
    }

    private static Director initDirector(long i) {
        Director director = new Director();
        director.setId(i);
        director.setName("Director " + i);
        return director;
    }

    private static Director initDirector(long i, Consumer<Director> consumer) {
        Director director = initDirector(i);
        consumer.accept(director);
        return director;
    }

    private static Stream<Arguments> provideInvalidDirectors() {
        return Stream.of(
                Arguments.of(initDirector(1, d -> d.setName(null))),
                Arguments.of(initDirector(2, film -> film.setName(""))),
                Arguments.of(initDirector(3, film -> film.setName("   ")))
        );
    }
}
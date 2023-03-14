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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MpaControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private MpaService service;

    @InjectMocks
    private MpaController controller;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void getMpaRatings_shouldReturnEmptyListOfRatings() throws Exception {
        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1)).getMpaRatings();
    }

    @Test
    void getMpaRatings_shouldReturnListOfRatings() throws Exception {
        Mpa rating1 = initRating();
        Mpa rating2 = initRating();

        List<Mpa> expected = List.of(rating1, rating2);
        String json = objectMapper.writeValueAsString(expected);

        when(service.getMpaRatings()).thenReturn(expected);

        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getMpaRatings();
    }

    @Test
    void getMpaRatingById_shouldReturnRatingById() throws Exception {
        Integer ratingId = 1;
        Mpa rating = initRating();
        String json = objectMapper.writeValueAsString(rating);

        when(service.getMpaRatingById(ratingId)).thenReturn(rating);

        mockMvc.perform(get("/mpa/{id}", ratingId))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getMpaRatingById(ratingId);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 999})
    void getMpaRatingById_shouldResponseWithNotFound_ifRatingDoesNotExist(Integer ratingId) throws Exception {
        when(service.getMpaRatingById(ratingId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/mpa/{id}", ratingId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getMpaRatingById(ratingId);
    }

    private Mpa initRating() {
        Mpa rating = new Mpa();

        rating.setName("G");

        return rating;
    }
}
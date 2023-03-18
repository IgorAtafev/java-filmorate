package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MpaServiceImplTest {

    @Mock
    private MpaStorage storage;

    @InjectMocks
    private MpaServiceImpl service;

    @Test
    void getMpaRatings_shouldReturnEmptyListOfRatings() {
        when(storage.getMpaRatings()).thenReturn(Collections.emptyList());

        assertTrue(service.getMpaRatings().isEmpty());

        verify(storage, times(1)).getMpaRatings();
    }

    @Test
    void getMpaRatings_shouldReturnListOfRatings() {
        Mpa rating1 = initRating();
        Mpa rating2 = initRating();

        List<Mpa> expected = List.of(rating1, rating2);

        when(storage.getMpaRatings()).thenReturn(expected);

        assertEquals(expected, service.getMpaRatings());

        verify(storage, times(1)).getMpaRatings();
    }

    @Test
    void getMpaRatingById_shouldReturnRatingById() {
        Integer ratingId = 1;
        Mpa rating = initRating();

        when(storage.getMpaRatingById(ratingId)).thenReturn(Optional.of(rating));

        assertEquals(rating, service.getMpaRatingById(ratingId));

        verify(storage, times(1)).getMpaRatingById(ratingId);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 999})
    void getMpaRatingById_shouldThrowAnException_ifRatingDoesNotExist(Integer ratingId) {
        when(storage.getMpaRatingById(ratingId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> service.getMpaRatingById(ratingId)
        );

        verify(storage, times(1)).getMpaRatingById(ratingId);
    }

    private Mpa initRating() {
        Mpa rating = new Mpa();

        rating.setName("G");

        return rating;
    }
}
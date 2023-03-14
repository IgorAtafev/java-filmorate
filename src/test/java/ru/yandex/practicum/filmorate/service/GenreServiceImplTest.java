package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
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
class GenreServiceImplTest {

    @Mock
    private GenreStorage storage;

    @InjectMocks
    private GenreServiceImpl service;

    @Test
    void getGenres_shouldReturnEmptyListOfGenres() {
        when(storage.getGenres()).thenReturn(Collections.emptyList());

        assertTrue(service.getGenres().isEmpty());

        verify(storage, times(1)).getGenres();
    }

    @Test
    void getGenres_shouldReturnListOfGenres() {
        Genre genre1 = initGenre();
        Genre genre2 = initGenre();

        List<Genre> expected = List.of(genre1, genre2);

        when(storage.getGenres()).thenReturn(expected);

        assertEquals(expected, service.getGenres());

        verify(storage, times(1)).getGenres();
    }

    @Test
    void getGenreById_shouldReturnGenreById() {
        Integer genreId = 1;
        Genre genre = initGenre();

        when(storage.getGenreById(genreId)).thenReturn(Optional.of(genre));

        assertEquals(genre, service.getGenreById(genreId));

        verify(storage, times(1)).getGenreById(genreId);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 999})
    void getGenreById_shouldThrowAnException_ifGenreDoesNotExist(Integer genreId) {
        when(storage.getGenreById(genreId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> service.getGenreById(genreId)
        );

        verify(storage, times(1)).getGenreById(genreId);
    }

    private Genre initGenre() {
        Genre genre = new Genre();

        genre.setName("Комедия");

        return genre;
    }
}
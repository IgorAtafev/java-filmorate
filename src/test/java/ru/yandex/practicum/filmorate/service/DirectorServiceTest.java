package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectorServiceTest {
    @Mock
    DirectorStorage storage;
    @InjectMocks
    private DirectorServiceImpl service;


    @Test
    void getDirectors_shouldReturnEmptyListOfDirectors() {
        when(storage.getDirectors()).thenReturn(Collections.emptyList());

        assertTrue(service.getDirectors().isEmpty());

        verify(storage, times(1)).getDirectors();
    }

    @Test
    void getDirectors_shouldReturnListOfDirectors() {
        Director director1 = initDirector(1);
        Director director2 = initDirector(2);

        List<Director> expected = List.of(director1, director2);

        when(storage.getDirectors()).thenReturn(expected);

        assertEquals(expected, service.getDirectors());

        verify(storage, times(1)).getDirectors();
    }

    @Test
    void getDirectorById_shouldReturnDirectorById() {
        Long directorId = 1L;
        Director director = initDirector(1);

        when(storage.getDirectorById(directorId)).thenReturn(director);

        assertEquals(director, service.getDirectorById(directorId));

        verify(storage, times(1)).getDirectorById(directorId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getDirectorById_shouldThrowAnException_ifDirectorDoesNotExist(Long directorId) {
        when(storage.getDirectorById(directorId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> service.getDirectorById(directorId)
        );

        verify(storage, times(1)).getDirectorById(directorId);
    }

    @Test
    void createDirector_shouldCreateDirector() {
        Director director = initDirector(1);

        when(storage.create(director)).thenReturn(director);
        assertEquals(director, service.create(director));

        verify(storage, times(1)).create(director);
    }

    @Test
    void updateDirector_shouldUpdateTheDirector() {
        Long directorId = 1L;
        Director director = initDirector(directorId);
        director.setId(directorId);

        when(storage.directorExists(directorId)).thenReturn(true);
        when(storage.update(director)).thenReturn(director);
        assertEquals(director, service.update(director));

        verify(storage, times(1)).update(director);
    }

    @Test
    void updateDirector_shouldThrowAnException_ifDirectorIdIsEmpty() {
        Director director = initDirector(1);

        assertThrows(
                ValidationException.class,
                () -> service.update(director)
        );

        verify(storage, never()).update(director);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void updateDirector_shouldThrowAnException_ifDirectorDoesNotExist(long directorId) {
        Director director = initDirector(directorId);
        director.setId(directorId);
        when(storage.directorExists(directorId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> service.update(director)
        );

        verify(storage, times(1)).directorExists(directorId);
        verify(storage, never()).update(director);
    }

    @Test
    void deleteDirector_shouldDeleteTheDirector() {
        Long directorId = 1L;

        when(storage.directorExists(directorId)).thenReturn(true);
        when(storage.delete(directorId)).thenReturn(1);
        assertEquals(1, service.delete(directorId));

        verify(storage, times(1)).delete(directorId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void deleteDirector_shouldThrowAnException_ifDirectorDoesNotExist(long directorId) {
        when(storage.directorExists(directorId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> service.delete(directorId)
        );

        verify(storage, times(1)).directorExists(directorId);
        verify(storage, never()).delete(directorId);
    }

    private static Director initDirector(long i) {
        Director director = new Director();
        director.setName("Director " + i);
        return director;
    }
}
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private static final String NOT_EMPTY_ID_ON_UPDATE = "The director must not have an empty ID when updating";
    private static final String DIRECTOR_DOSE_NOT_EXIST = "Director with id %d  does not exist";

    private final DirectorStorage storage;


    @Override
    public Director create(Director obj) {
        return storage.create(obj);
    }

    public Collection<Director> getDirectors() {
        return storage.getDirectors();
    }

    @Override
    public Director getDirectorById(long id) {
        return storage.getDirectorById(id);
    }

    @Override
    public int delete(long id) {
        if (!storage.directorExists(id)) {
            throw new NotFoundException(String.format(DIRECTOR_DOSE_NOT_EXIST, id));
        }
        return storage.delete(id);
    }

    @Override
    public Director update(Director obj) {
        if (isIdValueNull(obj)) {
            throw new ValidationException(NOT_EMPTY_ID_ON_UPDATE);
        }
        if (!storage.directorExists(obj.getId())) {
            throw new NotFoundException(String.format(DIRECTOR_DOSE_NOT_EXIST, obj.getId()));
        }
        return storage.update(obj);
    }

    private boolean isIdValueNull(Director director) {
        return director.getId() == null;
    }
}

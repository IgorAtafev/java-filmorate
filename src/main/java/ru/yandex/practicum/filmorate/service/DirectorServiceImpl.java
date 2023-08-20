package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

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
            throw new NotFoundException(String.format("Director with id %d  does not exist", id));
        }

        return storage.delete(id);
    }

    @Override
    public Director update(Director obj) {
        if (isIdValueNull(obj)) {
            throw new ValidationException("The director must not have an empty ID when updating");
        }

        if (!storage.directorExists(obj.getId())) {
            throw new NotFoundException(String.format("Director with id %d  does not exist", obj.getId()));
        }

        return storage.update(obj);
    }

    private boolean isIdValueNull(Director director) {
        return director.getId() == null;
    }
}

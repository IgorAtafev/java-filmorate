package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreStorage storage;

    @Override
    public List<Genre> getGenres() {
        return storage.getGenres();
    }

    @Override
    public Genre getGenreById(Integer id) {
        return storage.getGenreById(id).orElseThrow(
                () -> new NotFoundException(String.format("Genre width id %d does not exist", id))
        );
    }
}

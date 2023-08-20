package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaStorage storage;

    @Override
    public List<Mpa> getMpaRatings() {
        return storage.getMpaRatings();
    }

    @Override
    public Mpa getMpaRatingById(Integer id) {
        return storage.getMpaRatingById(id).orElseThrow(
                () -> new NotFoundException(String.format("Mpa rating width id %d does not exist", id))
        );
    }
}

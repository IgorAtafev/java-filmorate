package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getMpaRatings() {
        String sqlQuery = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpaRating);
    }

    @Override
    public Optional<Mpa> getMpaRatingById(Integer id) {
        String sqlQuery = "SELECT * FROM mpa WHERE id = ?";

        try {
            Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpaRating, id);
            return Optional.of(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Mpa mapRowToMpaRating(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();

        mpa.setId(resultSet.getInt("id"));
        mpa.setName(resultSet.getString("name"));

        return mpa;
    }
}
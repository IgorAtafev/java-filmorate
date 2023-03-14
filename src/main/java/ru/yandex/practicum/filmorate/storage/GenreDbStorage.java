package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";

        try {
            Genre genre = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
            return Optional.of(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();

        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("name"));

        return genre;
    }
}
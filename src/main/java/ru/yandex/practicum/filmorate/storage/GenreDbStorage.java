package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
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

    @Override
    public List<Genre> getGenresByIds(List<Integer> ids) {
        String sqlQuery = String.format("SELECT * " +
                        "FROM genres " +
                        "WHERE id IN (%s)",
                String.join(", ", Collections.nCopies(ids.size(), "?")));

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, ids.toArray());
    }

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        String sqlQuery = "SELECT g.* " +
                "FROM genres g " +
                "INNER JOIN film_genres fg ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();

        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("name"));

        return genre;
    }
}
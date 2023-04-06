package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectorDBStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id");

        final long directorId = simpleJdbcInsert.executeAndReturnKey(toMap(director)).longValue();
        director.setId(directorId);
        return director;
    }

    @Override
    public Collection<Director> getDirectors() {
        final String sql = "SELECT * FROM director ORDER BY director_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director getDirectorById(long id) {
        final String sql = "SELECT * FROM director WHERE director_id = ? ORDER BY director_id";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeDirector(rs), id);
        } catch (EmptyResultDataAccessException ex) {
            log.error(ex.getMessage());
            throw new NotFoundException(String.format("Director with id=%d not found.", id));
        }
    }

    @Override
    public Director update(Director director) {
        jdbcTemplate.update(
                "UPDATE director SET name = ? WHERE director_id = ?",
                director.getName(), director.getId());
        return director;
    }

    @Override
    public int delete(long id) {
        jdbcTemplate.update("DELETE FROM film_director WHERE director_id = ?", id);
        return jdbcTemplate.update("DELETE FROM director WHERE director_id = ?", id);
    }

    @Override
    public Collection<Director> getDirectorsByFilmId(Long id) {
        final String sql =
                "SELECT d.* " +
                        "FROM director AS d " +
                        "RIGHT JOIN film_director fd ON d.director_id = fd.director_id " +
                        "WHERE film_id = ? ORDER BY director_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), id);
    }

    @Override
    public boolean directorExists(long id) {
        String sql = "SELECT COUNT(1) FROM director WHERE director_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count > 0;
    }

    @Override
    public Map<Long, Set<Director>> getDirectorsByFilmIds(List<Long> filmIds) {
        String sqlQuery = String.format("SELECT fd.film_id, d.* " +
                        "FROM director d " +
                        "INNER JOIN film_director fd ON fd.director_id = d.director_id " +
                        "WHERE fd.film_id IN (%s)",
                String.join(", ", Collections.nCopies(filmIds.size(), "?")));

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, filmIds.toArray());
        Map<Long, Set<Director>> filmsDirectors = new HashMap<>();

        while (rowSet.next()) {
            Long filmId = rowSet.getLong("film_id");

            Director director = new Director();
            director.setId(rowSet.getLong("director_id"));
            director.setName(rowSet.getString("name"));

            if (!filmsDirectors.containsKey(filmId)) {
                Set<Director> directors = new HashSet<>();
                filmsDirectors.put(filmId, directors);
            }

            filmsDirectors.get(filmId).add(director);
        }

        return filmsDirectors;
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }

    private Map<String, Object> toMap(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        return values;
    }
}
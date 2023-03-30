package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.UpdateStorageException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectorDBStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director create(Director director) {
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("director")
                    .usingGeneratedKeyColumns("director_id");

            final long directorId = simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue();
            return getDirectorById(directorId);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UpdateStorageException(String.format("An error occurred while adding a director: {%s}", director));
        }
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
        try {
            jdbcTemplate.update(
                    "UPDATE director SET name = ? WHERE director_id = ?",
                    director.getName(), director.getId());
            return getDirectorById(director.getId());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UpdateStorageException(
                    String.format("An error occurred while updating the director's data: {%s}", director));
        }
    }

    @Override
    public int delete(long id) {
        jdbcTemplate.update("DELETE FROM film_director WHERE director_id = ?", id);
        return jdbcTemplate.update("DELETE FROM director WHERE director_id = ?", id);
    }

    @Override
    public Collection<Director> getDirectorsByFilmId(Long id) {
        final String sql =
                "SELECT d.* \n" +
                        "FROM director AS d \n" +
                        "RIGHT JOIN film_director fd ON d.director_id = fd.director_id \n" +
                        "WHERE film_id = ? ORDER BY director_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), id);
    }

    @Override
    public boolean directorExists(long id) {
        String sql = "SELECT COUNT(*) FROM director WHERE director_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count > 0;
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }

}

package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectorDBStorage implements DirectorStorage {
    protected static final int DATA_COLUMN = 2;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
        return getDirectorById(director.getId());
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
    public void addDirectorsToFilms(List<Film> films) {
        if (films.isEmpty()) return;

        MapSqlParameterSource parameters = new MapSqlParameterSource(
                "ids",
                films.stream()
                        .map(Film::getId)
                        .collect(Collectors.toList()));

        final String sql =
                "SELECT array_agg(DISTINCT d.director_id || ',' || d.name  ORDER BY d.director_id) AS directors_data, " +
                        "fd.film_id " +
                        "FROM director AS d  " +
                        "RIGHT JOIN film_director fd ON d.director_id = fd.director_id " +
                        "WHERE fd.film_id IN (:ids) " +
                        "GROUP BY fd.film_id";

        namedParameterJdbcTemplate.query(sql, parameters, (rs, rowNum) -> addDirectors(rs, films));
    }

    private Object addDirectors(ResultSet rs, List<Film> films) throws SQLException {
        Long filmId = rs.getLong("film_id");
        Film film = films.stream()
                .filter(f -> f.getId() == filmId)
                .findFirst()
                .get();
        ResultSet directorsDataResultSet = rs.getArray("directors_data").getResultSet();
        Set<Director> directors = new HashSet<>();
        while (directorsDataResultSet.next()) {
            String directorData = directorsDataResultSet.getString(DATA_COLUMN);

            if (directorData == null) {
                break;
            }

            String[] data = directorData.split(",");
            Director director = new Director();
            director.setId(Long.parseLong(data[0]));
            director.setName(data[1]);
            directors.add(director);
        }
        film.addDirectors(directors);
        directorsDataResultSet.close();
        return directors;
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

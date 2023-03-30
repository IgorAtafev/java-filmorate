package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final ReviewStorage reviewStorage;

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT f.*, m.name mpa_name " +
                "FROM films f " +
                "INNER JOIN mpa m ON m.id = f.mpa_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String sqlQuery = "SELECT f.*, m.name mpa_name " +
                "FROM films f " +
                "INNER JOIN mpa m ON m.id = f.mpa_id " +
                "WHERE f.id = ?";

        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());

            return ps;
        }, generatedKeyHolder);

        Long id = generatedKeyHolder.getKey().longValue();

        film.setId(id);

        addGenres(film.getId(), film.getGenres());
        addDirectors(film.getId(), film.getDirectors());

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        removeGenres(film.getId());
        addGenres(film.getId(), film.getGenres());
        addDirectors(film.getId(), film.getDirectors());

        return film;
    }

    @Override
    public void addLike(Long id, Long userId) {
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        String sqlQuery = "DELETE FROM film_likes " +
                "WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        String sqlQuery = "SELECT f.*, m.name mpa_name, " +
                "COUNT(fl.user_id) count_of_likes " +
                "FROM films f " +
                "INNER JOIN mpa m ON m.id = f.mpa_id " +
                "LEFT JOIN film_likes fl ON fl.film_id = f.id " +
                "GROUP BY f.id " +
                "ORDER BY count_of_likes DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public boolean filmExists(Long id) {
        String sqlQuery = "SELECT 1 FROM films WHERE id = ?";

        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);

        return row.next();
    }

    @Override
    public boolean filmDirectorExists(Long id) {
        String sqlQuery = "SELECT 1 FROM film_director WHERE film_id = ?";

        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);

        return row.next();
    }

    @Override
    public void removeFilm(Long id) {
        removeLikeFilm(id);
        removeGenreFilm(id);

        if (filmDirectorExists(id)) {
            removeFilmDirector(id);
        }
        if (reviewStorage.reviewFilmExists(id)) {
            List<Long> array = reviewStorage.getReviewIdByFilmId(id);
            array.forEach(reviewStorage::removeReviewById);
        }

        String sqlQuery = "DELETE FROM films " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void removeLikeFilm(Long id) {
        String sqlQuery = "DELETE FROM film_likes " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void removeGenreFilm(Long id) {
        String sqlQuery = "DELETE FROM film_genres " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void removeFilmDirector(long id) {
        String sqlQuery = "DELETE FROM film_director WHERE director_id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Film> getFilmsForDirector(Long directorId, String sortBy) {
        String sqlTemplate = "SELECT DISTINCT f.*, m.name mpa_name\n" +
                "FROM film_director fd \n" +
                "LEFT JOIN films f ON f.id = fd.film_id \n" +
                "INNER JOIN mpa m ON m.id = f.mpa_id \n" +
                "%s";
        String sqlQuery = "";
        if ("year".equals(sortBy)) {
            sqlQuery = String.format(sqlTemplate, "WHERE fd.director_ID = ? \n" +
                    "ORDER BY EXTRACT(YEAR FROM f.release_date)");
        } else {
            sqlQuery = String.format(sqlTemplate, "LEFT JOIN film_likes fl ON fl.film_id =f.id \n" +
                    "WHERE fd.director_ID = ? \n" +
                    "GROUP BY f.id \n" +
                    "ORDER BY COUNT(f.id)");
        }
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getRecommendations(Long id) {
        List<Film> recommendedFilms = new ArrayList<>();

        /* Finding a user with the most intersections by likes with the primary user */
        String sqlQueryForUserId = "SELECT user_id FROM film_likes " +
                "WHERE film_id IN (SELECT film_id FROM film_likes WHERE user_id = ?) AND user_id != ? " +
                "GROUP BY user_id ORDER BY COUNT(film_id) DESC LIMIT 1";

        SqlRowSet rowUser = jdbcTemplate.queryForRowSet(sqlQueryForUserId, id, id);

        if (!rowUser.first()) return recommendedFilms;

        Long userWithIntersections = rowUser.getLong("user_id");

        /* Finding films according to the user with most intersections that primary user haven't liked yet */
        String sqlQueryForFilms = "SELECT f.*, m.name mpa_name FROM film_likes fl " +
                "LEFT JOIN films f ON fl.film_id = f.id INNER JOIN mpa m ON m.id = f.mpa_id " +
                "WHERE user_id = ? AND film_id NOT IN " +
                "(SELECT film_id FROM film_likes WHERE user_id = ?)";

        return jdbcTemplate.query(sqlQueryForFilms, this::mapRowToFilm, userWithIntersections, id);
    }

    private void addGenres(Long filmId, Collection<Genre> filmGenres) {
        if (filmGenres == null || filmGenres.isEmpty()) {
            return;
        }

        List<Integer> genreIds = filmGenres.stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        List<Genre> genres = genreStorage.getGenresByIds(genreIds);

        if (genres.isEmpty()) {
            return;
        }

        String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sqlQuery,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setInt(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    private void addDirectors(Long filmId, Collection<Director> directors) {
        removeDirectorsFromFilm(filmId);
        if (directors == null || directors.isEmpty()) {
            return;
        }

        List<Director> listDirectors = new ArrayList<>(directors);
        jdbcTemplate.batchUpdate("INSERT INTO film_director(film_id, director_id) VALUES(? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, listDirectors.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return listDirectors.size();
                    }
                });
    }

    private void removeDirectorsFromFilm(Long filmId) {
        final String sql = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private void removeGenres(Long filmId) {
        String sqlQuery = "DELETE FROM film_genres " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_id"));
        mpa.setName(resultSet.getString("mpa_name"));
        film.setMpa(mpa);

        film.addGenres(genreStorage.getGenresByFilmId(film.getId()));
        film.addDirectors(directorStorage.getDirectorsByFilmId(film.getId()));

        return film;
    }
}
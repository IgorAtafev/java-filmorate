package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT t1.*, t2.name mpa_name " +
                "FROM films t1 " +
                "INNER JOIN mpa t2 ON t2.id = t1.mpa_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String sqlQuery = "SELECT t1.*, t2.name mpa_name " +
                "FROM films t1 " +
                "INNER JOIN mpa t2 ON t2.id = t1.mpa_id " +
                "WHERE t1.id = ?";

        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO films (id, name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());

        addGenresByFilm(film);

        film = getFilmById(film.getId()).get();

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        removeGenresByFilm(film);
        addGenresByFilm(film);

        film = getFilmById(film.getId()).get();

        return film;
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery,
                film.getId(),
                user.getId());
    }

    @Override
    public void removeLike(Film film, User user) {
        String sqlQuery = "DELETE FROM film_likes " +
                "WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getId(),
                user.getId());
    }

    @Override
    public List<Film> getPopular(int count) {
        String sqlQuery = "SELECT t1.*, t2.name mpa_name, " +
                "COUNT(t3.user_id) count_of_likes " +
                "FROM films t1 " +
                "INNER JOIN mpa t2 ON t2.id = t1.mpa_id " +
                "LEFT JOIN film_likes t3 ON t3.film_id = t1.id " +
                "GROUP BY t1.id " +
                "ORDER BY count_of_likes DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery,
                this::mapRowToFilm,
                count);
    }

    private void addGenresByFilm(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (?, ?)";

        List<Genre> genres = List.copyOf(film.getGenres());

        jdbcTemplate.batchUpdate(sqlQuery,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setInt(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    private void removeGenresByFilm(Film film) {
        String sqlQuery = "DELETE FROM film_genres " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private List<Genre> getGenresByFilm(Film film) {
        String sqlQuery = "SELECT t1.* " +
                "FROM genres t1 " +
                "INNER JOIN film_genres t2 ON t2.genre_id = t1.id " +
                "WHERE t2.film_id = ?";

        return jdbcTemplate.query(sqlQuery,
                this::mapRowToGenre,
                film.getId());
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

        film.addGenres(getGenresByFilm(film));

        return film;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();

        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("name"));

        return genre;
    }
}
package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Review> getReviews(int count) {
        String sqlQuery = "SELECT * " +
                "FROM reviews " +
                "ORDER BY useful DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        String sqlQuery = "SELECT * " +
                "FROM reviews " +
                "WHERE film_id = ? " +
                "ORDER BY useful DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        String sqlQuery = "SELECT * " +
                "FROM reviews " +
                "WHERE id = ?";

        try {
            Review review = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
            return Optional.of(review);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Review createReview(Review review) {
        String sqlQuery = "INSERT INTO reviews (content, is_positive, film_id, user_id) " +
                "VALUES (?, ?, ?, ?)";

        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getFilmId());
            ps.setLong(4, review.getUserId());

            return ps;
        }, generatedKeyHolder);

        review.setReviewId(generatedKeyHolder.getKey().longValue());

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "UPDATE reviews " +
                "SET content = ?, is_positive = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());

        return getReviewById(review.getReviewId()).get();
    }

    @Override
    public void removeReviewById(Long id) {
        String sqlQuery = "DELETE FROM review_likes " +
                "WHERE review_id = ?";

        jdbcTemplate.update(sqlQuery, id);

        sqlQuery = "DELETE FROM reviews " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void removeReviewByFilmId(Long id) {
        String sqlQuery = "DELETE FROM review_likes " +
                "WHERE review_id IN " +
                "(SELECT rl.review_id " +
                "FROM reviews r " +
                "JOIN review_likes rl ON r.ID  = rl.review_id " +
                "WHERE r.film_id = ?)";

        jdbcTemplate.update(sqlQuery, id);

        sqlQuery = "DELETE FROM reviews " +
                "WHERE film_id  = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void removeReviewByUserId(Long id) {
        String sqlQuery = "DELETE FROM review_likes " +
                "WHERE user_id  = ?";

        jdbcTemplate.update(sqlQuery, id);

        sqlQuery = "DELETE FROM reviews " +
                "WHERE user_id  = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        String sqlQuery = "INSERT INTO review_likes (review_id, user_id, is_useful) " +
                "VALUES (?, ?, ?)";

        jdbcTemplate.update(sqlQuery, id, userId, true);

        sqlQuery = "UPDATE reviews " +
                "SET useful = useful + 1 " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        String sqlQuery = "DELETE FROM review_likes " +
                "WHERE review_id = ? AND user_id = ? AND is_useful = ?";

        jdbcTemplate.update(sqlQuery, id, userId, true);

        sqlQuery = "UPDATE reviews " +
                "SET useful = useful - 1" +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void addDislike(Long id, Long userId) {
        String sqlQuery = "INSERT INTO review_likes (review_id, user_id, is_useful) " +
                "VALUES (?, ?, ?)";

        jdbcTemplate.update(sqlQuery, id, userId, false);

        sqlQuery = "UPDATE reviews " +
                "SET useful = useful - 1 " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void removeDislike(Long id, Long userId) {
        String sqlQuery = "DELETE FROM review_likes " +
                "WHERE review_id = ? AND user_id = ? AND is_useful = ?";

        jdbcTemplate.update(sqlQuery, id, userId, false);

        sqlQuery = "UPDATE reviews " +
                "SET useful = useful + 1" +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public boolean reviewExists(Long id) {
        String sqlQuery = "SELECT 1 FROM reviews WHERE id = ?";

        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);

        return row.next();
    }

    @Override
    public List<Long> getReviewIdByUserId(Long id) {
        String sqlQuery = "SELECT id FROM reviews WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapToList, id);
    }

    @Override
    public boolean reviewUserExists(Long id) {
        String sqlQuery = "SELECT 1 FROM reviews WHERE user_id = ?";

        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);

        return row.next();
    }

    @Override
    public boolean likeExists(Long id, Long userId) {
        String sqlQuery = "SELECT 1 FROM review_likes " +
                "WHERE review_id = ? AND user_id = ? AND is_useful = ?";

        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id, userId, true);

        return row.next();
    }

    @Override
    public boolean disLikeExists(Long id, Long userId) {
        String sqlQuery = "SELECT 1 FROM review_likes " +
                "WHERE review_id = ? AND user_id = ? AND is_useful = ?";

        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id, userId, false);

        return row.next();
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();

        review.setReviewId(resultSet.getLong("id"));
        review.setContent(resultSet.getString("content"));
        review.setIsPositive(resultSet.getBoolean("is_positive"));
        review.setFilmId(resultSet.getLong("film_id"));
        review.setUserId(resultSet.getLong("user_id"));
        review.setUseful(resultSet.getInt("useful"));

        return review;
    }
}
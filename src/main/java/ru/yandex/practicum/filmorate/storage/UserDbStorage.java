package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";

        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birth_day) " +
                "VALUES (?, ?, ?, ?)";

        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
                    PreparedStatement ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

                    ps.setString(1, user.getEmail());
                    ps.setString(2, user.getLogin());
                    ps.setString(3, user.getName());
                    ps.setDate(4, Date.valueOf(user.getBirthday()));

                    return ps;
                }, generatedKeyHolder);

        Long id = generatedKeyHolder.getKey().longValue();

        user.setId(id);

        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birth_day = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        String sqlQuery = "INSERT INTO user_friends (user_id, friend_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        String sqlQuery = "DELETE FROM user_friends " +
                "WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        String sqlQuery = "SELECT u.* " +
                "FROM users u " +
                "INNER JOIN user_friends uf ON uf.friend_id = u.id " +
                "WHERE uf.user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        String sqlQuery = "SELECT u.* " +
                "FROM users u " +
                "INNER JOIN user_friends uf ON uf.friend_id = u.id " +
                "INNER JOIN user_friends ufc ON ufc.friend_id = uf.friend_id " +
                "WHERE uf.user_id = ? AND ufc.user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, otherId);
    }

    @Override
    public void removeUser(Long id) {
        removeUserLike(id);
        removeUserFromFriends(id);
        removeReviewByUserId(id);

        String sqlQuery = "DELETE FROM users " +
                "WHERE id = ?";

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
    public boolean userExists(Long id) {
        String sqlQuery = "SELECT 1 FROM users WHERE id = ?";

        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);

        return row.next();
    }

    @Override
    public void removeUserLike(Long id) {
        String sqlQuery = "DELETE FROM film_likes " +
                "WHERE user_id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void removeUserFromFriends(Long id) {
        String sqlQuery = "DELETE FROM user_friends " +
                "WHERE user_id = ? OR friend_id = ?";

        jdbcTemplate.update(sqlQuery, id, id);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();

        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birth_day").toLocalDate());

        return user;
    }
}

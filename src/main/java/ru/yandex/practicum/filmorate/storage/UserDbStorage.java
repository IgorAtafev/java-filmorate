package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Primary
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
        String sqlQuery = "INSERT INTO users (id, email, login, name, birth_day) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());

        return getUserById(user.getId()).get();
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birth_day = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return getUserById(user.getId()).get();
    }

    @Override
    public void addFriend(User user, User friend) {
        String sqlQuery = "INSERT INTO user_friends (user_id, friend_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery,
                user.getId(),
                friend.getId());
    }

    @Override
    public void removeFriend(User user, User friend) {
        String sqlQuery = "DELETE FROM user_friends " +
                "WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(sqlQuery,
                user.getId(),
                friend.getId());
    }

    @Override
    public List<User> getFriends(User user) {
        String sqlQuery = "SELECT t1.* " +
                "FROM users t1 " +
                "INNER JOIN user_friends t2 ON t2.friend_id = t1.id " +
                "WHERE t2.user_id = ?";

        return jdbcTemplate.query(sqlQuery,
                this::mapRowToUser,
                user.getId());
    }

    @Override
    public List<User> getCommonFriends(User user, User other) {
        String sqlQuery = "SELECT t1.* " +
                "FROM users t1 " +
                "INNER JOIN user_friends t2 ON t2.friend_id = t1.id " +
                "INNER JOIN user_friends t3 ON t3.friend_id = t2.friend_id " +
                "WHERE t2.user_id = ? AND t3.user_id = ?";

        return jdbcTemplate.query(sqlQuery,
                this::mapRowToUser,
                user.getId(),
                other.getId());
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
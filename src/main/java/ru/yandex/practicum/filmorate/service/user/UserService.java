package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    /**
     * Returns a list of all users
     * @return list of all users
     */
    List<User> getUsers();

    /**
     * Returns user by id.
     * If user not found throws NotFoundException
     * @param id
     * @return user by id
     */
    User getUserById(Long id);

    /**
     * Creates a new user
     * @param user
     * @return new user
     */
    User createUser(User user);

    /**
     * Updates the user
     * @param user
     * @return updated user
     */
    User updateUser(User user);

    /**
     * Adds the user with id = friendId as a friend to the user with id = userId
     * @param userId
     * @param friendId
     */
    void addFriend(Long userId, Long friendId);
}
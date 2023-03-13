package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    private long nextId = 0;

    @Override
    public List<User> getUsers() {
        return storage.getUsers();
    }

    @Override
    public User getUserById(Long id) {
        return storage.getUserById(id).orElseThrow(
                () -> new NotFoundException(String.format("User width id %d does not exist", id))
        );
    }

    @Override
    public User createUser(User user) {
        if (!isIdValueNull(user)) {
            throw new ValidationException("The user must have an empty ID when created");
        }

        changeNameToLogin(user);

        user.setId(++nextId);
        return storage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        if (isIdValueNull(user)) {
            throw new ValidationException("The user must not have an empty ID when updating");
        }

        /**
         * Checks if a user exists by id
         * If the user is not found throws NotFoundException
         */
        getUserById(user.getId());

        changeNameToLogin(user);
        return storage.updateUser(user);
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        if (Objects.equals(id, friendId)) {
            throw new ValidationException("The user cannot add himself as a friend");
        }

        storage.addFriend(user, friend);
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        storage.removeFriend(user, friend);
    }

    @Override
    public List<User> getFriends(Long id) {
        User user = getUserById(id);

        return storage.getFriends(user);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = getUserById(id);
        User other = getUserById(otherId);

        return storage.getCommonFriends(user, other);
    }

    private void changeNameToLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private boolean isIdValueNull(User user) {
        return user.getId() == null;
    }
}
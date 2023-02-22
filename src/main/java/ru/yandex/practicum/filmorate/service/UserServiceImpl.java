package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

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

        return storage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        if (isIdValueNull(user)) {
            throw new ValidationException("The user must not have an empty ID when updating");
        }

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
        storage.addFriend(friend, user);
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        storage.removeFriend(user, friend);
        storage.removeFriend(friend, user);
    }

    @Override
    public List<User> getFriends(Long id) {
        User user = getUserById(id);
        List<Long> friends = storage.getFriends(user);

        return friends.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = getUserById(id);
        User other = getUserById(otherId);
        List<Long> friends = storage.getFriends(user);
        List<Long> otherFriends = storage.getFriends(other);

        return friends.stream()
                .filter(otherFriends::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
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
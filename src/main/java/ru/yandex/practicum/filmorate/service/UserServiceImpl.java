package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;
    private final FilmService filmService;

    @Override
    public List<User> getUsers() {
        return storage.getUsers();
    }

    @Override
    public User getUserById(Long id) {
        return storage.getUserById(id).orElseThrow(
                () -> new NotFoundException(String.format("User with id %d does not exist", id))
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

        if (!storage.userExists(user.getId())) {
            throw new NotFoundException(String.format("User with id %d does not exist", user.getId()));
        }

        changeNameToLogin(user);
        return storage.updateUser(user);
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        if (!storage.userExists(id)) {
            throw new NotFoundException(String.format("User with id %d does not exist", id));
        }

        if (!storage.userExists(friendId)) {
            throw new NotFoundException(String.format("User with id %d does not exist", friendId));
        }

        if (Objects.equals(id, friendId)) {
            throw new ValidationException("The user cannot add himself as a friend");
        }

        storage.addFriend(id, friendId);

        eventStorage.addEvent(Event.builder()
                .userId(id)
                .entityId(friendId)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        if (!storage.userExists(id)) {
            throw new NotFoundException(String.format("User with id %d does not exist", id));
        }

        if (!storage.userExists(friendId)) {
            throw new NotFoundException(String.format("User with id %d does not exist", friendId));
        }

        storage.removeFriend(id, friendId);

        eventStorage.addEvent(Event.builder()
                .userId(id)
                .entityId(friendId)
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @Override
    public List<User> getFriends(Long id) {
        if (!storage.userExists(id)) {
            throw new NotFoundException(String.format("User with id %d does not exist", id));
        }

        return storage.getFriends(id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        if (!storage.userExists(id)) {
            throw new NotFoundException(String.format("User with id %d does not exist", id));
        }

        if (!storage.userExists(otherId)) {
            throw new NotFoundException(String.format("User with id %d does not exist", otherId));
        }

        return storage.getCommonFriends(id, otherId);
    }

    @Override
    public void removeUser(Long id) {
        if (!storage.userExists(id)) {
            throw new NotFoundException(String.format("User with id %d does not exist", id));
        }

        storage.removeUser(id);
    }

    @Override
    public List<Film> getRecommendations(Long id) {
        if (!storage.userExists(id)) {
            throw new NotFoundException(String.format("User with id %d does not exist", id));
        }

        List<Film> films = filmStorage.getRecommendations(id);
        filmService.addGenresToFilms(films);
        filmService.addDirectorsToFilms(films);

        return films;
    }

    @Override
    public List<Event> getUserEvents(Long id) {
        if (!storage.userExists(id)) {
            throw new NotFoundException(String.format("User with id %d does not exist", id));
        }

        return eventStorage.getUserEvents(id);
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

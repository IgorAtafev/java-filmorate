package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private int nextId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        if (!isIdValueNullOrZero(user)) {
            throw new ValidationException("The user must have an empty ID when created");
        }

        changeNameToLogin(user);
        user.setId(++nextId);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        if (isIdValueNullOrZero(user)) {
            throw new ValidationException("The user must not have an empty ID when updating");
        }

        if (!users.containsKey(user.getId())) {
            throw new ValidationException("This user does not exist");
        }

        changeNameToLogin(user);
        users.put(user.getId(), user);

        return user;
    }

    private void changeNameToLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private boolean isIdValueNullOrZero(User user) {
        return user.getId() == null || user.getId() == 0;
    }
}
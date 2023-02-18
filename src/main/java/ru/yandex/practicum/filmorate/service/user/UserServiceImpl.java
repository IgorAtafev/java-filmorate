package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Override
    public List<User> getUsers() {
        return storage.getUsers();
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

        if (storage.getUserById(user.getId()).isEmpty()) {
            throw new NotFoundException("This user does not exist");
        }

        changeNameToLogin(user);
        return storage.updateUser(user);
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
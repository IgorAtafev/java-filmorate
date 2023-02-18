package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private final UserStorage storage = new InMemoryUserStorage();
    private final UserService service = new UserServiceImpl(storage);

    @Test
    void getUsers_shouldCheckForNull() {
        assertNotNull(service.getUsers());
    }

    @Test
    void getUsers_shouldReturnEmptyListOfUsers() {
        assertTrue(service.getUsers().isEmpty());
    }

    @Test
    void getUsers_shouldReturnListOfUsers() {
        User user1 = initUser();
        service.createUser(user1);
        User user2 = initUser();
        service.createUser(user2);

        List<User> expected = List.of(user1, user2);
        List<User> actual = service.getUsers();

        assertEquals(expected, actual);
    }

    @Test
    void createUser_shouldCreateAUser() {
        User user = initUser();
        service.createUser(user);

        List<User> expected = List.of(user);
        List<User> actual = service.getUsers();

        assertEquals(expected, actual);
    }

    @Test
    void createUser_shouldChangeNameToLogin_ifNameIsNull() {
        User user = initUser();
        user.setName(null);
        user = service.createUser(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void createUser_shouldChangeNameToLogin_ifNameIsNotNullAndEmpty() {
        User user = initUser();
        user.setName("");
        user = service.createUser(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void createUser_shouldThrowAnException_ifTheUserIdIsNotEmpty() {
        User user = initUser();
        user.setId(1L);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createUser(user)
        );
        assertEquals("The user must have an empty ID when created", exception.getMessage());
    }

    @Test
    void updateUser_shouldUpdateTheUser() {
        User user = initUser();
        service.createUser(user);
        user.setId(1L);
        user.setEmail("mail@yandex.ru");
        user.setLogin("doloreUpdate");
        user.setName("est adipisicing");
        user.setBirthday(LocalDate.of(1976, 9, 20));

        service.updateUser(user);

        List<User> expected = List.of(user);
        List<User> actual = service.getUsers();

        assertEquals(expected, actual);
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNull() {
        User user = initUser();
        service.createUser(user);
        user.setName(null);

        user = service.updateUser(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNotNullAndEmpty() {
        User user = initUser();
        service.createUser(user);
        user.setName("");

        user = service.updateUser(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void updateUser_shouldThrowAnException_ifTheUserIdIsEmpty() {
        User user = initUser();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.updateUser(user)
        );
        assertEquals("The user must not have an empty ID when updating", exception.getMessage());
    }

    @Test
    void updateUser_shouldThrowAnException_ifTheUserDoesNotExist() {
        User user1 = initUser();
        service.createUser(user1);
        User user2 = initUser();
        service.createUser(user2);

        User user3 = new User();
        user3.setId(999L);
        user3.setEmail("mail3@mail.ru");
        user3.setLogin("dolore3");
        user3.setName("Nick Name3");
        user3.setBirthday(LocalDate.of(1996, 8, 20));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.updateUser(user3)
        );
        assertEquals("This user does not exist", exception.getMessage());
    }

    private User initUser() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946, 8, 20));
        return user;
    }
}
package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.validator.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private User user1;
    private User user2;

    private final UserService service = new UserServiceImpl();

    @BeforeEach
    void setUp() {
        initUsers();
    }

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
        service.createUser(user1);
        service.createUser(user2);

        List<User> expected = List.of(user1, user2);
        List<User> actual = service.getUsers();

        assertEquals(expected, actual);
    }

    @Test
    void createUser_shouldCreateAUser() {
        service.createUser(user1);

        List<User> expected = List.of(user1);
        List<User> actual = service.getUsers();

        assertEquals(expected, actual);
    }

    @Test
    void createUser_shouldChangeNameToLogin_ifNameIsNull() {
        user1.setName(null);
        user1 = service.createUser(user1);
        assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    void createUser_shouldChangeNameToLogin_ifNameIsNotNullAndEmpty() {
        user1.setName("");
        user1 = service.createUser(user1);
        assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    void createUser_shouldThrowAnException_ifTheUserIdIsNotEmpty() {
        user1.setId(1);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createUser(user1)
        );
        assertEquals("The user must have an empty ID when created", exception.getMessage());
    }

    @Test
    void updateUser_shouldUpdateTheUser() {
        service.createUser(user1);
        user1 = new User(1, "mail@yandex.ru", "doloreUpdate", "est adipisicing",
                LocalDate.of(1976, 9, 20));

        service.updateUser(user1);

        List<User> expected = List.of(user1);
        List<User> actual = service.getUsers();

        assertEquals(expected, actual);
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNull() {
        service.createUser(user1);
        user1.setName(null);

        user1 = service.updateUser(user1);
        assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNotNullAndEmpty() {
        service.createUser(user1);
        user1.setName("");

        user1 = service.updateUser(user1);
        assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    void updateUser_shouldThrowAnException_ifTheUserIdIsEmpty() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.updateUser(user1)
        );
        assertEquals("The user must not have an empty ID when updating", exception.getMessage());
    }

    @Test
    void updateUser_shouldThrowAnException_ifTheUserDoesNotExist() {
        service.createUser(user1);
        service.createUser(user2);

        User user3 = new User(999, "mail3@mail.ru", "dolore3", "Nick Name3",
                LocalDate.of(1996, 8, 20));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.updateUser(user3)
        );
        assertEquals("This user does not exist", exception.getMessage());
    }

    private void initUsers() {
        user1 = new User(0, "mail@mail.ru", "dolore", "Nick Name",
                LocalDate.of(1946, 8, 20));
        user2 = new User(0, "mail2@mail.ru", "dolore2", "Nick Name2",
                LocalDate.of(1986, 1, 2));
    }
}
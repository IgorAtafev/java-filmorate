package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private User user1;

    private final UserService service = new UserServiceImpl();

    @BeforeEach
    void setUp() {
        user1 = initUser();
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
        User user2 = initUser();
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
        user1.setId(1);
        user1.setEmail("mail@yandex.ru");
        user1.setLogin("doloreUpdate");
        user1.setName("est adipisicing");
        user1.setBirthday(LocalDate.of(1976, 9, 20));

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
        User user2 = initUser();
        service.createUser(user2);

        User user3 = new User();
        user3.setId(999);
        user3.setEmail("mail3@mail.ru");
        user3.setLogin("dolore3");
        user3.setName("Nick Name3");
        user3.setBirthday(LocalDate.of(1996, 8, 20));

        ValidationException exception = assertThrows(
                ValidationException.class,
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
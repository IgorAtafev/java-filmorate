package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserServiceImpl(userStorage);

    @Test
    void getUsers_shouldCheckForNull() {
        assertNotNull(userService.getUsers());
    }

    @Test
    void getUsers_shouldReturnEmptyListOfUsers() {
        assertTrue(userService.getUsers().isEmpty());
    }

    @Test
    void getUsers_shouldReturnListOfUsers() {
        User user1 = initUser();
        userService.createUser(user1);
        User user2 = initUser();
        userService.createUser(user2);

        List<User> expected = List.of(user1, user2);
        List<User> actual = userService.getUsers();

        assertEquals(expected, actual);
    }

    @Test
    void getUserById_shouldReturnUserById() {
        User user1 = initUser();
        userService.createUser(user1);

        User user2 = userService.getUserById(user1.getId());
        assertEquals(user1, user2);
    }

    @Test
    void getUserById_shouldThrowAnException_ifUserDoesNotExist() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(0L)
        );
        assertEquals("User width id 0 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(-1L)
        );
        assertEquals("User width id -1 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());
    }

    @Test
    void createUser_shouldCreateAUser() {
        User user = initUser();
        userService.createUser(user);

        List<User> expected = List.of(user);
        List<User> actual = userService.getUsers();

        assertEquals(expected, actual);
    }

    @Test
    void createUser_shouldChangeNameToLogin_ifNameIsNull() {
        User user = initUser();
        user.setName(null);
        user = userService.createUser(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void createUser_shouldChangeNameToLogin_ifNameIsNotNullAndEmpty() {
        User user = initUser();
        user.setName("");
        user = userService.createUser(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void createUser_shouldThrowAnException_ifUserIdIsNotEmpty() {
        User user = initUser();
        user.setId(1L);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.createUser(user)
        );
        assertEquals("The user must have an empty ID when created", exception.getMessage());
    }

    @Test
    void updateUser_shouldUpdateTheUser() {
        User user = initUser();
        userService.createUser(user);
        user.setId(1L);
        user.setEmail("mail@yandex.ru");
        user.setLogin("doloreUpdate");
        user.setName("est adipisicing");
        user.setBirthday(LocalDate.of(1976, 9, 20));

        userService.updateUser(user);

        List<User> expected = List.of(user);
        List<User> actual = userService.getUsers();

        assertEquals(expected, actual);
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNull() {
        User user = initUser();
        userService.createUser(user);
        user.setName(null);

        user = userService.updateUser(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNotNullAndEmpty() {
        User user = initUser();
        userService.createUser(user);
        user.setName("");

        user = userService.updateUser(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void updateUser_shouldThrowAnException_ifUserIdIsEmpty() {
        User user = initUser();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUser(user)
        );
        assertEquals("The user must not have an empty ID when updating", exception.getMessage());
    }

    @Test
    void updateUser_shouldThrowAnException_ifUserDoesNotExist() {
        User user1 = initUser();
        userService.createUser(user1);
        User user2 = initUser();
        userService.createUser(user2);

        User user3 = new User();
        user3.setId(999L);
        user3.setEmail("mail3@mail.ru");
        user3.setLogin("dolore3");
        user3.setName("Nick Name3");
        user3.setBirthday(LocalDate.of(1996, 8, 20));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(user3)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());
    }

    @Test
    void addFriend_shouldAddTheUserAsAFriend() {
        User user1 = initUser();
        userService.createUser(user1);
        User user2 = initUser();
        userService.createUser(user2);

        userService.addFriend(user1.getId(), user2.getId());

        List<Long> expected = List.of(user2.getId());
        List<Long> actual = user1.getFriends();

        assertEquals(expected, actual);

        expected = List.of(user1.getId());
        actual = user2.getFriends();

        assertEquals(expected, actual);
    }

    @Test
    void addFriend_shouldThrowAnException_ifUserDoesNotExist() {
        User user = initUser();
        userService.createUser(user);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.addFriend(user.getId(), 999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> userService.addFriend(-1L, user.getId())
        );
        assertEquals("User width id -1 does not exist", exception.getMessage());
    }

    @Test
    void addFriend_shouldThrowAnException_ifUserAddsHimselfAsAFriend() {
        User user = initUser();
        userService.createUser(user);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.addFriend(user.getId(), user.getId())
        );
        assertEquals("The user cannot add himself as a friend", exception.getMessage());
    }

    @Test
    void removeFriend_shouldRemoveTheUserFromFriends() {
        User user1 = initUser();
        userService.createUser(user1);
        User user2 = initUser();
        userService.createUser(user2);
        User user3 = initUser();
        userService.createUser(user3);

        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());

        userService.removeFriend(user1.getId(), user2.getId());

        List<Long> expected = List.of(user3.getId());
        List<Long> actual = user1.getFriends();

        assertEquals(expected, actual);

        assertTrue(user2.getFriends().isEmpty());
    }

    @Test
    void removeFriend_shouldThrowAnException_ifUserDoesNotExist() {
        User user = initUser();
        userService.createUser(user);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.removeFriend(user.getId(), 999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> userService.addFriend(-1L, user.getId())
        );
        assertEquals("User width id -1 does not exist", exception.getMessage());
    }

    @Test
    void getFriends_shouldReturnEmptyListOfFriendsOfUser() {
        User user = initUser();
        userService.createUser(user);
        assertTrue(userService.getFriends(user.getId()).isEmpty());
    }

    @Test
    void getFriends_shouldReturnListOfFriendsOfUser() {
        User user1 = initUser();
        userService.createUser(user1);
        User user2 = initUser();
        userService.createUser(user2);

        userService.addFriend(user1.getId(), user2.getId());

        List<User> expected = List.of(user2);
        List<User> actual = userService.getFriends(user1.getId());

        assertEquals(expected, actual);

        expected = List.of(user1);
        actual = userService.getFriends(user2.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getFriends_shouldThrowAnException_ifUserDoesNotExist() {
        User user = initUser();
        userService.createUser(user);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getFriends(999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());
    }

    @Test
    void getCommonFriends_shouldReturnEmptyListOfCommonFriendsOfUsers() {
        User user1 = initUser();
        userService.createUser(user1);
        User user2 = initUser();
        userService.createUser(user2);

        assertTrue(userService.getCommonFriends(user1.getId(), user2.getId()).isEmpty());
    }

    @Test
    void getCommonFriends_shouldReturnListOfCommonFriendsOfUsers() {
        User user1 = initUser();
        userService.createUser(user1);
        User user2 = initUser();
        userService.createUser(user2);
        User user3 = initUser();
        userService.createUser(user3);

        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());
        userService.addFriend(user2.getId(), user3.getId());

        List<User> expected = List.of(user3);
        List<User> actual = userService.getCommonFriends(user1.getId(), user2.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getCommonFriends_shouldThrowAnException_ifUserDoesNotExist() {
        User user = initUser();
        userService.createUser(user);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getCommonFriends(user.getId(), 999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> userService.getCommonFriends(-1L, user.getId())
        );
        assertEquals("User width id -1 does not exist", exception.getMessage());
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
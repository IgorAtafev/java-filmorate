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
    void getUserById_shouldReturnUserById() {
        User user1 = initUser();
        service.createUser(user1);

        User user2 = service.getUserById(user1.getId());
        assertEquals(user1, user2);
    }

    @Test
    void getUserById_shouldThrowAnException_ifUserDoesNotExist() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.getUserById(0L)
        );
        assertEquals("User width id 0 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> service.getUserById(-1L)
        );
        assertEquals("User width id -1 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> service.getUserById(999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());
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
        assertEquals("User width id 999 does not exist", exception.getMessage());
    }

    @Test
    void addFriend_shouldAddTheUserAsAFriend() {
        User user1 = initUser();
        service.createUser(user1);
        User user2 = initUser();
        service.createUser(user2);

        service.addFriend(user1.getId(), user2.getId());

        List<User> expected = List.of(user2);
        List<User> actual = service.getFriends(user1.getId());

        assertEquals(expected, actual);

        expected = List.of(user1);
        actual = service.getFriends(user2.getId());

        assertEquals(expected, actual);
    }

    @Test
    void addFriend_shouldThrowAnException_ifTheUserDoesNotExist() {
        User user = initUser();
        service.createUser(user);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.addFriend(user.getId(), 999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> service.addFriend(-1L, user.getId())
        );
        assertEquals("User width id -1 does not exist", exception.getMessage());
    }

    @Test
    void addFriend_shouldThrowAnException_IfTheUserAddsHimselfAsAFriend() {
        User user = initUser();
        service.createUser(user);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.addFriend(user.getId(), user.getId())
        );
        assertEquals("The user cannot add himself as a friend", exception.getMessage());
    }

    @Test
    void removeFriend_shouldRemoveTheUserFromFriends() {
        User user1 = initUser();
        service.createUser(user1);
        User user2 = initUser();
        service.createUser(user2);
        service.addFriend(user1.getId(), user2.getId());

        service.removeFriend(user1.getId(), user2.getId());

        assertTrue(service.getFriends(user1.getId()).isEmpty());
        assertTrue(service.getFriends(user2.getId()).isEmpty());
    }

    @Test
    void removeFriend_shouldThrowAnException_ifTheUserDoesNotExist() {
        User user = initUser();
        service.createUser(user);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.removeFriend(user.getId(), 999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> service.addFriend(-1L, user.getId())
        );
        assertEquals("User width id -1 does not exist", exception.getMessage());
    }

    @Test
    void getFriends_shouldReturnEmptyListOfFriendsOfUser() {
        User user = initUser();
        service.createUser(user);
        assertTrue(service.getFriends(user.getId()).isEmpty());
    }

    @Test
    void getFriends_shouldReturnListOfFriendsOfUser() {
        User user1 = initUser();
        service.createUser(user1);
        User user2 = initUser();
        service.createUser(user2);

        service.addFriend(user1.getId(), user2.getId());

        List<User> expected = List.of(user2);
        List<User> actual = service.getFriends(user1.getId());

        assertEquals(expected, actual);

        expected = List.of(user1);
        actual = service.getFriends(user2.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getFriends_shouldThrowAnException_ifTheUserDoesNotExist() {
        User user = initUser();
        service.createUser(user);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.getFriends(999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());
    }

    @Test
    void getCommonFriends_shouldReturnEmptyListOfCommonFriendsOfUsers() {
        User user1 = initUser();
        service.createUser(user1);
        User user2 = initUser();
        service.createUser(user2);

        assertTrue(service.getCommonFriends(user1.getId(), user2.getId()).isEmpty());
    }

    @Test
    void getCommonFriends_shouldReturnListOfCommonFriendsOfUsers() {
        User user1 = initUser();
        service.createUser(user1);
        User user2 = initUser();
        service.createUser(user2);
        User user3 = initUser();
        service.createUser(user3);

        service.addFriend(user1.getId(), user2.getId());
        service.addFriend(user1.getId(), user3.getId());
        service.addFriend(user2.getId(), user3.getId());

        List<User> expected = List.of(user3);
        List<User> actual = service.getCommonFriends(user1.getId(), user2.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getCommonFriends_shouldThrowAnException_ifTheUserDoesNotExist() {
        User user = initUser();
        service.createUser(user);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.getCommonFriends(user.getId(), 999L)
        );
        assertEquals("User width id 999 does not exist", exception.getMessage());

        exception = assertThrows(
                NotFoundException.class,
                () -> service.getCommonFriends(-1L, user.getId())
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
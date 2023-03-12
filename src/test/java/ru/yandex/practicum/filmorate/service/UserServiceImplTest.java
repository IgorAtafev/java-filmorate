package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUsers_shouldReturnEmptyListOfUsers() {
        when(userStorage.getUsers()).thenReturn(Collections.emptyList());

        assertTrue(userService.getUsers().isEmpty());
    }

    @Test
    void getUsers_shouldReturnListOfUsers() {
        User user1 = initUser();
        User user2 = initUser();

        List<User> expected = List.of(user1, user2);

        when(userStorage.getUsers()).thenReturn(expected);

        assertEquals(expected, userService.getUsers());
    }

    @Test
    void getUserById_shouldReturnUserById() {
        Long userId = 1L;
        User user = initUser();

        when(userStorage.getUserById(userId)).thenReturn(Optional.of(user));

        assertEquals(user, userService.getUserById(userId));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getUserById_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        when(userStorage.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(userId)
        );
    }

    @Test
    void createUser_shouldCreateAUser() {
        User user = initUser();

        when(userStorage.createUser(user)).thenReturn(user);

        assertEquals(user, userService.createUser(user));
    }

    @Test
    void createUser_shouldChangeNameToLogin_ifNameIsNull() {
        User user = initUser();
        user.setName(null);

        when(userStorage.createUser(user)).thenReturn(user);

        user = userService.createUser(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void createUser_shouldChangeNameToLogin_ifNameIsNotNullAndEmpty() {
        User user = initUser();
        user.setName("");

        when(userStorage.createUser(user)).thenReturn(user);

        user = userService.createUser(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void createUser_shouldThrowAnException_ifUserIdIsNotEmpty() {
        Long userId = 1L;
        User user = initUser();
        user.setId(userId);

        assertThrows(
                ValidationException.class,
                () -> userService.createUser(user)
        );

        verify(userStorage, never()).createUser(user);
    }

    @Test
    void updateUser_shouldUpdateTheUser() {
        Long userId = 1L;
        User user = initUser();
        user.setId(userId);

        when(userStorage.getUserById(userId)).thenReturn(Optional.of(user));
        when(userStorage.updateUser(user)).thenReturn(user);

        assertEquals(user, userService.updateUser(user));
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNull() {
        Long userId = 1L;
        User user = initUser();
        user.setId(userId);
        user.setName(null);

        when(userStorage.getUserById(userId)).thenReturn(Optional.of(user));
        when(userStorage.updateUser(user)).thenReturn(user);

        user = userService.updateUser(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNotNullAndEmpty() {
        Long userId = 1L;
        User user = initUser();
        user.setId(userId);
        user.setName("");

        when(userStorage.getUserById(userId)).thenReturn(Optional.of(user));
        when(userStorage.updateUser(user)).thenReturn(user);

        user = userService.updateUser(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void updateUser_shouldThrowAnException_ifUserIdIsEmpty() {
        User user = initUser();

        assertThrows(
                ValidationException.class,
                () -> userService.updateUser(user)
        );

        verify(userStorage, never()).updateUser(user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void updateUser_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        User user = initUser();
        user.setId(userId);

        when(userStorage.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(user)
        );

        verify(userStorage, never()).updateUser(user);
    }

    @Test
    void addFriend_shouldAddTheUserAsAFriend() {
        Long userId = 1L;
        Long friendId = 2L;
        User user = initUser();
        user.setId(userId);
        User friend = initUser();
        friend.setId(friendId);

        when(userStorage.getUserById(userId)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(friendId)).thenReturn(Optional.of(friend));
        doNothing().when(userStorage).addFriend(user, friend);
        doNothing().when(userStorage).addFriend(friend, user);

        userService.addFriend(userId, friendId);

        verify(userStorage, times(1)).addFriend(user, friend);
        verify(userStorage, times(1)).addFriend(friend, user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addFriend_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long friendId = 2L;
        User user = initUser();
        User friend = initUser();

        when(userStorage.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> userService.addFriend(userId, friendId)
        );

        verify(userStorage, never()).addFriend(user, friend);
    }

    @Test
    void addFriend_shouldThrowAnException_ifUserAddsHimselfAsAFriend() {
        Long userId = 1L;
        Long friendId = 1L;
        User user = initUser();
        User friend = initUser();

        when(userStorage.getUserById(userId)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(friendId)).thenReturn(Optional.of(friend));

        assertThrows(
                ValidationException.class,
                () -> userService.addFriend(userId, friendId)
        );

        verify(userStorage, never()).addFriend(user, friend);
    }

    @Test
    void removeFriend_shouldRemoveTheUserFromFriends() {
        Long userId = 1L;
        Long friendId = 2L;
        User user = initUser();
        user.setId(userId);
        User friend = initUser();
        friend.setId(friendId);

        when(userStorage.getUserById(userId)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(friendId)).thenReturn(Optional.of(friend));
        doNothing().when(userStorage).removeFriend(user, friend);
        doNothing().when(userStorage).removeFriend(friend, user);

        userService.removeFriend(userId, friendId);

        verify(userStorage, times(1)).removeFriend(user, friend);
        verify(userStorage, times(1)).removeFriend(friend, user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeFriend_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long friendId = 2L;
        User user = initUser();
        User friend = initUser();

        when(userStorage.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> userService.removeFriend(userId, friendId)
        );

        verify(userStorage, never()).removeFriend(user, friend);
    }

    @Test
    void getFriends_shouldReturnEmptyListOfFriendsOfUser() {
        Long userId = 1L;
        User user = initUser();

        when(userStorage.getUserById(userId)).thenReturn(Optional.of(user));
        when(userStorage.getFriends(user)).thenReturn(Collections.emptyList());

        assertTrue(userService.getFriends(userId).isEmpty());
    }

    @Test
    void getFriends_shouldReturnListOfFriendsOfUser() {
        Long userId = 1L;
        Long friendId1 = 2L;
        Long friendId2 = 3L;
        User user = initUser();
        User friend1 = initUser();
        User friend2 = initUser();

        List<User> expected = List.of(friend1, friend2);

        when(userStorage.getUserById(userId)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(friendId1)).thenReturn(Optional.of(friend1));
        when(userStorage.getUserById(friendId2)).thenReturn(Optional.of(friend2));
        when(userStorage.getFriends(user)).thenReturn(List.of(friendId1, friendId2));

        assertEquals(expected, userService.getFriends(userId));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getFriends_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        User user = initUser();

        when(userStorage.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> userService.getFriends(userId)
        );

        verify(userStorage, never()).getFriends(user);
    }

    @Test
    void getCommonFriends_shouldReturnEmptyListOfCommonFriendsOfUsers() {
        Long userId1 = 1L;
        User user1 = initUser();
        user1.setId(userId1);
        Long userId2 = 2L;
        User user2 = initUser();
        user2.setId(userId2);
        Long userId3 = 3L;
        User user3 = initUser();
        user3.setId(userId3);

        when(userStorage.getUserById(userId1)).thenReturn(Optional.of(user1));
        when(userStorage.getUserById(userId2)).thenReturn(Optional.of(user2));

        when(userStorage.getFriends(user1)).thenReturn(Collections.emptyList());
        when(userStorage.getFriends(user2)).thenReturn(Collections.emptyList());

        assertTrue(userService.getCommonFriends(userId1, userId2).isEmpty());

        when(userStorage.getFriends(user1)).thenReturn(List.of(userId2));

        assertTrue(userService.getCommonFriends(userId1, userId2).isEmpty());

        when(userStorage.getFriends(user2)).thenReturn(List.of(userId3));

        assertTrue(userService.getCommonFriends(userId1, userId2).isEmpty());
    }

    @Test
    void getCommonFriends_shouldReturnListOfCommonFriendsOfUsers() {
        Long userId1 = 1L;
        User user1 = initUser();
        user1.setId(userId1);
        Long userId2 = 2L;
        User user2 = initUser();
        user2.setId(userId2);
        Long userId3 = 3L;
        User user3 = initUser();
        user3.setId(userId3);

        List<User> expected = List.of(user3);

        when(userStorage.getUserById(userId1)).thenReturn(Optional.of(user1));
        when(userStorage.getUserById(userId2)).thenReturn(Optional.of(user2));
        when(userStorage.getUserById(userId3)).thenReturn(Optional.of(user3));
        when(userStorage.getFriends(user1)).thenReturn(List.of(userId3));
        when(userStorage.getFriends(user2)).thenReturn(List.of(userId3));

        assertEquals(expected, userService.getCommonFriends(userId1, userId2));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getCommonFriends_shouldThrowAnException_ifUserDoesNotExist(Long userId1) {
        Long userId2 = 2L;
        User user = initUser();

        when(userStorage.getUserById(userId1)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> userService.getCommonFriends(userId1, userId2)
        );

        verify(userStorage, never()).getFriends(user);
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
package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.controller.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.model.User;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserStorage storage;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getUsers_shouldReturnEmptyListOfUsers() {
        when(storage.getUsers()).thenReturn(Collections.emptyList());

        assertTrue(service.getUsers().isEmpty());

        verify(storage, times(1)).getUsers();
    }

    @Test
    void getUsers_shouldReturnListOfUsers() {
        User user1 = initUser();
        User user2 = initUser();

        List<User> expected = List.of(user1, user2);

        when(storage.getUsers()).thenReturn(expected);

        assertEquals(expected, service.getUsers());

        verify(storage, times(1)).getUsers();
    }

    @Test
    void getUserById_shouldReturnUserById() {
        Long userId = 1L;
        User user = initUser();

        when(storage.getUserById(userId)).thenReturn(Optional.of(user));

        assertEquals(user, service.getUserById(userId));

        verify(storage, times(1)).getUserById(userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getUserById_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        when(storage.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> service.getUserById(userId)
        );

        verify(storage, times(1)).getUserById(userId);
    }

    @Test
    void createUser_shouldCreateAUser() {
        User user = initUser();

        when(storage.createUser(user)).thenReturn(user);

        assertEquals(user, service.createUser(user));

        verify(storage, times(1)).createUser(user);
    }

    @Test
    void createUser_shouldChangeNameToLogin_ifNameIsNull() {
        User user = initUser();
        user.setName(null);

        when(storage.createUser(user)).thenReturn(user);

        user = service.createUser(user);

        assertEquals(user.getName(), user.getLogin());

        verify(storage, times(1)).createUser(user);
    }

    @Test
    void createUser_shouldChangeNameToLogin_ifNameIsNotNullAndEmpty() {
        User user = initUser();
        user.setName("");

        when(storage.createUser(user)).thenReturn(user);

        user = service.createUser(user);

        assertEquals(user.getName(), user.getLogin());

        verify(storage, times(1)).createUser(user);
    }

    @Test
    void createUser_shouldThrowAnException_ifUserIdIsNotEmpty() {
        Long userId = 1L;
        User user = initUser();
        user.setId(userId);

        assertThrows(
                ValidationException.class,
                () -> service.createUser(user)
        );

        verify(storage, never()).createUser(user);
    }

    @Test
    void updateUser_shouldUpdateTheUser() {
        Long userId = 1L;
        User user = initUser();
        user.setId(userId);

        when(storage.getUserById(userId)).thenReturn(Optional.of(user));
        when(storage.updateUser(user)).thenReturn(user);

        assertEquals(user, service.updateUser(user));

        verify(storage, times(1)).getUserById(userId);
        verify(storage, times(1)).updateUser(user);
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNull() {
        Long userId = 1L;
        User user = initUser();
        user.setId(userId);
        user.setName(null);

        when(storage.getUserById(userId)).thenReturn(Optional.of(user));
        when(storage.updateUser(user)).thenReturn(user);

        user = service.updateUser(user);

        assertEquals(user.getName(), user.getLogin());

        verify(storage, times(1)).getUserById(userId);
        verify(storage, times(1)).updateUser(user);
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNotNullAndEmpty() {
        Long userId = 1L;
        User user = initUser();
        user.setId(userId);
        user.setName("");

        when(storage.getUserById(userId)).thenReturn(Optional.of(user));
        when(storage.updateUser(user)).thenReturn(user);

        user = service.updateUser(user);

        assertEquals(user.getName(), user.getLogin());

        verify(storage, times(1)).getUserById(userId);
        verify(storage, times(1)).updateUser(user);
    }

    @Test
    void updateUser_shouldThrowAnException_ifUserIdIsEmpty() {
        User user = initUser();

        assertThrows(
                ValidationException.class,
                () -> service.updateUser(user)
        );

        verify(storage, never()).updateUser(user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void updateUser_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        User user = initUser();
        user.setId(userId);

        when(storage.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> service.updateUser(user)
        );

        verify(storage, times(1)).getUserById(userId);
        verify(storage, never()).updateUser(user);
    }

    @Test
    void addFriend_shouldAddTheUserAsAFriend() {
        Long userId = 1L;
        Long friendId = 2L;
        User user = initUser();
        user.setId(userId);
        User friend = initUser();
        friend.setId(friendId);

        when(storage.getUserById(userId)).thenReturn(Optional.of(user));
        when(storage.getUserById(friendId)).thenReturn(Optional.of(friend));

        service.addFriend(userId, friendId);

        verify(storage, times(1)).getUserById(userId);
        verify(storage, times(1)).getUserById(friendId);
        verify(storage, times(1)).addFriend(user, friend);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addFriend_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long friendId = 2L;
        User user = initUser();
        User friend = initUser();

        when(storage.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> service.addFriend(userId, friendId)
        );

        verify(storage, times(1)).getUserById(userId);
        verify(storage, never()).getUserById(friendId);
        verify(storage, never()).addFriend(user, friend);
    }

    @Test
    void addFriend_shouldThrowAnException_ifUserAddsHimselfAsAFriend() {
        Long userId = 1L;
        Long friendId = 1L;
        User user = initUser();
        User friend = initUser();

        when(storage.getUserById(userId)).thenReturn(Optional.of(user));
        when(storage.getUserById(friendId)).thenReturn(Optional.of(friend));

        assertThrows(
                ValidationException.class,
                () -> service.addFriend(userId, friendId)
        );

        verify(storage, times(2)).getUserById(userId);
        verify(storage, never()).addFriend(user, friend);
    }

    @Test
    void removeFriend_shouldRemoveTheUserFromFriends() {
        Long userId = 1L;
        Long friendId = 2L;
        User user = initUser();
        user.setId(userId);
        User friend = initUser();
        friend.setId(friendId);

        when(storage.getUserById(userId)).thenReturn(Optional.of(user));
        when(storage.getUserById(friendId)).thenReturn(Optional.of(friend));

        service.removeFriend(userId, friendId);

        verify(storage, times(1)).getUserById(userId);
        verify(storage, times(1)).getUserById(friendId);
        verify(storage, times(1)).removeFriend(user, friend);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeFriend_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long friendId = 2L;
        User user = initUser();
        User friend = initUser();

        when(storage.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> service.removeFriend(userId, friendId)
        );

        verify(storage, times(1)).getUserById(userId);
        verify(storage, never()).getUserById(friendId);
        verify(storage, never()).removeFriend(user, friend);
    }

    @Test
    void getFriends_shouldReturnEmptyListOfFriendsOfUser() {
        Long userId = 1L;
        User user = initUser();

        when(storage.getUserById(userId)).thenReturn(Optional.of(user));
        when(storage.getFriends(user)).thenReturn(Collections.emptyList());

        assertTrue(service.getFriends(userId).isEmpty());

        verify(storage, times(1)).getUserById(userId);
        verify(storage, times(1)).getFriends(user);
    }

    @Test
    void getFriends_shouldReturnListOfFriendsOfUser() {
        Long userId = 1L;
        User user = initUser();
        User friend1 = initUser();
        User friend2 = initUser();

        List<User> expected = List.of(friend1, friend2);

        when(storage.getUserById(userId)).thenReturn(Optional.of(user));
        when(storage.getFriends(user)).thenReturn(List.of(friend1, friend2));

        assertEquals(expected, service.getFriends(userId));

        verify(storage, times(1)).getUserById(userId);
        verify(storage, times(1)).getFriends(user);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getFriends_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        User user = initUser();

        when(storage.getUserById(userId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> service.getFriends(userId)
        );

        verify(storage, times(1)).getUserById(userId);
        verify(storage, never()).getFriends(user);
    }

    @Test
    void getCommonFriends_shouldReturnEmptyListOfCommonFriendsOfUsers() {
        Long userId1 = 1L;
        User user1 = initUser();
        Long userId2 = 2L;
        User user2 = initUser();

        when(storage.getUserById(userId1)).thenReturn(Optional.of(user1));
        when(storage.getUserById(userId2)).thenReturn(Optional.of(user2));
        when(storage.getCommonFriends(user1, user2)).thenReturn(Collections.emptyList());

        assertTrue(service.getCommonFriends(userId1, userId2).isEmpty());

        verify(storage, times(1)).getUserById(userId1);
        verify(storage, times(1)).getUserById(userId2);
        verify(storage, times(1)).getCommonFriends(user1, user2);
    }

    @Test
    void getCommonFriends_shouldReturnListOfCommonFriendsOfUsers() {
        Long userId1 = 1L;
        User user1 = initUser();
        Long userId2 = 2L;
        User user2 = initUser();
        User user3 = initUser();

        List<User> expected = List.of(user3);

        when(storage.getUserById(userId1)).thenReturn(Optional.of(user1));
        when(storage.getUserById(userId2)).thenReturn(Optional.of(user2));
        when(storage.getCommonFriends(user1, user2)).thenReturn(List.of(user3));

        assertEquals(expected, service.getCommonFriends(userId1, userId2));

        verify(storage, times(1)).getUserById(userId1);
        verify(storage, times(1)).getUserById(userId2);
        verify(storage, times(1)).getCommonFriends(user1, user2);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getCommonFriends_shouldThrowAnException_ifUserDoesNotExist(Long userId1) {
        Long userId2 = 2L;
        User user1 = initUser();
        User user2 = initUser();

        when(storage.getUserById(userId1)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> service.getCommonFriends(userId1, userId2)
        );

        verify(storage, times(1)).getUserById(userId1);
        verify(storage, never()).getUserById(userId2);
        verify(storage, never()).getCommonFriends(user1, user2);
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
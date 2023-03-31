package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
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

    @Mock
    private EventStorage eventStorage;

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

        when(storage.userExists(userId)).thenReturn(true);
        when(storage.updateUser(user)).thenReturn(user);

        assertEquals(user, service.updateUser(user));

        verify(storage, times(1)).userExists(userId);
        verify(storage, times(1)).updateUser(user);
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNull() {
        Long userId = 1L;
        User user = initUser();
        user.setId(userId);
        user.setName(null);

        when(storage.userExists(userId)).thenReturn(true);
        when(storage.updateUser(user)).thenReturn(user);

        user = service.updateUser(user);

        assertEquals(user.getName(), user.getLogin());

        verify(storage, times(1)).userExists(userId);
        verify(storage, times(1)).updateUser(user);
    }

    @Test
    void updateUser_shouldChangeNameToLogin_ifNameIsNotNullAndEmpty() {
        Long userId = 1L;
        User user = initUser();
        user.setId(userId);
        user.setName("");

        when(storage.userExists(userId)).thenReturn(true);
        when(storage.updateUser(user)).thenReturn(user);

        user = service.updateUser(user);

        assertEquals(user.getName(), user.getLogin());

        verify(storage, times(1)).userExists(userId);
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

        when(storage.userExists(userId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> service.updateUser(user)
        );

        verify(storage, times(1)).userExists(userId);
        verify(storage, never()).updateUser(user);
    }

    @Test
    void addFriend_shouldAddTheUserAsAFriend() {
        Long userId = 1L;
        Long friendId = 2L;

        when(storage.userExists(userId)).thenReturn(true);
        when(storage.userExists(friendId)).thenReturn(true);

        service.addFriend(userId, friendId);

        verify(storage, times(1)).userExists(userId);
        verify(storage, times(1)).userExists(friendId);
        verify(storage, times(1)).addFriend(userId, friendId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addFriend_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long friendId = 2L;

        when(storage.userExists(userId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> service.addFriend(userId, friendId)
        );

        verify(storage, times(1)).userExists(userId);
        verify(storage, never()).userExists(friendId);
        verify(storage, never()).addFriend(userId, friendId);
    }

    @Test
    void addFriend_shouldThrowAnException_ifUserAddsHimselfAsAFriend() {
        Long userId = 1L;
        Long friendId = 1L;

        when(storage.userExists(userId)).thenReturn(true);
        when(storage.userExists(userId)).thenReturn(true);

        assertThrows(
                ValidationException.class,
                () -> service.addFriend(userId, friendId)
        );

        verify(storage, times(2)).userExists(userId);
        verify(storage, never()).addFriend(userId, friendId);
    }

    @Test
    void removeFriend_shouldRemoveTheUserFromFriends() {
        Long userId = 1L;
        Long friendId = 2L;

        when(storage.userExists(userId)).thenReturn(true);
        when(storage.userExists(friendId)).thenReturn(true);

        service.removeFriend(userId, friendId);

        verify(storage, times(1)).userExists(userId);
        verify(storage, times(1)).userExists(friendId);
        verify(storage, times(1)).removeFriend(userId, friendId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeFriend_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long friendId = 2L;

        when(storage.userExists(userId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> service.removeFriend(userId, friendId)
        );

        verify(storage, times(1)).userExists(userId);
        verify(storage, never()).userExists(friendId);
        verify(storage, never()).removeFriend(userId, friendId);
    }

    @Test
    void getFriends_shouldReturnEmptyListOfFriendsOfUser() {
        Long userId = 1L;

        when(storage.userExists(userId)).thenReturn(true);
        when(storage.getFriends(userId)).thenReturn(Collections.emptyList());

        assertTrue(service.getFriends(userId).isEmpty());

        verify(storage, times(1)).userExists(userId);
        verify(storage, times(1)).getFriends(userId);
    }

    @Test
    void getFriends_shouldReturnListOfFriendsOfUser() {
        Long userId = 1L;
        User friend1 = initUser();
        User friend2 = initUser();

        List<User> expected = List.of(friend1, friend2);

        when(storage.userExists(userId)).thenReturn(true);
        when(storage.getFriends(userId)).thenReturn(List.of(friend1, friend2));

        assertEquals(expected, service.getFriends(userId));

        verify(storage, times(1)).userExists(userId);
        verify(storage, times(1)).getFriends(userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getFriends_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        when(storage.userExists(userId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> service.getFriends(userId)
        );

        verify(storage, times(1)).userExists(userId);
        verify(storage, never()).getFriends(userId);
    }

    @Test
    void getCommonFriends_shouldReturnEmptyListOfCommonFriendsOfUsers() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        when(storage.userExists(userId1)).thenReturn(true);
        when(storage.userExists(userId2)).thenReturn(true);
        when(storage.getCommonFriends(userId1, userId2)).thenReturn(Collections.emptyList());

        assertTrue(service.getCommonFriends(userId1, userId2).isEmpty());

        verify(storage, times(1)).userExists(userId1);
        verify(storage, times(1)).userExists(userId2);
        verify(storage, times(1)).getCommonFriends(userId1, userId2);
    }

    @Test
    void getCommonFriends_shouldReturnListOfCommonFriendsOfUsers() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        User user3 = initUser();

        List<User> expected = List.of(user3);

        when(storage.userExists(userId1)).thenReturn(true);
        when(storage.userExists(userId2)).thenReturn(true);
        when(storage.getCommonFriends(userId1, userId2)).thenReturn(List.of(user3));

        assertEquals(expected, service.getCommonFriends(userId1, userId2));

        verify(storage, times(1)).userExists(userId1);
        verify(storage, times(1)).userExists(userId2);
        verify(storage, times(1)).getCommonFriends(userId1, userId2);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getCommonFriends_shouldThrowAnException_ifUserDoesNotExist(Long userId1) {
        Long userId2 = 2L;

        when(storage.userExists(userId1)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> service.getCommonFriends(userId1, userId2)
        );

        verify(storage, times(1)).userExists(userId1);
        verify(storage, never()).userExists(userId2);
        verify(storage, never()).getCommonFriends(userId1, userId2);
    }

    @Test
    void removeUser_shouldRemoveTheUser() {
        Long userId = 1L;

        when(storage.userExists(userId)).thenReturn(true);

        service.removeUser(userId);

        verify(storage, times(1)).userExists(userId);
        verify(storage, times(1)).removeUser(userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeUser_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        when(storage.userExists(userId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> service.removeUser(userId)
        );

        verify(storage, times(1)).userExists(userId);
        verify(storage, never()).removeUser(userId);
    }

    @Test
    void getUserEvent_shouldThrowNotFoundException_ifUserDoesNotExists() {
        Long userId = 9999L;
        assertThrows(NotFoundException.class,
                () -> service.getUserEvents(userId));
        verify(storage, times(1)).userExists(userId);
    }

    @Test
    void getUserEvents_shouldReturnListOfEvents() {
        List<Event> events = initEvents();
        long userId = 2L;

        when(eventStorage.getUserEvents(userId)).thenReturn(events);
        when(storage.userExists(userId)).thenReturn(true);

        assertEquals(service.getUserEvents(userId), events);

        verify(storage, times(1)).userExists(userId);
        verify(eventStorage, times(1)).getUserEvents(userId);
    }


    private User initUser() {
        User user = new User();

        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946, 8, 20));

        return user;
    }

    private static List<Event> initEvents() {
        Event event1 = Event.builder()
                .timestamp(System.currentTimeMillis())
                .eventType("LIKE")
                .operation("ADD")
                .eventId(3L)
                .entityId(2L)
                .build();

        Event event2 = Event.builder()
                .timestamp(System.currentTimeMillis())
                .eventType("FRIEND")
                .operation("REMOVE")
                .eventId(4L)
                .entityId(1L)
                .build();
        return List.of(event1, event2);
    }

}
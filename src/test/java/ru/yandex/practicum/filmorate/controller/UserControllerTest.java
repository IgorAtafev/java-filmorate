package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operations;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void getUsers_shouldReturnEmptyListOfUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1)).getUsers();
    }

    @Test
    void getUsers_shouldReturnListOfUsers() throws Exception {
        User user1 = initUser();
        User user2 = initUser();

        List<User> expected = List.of(user1, user2);
        String json = objectMapper.writeValueAsString(expected);

        when(service.getUsers()).thenReturn(expected);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getUsers();
    }

    @Test
    void getUserById_shouldReturnUserById() throws Exception {
        Long userId = 1L;
        User user = initUser();
        String json = objectMapper.writeValueAsString(user);

        when(service.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getUserById(userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getUserById_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        when(service.getUserById(userId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getUserById(userId);
    }

    @Test
    void createUser_shouldResponseWithOk() throws Exception {
        User user = initUser();
        String json = objectMapper.writeValueAsString(user);

        when(service.createUser(user)).thenReturn(user);

        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isOk());

        verify(service, times(1)).createUser(user);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUsers")
    void createUser_shouldResponseWithBadRequest_ifUserIsInvalid(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).createUser(user);
    }

    @Test
    void updateUser_shouldResponseWithOk() throws Exception {
        User user = initUser();
        String json = objectMapper.writeValueAsString(user);

        when(service.updateUser(user)).thenReturn(user);

        mockMvc.perform(put("/users").contentType("application/json").content(json))
                .andExpect(status().isOk());

        verify(service, times(1)).updateUser(user);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUsers")
    void updateUser_shouldResponseWithBadRequest_ifUserIsInvalid(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(put("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).updateUser(user);
    }

    @Test
    void addFriend_shouldResponseWithOk() throws Exception {
        Long userId = 1L;
        Long friendId = 2L;

        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isOk());

        verify(service, times(1)).addFriend(userId, friendId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addFriend_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        Long friendId = 1L;

        doThrow(NotFoundException.class).when(service).addFriend(userId, friendId);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).addFriend(userId, friendId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addFriend_shouldResponseWithNotFound_ifFriendDoesNotExist(Long friendId) throws Exception {
        Long userId = 1L;

        doThrow(NotFoundException.class).when(service).addFriend(userId, friendId);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).addFriend(userId, friendId);
    }

    @Test
    void addFriend_shouldResponseWithBadRequest_ifUserAddsHimselfAsAFriend() throws Exception {
        Long userId = 1L;
        Long friendId = 1L;

        doThrow(ValidationException.class).when(service).addFriend(userId, friendId);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).addFriend(userId, friendId);
    }

    @Test
    void removeFriend_shouldResponseWithOk() throws Exception {
        Long userId = 1L;
        Long friendId = 2L;

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isOk());

        verify(service, times(1)).removeFriend(userId, friendId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeFriend_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        Long friendId = 1L;

        doThrow(NotFoundException.class).when(service).removeFriend(userId, friendId);

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).removeFriend(userId, friendId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeFriend_shouldResponseWithNotFound_ifFriendDoesNotExist(Long friendId) throws Exception {
        Long userId = 1L;

        doThrow(NotFoundException.class).when(service).removeFriend(userId, friendId);

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).removeFriend(userId, friendId);
    }

    @Test
    void getFriends_shouldReturnEmptyListOfFriendsOfUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/users/{id}/friends", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1)).getFriends(userId);
    }

    @Test
    void getFriends_shouldReturnListOfFriendsOfUser() throws Exception {
        Long userId = 1L;
        User user1 = initUser();
        User user2 = initUser();

        List<User> expected = List.of(user1, user2);
        String json = objectMapper.writeValueAsString(expected);

        when(service.getFriends(userId)).thenReturn(expected);

        mockMvc.perform(get("/users/{id}/friends", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getFriends(userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getFriends_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        when(service.getFriends(userId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}/friends", userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getFriends(userId);
    }

    @Test
    void getCommonFriends_shouldReturnEmptyListOfCommonFriendsOfUsers() throws Exception {
        Long userId = 1L;
        Long userOtherId = 2L;

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", 1, 2))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1)).getCommonFriends(userId, userOtherId);
    }

    @Test
    void getCommonFriends_shouldReturnListOfCommonFriendsOfUsers() throws Exception {
        Long userId = 1L;
        Long userOtherId = 2L;
        User user1 = initUser();
        User user2 = initUser();

        List<User> expected = List.of(user1, user2);
        String json = objectMapper.writeValueAsString(expected);

        when(service.getCommonFriends(userId, userOtherId)).thenReturn(expected);

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", userId, userOtherId))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getCommonFriends(userId, userOtherId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getCommonFriends_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        Long userOtherId = 1L;

        when(service.getCommonFriends(userId, userOtherId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", userId, userOtherId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getCommonFriends(userId, userOtherId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getCommonFriends_shouldResponseWithNotFound_ifUserOtherDoesNotExist(Long userOtherId) throws Exception {
        Long userId = 1L;

        when(service.getCommonFriends(userId, userOtherId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", userId, userOtherId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getCommonFriends(userId, userOtherId);
    }

    @Test
    void getUserFeed_shouldReturnListOfEvents() throws Exception {
        List<Event> events = initEvents();
        String json = objectMapper.writeValueAsString(events);
        long userId = 2L;

        when(service.getUserEvents(userId)).thenReturn(events);

        mockMvc.perform(get("/users/{id}/feed", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getUserEvents(2L);
    }

    @Test
    void getUserFeed_shouldResponseWithNotFound_ifUserDoesNotExist() throws Exception {
        Long userId = 9999L;

        when(service.getUserEvents(userId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}/feed", userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getUserEvents(userId);
    }

    private static Stream<Arguments> provideInvalidUsers() {
        return Stream.of(
                Arguments.of(initUser(user -> user.setEmail(null))),
                Arguments.of(initUser(user -> user.setEmail(""))),
                Arguments.of(initUser(user -> user.setEmail("mail.ru"))),
                Arguments.of(initUser(user -> user.setLogin(null))),
                Arguments.of(initUser(user -> user.setLogin(""))),
                Arguments.of(initUser(user -> user.setLogin("logi"))),
                Arguments.of(initUser(user -> user.setLogin("login".repeat(10) + "n"))),
                Arguments.of(initUser(user -> user.setLogin("dolore ullamco"))),
                Arguments.of(initUser(user -> user.setName("dolor".repeat(10) + "e"))),
                Arguments.of(initUser(user -> user.setBirthday(null))),
                Arguments.of(initUser(user -> user.setBirthday(LocalDate.parse("2200-01-01"))))
        );
    }

    private static User initUser(Consumer<User> consumer) {
        User user = initUser();

        consumer.accept(user);

        return user;
    }

    private static User initUser() {
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
                .eventType(EventType.LIKE)
                .operation(Operations.ADD)
                .eventId(3L)
                .entityId(2L)
                .build();

        Event event2 = Event.builder()
                .timestamp(System.currentTimeMillis())
                .eventType(EventType.FRIEND)
                .operation(Operations.REMOVE)
                .eventId(4L)
                .entityId(1L)
                .build();
        return List.of(event1, event2);
    }


}
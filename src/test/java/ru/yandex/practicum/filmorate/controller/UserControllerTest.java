package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;
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
    }

    @Test
    void getUserById_shouldReturnUserById() throws Exception {
        Long userId = 1L;
        User user = initUser();
        String json = objectMapper.writeValueAsString(user);

        when(service.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentUser")
    void getUserById_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        when(service.getUserById(userId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_shouldResponseWithOk() throws Exception {
        User user = initUser();
        String json = objectMapper.writeValueAsString(user);

        when(service.createUser(user)).thenReturn(user);

        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isOk());
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

        doNothing().when(service).addFriend(userId, friendId);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isOk());

        verify(service, times(1)).addFriend(userId, friendId);
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentUser")
    void addFriend_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId, Long friendId) throws Exception {
        doThrow(NotFoundException.class).when(service).addFriend(userId, friendId);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addFriend_shouldResponseWithBadRequest_ifUserAddsHimselfAsAFriend() throws Exception {
        Long userId = 1L;
        Long friendId = 1L;

        doThrow(ValidationException.class).when(service).addFriend(userId, friendId);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeFriend_shouldResponseWithOk() throws Exception {
        Long userId = 1L;
        Long friendId = 2L;

        doNothing().when(service).removeFriend(userId, friendId);

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isOk());

        verify(service, times(1)).removeFriend(userId, friendId);
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentUser")
    void removeFriend_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId, Long friendId) throws Exception {
        doThrow(NotFoundException.class).when(service).removeFriend(userId, friendId);

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFriends_shouldReturnEmptyListOfFriendsOfUser() throws Exception {
        mockMvc.perform(get("/users/{id}/friends", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
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
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentUser")
    void getFriends_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        when(service.getFriends(userId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}/friends", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommonFriends_shouldReturnEmptyListOfCommonFriendsOfUsers() throws Exception {
        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", 1, 2))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
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
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentUser")
    void getCommonFriends_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId, Long userOtherId) throws Exception {
        when(service.getCommonFriends(userId, userOtherId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", userId, userOtherId))
                .andExpect(status().isNotFound());
    }

    private static Stream<Arguments> provideInvalidUsers() {
        return Stream.of(
                Arguments.of(initUser(user -> user.setEmail(null))),
                Arguments.of(initUser(user -> user.setEmail(""))),
                Arguments.of(initUser(user -> user.setEmail("mail.ru"))),
                Arguments.of(initUser(user -> user.setLogin(null))),
                Arguments.of(initUser(user -> user.setLogin(""))),
                Arguments.of(initUser(user -> user.setLogin("logi"))),
                Arguments.of(initUser(user -> user.setLogin("logi".repeat(5) + "i"))),
                Arguments.of(initUser(user -> user.setLogin("dolore ullamco"))),
                Arguments.of(initUser(user -> user.setName("dolore".repeat(5) + "d"))),
                Arguments.of(initUser(user -> user.setBirthday(null))),
                Arguments.of(initUser(user -> user.setBirthday(LocalDate.parse("2200-01-01"))))
        );
    }

    private static Stream<Arguments> provideNonExistentUser() {
        return Stream.of(
                Arguments.of(-1L, -1L),
                Arguments.of(0L, 0L),
                Arguments.of(999L, 999L)
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
}
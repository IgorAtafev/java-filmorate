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
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
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
        when(service.getUsers()).thenReturn(expected);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void getUserById_shouldReturnUserById() throws Exception {
        User user = initUser();
        when(service.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentUser")
    void getUserById_shouldResponseWithNotFound_ifUserDoesNotExist(Long id) throws Exception {
        when(service.getUserById(id)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_shouldResponseWithOk() throws Exception {
        User user = initUser();
        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users").contentType("application/json").content(json))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUsers")
    void createUser_shouldResponseWithBadRequest_ifUserIsInvalid(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFriend_shouldResponseWithOk() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", 1, 2))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentUser")
    void addFriend_shouldResponseWithNotFound_ifUserDoesNotExist(Long id, Long friendId) throws Exception {
        doThrow(NotFoundException.class).when(service).addFriend(id, friendId);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", id, friendId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addFriend_shouldResponseWithBadRequest_IfTheUserAddsHimselfAsAFriend() throws Exception {
        doThrow(ValidationException.class).when(service).addFriend(1L, 1L);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", 1, 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeFriend_shouldResponseWithOk() throws Exception {
        mockMvc.perform(delete("/users/{id}/friends/{friendId}", 1, 2))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentUser")
    void removeFriend_shouldResponseWithNotFound_ifUserDoesNotExist(Long id, Long friendId) throws Exception {
        doThrow(NotFoundException.class).when(service).removeFriend(id, friendId);

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", id, friendId))
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
        User user1 = initUser();
        User user2 = initUser();
        List<User> expected = List.of(user1, user2);
        when(service.getFriends(1L)).thenReturn(expected);

        mockMvc.perform(get("/users/{id}/friends", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentUser")
    void getFriends_shouldResponseWithNotFound_ifUserDoesNotExist(Long id) throws Exception {
        when(service.getFriends(id)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}/friends", id))
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
        User user1 = initUser();
        User user2 = initUser();
        List<User> expected = List.of(user1, user2);
        when(service.getCommonFriends(1L, 2L)).thenReturn(expected);

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", 1, 2))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @ParameterizedTest
    @MethodSource("provideNonExistentUser")
    void getCommonFriends_shouldResponseWithNotFound_ifUserDoesNotExist(Long id, Long otherId) throws Exception {
        when(service.getCommonFriends(id, otherId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", id, otherId))
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
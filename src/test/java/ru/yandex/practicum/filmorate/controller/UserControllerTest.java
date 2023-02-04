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

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
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
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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

    private static Stream<Arguments> provideInvalidUsers() {
        return Stream.of(
                Arguments.of(initUser(user -> user.setEmail(""))),
                Arguments.of(initUser(user -> user.setEmail("mail.ru"))),
                Arguments.of(initUser(user -> user.setLogin(""))),
                Arguments.of(initUser(user -> user.setLogin("dolore ullamco"))),
                Arguments.of(initUser(user -> user.setBirthday(LocalDate.parse("2200-01-01"))))
        );
    }

    private static User initUser(Consumer<User> consumer) {
        User user = new User();
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
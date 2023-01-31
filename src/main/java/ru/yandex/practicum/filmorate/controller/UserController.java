package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    UserService service = new UserService();

    @GetMapping
    public List<User> getUsers() {
        return service.getUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Request received POST /users: '{}'", user);
        return service.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Request received PUT /users: '{}'", user);
        return service.updateUser(user);
    }
}
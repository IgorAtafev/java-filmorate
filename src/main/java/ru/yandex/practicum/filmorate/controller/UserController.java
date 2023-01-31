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
    public User create(@RequestBody User user) {
        log.info("Получен запрос POST /users: '{}'", user);
        return service.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Получен запрос PUT /users: '{}'", user);
        return service.update(user);
    }
}
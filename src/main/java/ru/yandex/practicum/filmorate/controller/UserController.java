package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<User> getUsers() {
        return service.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        log.info("Request received POST /users: '{}'", user);
        return service.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        log.info("Request received PUT /users: '{}'", user);
        return service.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Request received PUT /users{}/friends/{}", id, friendId);
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Request received DELETE /users{}/friends/{}", id, friendId);
        service.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return service.getCommonFriends(id, otherId);
    }
}
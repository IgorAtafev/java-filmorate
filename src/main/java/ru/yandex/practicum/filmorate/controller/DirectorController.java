package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService service;

    @GetMapping
    public Collection<Director> getAll() {
        log.info("Request received GET '/directors'");
        return service.getDirectors();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@Valid @RequestBody Director obj) {
        log.info("Request received POST '/directors' :");
        log.info("add : {}", obj);
        return service.create(obj);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director obj) {
        log.info("Request received PUT '/directors' :");
        log.info("new data : {}", obj);
        return service.update(obj);
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable(name = "id") long id) {
        log.info("Request received GET '/directors/{}' :", id);
        return service.getDirectorById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "id") long id) {
        log.info("Request received DELETE '/directors/{}' :", id);
        service.delete(id);
    }
}

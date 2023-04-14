package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @GetMapping
    public List<Review> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count
    ) {
        return service.getReviews(filmId, count);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return service.getReviewById(id);
    }

    @PostMapping
    public Review createReview(@RequestBody @Valid Review review) {
        log.info("Request received POST /reviews: '{}'", review);
        return service.createReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        log.info("Request received PUT /reviews: '{}'", review);
        return service.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void removeReviewById(@PathVariable Long id) {
        log.info("Request received DELETE /reviews/{}", id);
        service.removeReviewById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Request received PUT /reviews/{}/like/{}", id, userId);
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Request received DELETE /reviews/{}/like/{}", id, userId);
        service.removeLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDisLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Request received PUT /reviews/{}/dislike/{}", id, userId);
        service.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDisLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Request received DELETE /reviews/{}/dislike/{}", id, userId);
        service.removeDislike(id, userId);
    }
}
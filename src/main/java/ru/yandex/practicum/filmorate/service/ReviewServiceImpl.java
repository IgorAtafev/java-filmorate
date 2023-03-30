package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final String REVIEW_DOES_NOT_EXIST = "Review width id %d does not exist";
    private static final String FILM_DOES_NOT_EXIST = "Film width id %d does not exist";
    private static final String USER_DOES_NOT_EXIST = "User width id %d does not exist";
    private static final String EMPTY_ID_ON_CREATION = "The review must have an empty ID when created";
    private static final String NOT_EMPTY_ID_ON_UPDATE = "The review must not have an empty ID when updating";

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public List<Review> getReviews(Long filmId, int count) {
        if (filmId == null) {
            return reviewStorage.getReviews(count);
        }

        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewStorage.getReviewById(id).orElseThrow(
                () -> new NotFoundException(String.format(REVIEW_DOES_NOT_EXIST, id))
        );
    }

    @Override
    public Review createReview(Review review) {
        if (!isIdValueNull(review)) {
            throw new ValidationException(EMPTY_ID_ON_CREATION);
        }

        if (!filmStorage.filmExists(review.getFilmId())) {
            throw new NotFoundException(String.format(FILM_DOES_NOT_EXIST, review.getFilmId()));
        }

        if (!userStorage.userExists(review.getUserId())) {
            throw new NotFoundException(String.format(USER_DOES_NOT_EXIST, review.getUserId()));
        }

        Review newReview = reviewStorage.createReview(review);
        userStorage.addEvent(Event.builder()
                .userId(newReview.getUserId())
                .entityId(newReview.getReviewId())
                .eventType("REVIEW")
                .operation("ADD")
                .timestamp(System.currentTimeMillis())
                .build());
        return newReview;
    }

    @Override
    public Review updateReview(Review review) {
        if (isIdValueNull(review)) {
            throw new ValidationException(NOT_EMPTY_ID_ON_UPDATE);
        }

        if (!reviewStorage.reviewExists(review.getReviewId())) {
            throw new NotFoundException(String.format(REVIEW_DOES_NOT_EXIST, review.getReviewId()));
        }

        Review newReview = reviewStorage.updateReview(review);
        userStorage.addEvent(Event.builder()
                .userId(newReview.getUserId())
                .entityId(newReview.getFilmId())
                .eventType("REVIEW")
                .operation("UPDATE")
                .timestamp(System.currentTimeMillis())
                .build());
        return newReview;
    }

    @Override
    public void removeReviewById(Long id) {
        if (!reviewStorage.reviewExists(id)) {
            throw new NotFoundException(String.format(REVIEW_DOES_NOT_EXIST, id));
        }

        Review review = getReviewById(id);
        userStorage.addEvent(Event.builder()
                .userId(review.getUserId())
                .entityId(review.getFilmId())
                .eventType("REVIEW")
                .operation("REMOVE")
                .timestamp(System.currentTimeMillis())
                .build());

        reviewStorage.removeReviewById(id);
    }

    @Override
    public void addLike(Long id, Long userId, boolean isUseful) {
        if (!reviewStorage.reviewExists(id)) {
            throw new NotFoundException(String.format(REVIEW_DOES_NOT_EXIST, id));
        }

        if (!userStorage.userExists(userId)) {
            throw new NotFoundException(String.format(USER_DOES_NOT_EXIST, userId));
        }

        reviewStorage.addLike(id, userId, isUseful);
    }

    @Override
    public void removeLike(Long id, Long userId, boolean isUseful) {
        if (!reviewStorage.reviewExists(id)) {
            throw new NotFoundException(String.format(REVIEW_DOES_NOT_EXIST, id));
        }

        if (!userStorage.userExists(userId)) {
            throw new NotFoundException(String.format(USER_DOES_NOT_EXIST, userId));
        }

        reviewStorage.removeLike(id, userId, isUseful);
    }

    private boolean isIdValueNull(Review review) {
        return review.getReviewId() == null;
    }
}
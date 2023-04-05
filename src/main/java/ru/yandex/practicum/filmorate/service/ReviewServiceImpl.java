package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

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
                () -> new NotFoundException(String.format("Review width id %d does not exist", id))
        );
    }

    @Override
    public Review createReview(Review review) {
        if (!isIdValueNull(review)) {
            throw new ValidationException("The review must have an empty ID when created");
        }

        if (!filmStorage.filmExists(review.getFilmId())) {
            throw new NotFoundException(String.format("Film width id %d does not exist", review.getFilmId()));
        }

        if (!userStorage.userExists(review.getUserId())) {
            throw new NotFoundException(String.format("User width id %d does not exist", review.getUserId()));
        }

        Review newReview = reviewStorage.createReview(review);

        eventStorage.addEvent(Event.builder()
                .userId(newReview.getUserId())
                .entityId(newReview.getReviewId())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .timestamp(System.currentTimeMillis())
                .build());

        return newReview;
    }

    @Override
    public Review updateReview(Review review) {
        if (isIdValueNull(review)) {
            throw new ValidationException("The review must not have an empty ID when updating");
        }

        if (!reviewStorage.reviewExists(review.getReviewId())) {
            throw new NotFoundException(String.format("Review width id %d does not exist", review.getReviewId()));
        }

        Review newReview = reviewStorage.updateReview(review);

        eventStorage.addEvent(Event.builder()
                .userId(newReview.getUserId())
                .entityId(newReview.getFilmId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .timestamp(System.currentTimeMillis())
                .build());

        return newReview;
    }

    @Override
    public void removeReviewById(Long id) {
        if (!reviewStorage.reviewExists(id)) {
            throw new NotFoundException(String.format("Review width id %d does not exist", id));
        }

        Review review = getReviewById(id);

        eventStorage.addEvent(Event.builder()
                .userId(review.getUserId())
                .entityId(review.getFilmId())
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .timestamp(System.currentTimeMillis())
                .build());

        reviewStorage.removeReviewById(id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        if (!reviewStorage.reviewExists(id)) {
            throw new NotFoundException(String.format("Review width id %d does not exist", id));
        }

        if (!userStorage.userExists(userId)) {
            throw new NotFoundException(String.format("User width id %d does not exist", userId));
        }

        if (reviewStorage.likeExists(id, userId)) {
            return;
        }

        reviewStorage.addLike(id, userId);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        if (!reviewStorage.reviewExists(id)) {
            throw new NotFoundException(String.format("Review width id %d does not exist", id));
        }

        if (!userStorage.userExists(userId)) {
            throw new NotFoundException(String.format("User width id %d does not exist", userId));
        }

        reviewStorage.removeLike(id, userId);
    }

    @Override
    public void addDislike(Long id, Long userId) {
        if (!reviewStorage.reviewExists(id)) {
            throw new NotFoundException(String.format("Review width id %d does not exist", id));
        }

        if (!userStorage.userExists(userId)) {
            throw new NotFoundException(String.format("User width id %d does not exist", userId));
        }

        if (reviewStorage.disLikeExists(id, userId)) {
            return;
        }

        reviewStorage.addDislike(id, userId);
    }

    @Override
    public void removeDislike(Long id, Long userId) {
        if (!reviewStorage.reviewExists(id)) {
            throw new NotFoundException(String.format("Review width id %d does not exist", id));
        }

        if (!userStorage.userExists(userId)) {
            throw new NotFoundException(String.format("User width id %d does not exist", userId));
        }

        reviewStorage.removeDislike(id, userId);
    }

    private boolean isIdValueNull(Review review) {
        return review.getReviewId() == null;
    }
}
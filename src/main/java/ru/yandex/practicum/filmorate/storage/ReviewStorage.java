package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    /**
     * Returns a list of reviews
     * The number of reviews is set by the parameter count
     * @return list of reviews
     */
    List<Review> getReviews(int count);

    /**
     * Returns a list of reviews by film id
     * The number of reviews is set by the parameter count
     * @return list of reviews by film id
     */
    List<Review> getReviewsByFilmId(Long filmId, int count);

    /**
     * Returns review by id
     * @param id
     * @return review or null if there was no one
     */
    Optional<Review> getReviewById(Long id);

    /**
     * Creates a new review
     * @param review
     * @return new review
     */
    Review createReview(Review review);

    /**
     * Updates the review
     * @param review
     * @return updated review
     */
    Review updateReview(Review review);

    /**
     * Removes review by id
     * @param id
     */
    void removeReviewById(Long id);

    /**
     * Adds a user like to a review
     * @param id
     * @param userId
     * @param isUseful
     */
    void addLike(Long id, Long userId, boolean isUseful);

    /**
     * Removes a user like to a review
     * @param id
     * @param userId
     * @param isUseful
     */
    void removeLike(Long id, Long userId, boolean isUseful);

    /**
     * Checks for the existence of review by id
     * @param id
     * @return true or false
     */
    boolean reviewExists(Long id);
}
package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    /**
     * Returns a list of reviews by film id
     * The number of reviews is set by the parameter count
     * If film id is not specified, returns all reviews
     *
     * @param filmId
     * @param count
     * @return list of reviews
     */
    List<Review> getReviews(Long filmId, int count);

    /**
     * Returns review by id
     * If the review is not found throws NotFoundException
     *
     * @param id
     * @return review by id
     */
    Review getReviewById(Long id);

    /**
     * Creates a new review
     *
     * @param review
     * @return new review
     */
    Review createReview(Review review);

    /**
     * Updates the review
     *
     * @param review
     * @return updated review
     */
    Review updateReview(Review review);

    /**
     * Removes review by id
     * If the review is not found throws NotFoundException
     *
     * @param id
     */
    void removeReviewById(Long id);

    /**
     * Adds a user like to a review
     * If the review or user is not found throws NotFoundException
     *
     * @param id
     * @param userId
     * @param isUseful
     */
    void addLike(Long id, Long userId, boolean isUseful);

    /**
     * Removes a user like to a review
     * If the review or user is not found throws NotFoundException
     *
     * @param id
     * @param userId
     * @param isUseful
     */
    void removeLike(Long id, Long userId, boolean isUseful);
}
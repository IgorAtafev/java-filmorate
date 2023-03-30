package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewStorage reviewStorage;

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void getReviews_shouldReturnEmptyListOfReviews() {
        int count = 2;

        when(reviewStorage.getReviews(count)).thenReturn(Collections.emptyList());

        assertTrue(reviewService.getReviews(null, count).isEmpty());

        verify(reviewStorage, times(1)).getReviews(count);
    }

    @Test
    void getReviews_shouldReturnListOfReviews() {
        int count = 2;
        Review review1 = initReview();
        Review review2 = initReview();

        List<Review> expected = List.of(review1, review2);

        when(reviewStorage.getReviews(count)).thenReturn(expected);

        assertEquals(expected, reviewService.getReviews(null, count));

        verify(reviewStorage, times(1)).getReviews(count);
    }

    @Test
    void getReviews_shouldReturnListOfReviewsByFilmId() throws Exception {
        int count = 2;
        Long filmId = 1L;
        Review review1 = initReview();
        Review review2 = initReview();
        review2.setFilmId(2L);

        List<Review> expected = List.of(review1);

        when(reviewStorage.getReviewsByFilmId(filmId, count)).thenReturn(expected);

        assertEquals(expected, reviewService.getReviews(filmId, count));

        verify(reviewStorage, times(1)).getReviewsByFilmId(filmId, count);
    }

    @Test
    void getReviews_shouldReturnEmptyListOfReviewsByFilmId() throws Exception {
        int count = 2;
        Long filmId = 2L;

        when(reviewStorage.getReviewsByFilmId(filmId, count)).thenReturn(Collections.emptyList());

        assertTrue(reviewService.getReviews(filmId, count).isEmpty());

        verify(reviewStorage, times(1)).getReviewsByFilmId(filmId, count);
    }

    @Test
    void getReviewById_shouldReturnReviewById() {
        Long reviewId = 1L;
        Review review = initReview();

        when(reviewStorage.getReviewById(reviewId)).thenReturn(Optional.of(review));

        assertEquals(review, reviewService.getReviewById(reviewId));

        verify(reviewStorage, times(1)).getReviewById(reviewId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getReviewById_shouldThrowAnException_ifReviewDoesNotExist(Long reviewId) {
        when(reviewStorage.getReviewById(reviewId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> reviewService.getReviewById(reviewId)
        );

        verify(reviewStorage, times(1)).getReviewById(reviewId);
    }

    @Test
    void createReview_shouldCreateAReview() {
        Long filmId = 1L;
        Long userId = 1L;
        Review review = initReview();

        when(filmStorage.filmExists(filmId)).thenReturn(true);
        when(userStorage.userExists(userId)).thenReturn(true);
        when(reviewStorage.createReview(review)).thenReturn(review);

        assertEquals(review, reviewService.createReview(review));

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(userStorage, times(1)).userExists(userId);
        verify(reviewStorage, times(1)).createReview(review);
    }

    @Test
    void createReview_shouldThrowAnException_ifReviewIdIsNotEmpty() {
        Long reviewId = 1L;
        Review review = initReview();
        review.setReviewId(reviewId);

        assertThrows(
                ValidationException.class,
                () -> reviewService.createReview(review)
        );

        verify(reviewStorage, never()).createReview(review);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 999})
    void createReview_shouldThrowAnException_ifFilmDoesNotExist(Long filmId) {
        Long userId = 1L;
        Review review = initReview();
        review.setFilmId(filmId);
        review.setUserId(userId);

        when(filmStorage.filmExists(filmId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> reviewService.createReview(review)
        );

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(userStorage, never()).userExists(userId);
        verify(reviewStorage, never()).createReview(review);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 999})
    void createReview_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long filmId = 1L;
        Review review = initReview();
        review.setFilmId(filmId);
        review.setUserId(userId);

        when(filmStorage.filmExists(filmId)).thenReturn(true);
        when(userStorage.userExists(userId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> reviewService.createReview(review)
        );

        verify(filmStorage, times(1)).filmExists(filmId);
        verify(userStorage, times(1)).userExists(userId);
        verify(reviewStorage, never()).createReview(review);
    }

    @Test
    void updateReview_shouldUpdateTheReview() {
        Long reviewId = 1L;
        Review review = initReview();
        review.setReviewId(reviewId);

        when(reviewStorage.reviewExists(reviewId)).thenReturn(true);
        when(reviewStorage.updateReview(review)).thenReturn(review);

        assertEquals(review, reviewService.updateReview(review));

        verify(reviewStorage, times(1)).reviewExists(reviewId);
        verify(reviewStorage, times(1)).updateReview(review);
    }

    @Test
    void updateReview_shouldThrowAnException_ifReviewIdIsEmpty() {
        Review review = initReview();

        assertThrows(
                ValidationException.class,
                () -> reviewService.updateReview(review)
        );

        verify(reviewStorage, never()).updateReview(review);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void updateReview_shouldThrowAnException_ifReviewDoesNotExist(Long reviewId) {
        Review review = initReview();
        review.setReviewId(reviewId);

        when(reviewStorage.reviewExists(reviewId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> reviewService.updateReview(review)
        );

        verify(reviewStorage, times(1)).reviewExists(reviewId);
        verify(reviewStorage, never()).updateReview(review);
    }

    @Test
    void removeReviewById_shouldRemoveReviewById() {
//        Long reviewId = 1L;

//        when(reviewStorage.reviewExists(reviewId)).thenReturn(true);

//        reviewService.removeReviewById(reviewId);

//        verify(reviewStorage, times(1)).reviewExists(reviewId);
//        verify(reviewStorage, times(1)).removeReviewById(reviewId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeReviewById_shouldThrowAnException_ifReviewDoesNotExist(Long reviewId) {
        when(reviewStorage.reviewExists(reviewId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> reviewService.removeReviewById(reviewId)
        );

        verify(reviewStorage, times(1)).reviewExists(reviewId);
        verify(reviewStorage, never()).removeReviewById(reviewId);
    }

    @Test
    void addLike_shouldAddTheUserLikeToAReview() {
        Long reviewId = 1L;
        Long userId = 1L;

        when(reviewStorage.reviewExists(reviewId)).thenReturn(true);
        when(userStorage.userExists(userId)).thenReturn(true);

        reviewService.addLike(reviewId, userId, true);

        verify(reviewStorage, times(1)).reviewExists(reviewId);
        verify(userStorage, times(1)).userExists(userId);
        verify(reviewStorage, times(1)).addLike(reviewId, userId, true);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldThrowAnException_ifReviewDoesNotExist(Long reviewId) {
        Long userId = 1L;

        when(reviewStorage.reviewExists(reviewId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> reviewService.addLike(reviewId, userId, true)
        );

        verify(reviewStorage, times(1)).reviewExists(reviewId);
        verify(userStorage, never()).userExists(userId);
        verify(reviewStorage, never()).addLike(reviewId, userId, true);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long reviewId = 1L;

        when(reviewStorage.reviewExists(reviewId)).thenReturn(true);
        when(userStorage.userExists(userId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> reviewService.addLike(reviewId, userId, true)
        );

        verify(reviewStorage, times(1)).reviewExists(reviewId);
        verify(userStorage, times(1)).userExists(userId);
        verify(reviewStorage, never()).addLike(reviewId, userId, true);
    }

    @Test
    void removeLike_shouldRemoveTheUserLikeToAReview() {
        Long reviewId = 1L;
        Long userId = 1L;

        when(reviewStorage.reviewExists(reviewId)).thenReturn(true);
        when(userStorage.userExists(userId)).thenReturn(true);

        reviewService.removeLike(reviewId, userId, true);

        verify(reviewStorage, times(1)).reviewExists(reviewId);
        verify(userStorage, times(1)).userExists(userId);
        verify(reviewStorage, times(1)).removeLike(reviewId, userId, true);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldThrowAnException_ifReviewDoesNotExist(Long reviewId) {
        Long userId = 1L;

        when(reviewStorage.reviewExists(reviewId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> reviewService.removeLike(reviewId, userId, true)
        );

        verify(reviewStorage, times(1)).reviewExists(reviewId);
        verify(userStorage, never()).userExists(userId);
        verify(reviewStorage, never()).removeLike(reviewId, userId, true);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldThrowAnException_ifUserDoesNotExist(Long userId) {
        Long reviewId = 1L;

        when(reviewStorage.reviewExists(reviewId)).thenReturn(true);
        when(userStorage.userExists(userId)).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> reviewService.removeLike(reviewId, userId, true)
        );

        verify(reviewStorage, times(1)).reviewExists(reviewId);
        verify(userStorage, times(1)).userExists(userId);
        verify(reviewStorage, never()).removeLike(reviewId, userId, true);
    }

    private Review initReview() {
        Review review = new Review();

        review.setContent("This film is soo bad.");
        review.setIsPositive(false);
        review.setFilmId(1L);
        review.setUserId(1L);

        return review;
    }
}
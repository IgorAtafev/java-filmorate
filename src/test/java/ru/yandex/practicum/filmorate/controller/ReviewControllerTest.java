package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private ReviewService service;

    @InjectMocks
    private ReviewController controller;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void getReviews_shouldReturnEmptyListOfReviews() throws Exception {
        int count = 2;

        mockMvc.perform(get("/reviews?count={count}", count))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1)).getReviews(null, count);
    }

    @Test
    void getReviews_shouldReturnListOfReviews() throws Exception {
        int count = 2;
        Review review1 = initReview();
        Review review2 = initReview();

        List<Review> expected = List.of(review1, review2);
        String json = objectMapper.writeValueAsString(expected);

        when(service.getReviews(null, count)).thenReturn(expected);

        mockMvc.perform(get("/reviews?count={count}", count))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getReviews(null, count);
    }

    @Test
    void getReviews_shouldReturnListOfReviewsByFilmId() throws Exception {
        int count = 2;
        Long filmId = 1L;
        Review review1 = initReview();
        Review review2 = initReview();
        review2.setFilmId(2L);

        List<Review> expected = List.of(review1);
        String json = objectMapper.writeValueAsString(expected);

        when(service.getReviews(filmId, count)).thenReturn(expected);

        mockMvc.perform(get("/reviews?filmId={filmId}&count={count}", filmId, count))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getReviews(filmId, count);
    }

    @Test
    void getReviews_shouldReturnEmptyListOfReviewsByFilmId() throws Exception {
        int count = 2;
        Long filmId = 2L;

        mockMvc.perform(get("/reviews?filmId={filmId}&count={count}", filmId, count))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service, times(1)).getReviews(filmId, count);
    }

    @Test
    void getReviewById_shouldReturnReviewById() throws Exception {
        Long reviewId = 1L;
        Review review = initReview();
        String json = objectMapper.writeValueAsString(review);

        when(service.getReviewById(reviewId)).thenReturn(review);

        mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(service, times(1)).getReviewById(reviewId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void getReviewById_shouldResponseWithNotFound_ifReviewDoesNotExist(Long reviewId) throws Exception {
        when(service.getReviewById(reviewId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getReviewById(reviewId);
    }

    @Test
    void createReview_shouldResponseWithOk() throws Exception {
        Review review = initReview();
        String json = objectMapper.writeValueAsString(review);

        when(service.createReview(review)).thenReturn(review);

        mockMvc.perform(post("/reviews").contentType("application/json").content(json))
                .andExpect(status().isOk());

        verify(service, times(1)).createReview(review);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidReviews")
    void createReview_shouldResponseWithBadRequest_ifReviewIsInvalid(Review review) throws Exception {
        String json = objectMapper.writeValueAsString(review);

        mockMvc.perform(post("/reviews").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).createReview(review);
    }

    @Test
    void updateReview_shouldResponseWithOk() throws Exception {
        Review review = initReview();
        String json = objectMapper.writeValueAsString(review);

        when(service.updateReview(review)).thenReturn(review);

        mockMvc.perform(put("/reviews").contentType("application/json").content(json))
                .andExpect(status().isOk());

        verify(service, times(1)).updateReview(review);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidReviews")
    void updateReview_shouldResponseWithBadRequest_ifReviewIsInvalid(Review review) throws Exception {
        String json = objectMapper.writeValueAsString(review);

        mockMvc.perform(put("/reviews").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).updateReview(review);
    }

    @Test
    void removeReviewById_shouldResponseWithOk() throws Exception {
        Long reviewId = 1L;

        mockMvc.perform(delete("/reviews/{id}", reviewId))
                .andExpect(status().isOk());

        verify(service, times(1)).removeReviewById(reviewId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeReviewById_shouldResponseWithNotFound_ifReviewDoesNotExist(Long reviewId) throws Exception {
        doThrow(NotFoundException.class).when(service).removeReviewById(reviewId);

        mockMvc.perform(delete("/reviews/{id}", reviewId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).removeReviewById(reviewId);
    }

    @Test
    void addLike_shouldResponseWithOk() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;

        mockMvc.perform(put("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isOk());

        verify(service, times(1)).addLike(reviewId, userId, true);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldResponseWithNotFound_ifReviewDoesNotExist(Long reviewId) throws Exception {
        Long userId = 1L;

        doThrow(NotFoundException.class).when(service).addLike(reviewId, userId, true);

        mockMvc.perform(put("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).addLike(reviewId, userId, true);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addLike_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        Long filmId = 1L;

        doThrow(NotFoundException.class).when(service).addLike(filmId, userId, true);

        mockMvc.perform(put("/reviews/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).addLike(filmId, userId, true);
    }

    @Test
    void removeLike_shouldResponseWithOk() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;

        mockMvc.perform(delete("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isOk());

        verify(service, times(1)).removeLike(reviewId, userId, true);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldResponseWithNotFound_ifReviewDoesNotExist(Long reviewId) throws Exception {
        Long userId = 1L;

        doThrow(NotFoundException.class).when(service).removeLike(reviewId, userId, true);

        mockMvc.perform(delete("/reviews/{id}/like/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).removeLike(reviewId, userId, true);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeLike_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        Long filmId = 1L;

        doThrow(NotFoundException.class).when(service).removeLike(filmId, userId, true);

        mockMvc.perform(delete("/reviews/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).removeLike(filmId, userId, true);
    }

    @Test
    void addDisLike_shouldResponseWithOk() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;

        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", reviewId, userId))
                .andExpect(status().isOk());

        verify(service, times(1)).addLike(reviewId, userId, false);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addDisLike_shouldResponseWithNotFound_ifReviewDoesNotExist(Long reviewId) throws Exception {
        Long userId = 1L;

        doThrow(NotFoundException.class).when(service).addLike(reviewId, userId, false);

        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).addLike(reviewId, userId, false);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void addDisLike_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        Long filmId = 1L;

        doThrow(NotFoundException.class).when(service).addLike(filmId, userId, false);

        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", filmId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).addLike(filmId, userId, false);
    }

    @Test
    void removeDisLike_shouldResponseWithOk() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;

        mockMvc.perform(delete("/reviews/{id}/dislike/{userId}", reviewId, userId))
                .andExpect(status().isOk());

        verify(service, times(1)).removeLike(reviewId, userId, false);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeDisLike_shouldResponseWithNotFound_ifReviewDoesNotExist(Long reviewId) throws Exception {
        Long userId = 1L;

        doThrow(NotFoundException.class).when(service).removeLike(reviewId, userId, false);

        mockMvc.perform(delete("/reviews/{id}/dislike/{userId}", reviewId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).removeLike(reviewId, userId, false);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 999L})
    void removeDisLike_shouldResponseWithNotFound_ifUserDoesNotExist(Long userId) throws Exception {
        Long filmId = 1L;

        doThrow(NotFoundException.class).when(service).removeLike(filmId, userId, false);

        mockMvc.perform(delete("/reviews/{id}/dislike/{userId}", filmId, userId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).removeLike(filmId, userId, false);
    }

    private static Stream<Arguments> provideInvalidReviews() {
        return Stream.of(
                Arguments.of(initReview(review -> review.setContent(null))),
                Arguments.of(initReview(review -> review.setContent("a"))),
                Arguments.of(initReview(review -> review.setContent("long strin".repeat(50) + "g"))),
                Arguments.of(initReview(review -> review.setIsPositive(null))),
                Arguments.of(initReview(review -> review.setFilmId(null))),
                Arguments.of(initReview(review -> review.setUserId(null)))
        );
    }

    private static Review initReview(Consumer<Review> consumer) {
        Review review = initReview();

        consumer.accept(review);

        return review;
    }

    private static Review initReview() {
        Review review = new Review();

        review.setContent("This film is soo bad.");
        review.setIsPositive(false);
        review.setFilmId(1L);
        review.setUserId(1L);

        return review;
    }
}
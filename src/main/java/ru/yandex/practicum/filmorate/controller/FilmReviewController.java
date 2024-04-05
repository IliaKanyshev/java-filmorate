package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.film.FilmReviewService;
import ru.yandex.practicum.filmorate.validators.Marker;

import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping("/reviews")
@Slf4j
@Validated
@RequiredArgsConstructor
public class FilmReviewController {

    private final FilmReviewService filmReviewService;

    @PostMapping
    public Review saveReview(@RequestBody @Valid Review review) {
        log.info("Получен запрос на добавление отзыва.");
        return filmReviewService.saveReview(review);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Review updateReview(@RequestBody @Valid Review review) {
        log.info("Получен запрос на обновление отзыва: {}.", review);
        return filmReviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Integer id) {
        filmReviewService.deleteReview(id);
        log.info("Отзыв с id {} удален.", id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Integer id) {
        log.info("Получен запрос на получение отзыва с id= {}", id);
        return filmReviewService.getReviewById(id);
    }

    @GetMapping
    public Collection<Review> getAllFilmsReviews(
            @RequestParam(name = "filmId", required = false) Integer filmId,
            @RequestParam(name = "count", defaultValue = "10", required = false) Integer count
    ) {
        log.info("Получен запрос на получение отзывов для фильма(-ов).");
        return filmReviewService.getAllFilmsReviews(filmId, count);
    }


    @PutMapping("/{id}/like/{userId}")
    public Review setLike(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        log.info("PUT request to set like from user_id={} for review_id={}.", userId, reviewId);
        Review review = filmReviewService.setLikeForFilmReview(reviewId, userId);
        log.info("Like from user_id={} for review_id={} were set", userId, reviewId);
        return review;
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review setDislike(
            @PathVariable("id") Integer reviewId,
            @PathVariable("userId") Integer userId) {
        log.info("PUT request to set dislike from user_id={} for review_id={}.", userId, reviewId);
        Review review = filmReviewService.setDislikeForFilmReview(reviewId, userId);
        log.info("Dislike from user_id={} for review_id={} were set", userId, reviewId);
        return review;
    }

    @DeleteMapping({
            "/{id}/like/{userId}",
            "/{id}/dislike/{userId}"})
    public Review deleteReviewReaction(
            @PathVariable("id") Integer reviewId,
            @PathVariable Integer userId) {
        log.info("Получен запрос на удаление реакции на отзыв: id={}.", reviewId);
        return filmReviewService.deleteUserReaction(reviewId, userId);
    }

}

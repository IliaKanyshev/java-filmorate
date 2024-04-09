package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface FilmReviewStorage {
    Collection<Review> getAllReviews();

    Review saveReview(Review review);

    Optional<Review> updateReview(Review review);

    void deleteReviewById(Integer id);

    Optional<Review> getReviewById(Integer id);

    Collection<Review> getAllReviewsLimited(Integer count);

    Collection<Review> getAllFilmReviewsLimited(Integer filmId, Integer count);

    Optional<Review> setReaction(Integer reviewId, Integer userId, int i);

    Optional<Review> deleteUserReaction(Integer reviewId, Integer userId);
}


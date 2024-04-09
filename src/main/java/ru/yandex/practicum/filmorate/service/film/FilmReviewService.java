package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmReviewStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.LogStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.text.MessageFormat;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmReviewService {

    private final FilmReviewStorage filmReviewStorage;
    private final FilmStorage filmDbStorage;
    private final UserStorage userStorage;
    private final LogStorage logStorage;

    public Review saveReview(Review review) {
        validateReview(review);

        Review reviewSaved = filmReviewStorage.saveReview(review);
        int u = reviewSaved.getUserId();
        int r = reviewSaved.getReviewId();
        logStorage.saveLog(u, r, "REVIEW", "ADD");
        return reviewSaved;
    }

    private void validateReview(Review review) {
        userStorage.findUserById(review.getUserId());
        filmDbStorage.getFilmById(review.getFilmId());
    }

    public Review updateReview(Review review) {
        validateReview(review);
        var rew = filmReviewStorage.updateReview(review)
                .orElseThrow(
                        () -> new NotFoundException(
                                MessageFormat.format("Review with id={0} not found", review.getReviewId())));
        var u = rew.getUserId();
        var r = rew.getReviewId();
        logStorage.saveLog(u, r, "REVIEW", "UPDATE");
        return rew;
    }

    public void deleteReview(Integer id) {
        var rew = getReviewById(id);
        int u = rew.getUserId();
        int r = rew.getReviewId();
        logStorage.saveLog(u, r, "REVIEW", "REMOVE");
        filmReviewStorage.deleteReviewById(id);
    }

    public Review getReviewById(Integer id) {
        return filmReviewStorage.getReviewById(id)
                .orElseThrow(
                        () -> new NotFoundException(MessageFormat.format("Review with id={0} not found", id)));
    }

    public Collection<Review> getAllFilmsReviews(Integer filmId, Integer count) {
        if (filmId == null) {
            return filmReviewStorage.getAllReviewsLimited(count);
        }
        filmDbStorage.getFilmById(filmId);
        return filmReviewStorage.getAllFilmReviewsLimited(filmId, count);
    }

    public Review setLikeForFilmReview(Integer reviewId, Integer userId) {
        userStorage.findUserById(userId);
        var rew = filmReviewStorage.setReaction(reviewId, userId, 1).orElseThrow(
                () -> new NotFoundException(MessageFormat.format("Review with id={0} not found", reviewId)));
        return rew;
    }

    public Review setDislikeForFilmReview(Integer reviewId, Integer userId) {
        userStorage.findUserById(userId);
        var rew = filmReviewStorage.setReaction(reviewId, userId, -1).orElseThrow(
                () -> new NotFoundException(MessageFormat.format("Review with id={0} not found", reviewId)));
        return rew;
    }

    public Review deleteUserReaction(Integer reviewId, Integer userId) {
        userStorage.findUserById(userId);
        var rew = filmReviewStorage.deleteUserReaction(reviewId, userId)
                .orElseThrow(
                        () -> new NotFoundException(MessageFormat.format("Review with id={0} not found", reviewId)));
        return rew;
    }

}

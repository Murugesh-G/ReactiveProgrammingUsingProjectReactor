package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public Flux<Movie> getAllMovies() {
        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux.flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();

                    return reviewsMono.map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(throwable -> {
                    log.error("Exception is : ", throwable);
                    throw new MovieException(throwable.getMessage());
                })
                .log();
    }

    public Flux<Movie> getAllMovies_retry() {
        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux.flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();

                    return reviewsMono.map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(throwable -> {
                    log.error("Exception is : ", throwable);
                    throw new MovieException(throwable.getMessage());
                })
                .retry(3)
                .log();
    }

    public Mono<Movie> getMovieById(long movieId) {
        var movieinfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsFlux = reviewService.retrieveReviewsFlux(movieId).collectList();
        return movieinfoMono.zipWith(reviewsFlux, (movieinfo, reviews) -> new Movie(movieinfo, reviews));
    }
}

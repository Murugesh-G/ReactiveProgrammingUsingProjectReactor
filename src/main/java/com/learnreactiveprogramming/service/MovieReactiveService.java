package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
        }).log();
    }

    public Mono<Movie> getMovieById(long movieId){
        var movieinfoMono=movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsFlux=reviewService.retrieveReviewsFlux(movieId).collectList();

        return movieinfoMono.zipWith(reviewsFlux, (movieinfo, reviews) -> new Movie(movieinfo, reviews));
    }
}

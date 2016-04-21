package com.example.popularmovies.data.api;

import com.example.popularmovies.BuildConfig;
import com.example.popularmovies.models.MovieBean;
import com.example.popularmovies.models.MovieReview;
import com.example.popularmovies.models.MovieTrailer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by vishalvyas on 4/21/16.
 */
public interface MoviesApi {

    @GET("movie/popular?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY)
    Call<MovieBean.Response> popularMovies();

    @GET("movie/top_rated?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY)
    Call<MovieBean.Response> topRatedMovies();

    @GET("movie/{id}/videos?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY)
    Call<MovieTrailer> movieVideos(@Path("id") long movieId);

    @GET("movie/{id}/reviews?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY)
    Call<MovieReview> movieReviews(@Path("id") long movieId);

}

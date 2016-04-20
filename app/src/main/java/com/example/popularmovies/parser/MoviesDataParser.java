package com.example.popularmovies.parser;

import android.text.TextUtils;

import com.example.popularmovies.models.MovieBean;
import com.example.popularmovies.models.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vishalvyas on 4/18/16.
 */
public class MoviesDataParser {

    public static List<MovieBean> getMovies(String jsonMoviesString) throws JSONException {
        if (TextUtils.isEmpty(jsonMoviesString))
            return Collections.EMPTY_LIST;

        JSONObject jsonObject = new JSONObject(jsonMoviesString);
        JSONArray results = jsonObject.getJSONArray("results");
        List<MovieBean> lstMovies = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            MovieBean movieBean = new MovieBean();
            JSONObject jsonMovie = results.getJSONObject(i);
            movieBean.setId(jsonMovie.getString("id"));
            movieBean.setOriginalTitle(jsonMovie.getString("original_title"));
            movieBean.setOverview(jsonMovie.getString("overview"));
            movieBean.setPosterPath(jsonMovie.getString("poster_path"));
            movieBean.setReleaseDate(jsonMovie.getString("release_date"));
            movieBean.setTitle(jsonMovie.getString("title"));
            movieBean.setVoteAverage(jsonMovie.getString("vote_average"));
            movieBean.setVoteCount(jsonMovie.getString("vote_count"));
            lstMovies.add(movieBean);
        }
        return lstMovies;
    }

    public static List<MovieTrailer> getMovieTrailers(String jsonMoviesString) throws JSONException {
        if (TextUtils.isEmpty(jsonMoviesString))
            return Collections.EMPTY_LIST;
        JSONObject jsonObject = new JSONObject(jsonMoviesString);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        List<MovieTrailer> lstMovieTrailers = new ArrayList<>();
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            MovieTrailer movieTrailer = new MovieTrailer();
            JSONObject jsonMovieTrailer = jsonArray.getJSONObject(i);
            movieTrailer.setId(jsonMovieTrailer.getString("id"));
            movieTrailer.setKey(jsonMovieTrailer.getString("key"));
            movieTrailer.setName(jsonMovieTrailer.getString("name"));
            movieTrailer.setSite(jsonMovieTrailer.getString("site"));
            movieTrailer.setType(jsonMovieTrailer.getString("type"));
            lstMovieTrailers.add(movieTrailer);
        }
        return lstMovieTrailers;
    }
}
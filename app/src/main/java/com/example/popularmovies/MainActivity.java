package com.example.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.popularmovies.data.api.MoviesApi;
import com.example.popularmovies.models.MovieBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.popularmovies.data.provider.MoviesContract.Movies;
import static com.example.popularmovies.data.provider.MoviesContract.MoviesColumns;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static final String TAG = "PM_TAG";
    public static final String THE_MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupRecyclerView();
    }

    private List<MovieBean> getSavedMovies() {
        final Cursor cursor = getContentResolver().query(Movies.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            try {
                List<MovieBean> lstMovies = new ArrayList<>();
                while (cursor.moveToNext()) {
                    MovieBean movieBean = new MovieBean();
                    movieBean.setId(cursor.getString(cursor.getColumnIndex(MoviesColumns.MOVIE_ID)));
                    movieBean.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesColumns.MOVIE_ORIGINAL_TITLE)));
                    movieBean.setOverview(cursor.getString(cursor.getColumnIndex(MoviesColumns.MOVIE_OVERVIEW)));
                    movieBean.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesColumns.MOVIE_POSTER_PATH)));
                    movieBean.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesColumns.MOVIE_RELEASE_DATE)));
                    movieBean.setTitle(cursor.getString(cursor.getColumnIndex(MoviesColumns.MOVIE_TITLE)));
                    movieBean.setVoteAverage(cursor.getString(cursor.getColumnIndex(MoviesColumns.MOVIE_VOTE_AVERAGE)));
                    movieBean.setVoteCount(cursor.getString(cursor.getColumnIndex(MoviesColumns.MOVIE_VOTE_COUNT)));
                    lstMovies.add(movieBean);
                }
                return lstMovies;
            } finally {
                cursor.close();
            }
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private void getMovies(String moviesSortOrder) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(THE_MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MoviesApi moviesApi = retrofit.create(MoviesApi.class);
        Call<MovieBean.Response> moviesCall = null;
        if (TextUtils.isEmpty(moviesSortOrder) || moviesSortOrder.equalsIgnoreCase("Most popular")) {
            moviesCall = moviesApi.popularMovies();
        } else if (moviesSortOrder.equalsIgnoreCase("favorites")) {
            final List<MovieBean> savedMovies = getSavedMovies();
            recyclerView.setAdapter(new MoviesAdapter(recyclerView.getContext(), savedMovies));
        } else {
            moviesCall = moviesApi.topRatedMovies();
        }
        if (moviesCall == null) {
            return;
        }
        moviesCall.enqueue(new Callback<MovieBean.Response>() {
            @Override
            public void onResponse(Call<MovieBean.Response> call, Response<MovieBean.Response> response) {
                List<MovieBean> lstMovies = response.body().lstMovies;
                if (lstMovies == null)
                    Log.d(TAG, "Empty movies list");
                recyclerView.setAdapter(new MoviesAdapter(recyclerView.getContext(), lstMovies));
            }

            @Override
            public void onFailure(Call<MovieBean.Response> call, Throwable t) {

            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(
                R.id.recyclerView);
        recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String moviesSortOrder = prefs.getString("movies_sort_order", null);
        getMovies(moviesSortOrder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
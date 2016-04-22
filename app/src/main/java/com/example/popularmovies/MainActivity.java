package com.example.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.popularmovies.fragments.MovieDetailsFragment;
import com.example.popularmovies.fragments.MoviesFragment;
import com.example.popularmovies.models.MovieBean;

public class MainActivity extends AppCompatActivity implements MoviesFragment.CallbackMovieClicked {

    public static final String THE_MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String MOVIES_FRAGMENT_TAG = "movies_fragment_tag";
    private static final String MOVIES_DETAILS_FRAGMENT_TAG = "movies_details_fragment_tag";
    private boolean mTwoPane;
    View lblNoMovieSelected;
    private MoviesFragment moviesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTwoPane = findViewById(R.id.movie_details_container) != null;
        if (savedInstanceState == null) {
            moviesFragment = MoviesFragment.newInstance(null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content, moviesFragment, MOVIES_FRAGMENT_TAG);
            if (mTwoPane) {
                lblNoMovieSelected = findViewById(R.id.lblNoMovieSelected);
                final MovieDetailsFragment movieDetailsFragment = MovieDetailsFragment.newInstance(null);
                FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                fragmentTransaction1.replace(R.id.movie_details_container, movieDetailsFragment, MOVIES_DETAILS_FRAGMENT_TAG);
            }
            fragmentTransaction.commit();
        }
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

    @Override
    public void onMovieClicked(MovieBean movieBean) {
        if (mTwoPane) {
            lblNoMovieSelected.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDetailsActivity.MOVIE_PARCEL, movieBean);
            FragmentManager fragmentManager = getSupportFragmentManager();
            final MovieDetailsFragment movieDetailsFragment = MovieDetailsFragment.newInstance(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.movie_details_container, movieDetailsFragment, MOVIES_DETAILS_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onMoviesLoaded() {
        if (mTwoPane) {
            MovieBean movieBean = moviesFragment.getMoviesAdapter().getItem(0);
            onMovieClicked(movieBean);
        }
    }
}
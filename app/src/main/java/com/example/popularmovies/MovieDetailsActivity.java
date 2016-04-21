package com.example.popularmovies;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.popularmovies.fragments.MovieDetailsFragment;
import com.example.popularmovies.models.MovieBean;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String MOVIE_PARCEL = "movie_parcel";
    private static final String TAG = "MovieDetailsActivity";
    private String MOVIE_DETAILS_TAG = "movie_details_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (savedInstanceState == null) {
            final MovieBean movieBean = getIntent().getParcelableExtra(MOVIE_PARCEL);
            Bundle bundle = new Bundle();
            bundle.putParcelable(MOVIE_PARCEL, movieBean);
            final MovieDetailsFragment movieDetailsFragment = MovieDetailsFragment.newInstance(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content, movieDetailsFragment, MOVIE_DETAILS_TAG);
            fragmentTransaction.commit();
        }
    }
}

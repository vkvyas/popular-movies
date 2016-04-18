package com.example.popularmovies;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String MOVIE_PARCEL = "movie_parcel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        final TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        final TextView txtOverview = (TextView) findViewById(R.id.txtOverview);
        final TextView txtRating = (TextView) findViewById(R.id.txtRating);
        final TextView txtReleaseDate = (TextView) findViewById(R.id.txtReleaseDate);
        final ImageView imgPoster = (ImageView) findViewById(R.id.imgPoster);

        final MovieBean movieBean = getIntent().getParcelableExtra(MOVIE_PARCEL);
        if (movieBean != null) {
            txtTitle.setText(movieBean.getOriginalTitle());
            txtOverview.setText(movieBean.getOverview());
            txtReleaseDate.setText("Release Date\n" + movieBean.getReleaseDate());
            txtRating.setText("Rating\n" + movieBean.getVoteAverage());
        }
        Picasso.with(getApplicationContext())
                .load(MoviesAdapter.HTTP_BASE_URL_FOR_IMAGES + movieBean.getPosterPath())
                .into(imgPoster);
    }
}

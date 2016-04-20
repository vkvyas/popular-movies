package com.example.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.models.MovieBean;
import com.example.popularmovies.models.MovieReview;
import com.example.popularmovies.models.MovieTrailer;
import com.example.popularmovies.parser.MoviesDataParser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String MOVIE_PARCEL = "movie_parcel";
    private static final String TAG = "MovieDetailsActivity";
    ViewGroup lytTrailers, lytReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        final TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        final TextView txtOverview = (TextView) findViewById(R.id.txtOverview);
        final TextView txtRating = (TextView) findViewById(R.id.txtRating);
        final TextView txtReleaseDate = (TextView) findViewById(R.id.txtReleaseDate);
        final ImageView imgPoster = (ImageView) findViewById(R.id.imgPoster);
        lytTrailers = (ViewGroup) findViewById(R.id.lytTrailers);
        lytReviews = (ViewGroup) findViewById(R.id.lytReviews);

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

        loadTrailers(movieBean);
        loadReviews(movieBean);
    }

    private void loadTrailers(MovieBean movieBean) {
        HttpUrl.Builder urlBuilder =
                HttpUrl.parse(MainActivity.THE_MOVIE_DB_BASE_URL + "/movie/" + movieBean.getId() + "/videos").newBuilder();
        urlBuilder.addQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
        String url = urlBuilder.build().toString();
        OkHttpClient okHttpClient = new OkHttpClient();
        Log.d(TAG, url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                try {
                    final List<MovieTrailer> movieTrailers = MoviesDataParser.getMovieTrailers(responseData);
                    MovieDetailsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final LayoutInflater layoutInflater = getLayoutInflater();
                            Log.d(TAG, "no of trailers " + movieTrailers.size());
                            for (MovieTrailer movieTrailer : movieTrailers) {
                                final ViewGroup rowTrailer = (ViewGroup) layoutInflater.inflate(R.layout.row_trailer, lytTrailers, false);
                                TextView txtTrailer = (TextView) rowTrailer.findViewById(R.id.txtTrailer);
                                txtTrailer.setText(movieTrailer.getName());
                                lytTrailers.addView(rowTrailer);
                                rowTrailer.setTag(movieTrailer);
                                rowTrailer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        MovieTrailer movieTrailer = (MovieTrailer) v.getTag();
                                        startActivity(new Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("http://www.youtube.com/watch?v=" + movieTrailer.getKey())
                                        ));
                                    }
                                });
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadReviews(MovieBean movieBean) {
        HttpUrl.Builder urlBuilder =
                HttpUrl.parse(MainActivity.THE_MOVIE_DB_BASE_URL + "/movie/" + movieBean.getId() + "/reviews").newBuilder();
        urlBuilder.addQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
        String url = urlBuilder.build().toString();
        OkHttpClient okHttpClient = new OkHttpClient();
        Log.d(TAG, url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                try {
                    final List<MovieReview> movieReviews = MoviesDataParser.getMovieReviews(responseData);
                    MovieDetailsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final LayoutInflater layoutInflater = getLayoutInflater();
                            Log.d(TAG, "no of reviews " + movieReviews.size());
                            for (MovieReview movieReview : movieReviews) {
                                final ViewGroup rowReview = (ViewGroup) layoutInflater.inflate(R.layout.row_review, lytReviews, false);
                                TextView txtAuthor = (TextView) rowReview.findViewById(R.id.txtAuthor);
                                txtAuthor.setText(movieReview.getAuthor());
                                TextView txtReview = (TextView) rowReview.findViewById(R.id.txtReview);
                                txtReview.setText(movieReview.getContent());
                                lytReviews.addView(rowReview);
                                rowReview.setTag(movieReview);
                                rowReview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

package com.example.popularmovies.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmovies.BuildConfig;
import com.example.popularmovies.MainActivity;
import com.example.popularmovies.MovieDetailsActivity;
import com.example.popularmovies.MoviesAdapter;
import com.example.popularmovies.R;
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
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

import static com.example.popularmovies.data.provider.MoviesContract.Movies;

/**
 * Created by vishal vyas on 21/04/16.
 */
public class MovieDetailsFragment extends Fragment {

    ViewGroup lytTrailers, lytReviews;
    private static final String TAG = "MovieDetailsFragment";
    private ActionBar supportActionBar;

    public static MovieDetailsFragment newInstance(Bundle bundle) {
        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
        movieDetailsFragment.setArguments(bundle);
        return movieDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            Log.e(TAG, "No Bundle arguments!!");
            return null;
        }
        final MovieBean movieBean = getArguments().getParcelable(MovieDetailsActivity.MOVIE_PARCEL);
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate
                (R.layout.fragment_movie_details, container, false);
        final TextView txtTitle = (TextView) viewGroup.findViewById(R.id.txtTitle);
        final TextView txtOverview = (TextView) viewGroup.findViewById(R.id.txtOverview);
        final TextView txtRating = (TextView) viewGroup.findViewById(R.id.txtRating);
        final TextView txtReleaseDate = (TextView) viewGroup.findViewById(R.id.txtReleaseDate);
        final ImageView imgPoster = (ImageView) viewGroup.findViewById(R.id.imgPoster);
        lytTrailers = (ViewGroup) viewGroup.findViewById(R.id.lytTrailers);
        lytReviews = (ViewGroup) viewGroup.findViewById(R.id.lytReviews);

        txtTitle.setText(movieBean.getOriginalTitle());
        txtOverview.setText(movieBean.getOverview());
        txtReleaseDate.setText("Release Date\n" + movieBean.getReleaseDate());
        txtRating.setText("Rating\n" + movieBean.getVoteAverage());

        Picasso.with(getActivity().getApplicationContext())
                .load(MoviesAdapter.HTTP_BASE_URL_FOR_IMAGES + movieBean.getPosterPath())
                .into(imgPoster);

        loadTrailers(movieBean);
        loadReviews(movieBean);

        CheckBox btnFavorites = (CheckBox) viewGroup.findViewById(R.id.btnFavorites);
        final ContentResolver contentResolver = getContext().getContentResolver();
        if (isMovieFavourite(movieBean)) {
            btnFavorites.setChecked(true);
        }
        btnFavorites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final Uri uri = Uri.withAppendedPath(Movies.CONTENT_URI, movieBean.getId());
                if (isChecked) {
                    getInsertObservable(uri, contentResolver, movieBean)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if (aBoolean) {
                                        Toast.makeText(getContext(), "Added to your favorites list!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    getDeleteObservable(uri, contentResolver)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if (aBoolean) {
                                        Toast.makeText(getContext(), "Deleted from your favorites list!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        return viewGroup;
    }

    Observable<Boolean> getDeleteObservable(final Uri uri, final ContentResolver contentResolver) {
        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                return Observable.just(deleteFromDb(uri, contentResolver));
            }
        });
    }

    private Boolean deleteFromDb(Uri uri, ContentResolver contentResolver) {
        return contentResolver.delete(uri, null, null) >= 1;
    }

    Observable<Boolean> getInsertObservable(final Uri uri, final ContentResolver contentResolver, final MovieBean movieBean) {
        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                return Observable.just(insertIntoDb(uri, contentResolver, movieBean));
            }
        });
    }

    private Boolean insertIntoDb(Uri uri, ContentResolver contentResolver, MovieBean movieBean) {
        return contentResolver.insert(uri, Movies.toContentValues(movieBean)) != null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isMovieFavourite(MovieBean movieBean) {
        final Cursor cursor = getContext().getContentResolver()
                .query(Uri.withAppendedPath(Movies.CONTENT_URI, movieBean.getId()), null, null, null, null);
        return (cursor != null && cursor.getCount() > 0);
    }

    private void loadTrailers(MovieBean movieBean) {
        HttpUrl.Builder urlBuilder =
                HttpUrl.parse(MainActivity.THE_MOVIE_DB_BASE_URL + "movie/" + movieBean.getId() + "/videos").newBuilder();
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
                    final FragmentActivity activity = getActivity();
                    if (activity == null) {
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final LayoutInflater layoutInflater = activity.getLayoutInflater();
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
                HttpUrl.parse(MainActivity.THE_MOVIE_DB_BASE_URL + "movie/" + movieBean.getId() + "/reviews").newBuilder();

        Log.d(TAG, "loading reviews for " + movieBean.getId());
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
                    final FragmentActivity activity = getActivity();
                    if (activity == null) {
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final LayoutInflater layoutInflater = activity.getLayoutInflater();
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

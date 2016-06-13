package com.example.popularmovies.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.popularmovies.MainActivity;
import com.example.popularmovies.MarginDecoration;
import com.example.popularmovies.MoviesAdapter;
import com.example.popularmovies.NetworkChangeReceiver;
import com.example.popularmovies.R;
import com.example.popularmovies.data.api.MoviesApi;
import com.example.popularmovies.data.provider.MoviesContract;
import com.example.popularmovies.models.MovieBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by vishalvyas on 4/22/16.
 */
public class MoviesFragment extends Fragment implements NetworkChangeReceiver.NetworkConnectedListener {

    private static final String LIST_POSITION = "list_position";
    private static final String SAVED_MOVIES = "saved_movies";

    private RecyclerView recyclerView;
    public static final String TAG = "MoviesFragment";
    private CallbackMovieClicked mCallback;
    private MoviesAdapter mMoviesAdapter;
    private List<MovieBean> mSavedMovies;

    public static MoviesFragment newInstance(Bundle bundle) {
        MoviesFragment moviesFragment = new MoviesFragment();
        moviesFragment.setArguments(bundle);
        return moviesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMovies();
    }

    private void loadMovies() {
        if (mSavedMovies == null || mSavedMovies.isEmpty()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            final String moviesSortOrder = prefs.getString("movies_sort_order", "Most popular");
            getMovies(moviesSortOrder);
        } else {
            mMoviesAdapter = new MoviesAdapter(recyclerView.getContext(), mSavedMovies);
            mMoviesAdapter.setMovieClickedCallback(mCallback);
            recyclerView.setAdapter(mMoviesAdapter);
            mCallback.onMoviesLoaded();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCallback = (CallbackMovieClicked) getActivity();
        if (savedInstanceState != null) {
            Log.d(TAG, "found savedInstanceState");
            boolean containsKey = savedInstanceState.containsKey(LIST_POSITION);
            if (containsKey) {
                int listPosition = savedInstanceState.getInt(LIST_POSITION);
                Log.d(TAG, "listPosition " + listPosition);
                recyclerView.getLayoutManager().scrollToPosition(savedInstanceState.getInt(LIST_POSITION));
            }
            containsKey = savedInstanceState.containsKey(SAVED_MOVIES);
            Log.d(TAG, "SAVED_MOVIES " + containsKey);
            if (containsKey) {
                mSavedMovies = savedInstanceState.getParcelableArrayList(SAVED_MOVIES);
                if (mSavedMovies != null) {
                    Log.d(TAG, "mSavedMovies " + mSavedMovies.size());
                }
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int mScrollPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        outState.putInt(LIST_POSITION, mScrollPosition);
        outState.putParcelableArrayList(SAVED_MOVIES, (ArrayList<? extends Parcelable>) mSavedMovies);
        super.onSaveInstanceState(outState);
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) getActivity().findViewById(
                R.id.recyclerView);
        recyclerView.addItemDecoration(new MarginDecoration(getContext()));
        recyclerView.setHasFixedSize(true);
    }

    @NonNull
    private Observable<List<MovieBean>> getSavedMoviesMapObservable() {
        return Observable.just(MoviesContract.Movies.CONTENT_URI).map(new Func1<Uri, List<MovieBean>>() {
            @Override
            public List<MovieBean> call(Uri uri) {
                Log.d(TAG, "running getSavedMoviesMapObservable on " + Thread.currentThread().getName());
                final Cursor cursor = getContext()
                        .getContentResolver()
                        .query(uri, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    try {
                        List<MovieBean> lstMovies = new ArrayList<>();
                        while (cursor.moveToNext()) {
                            MovieBean movieBean = new MovieBean();
                            movieBean.setId(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_ID)));
                            movieBean.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_ORIGINAL_TITLE)));
                            movieBean.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_OVERVIEW)));
                            movieBean.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_POSTER_PATH)));
                            movieBean.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_RELEASE_DATE)));
                            movieBean.setTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_TITLE)));
                            movieBean.setVoteAverage(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_VOTE_AVERAGE)));
                            movieBean.setVoteCount(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_VOTE_COUNT)));
                            lstMovies.add(movieBean);
                        }
                        return lstMovies;
                    } finally {
                        cursor.close();
                    }
                } else {
                    return Collections.emptyList();
                }
            }
        });
    }

    private Observable<List<MovieBean>> getSavedMoviesObservable() {
        return Observable.defer(new Func0<Observable<List<MovieBean>>>() {
            @Override
            public Observable<List<MovieBean>> call() {
                return Observable.just(getSavedMovies());
            }
        });
    }

    private List<MovieBean> getSavedMovies() {
        Log.d(TAG, "running getSavedMovies on " + Thread.currentThread().getName());
        final Cursor cursor = getContext()
                .getContentResolver()
                .query(MoviesContract.Movies.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            try {
                List<MovieBean> lstMovies = new ArrayList<>();
                while (cursor.moveToNext()) {
                    MovieBean movieBean = new MovieBean();
                    movieBean.setId(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_ID)));
                    movieBean.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_ORIGINAL_TITLE)));
                    movieBean.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_OVERVIEW)));
                    movieBean.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_POSTER_PATH)));
                    movieBean.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_RELEASE_DATE)));
                    movieBean.setTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_TITLE)));
                    movieBean.setVoteAverage(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_VOTE_AVERAGE)));
                    movieBean.setVoteCount(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesColumns.MOVIE_VOTE_COUNT)));
                    lstMovies.add(movieBean);
                }
                return lstMovies;
            } finally {
                cursor.close();
            }
        } else {
            return Collections.emptyList();
        }
    }

    private void getMovies(String moviesSortOrder) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.THE_MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MoviesApi moviesApi = retrofit.create(MoviesApi.class);
        Call<MovieBean.Response> moviesCall = null;
        if (moviesSortOrder.equalsIgnoreCase("favorites")) {
            getSavedMoviesObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<MovieBean>>() {
                        @Override
                        public void call(List<MovieBean> savedMovies) {
                            mSavedMovies = savedMovies;
                            mMoviesAdapter = new MoviesAdapter(recyclerView.getContext(), mSavedMovies);
                            mMoviesAdapter.setMovieClickedCallback(mCallback);
                            recyclerView.setAdapter(mMoviesAdapter);
                            mCallback.onMoviesLoaded();
                        }
                    });
        } else if (moviesSortOrder.equalsIgnoreCase("Most popular")) {
            moviesCall = moviesApi.popularMovies();
        } else {
            moviesCall = moviesApi.topRatedMovies();
        }

        if (moviesCall == null) {
            return;
        }
        moviesCall.enqueue(new Callback<MovieBean.Response>() {
            @Override
            public void onResponse(Call<MovieBean.Response> call, Response<MovieBean.Response> response) {
                MovieBean.Response body = response.body();
                if (null == body) {
                    Log.d(TAG, "Empty response body.");
                    return;
                }
                mSavedMovies = body.lstMovies;
                if (mSavedMovies == null) {
                    Log.d(TAG, "Empty movies list");
                    return;
                }

                mMoviesAdapter = new MoviesAdapter(recyclerView.getContext(), mSavedMovies);
                mMoviesAdapter.setMovieClickedCallback(mCallback);
                recyclerView.setAdapter(mMoviesAdapter);
                mCallback.onMoviesLoaded();
            }

            @Override
            public void onFailure(Call<MovieBean.Response> call, Throwable t) {

            }
        });
    }

    public MoviesAdapter getMoviesAdapter() {
        return mMoviesAdapter;
    }

    @Override
    public void onConnected() {
        loadMovies();
    }

    public interface CallbackMovieClicked {
        void onMovieClicked(MovieBean movieBean);

        void onMoviesLoaded();
    }

}
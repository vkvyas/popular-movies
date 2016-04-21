package com.example.popularmovies.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.popularmovies.data.provider.MoviesContract.*;
import static com.example.popularmovies.data.provider.MoviesContract.CONTENT_AUTHORITY;
import static com.example.popularmovies.data.provider.MoviesContract.Movies;
import static com.example.popularmovies.data.provider.MoviesDatabase.Tables;

/**
 * Created by vishalvyas on 4/21/16.
 * Movies content provider
 */
public class MoviesProvider extends ContentProvider {

    private MoviesDatabase mOpenHelper;
    private SQLiteDatabase mSqLiteDatabase;

    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static final int CODE_MOVIES = 200;
    private static final int CODE_MOVIES_ID = 201;

    private static final String TAG = "MoviesProvider";


    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, "movies", CODE_MOVIES);
        uriMatcher.addURI(CONTENT_AUTHORITY, "movies/*", CODE_MOVIES_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDatabase(getContext());
        mSqLiteDatabase = mOpenHelper.getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, uri.toString());
        final int code = sUriMatcher.match(uri);
        switch (code) {
            case CODE_MOVIES:
                return mSqLiteDatabase.query(Tables.MOVIES, projection, selection, selectionArgs, null, null, sortOrder);
            case CODE_MOVIES_ID:
                return mSqLiteDatabase.query(Tables.MOVIES, projection, Movies.MOVIE_ID + "=?", new String[]{Movies.getMovieId(uri)}, null, null, sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int code = sUriMatcher.match(uri);
        switch (code) {
            case CODE_MOVIES:
                return Movies.CONTENT_TYPE;
            case CODE_MOVIES_ID:
                return Movies.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, uri.toString() + ", values=" + values.toString());
        final int code = sUriMatcher.match(uri);
        switch (code) {
            case CODE_MOVIES:
            case CODE_MOVIES_ID:
                String selection = Movies.MOVIE_ID + "=?";
                String selectionArgs[] = new String[]{Movies.getMovieId(uri)};

                // UPDATE if the movie already exists in database. If not then INSERT
                final Cursor cursor = query(uri, new String[]{MoviesColumns.MOVIE_ID}, selection, selectionArgs, null);
                if (cursor != null && cursor.getCount() > 0) {
                    Log.d(TAG, "Movie exists. Updating");
                    final int update = update(uri, values, selection, selectionArgs);
                    Log.d(TAG, "Movie exists. Rows affected " + update);
                } else {
                    final long id = mSqLiteDatabase.insertOrThrow(Tables.MOVIES, null, values);
                }
                notifyChange(uri);
                return Movies.buildMovieUri(values.getAsString(Movies.MOVIE_ID));
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int delete = mSqLiteDatabase.delete(Tables.MOVIES
                , Movies.MOVIE_ID + "=?"
                , new String[]{Movies.getMovieId(uri)});
        notifyChange(uri);
        return delete;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return mSqLiteDatabase.update(Tables.MOVIES, values, selection, selectionArgs);
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
}

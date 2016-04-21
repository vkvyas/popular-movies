package com.example.popularmovies.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

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
        return null;
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
                mSqLiteDatabase.insertOrThrow(Tables.MOVIES, null, values);
                notifyChange(uri);
                return Movies.buildMovieUri(values.getAsString(Movies.MOVIE_ID));
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int delete = mSqLiteDatabase.delete(Tables.MOVIES
                , "WHERE " + Movies.MOVIE_ID + "=?"
                , new String[]{Movies.getMovieId(uri)});
        notifyChange(uri);
        return delete;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
}

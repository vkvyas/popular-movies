package com.example.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static final String TAG = "PM_TAG";
    private StringBuilder apiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(
                R.id.recyclerView);
        recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String moviesSortOrder = prefs.getString("movies_sort_order", null);
        apiUrl = new StringBuilder();
        apiUrl.append("https://api.themoviedb.org/3");
        if (TextUtils.isEmpty(moviesSortOrder) || moviesSortOrder.equalsIgnoreCase("Most popular")) {
            apiUrl.append("/movie/popular");
        } else {
            apiUrl.append("/movie/top_rated");
        }
        apiUrl.append("?api_key=b6267c089cade0eccdb1732984aa12b9");
        new FetchMoviesTask(recyclerView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, apiUrl.toString());
        Log.d(TAG, apiUrl.toString());
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

    class FetchMoviesTask extends AsyncTask<String, String, List<MovieBean>> {

        RecyclerView recyclerView;
        OkHttpClient okHttpClient;

        public FetchMoviesTask(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        protected List<MovieBean> doInBackground(String... params) {
            try {
                okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseStr = response.body().string();
                List<MovieBean> movies = MoviesDataParser.getMovies(responseStr);
                return movies;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        }

        @Override
        protected void onPostExecute(List<MovieBean> movies) {
            recyclerView.setAdapter(new MoviesAdapter(recyclerView.getContext(), movies));
        }
    }
}
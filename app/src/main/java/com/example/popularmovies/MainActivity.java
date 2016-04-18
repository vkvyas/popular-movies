package com.example.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

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
        new FetchMoviesTask(recyclerView).execute((Void[]) null);
    }

    class FetchMoviesTask extends AsyncTask<Void, String, List<MovieBean>> {

        RecyclerView recyclerView;
        OkHttpClient okHttpClient;
        String url = "https://api.themoviedb.org/3/movie/popular?api_key=b6267c089cade0eccdb1732984aa12b9";

        public FetchMoviesTask(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        protected List<MovieBean> doInBackground(Void... params) {
            try {
                okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
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
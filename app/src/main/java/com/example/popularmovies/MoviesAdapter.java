package com.example.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.popularmovies.fragments.MoviesFragment;
import com.example.popularmovies.models.MovieBean;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vishalvyas on 4/18/16.
 */

public class MoviesAdapter extends RecyclerView.Adapter<ImageViewHolder> {

    public static String HTTP_BASE_URL_FOR_IMAGES = "http://image.tmdb.org/t/p/w185/";

    private List<MovieBean> mLstMovies;
    private Context mContext;
    private MoviesFragment.CallbackMovieClicked mCallback;

    public MoviesAdapter(Context context, List<MovieBean> lstMovies) {
        mLstMovies = lstMovies;
        mContext = context;
    }

    public void setMovieClickedCallback(MoviesFragment.CallbackMovieClicked callback) {
        this.mCallback = callback;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final MovieBean movieBean = mLstMovies.get(position);
        Picasso.with(mContext)
                .load(HTTP_BASE_URL_FOR_IMAGES + movieBean.getPosterPath())
                .into(holder.imageView);
        holder.imageView.setTag(movieBean);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    MovieBean movieBean = (MovieBean) v.getTag();
                    mCallback.onMovieClicked(movieBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLstMovies.size();
    }

    public MovieBean getItem(int position) {
        return mLstMovies.get(position);
    }
}

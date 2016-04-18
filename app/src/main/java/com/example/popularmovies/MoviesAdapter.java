package com.example.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vishalvyas on 4/18/16.
 */

public class MoviesAdapter extends RecyclerView.Adapter<ImageViewHolder> {

    public static String HTTP_BASE_URL_FOR_IMAGES = "http://image.tmdb.org/t/p/w185/";

    private List<MovieBean> mLstMovies;
    private Context mContext;

    public MoviesAdapter(Context context, List<MovieBean> lstMovies) {
        mLstMovies = lstMovies;
        mContext = context;
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
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(MovieDetailsActivity.MOVIE_PARCEL, movieBean);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLstMovies.size();
    }
}

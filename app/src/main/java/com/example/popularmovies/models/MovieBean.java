package com.example.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishalvyas on 4/18/16.
 */
public class MovieBean implements Parcelable {
    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("title")
    private String title;
    @Expose
    @SerializedName("poster_path")
    private String posterPath;
    @Expose
    @SerializedName("release_date")
    private String releaseDate;
    @Expose
    @SerializedName("vote_count")
    private String voteCount;
    @Expose
    @SerializedName("vote_average")
    private String voteAverage;
    @Expose
    @SerializedName("original_title")
    private String originalTitle;
    @Expose
    @SerializedName("overview")
    private String overview;

    public MovieBean() {
    }

    public MovieBean(Parcel in) {
        id = in.readString();
        title = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        voteCount = in.readString();
        voteAverage = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeString(voteCount);
        dest.writeString(voteAverage);
        dest.writeString(originalTitle);
        dest.writeString(overview);
    }

    static final Parcelable.Creator<MovieBean> CREATOR
            = new Parcelable.Creator<MovieBean>() {

        public MovieBean createFromParcel(Parcel in) {
            return new MovieBean(in);
        }

        public MovieBean[] newArray(int size) {
            return new MovieBean[size];
        }
    };

    public static final class Response {

        @Expose
        public int page;

        @Expose
        @SerializedName("total_pages")
        public int totalPages;

        @Expose
        @SerializedName("total_results")
        public int totalMovies;

        @Expose
        @SerializedName("results")
        public List<MovieBean> lstMovies = new ArrayList<>();
    }

}

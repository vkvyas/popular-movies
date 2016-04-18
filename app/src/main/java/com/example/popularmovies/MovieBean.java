package com.example.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vishalvyas on 4/18/16.
 */
public class MovieBean implements Parcelable {
    private String id;
    private String title;
    private String posterPath;
    private String releaseDate;
    private String voteCount;
    private String voteAverage;
    private String originalTitle;
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

}

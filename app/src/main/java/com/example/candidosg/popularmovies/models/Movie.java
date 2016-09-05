package com.example.candidosg.popularmovies.models;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by candidosg on 20/07/16.
 */
@Parcel
public class Movie {

    long id;
    String originalTitle;
    String originalLanguage;
    String posterPath;
    String backdropPath;
    String overview;
    String releaseDate;
    String voteAverage;
    List<MovieReview> movieReviewList;
    List<MovieVideo> movieVideoList;

    public Movie() {

    }

    public Movie(long id, String originalTitle, String originalLanguage, String posterPath, String backdropPath, String overview, String releaseDate, String voteAverage, List<MovieReview> movieReviewList, List<MovieVideo> movieVideoList) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.movieReviewList = movieReviewList;
        this.movieVideoList = movieVideoList;

    }

    public Float getVoteAverage() {
        return ( Float.parseFloat(voteAverage) * (float) 0.5 );
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterUrlPath() {
        return "http://image.tmdb.org/t/p/w185/" + this.posterPath;
    }

    public List<MovieReview> getMovieReviewList() {
        return movieReviewList;
    }

    public void setMovieReviewList(List<MovieReview> movieReviewList) {
        this.movieReviewList = movieReviewList;
    }

    public List<MovieVideo> getMovieVideoList() {
        return movieVideoList;
    }

    public void setMovieVideoList(List<MovieVideo> movieVideoList) {
        this.movieVideoList = movieVideoList;
    }
}
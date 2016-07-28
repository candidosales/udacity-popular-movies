package com.example.candidosg.popularmovies;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by candidosg on 20/07/16.
 */
@Parcel
public class Movie {

    String id;
    String originalTitle;
    String originalLanguage;
    String posterPath;
    String backdropPath;
    String overview;
    String releaseDate;
    String voteAverage;

    public Movie() {

    }

    public Movie(String id, String originalTitle, String originalLanguage, String posterPath, String backdropPath, String overview, String releaseDate, String voteAverage) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;

    }

    public Float getVoteAverage() {
        return ( Float.parseFloat(voteAverage) * (float) 0.5 );
    }

    public String getId() {
        return id;
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

}
package com.example.candidosg.popularmovies;

import org.parceler.Parcel;

/**
 * Created by candidosg on 20/07/16.
 */
@Parcel
public class Movie {

    private String id;
    private String originalTitle;
    private String originalLanguage;
    private String posterPath;
    private String backdropPath;
    private String overview;
    private String releaseDate;

    public Movie() {
    }

    public Movie(String id, String originalTitle, String originalLanguage, String posterPath, String backdropPath, String overview, String releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
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
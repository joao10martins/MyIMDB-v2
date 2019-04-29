package com.example.myimdb.model.realm;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MovieRealm extends RealmObject {

    @PrimaryKey
    private int id;
    private int vote_count;
    private double vote_average;
    private String title;
    private String poster_path;
    //private RealmList<Integer> genre_ids;
    private String backdrop_path;
    private String overview;
    private String release_date;

    private String genresDescription;

    public MovieRealm(){}

    public MovieRealm(
            int vote_count,
            int id,
            double vote_average,
            String title,
            String poster_path,
            //RealmList<Integer> genre_ids,
            String backdrop_path,
            String overview,
            String release_date
    ) {
        this.vote_count = vote_count;
        this.id = id;
        this.vote_average = vote_average;
        this.title = title;
        this.poster_path = poster_path;
        //this.genre_ids = genre_ids;
        this.backdrop_path = backdrop_path;
        this.overview = overview;
        this.release_date = release_date;
    }

    public String getGenresDescription() { return genresDescription; }

    public void setGenresDescription(String genresDescription) { this.genresDescription = genresDescription; }

    public int getVote_count() { return vote_count; }

    public void setVote_count(int vote_count) { this.vote_count = vote_count; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public double getVote_average() { return vote_average; }

    public void setVote_average(double vote_average) { this.vote_average = vote_average; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getPoster_path() { return poster_path; }

    public void setPoster_path(String poster_path) { this.poster_path = poster_path; }

    //public RealmList<Integer> getGenre_ids() { return genre_ids; }

    //public void setGenre_ids(RealmList<Integer> genre_ids) { this.genre_ids = genre_ids; }

    public String getBackdrop_path() { return backdrop_path; }

    public void setBackdrop_path(String backdrop_path) { this.backdrop_path = backdrop_path; }

    public String getOverview() { return overview; }

    public void setOverview(String overview) { this.overview = overview; }

    public String getRelease_date() { return release_date; }

    public void setRelease_date(String release_date) { this.release_date = release_date; }
}


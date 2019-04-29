package com.example.myimdb.model.response;

import java.util.List;

public class SearchMovie {
    private int id;
    private String title;
    private String poster_path;
    private String release_date;
    private double vote_average;
    private List<Integer> genre_ids;


    private String genresDescription;

    public SearchMovie(int id, String title, String poster_path, String release_date, List<Integer> genre_ids, double vote_average){
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.genre_ids = genre_ids;
        this.vote_average = vote_average;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getGenresDescription() {
        return genresDescription;
    }

    public void setGenresDescription(String genresDescription) {
        this.genresDescription = genresDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(List<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }
}

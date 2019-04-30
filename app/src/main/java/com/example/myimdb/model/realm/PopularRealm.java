package com.example.myimdb.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class PopularRealm extends RealmObject {

    private int id;
    private double vote_average;
    private String title;
    private double popularity;
    private String poster_path;
    private RealmList<Integer> genre_ids; // maybe change to Details


    public PopularRealm(){}


    public PopularRealm(int id, double vote_average, String title, double popularity, String poster_path, RealmList<Integer> genre_ids) {
        this.id = id;
        this.vote_average = vote_average;
        this.title = title;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.genre_ids = genre_ids;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public RealmList<Integer> getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(RealmList<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }
}
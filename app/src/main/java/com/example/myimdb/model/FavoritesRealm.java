package com.example.myimdb.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FavoritesRealm extends RealmObject {

    @PrimaryKey
    private int id;
    private double vote_average;
    private String title;
    private String poster_path;
    private boolean like;


    public FavoritesRealm(){}

    public FavoritesRealm(
            int id,
            double vote_average,
            String title,
            String poster_path,
            boolean like
    ) {
        this.id = id;
        this.vote_average = vote_average;
        this.title = title;
        this.poster_path = poster_path;
        this.like = like;
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

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}

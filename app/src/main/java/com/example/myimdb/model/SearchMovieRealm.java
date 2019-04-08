package com.example.myimdb.model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class SearchMovieRealm extends RealmObject {
    private int id;
    private String title;
    private String poster_path;
    private String release_date;
    private RealmList<Integer> genre_ids;


    private String genresDescription;

    public SearchMovieRealm(){

    }

    public SearchMovieRealm(int id, String title, String poster_path, String release_date, RealmList<Integer> genre_ids){
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.genre_ids = genre_ids;
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

    public RealmList<Integer> getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(RealmList<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }
}

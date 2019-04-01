package com.example.myimdb.model;

import com.example.myimdb.model.MovieGenre;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class MovieGenreResults extends RealmObject {

    @SerializedName("genres")
    public RealmList<MovieGenre> genreList;

}

package com.example.myimdb.model;

import com.example.myimdb.model.Movie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class MovieResults extends RealmObject {

    @SerializedName("results")
    public RealmList<Movie> movieList;
}

package com.example.myimdb.model;

import com.example.myimdb.model.Movie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResults {

    @SerializedName("results")
    public List<Movie> movieList;
}

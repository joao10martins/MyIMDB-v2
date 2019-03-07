package com.example.myimdb;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResults {

    @SerializedName("results")
    public List<Movie> movieList;
}

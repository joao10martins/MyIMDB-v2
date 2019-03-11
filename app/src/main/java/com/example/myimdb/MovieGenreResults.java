package com.example.myimdb;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieGenreResults {

    @SerializedName("genres")
    public List<MovieGenre> genreList;

}

package com.example.myimdb.model;

import com.example.myimdb.model.MovieGenre;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieGenreResults {

    @SerializedName("genres")
    public List<MovieGenre> genreList;

}

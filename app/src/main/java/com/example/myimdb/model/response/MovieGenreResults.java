package com.example.myimdb.model.response;

import com.example.myimdb.model.response.MovieGenre;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieGenreResults {

    @SerializedName("genres")
    public List<MovieGenre> genreList;

}

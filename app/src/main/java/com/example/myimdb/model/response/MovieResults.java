package com.example.myimdb.model.response;

import com.example.myimdb.model.response.Movie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResults {

    @SerializedName("results")
    public List<Movie> movieList;
    @SerializedName("total_pages")
    public int totalPages;

    public int getTotal_pages() {
        return totalPages;
    }

    public void setTotal_pages(int totalPages) {
        this.totalPages = totalPages;
    }
}

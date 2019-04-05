package com.example.myimdb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchMovieResults {
    @SerializedName("results")
    public List<SearchMovie> searchMovieList;
    @SerializedName("total_pages")
    public int totalPages;


    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}

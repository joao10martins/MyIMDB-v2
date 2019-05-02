package com.example.myimdb.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpcomingResults {

    @SerializedName("results")
    public List<Upcoming> upcomingrMovieList;
    @SerializedName("total_pages")
    public int totalPages;

    public int getTotal_pages() {
        return totalPages;
    }

    public void setTotal_pages(int totalPages) {
        this.totalPages = totalPages;
    }
}

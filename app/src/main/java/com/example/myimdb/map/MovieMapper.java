package com.example.myimdb.map;

import com.example.myimdb.model.Movie;
import com.example.myimdb.model.MovieRealm;

import java.util.ArrayList;
import java.util.List;

public class MovieMapper {
    List<MovieRealm> myMovieRealmList = new ArrayList<>();

    List<MovieRealm> toMovieRealmList(List<Movie> responseList){
        for (Movie movie : responseList){
            MovieRealm myRealm = new MovieRealm(
                    movie.getVote_count(),
                    movie.getId(),
                    movie.getVote_average(),
                    movie.getTitle(),
                    movie.getPoster_path(),
                    movie.getGenre_ids(),
                    movie.getBackdrop_path(),
                    movie.getOverview(),
                    movie.getRelease_date());
            myMovieRealmList.add(myRealm);
        }
        return myMovieRealmList;
    }
}


package com.example.myimdb.map;

import com.example.myimdb.model.Movie;
import com.example.myimdb.model.MovieRealm;
import com.example.myimdb.model.SearchMovie;

import java.util.ArrayList;
import java.util.List;

public class MovieMapper {
    List<MovieRealm> myMovieRealmList = new ArrayList<>();
    List<SearchMovieRealm> mySearchRealmList = new ArrayList<>();

    public List<MovieRealm> toMovieRealmList(List<Movie> responseList){
        for (Movie movie : responseList){
            MovieRealm myMovieRealm = new MovieRealm(
                    movie.getVote_count(),
                    movie.getId(),
                    movie.getVote_average(),
                    movie.getTitle(),
                    movie.getPoster_path(),
                    //movie.getGenre_ids(), //error here (types)
                    movie.getBackdrop_path(),
                    movie.getOverview(),
                    movie.getRelease_date());
            myMovieRealmList.add(myMovieRealm);
        }
        return myMovieRealmList;
    }


    // TODO:
    // create funtion to parse SearchMovie to SearchMovieRealm(need to create RealmModels)
    public List<SearchMovieRealm> toSearchRealmList(List<SearchMovie> responseList){
        for (SearchMovie movie : responseList){
            SearchMovieRealm mySearchRealm = new SearchMovieRealm(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getPoster_path(),
                    //movie.getGenre_ids(), //error here (types)
                    movie.getRelease_date(),
                    movie.getGenresDescription());
            mySearchRealmList.add(mySearchRealm);
        }
        return mySearchRealmList;
    }
}


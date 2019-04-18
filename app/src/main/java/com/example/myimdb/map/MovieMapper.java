
package com.example.myimdb.map;

import com.example.myimdb.model.FavoritesRealm;
import com.example.myimdb.model.Movie;
import com.example.myimdb.model.MovieDetails;
import com.example.myimdb.model.MovieGenreRealm;
import com.example.myimdb.model.MovieRealm;
import com.example.myimdb.model.SearchMovie;
import com.example.myimdb.model.SearchMovieRealm;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class MovieMapper {
    List<MovieRealm> myMovieRealmList = new ArrayList<>();
    List<SearchMovieRealm> mySearchRealmList = new ArrayList<>();

    //RealmList<Integer> movieGenres = new RealmList<>();
    Realm mRealm = Realm.getDefaultInstance();


    public List<MovieRealm> toMovieRealmList(List<Movie> responseList){
        for (Movie movie : responseList){
            MovieRealm myMovieRealm = new MovieRealm(
                    movie.getVote_count(),
                    movie.getId(),
                    movie.getVote_average(),
                    movie.getTitle(),
                    movie.getPoster_path(),
                    //movie.getGenre_ids(), //error here (types) //TODO
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
        /*RealmList<Integer> genres = new RealmList<>();
        for(MovieGenreRealm genre : mRealm.where(MovieGenreRealm.class).findAllAsync()) {
            genres.add(genre.getId());
        }*/

        for (SearchMovie searchMovie : responseList){
                List<Integer> genres = searchMovie.getGenre_ids();

                SearchMovieRealm mySearchRealm = new SearchMovieRealm(
                        searchMovie.getId(),
                        searchMovie.getTitle(),
                        searchMovie.getPoster_path(),
                        //movie.getGenre_ids(), //error here (types)
                        searchMovie.getRelease_date(),
                        searchMovie.getGenresDescription());
                mySearchRealmList.add(mySearchRealm);
            }
        return mySearchRealmList;
    }



    public RealmList<Integer> intListToIntRealmList(List<Integer> list){
        RealmList<Integer> movieGenres = new RealmList<>();
        movieGenres.addAll(list);
        return movieGenres;
    }

    public FavoritesRealm toFavoritesRealmList(MovieDetails movie, boolean like) {


        FavoritesRealm myFavoritesRealm = new FavoritesRealm(
                movie.getId(),
                movie.getVote_average(),
                movie.getOriginal_title(),
                movie.getPoster_path(),
                like);
        return myFavoritesRealm;
    }
}


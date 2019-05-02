
package com.example.myimdb.map;

import com.example.myimdb.model.realm.FavoritesRealm;
import com.example.myimdb.model.realm.PopularRealm;
import com.example.myimdb.model.realm.TopRatedRealm;
import com.example.myimdb.model.realm.UpcomingRealm;
import com.example.myimdb.model.response.Movie;
import com.example.myimdb.model.response.MovieDetails;
import com.example.myimdb.model.realm.MovieRealm;
import com.example.myimdb.model.response.Popular;
import com.example.myimdb.model.response.SearchMovie;
import com.example.myimdb.model.realm.SearchMovieRealm;
import com.example.myimdb.model.response.TopRated;
import com.example.myimdb.model.response.Upcoming;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class MovieMapper {
    List<MovieRealm> myMovieRealmList = new ArrayList<>();
    List<PopularRealm> myPopularRealmList = new ArrayList<>();
    List<TopRatedRealm> myTopRatedRealmList = new ArrayList<>();
    List<UpcomingRealm> myUpcomingRealmList = new ArrayList<>();
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



    public List<PopularRealm> toPopularRealmList(List<Popular> responseList){
        for (Popular movie : responseList){
            PopularRealm myPopularRealm = new PopularRealm(
                    movie.getId(),
                    movie.getVote_average(),
                    movie.getTitle(),
                    movie.getPopularity(),
                    movie.getPoster_path(),
                    //movie.getGenre_ids(), //error here (types) //TODO
                    movie.getRelease_date());
            myPopularRealmList.add(myPopularRealm);
        }
        return myPopularRealmList;
    }


    public List<TopRatedRealm> toTopRatedRealmList(List<TopRated> responseList){
        for (TopRated movie : responseList){
            TopRatedRealm myTopRatedRealm = new TopRatedRealm(
                    movie.getId(),
                    movie.getVote_average(),
                    movie.getTitle(),
                    movie.getPopularity(),
                    movie.getPoster_path(),
                    //movie.getGenre_ids(), //error here (types) //TODO
                    movie.getRelease_date());
            myTopRatedRealmList.add(myTopRatedRealm);
        }
        return myTopRatedRealmList;
    }



    public List<UpcomingRealm> toUpcomingRealmList(List<Upcoming> responseList){
        for (Upcoming movie : responseList){
            UpcomingRealm myUpcomingRealm = new UpcomingRealm(
                    movie.getId(),
                    movie.getVote_average(),
                    movie.getTitle(),
                    movie.getPopularity(),
                    movie.getPoster_path(),
                    //movie.getGenre_ids(), //error here (types) //TODO
                    movie.getRelease_date());
            myUpcomingRealmList.add(myUpcomingRealm);
        }
        return myUpcomingRealmList;
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
                        searchMovie.getGenresDescription(),
                        searchMovie.getVote_average());
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
                movie.getPopularity(),
                movie.getRelease_date(),
                like);
        return myFavoritesRealm;
    }
}


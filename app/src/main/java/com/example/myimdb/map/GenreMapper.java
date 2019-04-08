package com.example.myimdb.map;

import com.example.myimdb.model.Movie;
import com.example.myimdb.model.MovieGenre;
import com.example.myimdb.model.MovieGenreRealm;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.realm.RealmResults;

public class GenreMapper {
    List<MovieGenreRealm> myGenreRealmList = new ArrayList<>();
    HashMap<Integer, MovieGenreRealm> genreRealmHashMap = new HashMap<>();

    public List<MovieGenreRealm> toGenreRealmList(List<MovieGenre> responseList){
        for (MovieGenre genre : responseList){
            MovieGenreRealm myMovieGenreRealm = new MovieGenreRealm(
                    genre.getId(),
                    genre.getName());
            myGenreRealmList.add(myMovieGenreRealm);
        }
        return myGenreRealmList;
    }


    public HashMap<Integer, MovieGenreRealm> toGenreRealmMap(RealmResults<MovieGenreRealm> genreRealmList){
        for (MovieGenreRealm realmGenre : genreRealmList) {
            genreRealmHashMap.put(realmGenre.getId(), realmGenre);
        }
        return genreRealmHashMap;

        /*Iterator it = genreRealmList.entrySet().iterator();
        while (it.hasNext()){
            HashMap.Entry pair = (HashMap.Entry) it.next();
            HashMap<Integer, MovieGenreRealm> myMovieGenreRealmMap = new HashMap<>(
                    pair.getKey().hashCode(), ((float) pair.getValue().hashCode()));
            genreRealmHashMap.put(myMovieGenreRealmMap);
        }
        return genreRealmHashMap;*/
    }
}

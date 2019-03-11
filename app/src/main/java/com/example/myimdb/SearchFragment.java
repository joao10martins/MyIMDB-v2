package com.example.myimdb;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private View mView;
    private RequestQueue mRequestQueue;
    private String mUrl;
    private RecyclerView mRecyclerView;
    private List<MovieGenre> mGenreList = new ArrayList<>();
    private HashMap<Integer , MovieGenre> mGenreMap = new HashMap<>();
    private SearchRecyclerAdapter mAdapter;


    private String sharedPrefsFile = "com.example.myimdb";
    private SharedPreferences mPreferences;
    private List<Movie> mMovieList = new ArrayList<>();


    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        // Initialize the list of Genres.
        getGenreList();
        // Save the list of Genre to SharedPrefs
        /*mPreferences = this.getActivity().getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(sharedPrefsFile, MODE_PRIVATE).edit();
        for (MovieGenre genre : mGenreList){
            editor.putInt("id", genre.getId());
            editor.putString("name", genre.getName());
        }
        editor.apply();*/
        test();


        return mView;
    }


    // Get List of all the existing Genres in the API.
    private void getGenreList() {
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        mUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US";

        GsonRequest<MovieGenreResults> request = new GsonRequest<>(mUrl,
                MovieGenreResults.class,
                getGenreSuccessListener(),
                getGenreErrorListener());

        mRequestQueue.add(request);
    }


    private Response.Listener<MovieGenreResults> getGenreSuccessListener() {
        return new Response.Listener<MovieGenreResults>() {
            @Override
            public void onResponse(MovieGenreResults response) {
                try {

                    for (MovieGenre movieGenre : response.genreList) {
                        mGenreMap.put(movieGenre.getId(), movieGenre);
                    }

                    //mGenreList.addAll(response.genreList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }



    private Response.ErrorListener getGenreErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();
                error.printStackTrace();
            }
        };
    }



    // Test query
    private void test() {
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        mUrl = "https://api.themoviedb.org/3/search/movie?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US&query=the&page=1&include_adult=false";

        GsonRequest<MovieResults> request = new GsonRequest<>(mUrl,
                MovieResults.class,
                getMovieSuccessListener(),
                getGenreErrorListener());

        mRequestQueue.add(request);
    }


    private Response.Listener<MovieResults> getMovieSuccessListener() {
        return new Response.Listener<MovieResults>() {
            @Override
            public void onResponse(MovieResults response) {
                mRecyclerView = mView.findViewById(R.id.rvSearch);
                try {
                    mMovieList.addAll(response.movieList);
                    if (mAdapter == null) {

                        //TODO: iterar mMoviesList
                            // iterar genres_ids da API
                                //
                            //TODO: set genresDescription

                        mAdapter = new SearchRecyclerAdapter(getContext(), mMovieList);
                        mRecyclerView.setAdapter(mAdapter); // problemas aqui ao dar refresh(não mantém a posição do scroll)
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}

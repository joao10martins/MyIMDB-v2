package com.example.myimdb;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment {
    private ImageView mImageMovie;
    private TextView mTitleMovie;
    private List<Movie> nowPlayingList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private NowPlayingRecyclerAdapter mAdapter;

    private View mView;

    public NowPlayingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        // Views
        //mImageMovie = mView.findViewById(R.id.movie_img_id);
        //mTitleMovie = mView.findViewById(R.id.movie_title_id);


        getNowPlaying();




        return mView;
    }

    public static NowPlayingFragment newInstance() {
        return new NowPlayingFragment();
    }


    private void getNowPlaying(){

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        final String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US&page=1";


        GsonRequest<MovieResults> request = new GsonRequest<>(url,
                                                            MovieResults.class,
                                                            createMyReqSuccessListener(),
                                                            createMyReqErrorListener());

        requestQueue.add(request);


    }


    private Response.Listener<MovieResults> createMyReqSuccessListener() {
        return new Response.Listener<MovieResults>() {
            @Override
            public void onResponse(MovieResults response) {
                try {
                    nowPlayingList = response.movieList;
                    mRecyclerView = mView.findViewById(R.id.rv_NowPlaying);
                    mAdapter = new NowPlayingRecyclerAdapter(getContext(), nowPlayingList);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    //mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();
                error.printStackTrace();
            }
        };
    }


}



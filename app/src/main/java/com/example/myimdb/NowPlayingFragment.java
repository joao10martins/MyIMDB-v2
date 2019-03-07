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
    final List<Movie> NowPlaying = new ArrayList<>();

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
        mImageMovie = mView.findViewById(R.id.movie_img_id);
        mTitleMovie = mView.findViewById(R.id.movie_title_id);


        getNowPlaying();




        return mView;
    }

    public static NowPlayingFragment newInstance() {
        return new NowPlayingFragment();
    }


    private void getNowPlaying(){

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        final String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US&page=1";


        GsonRequest<Movie> request = new GsonRequest<Movie>(url,
                                                            Movie.class,
                                                            createMyReqSuccessListener(),
                                                            createMyReqErrorListener());
        /*JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray objArray = response.getJSONArray("results");
                            for (int i = 0; i < objArray.length(); i++) {
                                Movie movie = new Movie(
                                        objArray.getJSONObject(i).getInt("vote_count"),
                                        objArray.getJSONObject(i).getInt("id"),
                                        objArray.getJSONObject(i).getBoolean("video"),
                                        objArray.getJSONObject(i).getDouble("vote_average"),
                                        objArray.getJSONObject(i).getString("title"),
                                        objArray.getJSONObject(i).getDouble("popularity"),
                                        objArray.getJSONObject(i).getString("poster_path"),
                                        objArray.getJSONObject(i).getString("original_language"),
                                        objArray.getJSONObject(i).getString("original_title"),
                                        (List<Integer>) objArray.getJSONObject(i).get("genre_ids"),
                                        objArray.getJSONObject(i).getString("backdrop_path"),
                                        objArray.getJSONObject(i).getBoolean("adult"),
                                        objArray.getJSONObject(i).getString("overview"),
                                        objArray.getJSONObject(i).getString("release_date"));
                                NowPlaying.add(movie);
                                //NowPlaying.no
                                //notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });*/

        requestQueue.add(request);

        RecyclerView mRecyclerView = mView.findViewById(R.id.rv_NowPlaying);
        NowPlayingRecyclerAdapter mAdapter = new NowPlayingRecyclerAdapter(getContext(), NowPlaying);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    private Response.Listener<Movie> createMyReqSuccessListener() {
        return new Response.Listener<Movie>() {
            @Override
            public void onResponse(Movie response) {
                try {
                    NowPlaying.add(response);
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



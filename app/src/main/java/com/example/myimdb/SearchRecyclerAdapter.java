package com.example.myimdb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder> {
    private final List<Movie> mMovieList;
    private LayoutInflater mInflater;
    private Context context;


    private List<MovieGenre> mGenreList;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer , List<MovieGenre>> mGenreMap = new HashMap<>();

    //private NowPlayingListener nowPlayingListener;

    public SearchRecyclerAdapter(Context context,
                                     List<Movie> movieList) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mMovieList = movieList;
        //this.mGenreList = genreList;

        //this.nowPlayingListener = null;
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView genre;
        final ImageView movieImage;

        final SearchRecyclerAdapter mAdapter;

        public SearchViewHolder(View movieView, SearchRecyclerAdapter adapter) {
            super(movieView);
            title = movieView.findViewById(R.id.search_movie_title);
            genre = movieView.findViewById(R.id.search_movie_genre);
            movieImage = movieView.findViewById(R.id.movie_thumbnail);
            this.mAdapter = adapter;
        }
    }


    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mMovieView = mInflater.inflate(R.layout.cardview_search_item_result, parent, false);
        return new SearchViewHolder(mMovieView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        final Movie currentItem = mMovieList.get(position);
        String imagePath = "https://image.tmdb.org/t/p/original/" + currentItem.getPoster_path();
        StringBuilder sb = new StringBuilder(currentItem.getGenresDescription());
        sb.deleteCharAt(sb.length()-1); // Delete whitespace.
        sb.deleteCharAt(sb.length()-1); // Delete last comma.


        holder.title.setText(currentItem.getTitle());
        holder.genre.setText(sb);

        if (currentItem.getPoster_path() != null){
            Glide.with(context)
                    .load(imagePath)
                    .into(holder.movieImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_no_image_available)
                    .into(holder.movieImage);
        }



    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }




    /*// Get List of all the existing Genres in the API.
    private void getGenreList() {
        mRequestQueue = Volley.newRequestQueue(context);
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
                    mGenreList.addAll(response.genreList);
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
    }*/

}

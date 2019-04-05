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

import com.bumptech.glide.Glide;
import com.example.myimdb.model.Movie;
import com.example.myimdb.model.MovieGenre;
import com.example.myimdb.model.SearchMovie;

import java.util.HashMap;
import java.util.List;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder> {
    private final List<SearchMovie> mMovieList;
    private LayoutInflater mInflater;
    private Context context;

    private OnMovieClick mListener;

    private List<MovieGenre> mGenreList;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer , List<MovieGenre>> mGenreMap = new HashMap<>();

    //private NowPlayingListener nowPlayingListener;

    public SearchRecyclerAdapter(Context context,
                                     List<SearchMovie> movieList,
                                     OnMovieClick listener) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mMovieList = movieList;
        this.mListener = listener;

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

        final SearchMovie currentItem = mMovieList.get(position);
        String imagePath = "https://image.tmdb.org/t/p/original/" + currentItem.getPoster_path();


        holder.title.setText(currentItem.getTitle());
        holder.genre.setText(currentItem.getGenresDescription());

        if (currentItem.getPoster_path() != null){
            Glide.with(context)
                    .load(imagePath)
                    .into(holder.movieImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_no_image_available)
                    .into(holder.movieImage);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(currentItem.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }



    public interface OnMovieClick {
        void onItemClick(int movieId);
    }

}

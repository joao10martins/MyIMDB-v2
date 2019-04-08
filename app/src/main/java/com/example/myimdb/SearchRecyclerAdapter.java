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
import com.example.myimdb.model.MovieRealm;
import com.example.myimdb.model.SearchMovie;
import com.example.myimdb.model.SearchMovieRealm;

import java.util.HashMap;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class SearchRecyclerAdapter extends RealmRecyclerViewAdapter<SearchMovieRealm, SearchRecyclerAdapter.SearchViewHolder> {

    private LayoutInflater mInflater;
    private Context context;

    private OnMovieClick mListener;

    private List<MovieGenre> mGenreList;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer , List<MovieGenre>> mGenreMap = new HashMap<>();

    //private NowPlayingListener nowPlayingListener;

    SearchRecyclerAdapter(Context context,
                                 OrderedRealmCollection<SearchMovieRealm> data,
                                 OnMovieClick listener) {
        super(data, true);
        this.context = context;
        this.mListener = listener;
        setHasStableIds(false);
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView genre;
        final ImageView movieImage;
        public SearchMovieRealm data;

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
        View mMovieView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_search_item_result, parent, false);
        return new SearchViewHolder(mMovieView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        final SearchMovieRealm currentItem = getItem(position);
        holder.data = currentItem;
        final int currentItemId = currentItem.getId();
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




    public interface OnMovieClick {
        void onItemClick(int movieId);
    }

}

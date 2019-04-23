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

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;

public class SearchRecyclerAdapter extends RealmRecyclerViewAdapter<SearchMovieRealm, SearchRecyclerAdapter.SearchViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    Realm mRealm;

    private OnMovieClick mListener;
    private boolean isMovieViewAsList;

    private List<MovieGenre> mGenreList;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer , List<MovieGenre>> mGenreMap = new HashMap<>();

    //private NowPlayingListener nowPlayingListener;

    SearchRecyclerAdapter(Context context,
                          OrderedRealmCollection<SearchMovieRealm> data,
                          OnMovieClick listener,
                          boolean isMovieViewAsList) {
        super(data, true);
        this.context = context;
        this.mListener = listener;
        this.isMovieViewAsList = isMovieViewAsList;
        setHasStableIds(false);
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        TextView genre;
        public TextView movieYear;
        public ImageView movieImage;
        public SearchMovieRealm data;

        final SearchRecyclerAdapter mAdapter;

        public SearchViewHolder(View movieView, SearchRecyclerAdapter adapter) {
            super(movieView);
            if (isMovieViewAsList){
                title = movieView.findViewById(R.id.search_grid_layout_title);
                movieImage = movieView.findViewById(R.id.search_grid_layout_img);
                movieYear = movieView.findViewById(R.id.search_grid_layout_year);
            } else {
                title = movieView.findViewById(R.id.search_movie_title);
                genre = movieView.findViewById(R.id.search_movie_genre); //TODO: NPE? Mudei de final para public(wtf)
                movieImage = movieView.findViewById(R.id.movie_thumbnail);
            }
            this.mAdapter = adapter;
        }
    }


    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mMovieView = LayoutInflater.from(parent.getContext()).inflate(isMovieViewAsList ? R.layout.search_grid_layout : R.layout.cardview_search_item_result, parent, false);
        return new SearchViewHolder(mMovieView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        final SearchMovieRealm currentItem = getItem(position);
        holder.data = currentItem;
        final int currentItemId = currentItem.getId();
        String imagePath = "https://image.tmdb.org/t/p/original/" + currentItem.getPoster_path();


        holder.title.setText(currentItem.getTitle());
        if (isMovieViewAsList){
            String year = currentItem.getRelease_date().substring(0, Math.min(currentItem.getRelease_date().length(), 4));
            if (year.equals("")){
                holder.movieYear.setText("N/A");
            } else {
                holder.movieYear.setText(year);
            }

        } else {
            holder.genre.setText(currentItem.getGenresDescription());
        }


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
                mListener.onItemClick(currentItem.getId(), currentItem.getTitle());
            }
        });

    }



    public interface OnMovieClick {
        void onItemClick(int movieId, String title);
    }

}

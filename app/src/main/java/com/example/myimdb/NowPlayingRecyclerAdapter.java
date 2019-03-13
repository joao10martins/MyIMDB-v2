package com.example.myimdb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NowPlayingRecyclerAdapter extends RecyclerView.Adapter<NowPlayingRecyclerAdapter.NowPlayingViewHolder>  {
    private final List<Movie> mMovieList;
    private LayoutInflater mInflater;
    private Context context;

    private OnMovieClick mListener;

    //private NowPlayingListener nowPlayingListener;

    public NowPlayingRecyclerAdapter(Context context,
                                     List<Movie> movieList,
                                     OnMovieClick listener) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mMovieList = movieList;
        this.mListener = listener;
        //this.nowPlayingListener = null;
    }

    class NowPlayingViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final ImageView movieImage;

        final NowPlayingRecyclerAdapter mAdapter;

        public NowPlayingViewHolder(View movieView, NowPlayingRecyclerAdapter adapter) {
            super(movieView);
            title = movieView.findViewById(R.id.movie_title_id);
            movieImage = movieView.findViewById(R.id.movie_img_id);
            this.mAdapter = adapter;
        }
    }


    @NonNull
    @Override
    public NowPlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mMovieView = mInflater.inflate(R.layout.cardview_item_movie, parent, false);
        return new NowPlayingViewHolder(mMovieView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull NowPlayingViewHolder holder, int position) {

        final Movie currentItem = mMovieList.get(position);
        holder.title.setText(currentItem.getTitle());
        String imagePath = "https://image.tmdb.org/t/p/original/" + currentItem.getPoster_path();


        if (currentItem.getPoster_path() != null){
            Glide.with(context)
                    .load(imagePath)
                    .into(holder.movieImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_no_image_available)
                    .into(holder.movieImage);
        }


        //holder.movieImage.setTag(currentItem.getId());
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

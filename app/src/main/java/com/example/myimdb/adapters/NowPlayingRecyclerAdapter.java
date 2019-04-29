package com.example.myimdb.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myimdb.R;
import com.example.myimdb.model.realm.MovieRealm;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class NowPlayingRecyclerAdapter extends RealmRecyclerViewAdapter<MovieRealm, NowPlayingRecyclerAdapter.NowPlayingViewHolder>  {


    //private final RealmList<MovieRealm> mMovieList;
    private LayoutInflater mInflater;
    private Context context;
    private boolean isMovieViewAsList;
    private OnMovieClick mListener;

    //private NowPlayingListener nowPlayingListener;

    public NowPlayingRecyclerAdapter(OrderedRealmCollection<MovieRealm> data,
                                     Context context,
                                     OnMovieClick listener,
                                     boolean isMovieViewAsList) {
        super(data, true);
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        this.context = context;
        this.isMovieViewAsList = isMovieViewAsList;
        /*mInflater = LayoutInflater.from(context);
        this.mMovieList = movieList;*/
        this.mListener = listener;
        setHasStableIds(false);
    }


    /*public NowPlayingRecyclerAdapter(Context context,
                                     List<Movie> movieList,
                                     OnMovieClick listener) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mMovieList = movieList;
        this.mListener = listener;
        //this.nowPlayingListener = null;
    }*/

    class NowPlayingViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final ImageView movieImage;
        public TextView movieYear;
        public TextView genre;
        public MovieRealm data;

        final NowPlayingRecyclerAdapter mAdapter;

        public NowPlayingViewHolder(View movieView, NowPlayingRecyclerAdapter adapter) {
            super(movieView);
            if (isMovieViewAsList){
                title = movieView.findViewById(R.id.search_movie_title);
                movieImage = movieView.findViewById(R.id.movie_thumbnail);
                genre = movieView.findViewById(R.id.search_movie_genre);
            } else {
                title = movieView.findViewById(R.id.np_movie_title);
                movieImage = movieView.findViewById(R.id.np_movie_img);
                movieYear = movieView.findViewById(R.id.np_movie_year);
            }


            this.mAdapter = adapter;
        }
    }


    @NonNull
    @Override
    public NowPlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //test
        View mMovieView = LayoutInflater.from(parent.getContext()).inflate(isMovieViewAsList ? R.layout.cardview_search_item_result : R.layout.cardview_item_movie, parent, false);
        //View mMovieView = mInflater.inflate(R.layout.cardview_item_movie, parent, false);
        return new NowPlayingViewHolder(mMovieView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull NowPlayingViewHolder holder, int position) {

        final MovieRealm currentItem = getItem(position);
        holder.data = currentItem;
        final int currentItemId = currentItem.getId();
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

        String year = currentItem.getRelease_date().substring(0, Math.min(currentItem.getRelease_date().length(), 4));
        if (isMovieViewAsList){
            holder.genre.setText(currentItem.getGenresDescription());
        } else {
            holder.movieYear.setText(year);
        }



        //holder.movieImage.setTag(currentItem.getId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mListener.onItemClick(currentItem.getId(), currentItem.getTitle());
            }
        });


    }



    /*@Override
    public int getItemCount() {
        return getData().size();
    }*/


    public interface OnMovieClick {
        void onItemClick(int movieId, String title);
    }




}

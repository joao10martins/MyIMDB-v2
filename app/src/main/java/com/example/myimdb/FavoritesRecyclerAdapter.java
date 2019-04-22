package com.example.myimdb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myimdb.model.FavoritesRealm;
import com.example.myimdb.model.Movie;
import com.example.myimdb.model.MovieRealm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class FavoritesRecyclerAdapter extends RealmRecyclerViewAdapter<FavoritesRealm, FavoritesRecyclerAdapter.FavoritesViewHolder>  {


    private LayoutInflater mInflater;
    private Context context;
    private boolean isMovieViewAsList;
    private OnMovieClick mListener;
    private Realm mRealm;
    private boolean isChecked = false;
    private boolean isCancel = false;
    private boolean isRemove = false;


    FavoritesRecyclerAdapter(OrderedRealmCollection<FavoritesRealm> data,
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
        this.mListener = listener;
        this.mRealm = Realm.getDefaultInstance();
        setHasStableIds(false);
    }


    class FavoritesViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final ImageView movieImage;
        public ImageView like;
        public TextView rating;
        public FavoritesRealm data;

        final FavoritesRecyclerAdapter mAdapter;

        public FavoritesViewHolder(View movieView, FavoritesRecyclerAdapter adapter) {
            super(movieView);
            if (isMovieViewAsList){
                title = movieView.findViewById(R.id.search_movie_title);
                movieImage = movieView.findViewById(R.id.movie_thumbnail);
                rating = movieView.findViewById(R.id.search_movie_genre);
                //TODO: list layout for favorites
            } else {
                title = movieView.findViewById(R.id.fav_grid_layout_title);
                movieImage = movieView.findViewById(R.id.fav_grid_layout_img);
                rating = movieView.findViewById(R.id.fav_grid_layout_rating);
                like = movieView.findViewById(R.id.favorites_like);
            }


            this.mAdapter = adapter;
        }
    }


    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //test
        View mMovieView = LayoutInflater.from(parent.getContext()).inflate(isMovieViewAsList ? R.layout.cardview_search_item_result : R.layout.favorites_grid_layout, parent, false);
        return new FavoritesViewHolder(mMovieView, this);
    }


    @Override
    public void onBindViewHolder(@NonNull final FavoritesViewHolder holder, int position) {

        final FavoritesRealm currentItem = getItem(position);
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

        holder.rating.setText(String.valueOf(currentItem.getVote_average()));
        /*if (currentItem.isLike()) { //

        }*/
        holder.like.setImageResource(R.drawable.ic_favorite_24dp);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isChecked && isRemove){
                    mRealm.executeTransaction(new Realm.Transaction() { // Delete selected movie from the Favorites.
                        @Override
                        public void execute(Realm realm) {
                            // adapter.remove() -> not supported by RealmResults or OrderedRealmCollection
                            currentItem.deleteFromRealm();
                            notifyDataSetChanged();
                        }
                    });
                }

                if (!isChecked || (isRemove && !isChecked) || (isChecked && isCancel)){
                    isCancel = false;
                    isChecked = false;
                    isRemove = false;
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_favorites_alert_dialog, (ViewGroup) v.getRootView(), false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setCancelable(true);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    // dismiss
                    final Button cancel = dialogView.findViewById(R.id.buttonCancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isCancel = !isCancel;
                            alertDialog.dismiss();
                        }
                    });


                    // remove
                    // TODO: update recyclerview after delete(preferably with animation)
                    Button remove = dialogView.findViewById(R.id.buttonRemove);
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isRemove = !isRemove;
                            mRealm.executeTransaction(new Realm.Transaction() { // Delete selected movie from the Favorites.
                                @Override
                                public void execute(Realm realm) {
                                    // adapter.remove() -> not supported by RealmResults or OrderedRealmCollection
                                    currentItem.deleteFromRealm();
                                    notifyDataSetChanged();
                                    alertDialog.dismiss();
                                }
                            });
                        }
                    });


                    CheckBox checkBox = dialogView.findViewById(R.id.alertDialog_checkbox);
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isChecked = !isChecked;
                        }
                    });
                }

            }
        });


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
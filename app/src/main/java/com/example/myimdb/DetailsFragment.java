package com.example.myimdb;


import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.myimdb.map.MovieMapper;
import com.example.myimdb.model.realm.FavoritesRealm;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import xyz.hanks.library.bang.SmallBangView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private View mView;

    // Declaring views
    private ImageView mBackpathImage;
    private ImageView mPosterpathImage;
    private ImageView mLike;
    private SmallBangView mSmallBangLike;
    private TextView mOriginalTitle;
    private TextView mRating;
    private TextView mVoteCount;
    private TextView mReleaseDate;
    private TextView mDuration;
    private TextView mOverview;
    private Toolbar mToolbar;
    private boolean isLiked = false;
    private String mSessionId;

    /* Realm */
    Realm mRealm;


    List<FavoritesRealm> favorites = new ArrayList<>();
    RealmList<FavoritesRealm> _favorites = new RealmList<>();
    boolean isDetailsFromSearch = false;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_details, container, false);
        setHasOptionsMenu(false);
        mRealm = Realm.getDefaultInstance();
        // Views
        mBackpathImage = mView.findViewById(R.id.details_movie_background_img);
        mPosterpathImage = mView.findViewById(R.id.details_movie_thumbnail);
        mSmallBangLike = mView.findViewById(R.id.like_heart);
        mLike = mView.findViewById(R.id.details_like);
        mOriginalTitle = mView.findViewById(R.id.details_title);
        mRating = mView.findViewById(R.id.txt_vote_average);
        mVoteCount = mView.findViewById(R.id.txt_number_of_votes);
        mReleaseDate = mView.findViewById(R.id.txt_release_date);
        mDuration = mView.findViewById(R.id.txt_duration);
        mOverview = mView.findViewById(R.id.txt_overview);



        // Get bundle which contains the response data
        final Bundle bundle = getArguments();
        // Set the View's images.
        String imageBackpath = "https://image.tmdb.org/t/p/original/" + bundle.getString("backdrop_path"); // Background Image.
        if (bundle.getString("backdrop_path") != null){
            Glide.with(getActivity().getApplicationContext())
                    .load(imageBackpath)
                    .into(mBackpathImage);
        } else {
            Glide.with(getActivity().getApplicationContext())
                    .load(R.drawable.ic_no_image_available)
                    .into(mBackpathImage);
        }

        String imagePoster = "https://image.tmdb.org/t/p/original/" + bundle.getString("poster_path"); // Thumbnail Image.
        if (bundle.getString("poster_path") != null){
            Glide.with(getActivity().getApplicationContext())
                    .load(imagePoster)
                    .into(mPosterpathImage);
        } else {
            Glide.with(getActivity().getApplicationContext())
                    .load(R.drawable.ic_no_image_available)
                    .into(mPosterpathImage);
        }


        // Set the View's info.
        mOriginalTitle.setText(bundle.getString("original_title"));
        //mToolbar.setTitle(bundle.getString("original_title"));
        // String formatters can be an alternative to the '/10' label
        // and to the usages of 'String.valueOf()'
        if (bundle.getDouble("vote_average") == 0) {
            mRating.setText("N/A");
        } else {
            mRating.setText(String.valueOf(bundle.getDouble("vote_average")));
        }


        if (bundle.getInt("vote_count") == 0) {
            mVoteCount.setText("N/A");
        } else {
            mVoteCount.setText(String.valueOf(bundle.getInt("vote_count")));
        }


        if (bundle.getString("release_date").equals("")) {
            mReleaseDate.setText("N/A");
        } else {
            mReleaseDate.setText(bundle.getString("release_date"));
        }


        // Formatting the duration layout
        if (bundle.getInt("runtime") == 0) {
            mDuration.setText("N/A");
        } else {
            int hours = bundle.getInt("runtime") / 60;
            String mins = String.valueOf(bundle.getInt("runtime") % 60);
            mins = mins.length() == 1 ? "0" + mins : mins;  // if the mins has length == 1(i.e  4mins, add '0' behind it)
            String formattedDuration = hours + "h" + mins + "min";
            mDuration.setText(formattedDuration);
        }


        if (bundle.getString("overview").equals("")) {
            mOverview.setText("No overview available for display.");
        } else {
            mOverview.setText(bundle.getString("overview"));
            mOverview.setMovementMethod(new ScrollingMovementMethod());
        }



        // If the movie exists in the Favorites database, set the Like image to "LIKED".
        RealmResults<FavoritesRealm> results = mRealm.where(FavoritesRealm.class)
                .equalTo("id", bundle.getInt("id")).findAll();
        if (results.size() != 0){
            mLike.setImageResource(R.drawable.ic_favorite_24dp);
            isLiked = true;
        }

        mSmallBangLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if true -> set false | if false -> set true.
                isLiked = !isLiked;
                if (isLiked) {
                    saveMovieToFavoritesDb(bundle, isLiked);
                    mSmallBangLike.setSelected(true);
                    mSmallBangLike.likeAnimation();

                } else {
                    //remove from db.
                    mSmallBangLike.setSelected(false);
                    mSmallBangLike.likeAnimation();
                    //mLike.setImageResource(R.drawable.ic_favorites_selector);
                    mRealm.executeTransactionAsync(new Realm.Transaction() { // Delete selected movie from the Favorites.
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<FavoritesRealm> results = realm.where(FavoritesRealm.class).equalTo("id", bundle.getInt("id")).findAll();
                            results.deleteAllFromRealm();
                        }
                    });
                }
            }
        });

        return mView;
    }





    private void saveMovieToFavoritesDb(final Bundle movie, final boolean isLiked){
        // TEST
        final MovieMapper movieMapper = new MovieMapper();

        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    /*FavoritesRealm favorite = movieMapper.toFavoritesRealmList(movie, isLiked);
                    favorites.add(favorite);
                    _favorites.add(favorites);*/
                    FavoritesRealm favorite = new FavoritesRealm(
                            movie.getInt("id"),
                            movie.getDouble("vote_average"),
                            movie.getString("original_title"),
                            movie.getString("poster_path"),
                            movie.getDouble("popularity"),
                            movie.getString("release_date"),
                            isLiked);
                    _favorites.add(favorite);
                    realm.insertOrUpdate(_favorites);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // GREAT SUCCESS (•̀ᴗ•́)و ̑̑
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    // sad reactions only
                }
            });
        } catch (Exception e) {
            // Wow such exception
            e.printStackTrace();
        } finally {
            // Wow such finally
        }
    }




}

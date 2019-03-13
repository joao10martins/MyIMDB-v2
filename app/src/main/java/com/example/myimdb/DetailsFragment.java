package com.example.myimdb;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private View mView;

    // Declaring views
    private ImageView mBackpathImage;
    private ImageView mPosterpathImage;
    private TextView mOriginalTitle;
    private TextView mRating;
    private TextView mVoteCount;
    private TextView mReleaseDate;
    private TextView mDuration;
    private TextView mOverview;

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
        // Views
        mBackpathImage = mView.findViewById(R.id.details_movie_background_img);
        mPosterpathImage = mView.findViewById(R.id.details_movie_thumbnail);
        mOriginalTitle = mView.findViewById(R.id.details_title);
        mRating = mView.findViewById(R.id.txt_vote_average);
        mVoteCount = mView.findViewById(R.id.txt_number_of_votes);
        mReleaseDate = mView.findViewById(R.id.txt_release_date);
        mDuration = mView.findViewById(R.id.txt_duration);
        mOverview = mView.findViewById(R.id.txt_overview);


        // Get bundle which contains the response data
        Bundle bundle = getArguments();
        // Set the View's images.
        String imageBackpath = "https://image.tmdb.org/t/p/original/" + bundle.getString("backdrop_path"); // Background Image.
        if (imageBackpath != null){
            Glide.with(getActivity().getApplicationContext())
                    .load(imageBackpath)
                    .into(mBackpathImage);
        } else {
            Glide.with(getActivity().getApplicationContext())
                    .load(R.drawable.ic_no_image_available)
                    .into(mBackpathImage);
        }

        String imagePoster = "https://image.tmdb.org/t/p/original/" + bundle.getString("poster_path"); // Thumbnail Image.
        if (imagePoster != null){
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
        // String formatters can be an alternative to the '/10' label
        // and to the usages of 'String.valueOf()'
        mRating.setText(String.valueOf(bundle.getDouble("vote_average")));
        mVoteCount.setText(String.valueOf(bundle.getInt("vote_count")));
        mReleaseDate.setText(bundle.getString("release_date"));
        // Formatting the duration layout
        int hours = bundle.getInt("runtime") / 60;
        String mins = String.valueOf(bundle.getInt("runtime") % 60);
        mins = mins.length() == 1 ? "0" + mins : mins;  // if the mins has length == 1(i.e  4mins, add '0' behind it)
        String formattedDuration = hours + "h" + mins + "min";
        mDuration.setText(formattedDuration);
        mOverview.setText(bundle.getString("overview"));
        mOverview.setMovementMethod(new ScrollingMovementMethod());

        return mView;
    }

}

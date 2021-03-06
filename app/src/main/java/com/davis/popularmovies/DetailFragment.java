package com.davis.popularmovies;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.davis.popularmovies.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailFragment extends Fragment {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private String title;
    private String description;
    private String releaseDate;
    private String rating;

    private String imagePath;

    JSONObject object;

//    private ShareActionProvider mShareActionProvider;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // fragments don't like constructors... why?
    public void initFragment(JSONObject object) throws Exception {

        this.object = object;

        final String TITLE = "title";
        final String DESCRIPTION = "overview";
        final String RELEASE_DATE = "release_date";
        final String RATING = "vote_average";

        if (object != null) {
            try {
                title = object.getString(TITLE);
                description = object.getString(DESCRIPTION);
                releaseDate = object.getString(RELEASE_DATE);
                rating = object.getString(RATING);

                imagePath = "https://image.tmdb.org/t/p/w185" + object.getString("poster_path");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_detail, container, false);
        fragmentView.setClickable(true);

        final TextView movieTitleText = (TextView) fragmentView.findViewById(R.id.movie_title_text);
        movieTitleText.setText(title);

        final TextView movieDescInfoText = (TextView) fragmentView.findViewById(R.id.movie_desc_info_text);
        movieDescInfoText.setText(description);

        final TextView movieRatingText = (TextView) fragmentView.findViewById(R.id.movie_rating_text);
        movieRatingText.setText(rating);

        final TextView movieReleaseText = (TextView) fragmentView.findViewById(R.id.movie_release_date_text);
        movieReleaseText.setText(releaseDate);

        final ImageView posterImage = (ImageView) fragmentView.findViewById(R.id.posterImageView);
        Picasso.with(getActivity().getApplicationContext()).load(imagePath).into(posterImage);

        final ImageView favStar = (ImageView) fragmentView.findViewById(R.id.favStar);

        final Button b = (Button) fragmentView.findViewById(R.id.favoriteButton);

        final DBAdapter dbAdapter = new DBAdapter(getActivity().getApplicationContext());

        if (dbAdapter.findMovie(object)) {
            // if movie is already a favorite
            b.setText(R.string.unfavorite);
            favStar.setVisibility(View.VISIBLE);
        } else {
            // if movie is not a favorite
            b.setText(R.string.favorite);
            favStar.setVisibility(View.INVISIBLE);
        }

        b.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (favStar.isShown()) {
                    dbAdapter.deleteMovie(object);
                    b.setText(R.string.favorite);
                    favStar.setVisibility(View.INVISIBLE);
                    MovieEntry.numOfFavoritedItems--;
                } else {
                    dbAdapter.addMovie(object);
                    b.setText(R.string.unfavorite);
                    favStar.setVisibility(View.VISIBLE);
                    MovieEntry.numOfFavoritedItems++;
                }
            }

        });

        FetchTrailersTask ftt = new FetchTrailersTask(fragmentView);
        try {
            ftt.execute(object.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FetchReviewsTask frr = new FetchReviewsTask(fragmentView);
        try {
            frr.execute(object.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (screenSize < Configuration.SCREENLAYOUT_SIZE_LARGE) {
            menu.findItem(R.id.sortBy).setVisible(false);
            menu.findItem(R.id.favorites).setVisible(false);
        }
    }
}

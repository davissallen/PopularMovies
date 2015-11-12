package com.davis.popularmovies;

import android.app.Fragment;
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

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // fragments don't like constructors
    public void initFragment(JSONObject object) throws Exception {

        this. object = object;

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

        final TextView movieReleaseTExt = (TextView) fragmentView.findViewById(R.id.movie_release_date_text);
        movieReleaseTExt.setText(releaseDate);

        final ImageView posterImage = (ImageView) fragmentView.findViewById(R.id.posterImageView);
        Picasso.with(getActivity().getApplicationContext()).load(imagePath).into(posterImage);

        final ImageView favStar = (ImageView) fragmentView.findViewById(R.id.favStar);

        final Button b = (Button) fragmentView.findViewById(R.id.favoriteButton);
        if (FavoriteMovies.isFavorite(object) == -1) {
            b.setText("Favorite");
            favStar.setVisibility(View.INVISIBLE);
        }
        else {
            b.setText("un-Favorite");
            favStar.setVisibility(View.VISIBLE);
        }

        b.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int position = FavoriteMovies.isFavorite(object);

                if (position != -1) {
                    FavoriteMovies.removeFavoriteMovie(getActivity().getApplicationContext(), position);
                    b.setText("Favorite");
                    favStar.setVisibility(View.INVISIBLE);
                } else {
                    FavoriteMovies.addFavoriteMovie(getActivity().getApplicationContext(), object);
                    b.setText("un-Favorite");
                    favStar.setVisibility(View.VISIBLE);
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

        menu.findItem(R.id.sort).setVisible(false);
    }
}

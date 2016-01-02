package com.davis.popularmovies;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
            b.setText("un-Favorite");
            favStar.setVisibility(View.VISIBLE);
        } else {
            // if movie is not a favorite
            b.setText("Favorite");
            favStar.setVisibility(View.INVISIBLE);
        }

        b.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (favStar.isShown()) {
                    // remove from favorites list
                    SQLiteDatabase db = dbAdapter.getWritableDatabase();
                    String selection = MovieEntry.COLUMN_NAME_MOVIES_JSON + "= ?";
                    String[] selectionArgs = {object.toString()};
                    db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);

                    // update id numbers
                    String[] selectionArgsInt = {"0"};
                    String[] columns = {MovieEntry.COLUMN_NAME_ENTRY_ID};
                    Cursor cursor = db.query(MovieEntry.TABLE_NAME, columns, MovieEntry.COLUMN_NAME_ENTRY_ID + " = ?",
                            selectionArgsInt, null, null, null);
                    cursor.moveToFirst();
                    int deletedRow = cursor.getInt(0);

                    ContentValues values = new ContentValues();
                    values.put(MovieEntry.COLUMN_NAME_ENTRY_ID, Integer.toString(cursor.getInt(0) - 1));
                    String where = MovieEntry.COLUMN_NAME_ENTRY_ID + " > ";
                    String[] whereArgs = { Integer.toString(deletedRow) };
                    db.update(MovieEntry.TABLE_NAME, values, where, whereArgs);
                    db.close();
                    cursor.close();

                    b.setText("Favorite");
                    favStar.setVisibility(View.INVISIBLE);
                    MovieEntry.numOfFavoritedItems--;
                } else {
                    // need to add to favorites list
                    SQLiteDatabase db = dbAdapter.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(MovieEntry.COLUMN_NAME_ENTRY_ID, MovieEntry.numOfFavoritedItems);
                    values.put(MovieEntry.COLUMN_NAME_MOVIES_JSON, object.toString());

                    db.insert(MovieEntry.TABLE_NAME, null, values);
                    db.close();

                    b.setText("un-Favorite");
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

        menu.findItem(R.id.sort).setVisible(false);
    }
}

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
import android.widget.LinearLayout;
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

//    public class FetchReviewsTask extends AsyncTask<String, Void, JSONObject> {
//
//        public FetchReviewsTask() {
//
//        }
//
//        @Override
//        protected JSONObject doInBackground(String... params) {
//
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            JSONObject reviewObject;
//
//            final String myKey = "fc49cd59ea3b93d645752f06ab70ca50";
//
//            try {
//
//                // http://api.themoviedb.org/3/review/id?api_key=fc49cd59ea3b93d645752f06ab70ca50
//
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("http")
//                        .authority("api.themoviedb.org")
//                        .appendPath("3")
//                        .appendPath("review")
//                        .appendPath(params[0])
//                        .appendQueryParameter("api_key", myKey)
//                        .build();
//
//                URL url = new URL(builder.toString());
//
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                InputStream inputStream = urlConnection.getInputStream();
//
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    return null;
//                }
//
//                String trailerJSONString = buffer.toString();
//
//                reviewObject = new JSONObject(trailerJSONString);
//
//            } catch (Exception e) {
//                Log.e(LOG_TAG, "Could not connect to the API ", e);
//                reviewObject = null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final Exception e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//            return reviewObject;
//        }
//
//        @Override
//        protected void onPostExecute(JSONObject jsonObject) {
//
//            try {
//                int length = jsonObject.getJSONArray("results").length();
//
//                for (int i = 0; i < length; i++) {
//                    trailerPaths[i] = jsonObject.getJSONArray("results").getJSONObject(i).getString("key");
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            LinearLayout trailerButtonLayout = (LinearLayout) getView().findViewById(R.id.trailerButtonLayout);
//
//            trailerButtonLayout.setWeightSum(trailerPaths.length);
//
//            Button[] trailerButtons = new Button[trailerPaths.length];
//
//            for (int i = 0; i < trailerPaths.length; i++) {
//                trailerButtons[i] = new Button(getActivity().getApplicationContext());
//                trailerButtons[i].setText("Trailer " + (i + 1));
//
//                trailerButtonLayout.addView(trailerButtons[i]);
//
//                final int finalI = i;
//                trailerButtons[i].setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        youtubeInit(finalI);
//                    }
//                });
//            }
//
//
//        }
//    }

//    private void youtubeInit(int trailerID) {
//        String path = "http://www.youtube.com/watch?v=";
//
//        try {
//            switch (trailerID) {
//                case (0):
//                    path += trailerPaths[0];
//                    break;
//                case (1):
//                    path += trailerPaths[1];
//                    break;
//                case (2):
//                    path += trailerPaths[2];
//                    break;
//                default:
//                    break;
//            }
//        } catch (Exception e) {
//            Log.e(LOG_TAG, "Incorrect trailer path");
//        }
//
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(path)));
//    }

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

        LinearLayout trailerButtonLayout = (LinearLayout) fragmentView.findViewById(R.id.trailerButtonLayout);

        FetchTrailerTask ftt = new FetchTrailerTask(trailerButtonLayout);
        try {
            ftt.execute(object.getInt("id"));
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

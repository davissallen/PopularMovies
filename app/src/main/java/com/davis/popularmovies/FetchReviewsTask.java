package com.davis.popularmovies;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchReviewsTask extends AsyncTask<Integer, Void, JSONObject> {

    private final String LOG_TAG = getClass().getSimpleName();

    private final LinearLayout reviewsLinearLayout;
    private final TextView reviewHeaderTextView;

    public FetchReviewsTask(View view) {
        reviewsLinearLayout = (LinearLayout) view.findViewById(R.id.reviews_linear_layout);
        reviewHeaderTextView = (TextView) view.findViewById(R.id.reviews);
    }

    @Override
    protected JSONObject doInBackground(Integer... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        JSONObject reviewObject;

        final String myKey = "fc49cd59ea3b93d645752f06ab70ca50";

        try {

            // http://api.themoviedb.org/3/movies/id/reviews?api_key=fc49cd59ea3b93d645752f06ab70ca50

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(Integer.toString(params[0])) // id
                    .appendPath("reviews")
                    .appendQueryParameter("api_key", myKey)
                    .build();

            URL url = new URL(builder.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            String reviewsJSONString = buffer.toString();

            reviewObject = new JSONObject(reviewsJSONString);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not connect to the API ", e);
            reviewObject = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final Exception e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return reviewObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        int length;

        TextView reviewTextView;
        String reviewToShow;

        TextView authorTextView;
        String authorToShow;

        try {
            length = jsonObject.getJSONArray("results").length();

            if (length == 0) {
                reviewHeaderTextView.setVisibility(View.INVISIBLE);
            } else {
                reviewHeaderTextView.setVisibility(View.VISIBLE);

                String[] reviewsArray = new String[length];
                String[] authorsArray = new String[length];

                for (int i = 0; i < length; i++) {
                    reviewsArray[i] = jsonObject.getJSONArray("results").getJSONObject(i).getString("content");
                    authorsArray[i] = jsonObject.getJSONArray("results").getJSONObject(i).getString("author");

                    authorTextView = new TextView(App.context());
                    authorToShow = "By " + authorsArray[i] + ":";
                    authorTextView.setText(authorToShow);
                    authorTextView.setTextColor(Color.BLACK);
                    authorTextView.setPadding(0, 20, 5, 0);

                    reviewTextView = new TextView(App.context());
                    reviewToShow = "\"" + reviewsArray[i] + "\"";
                    reviewTextView.setText(reviewToShow);
                    reviewTextView.setTextColor(Color.GRAY);
                    reviewTextView.setPadding(30, 0, 0, 0);

                    reviewsLinearLayout.addView(authorTextView);
                    reviewsLinearLayout.addView(reviewTextView);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
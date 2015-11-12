package com.davis.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchMoviesTask extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private View view;

    public FetchMoviesTask(View view) {
        this.view = view;
    }

    @Override
    protected String doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJSONString = null;

        final String myKey = "fc49cd59ea3b93d645752f06ab70ca50";

        // inputted sort query
        String sortBy = params[0];

        try {

            // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=fc49cd59ea3b93d645752f06ab70ca50

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("sort_by", sortBy)
                    .appendQueryParameter("api_key", myKey)
                    .build();

            URL url = new URL(builder.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
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
                // Stream was empty.  No point in parsing.
                return null;
            }

            movieJSONString = buffer.toString();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not connect to the API ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            // if website DNE, quit
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

            return movieJSONString;

        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(jsonArray, R.id.sortByPopularity));
    }
}
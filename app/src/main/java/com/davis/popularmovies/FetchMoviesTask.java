package com.davis.popularmovies;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import org.json.JSONArray;
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

    int screenSize;

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
            screenSize = Integer.parseInt(params[1]);
        } catch (Exception e) {
            // oh well
        }

        try {

            // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=********

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

        } catch (Exception e) {
            Log.e(LOG_TAG, "No string returned from API call to TMDB", e);
            e.printStackTrace();
            ShowToast.showToast("No Internet Connection :(");
        }

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(jsonArray, R.id.sortByPopularity));

        // if the device is big, fill out detail fragment
        if (screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            gridview.performItemClick(gridview, 0, 0);
        }
    }
}
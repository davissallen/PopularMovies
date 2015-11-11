package com.davis.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchTrailerTask extends AsyncTask<Integer, Void, JSONObject> {

    private final String LOG_TAG = getClass().getSimpleName();

    private LinearLayout trailerButtonLayout;
    String[] trailerPaths;


    public FetchTrailerTask(View view) {
        trailerButtonLayout = (LinearLayout) view;
    }

    @Override
    protected JSONObject doInBackground(Integer... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        JSONObject trailerObject;

        final String myKey = "fc49cd59ea3b93d645752f06ab70ca50";

        try {

            // http://api.themoviedb.org/3/movie/76341/videos?api_key=fc49cd59ea3b93d645752f06ab70ca50

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(Integer.toString(params[0]))
                    .appendPath("videos")
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

            String movieJSONString = buffer.toString();

            trailerObject = new JSONObject(movieJSONString);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not connect to the API ", e);
            trailerObject = null;
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
        return trailerObject;
    }


    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        try {
            int length = jsonObject.getJSONArray("results").length();

            trailerPaths = new String[length];

            for (int i = 0; i < length; i++) {
                trailerPaths[i] = jsonObject.getJSONArray("results").getJSONObject(i).getString("key");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            trailerPaths = null;
        }

        trailerButtonLayout.setWeightSum(trailerPaths.length);

        Button[] trailerButtons = new Button[trailerPaths.length];

        for (int i = 0; i < trailerPaths.length; i++) {
            trailerButtons[i] = new Button(trailerButtonLayout.getContext());
            trailerButtons[i].setText("Trailer " + (i + 1));

            trailerButtonLayout.addView(trailerButtons[i]);

            final int finalI = i;
            trailerButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    youtubeInit(finalI);
                }
            });
        }
    }

    private void youtubeInit(int trailerID) {
        String path = "http://www.youtube.com/watch?v=";

        try {
            switch (trailerID) {
                case (0):
                    path += trailerPaths[0];
                    break;
                case (1):
                    path += trailerPaths[1];
                    break;
                case (2):
                    path += trailerPaths[2];
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Incorrect trailer path");
        }

        trailerButtonLayout.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(path)));
    }
}


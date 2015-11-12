package com.davis.popularmovies;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FavoriteMovies {
    private static String LOG_TAG = "FavoriteMovies.java";

    // initiated outside of constructor so that all objects share one data source
    private static JSONArray favoriteMoviesArray = new JSONArray();
    private static int numItemsInQueue = 0;

    public static int getNumItemsInQueue() {
        return numItemsInQueue;
    }

    public static int isFavorite(JSONObject jsonObject) {
        int movieID;
        for(int i = 0; i < numItemsInQueue; i++) {
            try {
                movieID = jsonObject.getInt("id");

                // if the movie exists in the favorites queue return position of movie
                if (movieID == favoriteMoviesArray.getJSONObject(i).getInt("id")) {
                    return i;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "error in parsing through favorites array");
            }
        }
        return -1;
    }

    public static void addFavoriteMovie(Context context, JSONObject jsonObject) {
        CharSequence text;

        favoriteMoviesArray.put(jsonObject);
        numItemsInQueue++;
        text = "Movie added to Favorites!";

        ShowToast.showToast(text);
    }

    public static void removeFavoriteMovie(Context context, int position) {

        try {
            favoriteMoviesArray.put(position, favoriteMoviesArray.get(numItemsInQueue - 1));

            // don't want to .remove() because it is only supported in api level 19 or higher (late 2013)
            // how else do i do this?
            // because i need to adjust the jsonarray.length ... hmm (will work on later)!
            // feature that i desire but not as important right now
            favoriteMoviesArray.remove(numItemsInQueue-1);

            numItemsInQueue--;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CharSequence text = "Movie removed from Favorites List";
        ShowToast.showToast(text);
    }

    public static JSONArray getFavoriteMoviesArray() {
        return favoriteMoviesArray;
    }

}
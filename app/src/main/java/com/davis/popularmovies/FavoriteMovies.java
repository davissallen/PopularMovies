package com.davis.popularmovies;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class FavoriteMovies {
    private static String LOG_TAG = "FavoriteMovies.java";

    // up to 30 favorite movies, high arbitrary number...
    // initiated outside of constructor so that all objects share one data source
    private static JSONObject[] favoriteMoviesArray = new JSONObject[30];
    private static int numItemsInQueue = 0;

    public static int isFavorite(JSONObject jsonObject) {
        int movieID;
        for(int i = 0; i < numItemsInQueue; i++) {
            try {
                movieID = jsonObject.getInt("id");

                // if the movie exists in the favorites queue return position of movie
                if (movieID == favoriteMoviesArray[i].getInt("id")) {
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

        if (numItemsInQueue < 30) {
            favoriteMoviesArray[numItemsInQueue] = jsonObject;
            numItemsInQueue++;
            text = "Movie added to Favorites!";
        }
        else {
            text = "No more room in favorites list!";
        }

        ShowToast.showToast(context, text);
    }

    public static void removeFavoriteMovie(Context context, int position) {
        favoriteMoviesArray[position] = favoriteMoviesArray[numItemsInQueue-1];
        numItemsInQueue--;

        CharSequence text = "Movie removed from Favorites List";
        ShowToast.showToast(context, text);
    }

    public static JSONObject[] getFavoriteMoviesArray() {
        return favoriteMoviesArray;
    }

}
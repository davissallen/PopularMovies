package com.davis.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class DBAdapter extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Favorite_Movies.db";
    private static final String TABLE_NAME = "Favorite_Movies_Table";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MOVIE_JSON = "movie_json";

    public DBAdapter(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table of 3 columns: id, movie title, movie json data
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_MOVIE_JSON + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addMovie(JSONObject movieJSON) {
        String movieStr = movieJSON.toString();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_JSON, movieStr);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public JSONObject findMovie(JSONObject movieJSON) {
        String movieStr = movieJSON.toString();
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_MOVIE_JSON + " =  \"" + movieStr + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        JSONObject movie = null;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            try { movie = new JSONObject(cursor.getString(0)); }
            catch (JSONException e) { e.printStackTrace(); }

            cursor.close();
        } else {
            movie = null;
        }
        db.close();
        return movie;
    }

    public boolean deleteMovie(JSONObject movieJSON) {

        boolean result = false;

        String str = movieJSON.toString();
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_MOVIE_JSON + " =  \"" + str + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Movie movie = new Movie();

        if (cursor.moveToFirst()) {
            movie.setID(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(movie.getID()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
}
package com.davis.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.davis.popularmovies.MovieContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBAdapter extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Favorite_Movies.db";

    private final String CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "("
            + MovieEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY,"
            + MovieEntry.COLUMN_NAME_MOVIES_JSON + " TEXT" + ")";

    public DBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }

    public boolean findMovie(JSONObject movieJSON) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + MovieEntry.TABLE_NAME + " WHERE " +
                MovieEntry.COLUMN_NAME_MOVIES_JSON + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{movieJSON.toString()});

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            return true;
        }


    }

    public JSONArray getMovies() {
        String query = "SELECT " + MovieEntry.COLUMN_NAME_MOVIES_JSON + " FROM " + MovieEntry.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToNext();
        String s = cursor.getString(0);

        cursor.close();

        return new JSONArray();
    }
}
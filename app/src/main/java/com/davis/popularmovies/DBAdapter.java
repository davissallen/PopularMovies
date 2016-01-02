package com.davis.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        String[] columns = {MovieEntry.COLUMN_NAME_MOVIES_JSON};
        String selection = MovieEntry.COLUMN_NAME_MOVIES_JSON + " = ?";
        String[] selectionArgs = {movieJSON.toString()};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MovieEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();

        try {
            cursor.getString(0);
        } catch (Exception e) {
            Log.e("SQL ERROR", "no string thurr");
            cursor.close();
            db.close();
            return false;
        }

        cursor.close();
        db.close();
        return true;
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
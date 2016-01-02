package com.davis.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.davis.popularmovies.MovieContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBAdapter extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Favorite_Movies.db";

    private final String CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "("
            + MovieEntry.COLUMN_NAME_ENTRY_ID + " INT,"
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
        cursor.moveToFirst();

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            return true;
        }
    }

    public JSONArray getMovies() {
        JSONArray jsonArray = new JSONArray();

        String query = "SELECT " + MovieEntry.COLUMN_NAME_MOVIES_JSON + " FROM " + MovieEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        do {
            try {
                JSONObject jsonObject = new JSONObject(cursor.getString(0));
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } while (cursor.moveToNext());

        cursor.close();
        db.close();

        return jsonArray;
    }

    public void addMovie(JSONObject object) {
        // need to add to favorites list
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_NAME_ENTRY_ID, MovieEntry.numOfFavoritedItems);
        values.put(MovieEntry.COLUMN_NAME_MOVIES_JSON, object.toString());

        db.insert(MovieEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void deleteMovie(JSONObject object) {
        // remove from favorites list
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = MovieEntry.COLUMN_NAME_MOVIES_JSON + "= ?";
        String[] selectionArgs = {object.toString()};
        int deletedIDNum = this.findMovieID(object); // save id of deleted movie
        db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);

        // update id numbers
        String[] selectionArgsInt = {"0"};
        String[] columns = {MovieEntry.COLUMN_NAME_ENTRY_ID};
        String updateIDQuery = "SELECT * FROM " + MovieEntry.TABLE_NAME +
                " WHERE " + MovieEntry.COLUMN_NAME_ENTRY_ID + " > ?";
        Cursor cursor = db.rawQuery(updateIDQuery, new String[]{Integer.toString(deletedIDNum)});
        cursor.moveToFirst();

        do {
            ContentValues values = new ContentValues();
            values.put(MovieEntry.COLUMN_NAME_ENTRY_ID, ++deletedIDNum);
            String where = MovieEntry.COLUMN_NAME_ENTRY_ID + " > ?";
            String[] whereArgs = {Integer.toString(deletedIDNum)};
            db.update(MovieEntry.TABLE_NAME, values, where, whereArgs);
        } while (cursor.moveToNext());

        db.close();
        cursor.close();
    }

    private int findMovieID(JSONObject movieJSON) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + MovieEntry.TABLE_NAME + " WHERE " +
                MovieEntry.COLUMN_NAME_MOVIES_JSON + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{movieJSON.toString()});
        cursor.moveToFirst();
        int num = cursor.getInt(0);
        return num;
    }
}
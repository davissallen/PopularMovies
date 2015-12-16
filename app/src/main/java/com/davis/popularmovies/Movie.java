/*  Created by Davis Allen
 * 
 *  Class: ${CLASS_NAME}.java
 *  Project: PopularMovies
 */

package com.davis.popularmovies;

public class Movie {

    private int _id;
    private String _title;
    private String _json;

    public Movie() {

    }

    public Movie(int id, String title, String jsonObject) {
        this._id = id;
        this._title = title;
        this._json = jsonObject;
    }

    public Movie(String title, String jsonObject) {
        this._title = title;
        this._json = jsonObject;
    }

    public void setID(int id) {
        this._id = id;
    }

    public int getID() {
        return this._id;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public String getTitle() {
        return this._title;
    }

    public void setJSON(String jsonString) {
        this._json = jsonString;
    }

    public String getJSON() {
        return this._json;
    }
}

/*  Created by Davis Allen
 * 
 *  Class: ${CLASS_NAME}.java
 *  Project: PopularMovies
 */

package com.davis.popularmovies;

import android.provider.BaseColumns;

public class MovieContract {

    public MovieContract() {}

    public static abstract class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "Favorite_Movies_Table";
        public static final String COLUMN_NAME_ENTRY_ID = "_id";
        public static final String COLUMN_NAME_MOVIES_JSON = "movies_json";
        public static int numOfFavoritedItems;
    }
}

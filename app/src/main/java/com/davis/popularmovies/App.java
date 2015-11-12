/*  Created by Davis Allen
 * 
 *  Class: ${CLASS_NAME}.java
 *  Project: PopularMovies
 */

package com.davis.popularmovies;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static App mApp = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static Context context() {
        return mApp.getApplicationContext();
    }

}

package com.davis.popularmovies;

import android.widget.Toast;

public class ShowToast {

    public static void showToast(CharSequence text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(App.context(), text, duration);
        toast.show();
    }

}
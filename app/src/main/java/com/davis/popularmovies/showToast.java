package com.davis.popularmovies;

import android.content.Context;
import android.widget.Toast;

public class ShowToast {

    public static void showToast(Context context, CharSequence text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
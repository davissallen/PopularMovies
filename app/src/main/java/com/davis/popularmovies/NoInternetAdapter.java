/*  Created by Davis Allen
 * 
 *  Class: ${CLASS_NAME}.java
 *  Project: PopularMovies
 */

package com.davis.popularmovies;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;


public class NoInternetAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView textView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            textView = new TextView(App.context());
            textView.setLayoutParams(new GridView.LayoutParams(270, 400));
            textView.setGravity(TextView.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(8, 8, 8, 8);
        } else {
            textView = (TextView) convertView;
        }

        textView.setText(R.string.no_internet);
        return textView;
    }
}

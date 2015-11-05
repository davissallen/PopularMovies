package com.davis.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext; /* = getActivity().getApplicationContext(); */
    String[] posterPaths;

    private JSONArray jsonArray;

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public ImageAdapter(JSONArray ja, int sortByID, Context context) {

        mContext = context;
        jsonArray = sortJSONArray(ja, sortByID);
        int length = jsonArray.length();

        posterPaths = new String[length];

        for (int i = (length - 1); i >= 0; i--) {
            try {
                if (jsonArray.getJSONObject(i).getString("poster_path") != null) {
                    posterPaths[i] = "https://image.tmdb.org/t/p/w185" +
                            jsonArray.getJSONObject(i).getString("poster_path");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public int getCount() {
        return jsonArray.length();
    }

    public JSONObject getItem(int position) {
        try {
            return jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

//    private static class ViewHolder {
//        ImageView imageView;
//    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

//        ViewHolder holder = null;
//
//        if(convertView == null) {
//            //Now create the ViewHolder
//            holder = new ViewHolder();
//            //and set its textView field to the proper value
//            holder.imageView = new ImageView(mContext);
//            holder.imageView.setLayoutParams(new GridView.LayoutParams(270, 400));
//            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            holder.imageView.setPadding(8, 8, 8, 8);
//            //and store it as the 'tag' of our view
//            convertView.setTag(holder);
//        } else {
//            //We've already seen this one before!
//            holder = (ViewHolder) convertView.getTag();
//        }

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(270, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(posterPaths[position]).into(imageView);

        return imageView;
    }

    private JSONArray sortJSONArray(JSONArray jsonArray, int sortByInt) {

        final String sortBy;

        switch (sortByInt) {
            case R.id.sortByPopularity:
                sortBy = "popularity";
                break;
            case R.id.sortByHighestRated:
                sortBy = "vote_average";
                break;
            default:
                sortBy = null;
                break;
        }

        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonValues.add(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (sortBy != null) {
            Collections.sort(jsonValues, new Comparator<JSONObject>() {

                @Override
                public int compare(JSONObject a, JSONObject b) {

                    Double valA = 0.0;
                    Double valB = 0.0;

                    try {
                        valA = (Double) a.get(sortBy);
                        valB = (Double) b.get(sortBy);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return -valA.compareTo(valB);
                    //if you want to change the sort order, simply use the following:
                    //return -valA.compareTo(valB);
                }
            });
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        // finished sorting

        return sortedJsonArray;
    }
}
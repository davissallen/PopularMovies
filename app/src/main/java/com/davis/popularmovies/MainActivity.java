package com.davis.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setTitle("Most Popular");

        final GridView gridview = (GridView) findViewById(R.id.gridview);

        FetchMoviesTask fmt = new FetchMoviesTask(getApplicationContext(), gridview);
        fmt.execute("popularity.desc");

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                JSONObject jsonObject = (JSONObject) gridview.getAdapter().getItem(position);
                DetailFragment df = new DetailFragment();

                try {
                    df.initFragment(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.add(R.id.fragment_container, df);
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();

            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        GridView gridView = (GridView) findViewById(R.id.gridview);
        JSONArray jsonArray = ((ImageAdapter) (gridView.getAdapter())).getJsonArray();


        if (id == R.id.sortByPopularity) {
            gridView.setAdapter(new ImageAdapter(jsonArray, R.id.sortByPopularity, getApplicationContext()));
            setTitle("Most Popular");
            return true;
        } else if (id == R.id.sortByHighestRated) {
            gridView.setAdapter(new ImageAdapter(jsonArray, R.id.sortByHighestRated, getApplicationContext()));
            setTitle("Highest Rated");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
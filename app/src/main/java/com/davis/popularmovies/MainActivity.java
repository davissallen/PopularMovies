package com.davis.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
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

    DBAdapter dbAdapter;

    // flag: will make API call each time favorites is unselected,
    // so movies will always be updated
    int favoritesFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        setTitle("Most Popular");

        final GridView gridview = (GridView) findViewById(R.id.gridview);

        if (!isOnline() && (favoritesFlag != 0)) {
            gridview.setAdapter(new NoInternetAdapter());
            return;
        }

        FetchMoviesTask fmt = new FetchMoviesTask(gridview);
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate();
        } else {
            super.onBackPressed();
        }

        // if favorites is still the sorting method...
        if (favoritesFlag == 1) {
            GridView gridView = (GridView) findViewById(R.id.gridview);
            JSONArray jsonArray;
            dbAdapter = new DBAdapter(getApplicationContext());
            jsonArray = dbAdapter.getMovies();
            gridView.setAdapter(new ImageAdapter(jsonArray, R.id.sortByFavorites));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.menu_item_share).setVisible(false);
        menu.findItem(R.id.sort).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        GridView gridView = (GridView) findViewById(R.id.gridview);
        JSONArray jsonArray;

        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

        if (id == R.id.sortByPopularity) {
            if (favoritesFlag == 1) {
                FetchMoviesTask fmt = new FetchMoviesTask(gridView);
                fmt.execute("popularity.desc");
                favoritesFlag = 0;
            }

            jsonArray = ((ImageAdapter) (gridView.getAdapter())).getJsonArray();
            gridView.setAdapter(new ImageAdapter(jsonArray, R.id.sortByPopularity));
            setTitle("Most Popular");

            return true;
        } else if (id == R.id.sortByHighestRated) {
            if (favoritesFlag == 1) {
                FetchMoviesTask fmt = new FetchMoviesTask(gridView);
                fmt.execute("popularity.desc");
                favoritesFlag = 0;
            }

            jsonArray = ((ImageAdapter) (gridView.getAdapter())).getJsonArray();
            gridView.setAdapter(new ImageAdapter(jsonArray, R.id.sortByHighestRated));
            setTitle("Highest Rated");

            return true;
        } else if (id == R.id.sortByFavorites) {
            jsonArray = dbAdapter.getMovies();
            gridView.setAdapter(new ImageAdapter(jsonArray, R.id.sortByFavorites));
            setTitle("Favorites");
            favoritesFlag = 1;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
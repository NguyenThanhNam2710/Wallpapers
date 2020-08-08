package com.example.wallpapers;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.wallpapers.adapter.PhotoAdapter;
import com.example.wallpapers.loadmore.EndlessRecyclerViewScrollListener;
import com.example.wallpapers.model.Photo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private AppBarConfiguration mAppBarConfiguration;
    private RecyclerView mRecyclerView;
    private ArrayList<Photo> mArrayList = new ArrayList<>();
    private SwipeRefreshLayout mSrlLayout;
    private ProgressDialog mProgressDialog;

    PhotoAdapter mAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab_P);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_favorite, R.id.nav_gallery, R.id.nav_logOut)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                showList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == "") {
                    mArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                    mAdapter.notifyItemRangeRemoved(0, mArrayList.size());
                    page = 1;
                    getData_F(page);
                    mSrlLayout.setRefreshing(false);
                }
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onRefresh();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showList(String text) {

        mRecyclerView = findViewById(R.id.rvList_favorites);
        mSrlLayout = findViewById(R.id.srlLayout_favorites);
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mSrlLayout.setOnRefreshListener(MainActivity.this);


        mAdapter = new PhotoAdapter(mArrayList, MainActivity.this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(null);


        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.getRecycledViewPool().clear();
        mArrayList.clear();

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                page = 1;
                getData(page, text);
                return null;
            }
        };
        asyncTask.execute();


        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                MainActivity.this.page++;
                getData(MainActivity.this.page, text);
            }
        });
    }

    private void getData(int page, String text) {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                AndroidNetworking.post("https://www.flickr.com/services/rest")
                        .addBodyParameter("api_key", "71e2a9a70ac5d577d67e353e03938a96")
                        .addBodyParameter("text", text)
                        .addBodyParameter("extras", "views, media, path_alias, url_sq, url_t, url_s, url_q, url_m, url_n, url_z, url_c, url_l, url_o")
                        .addBodyParameter("format", "json")
                        .addBodyParameter("method", "flickr.photos.search")
                        .addBodyParameter("nojsoncallback", "1")
                        .addBodyParameter("per_page", "8")
                        .addBodyParameter("page", String.valueOf(page)).build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject photos = response.getJSONObject("photos");
                                    JSONArray photo = photos.getJSONArray("photo");
                                    mArrayList.addAll(new Gson().fromJson(photo.toString(), new TypeToken<ArrayList<Photo>>() {
                                    }.getType()));
                                    Log.e("listImage_length", mArrayList.size() + "");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e("onError_FF", anError.getErrorBody());
                            }
                        });
                return null;
            }
        };
        asyncTask.execute();

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mArrayList.clear();
                mAdapter.notifyDataSetChanged();
                mAdapter.notifyItemRangeRemoved(0, mArrayList.size());
                page = 1;
                getData_F(page);
                searchView.setQuery("", false);
                mSrlLayout.setRefreshing(false);
            }
        }, 2500);
    }


    private void getData_F(int page) {

        AndroidNetworking.post("https://www.flickr.com/services/rest")
                .addBodyParameter("api_key", "71e2a9a70ac5d577d67e353e03938a96")
                .addBodyParameter("user_id", "187043301@N04")
                .addBodyParameter("extras", "views, media, path_alias, url_sq, url_t, url_s, url_q, url_m, url_n, url_z, url_c, url_l, url_o")
                .addBodyParameter("format", "json")
                .addBodyParameter("method", "flickr.favorites.getList")
                .addBodyParameter("nojsoncallback", "1")
                .addBodyParameter("per_page", "10")
                .addBodyParameter("page", String.valueOf(page)).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", response.toString() + "");
                        try {
                            JSONObject photos = response.getJSONObject("photos");
                            Log.e("photos", photos.toString() + "");
                            JSONArray photo = photos.getJSONArray("photo");
                            Log.e("photo_length", photo.length() + "");
                            mArrayList.addAll(new Gson().fromJson(photo.toString(), new TypeToken<ArrayList<Photo>>() {
                            }.getType()));
                            Log.e("listImage_length", mArrayList.size() + "");
                            mSrlLayout.setRefreshing(false);
                            mAdapter.notifyDataSetChanged();
                            mAdapter.notifyItemRangeRemoved(0, mArrayList.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("onError_FF", anError.getErrorBody());
                    }
                });
    }
}
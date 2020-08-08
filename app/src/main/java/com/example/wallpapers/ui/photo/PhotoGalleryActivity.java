package com.example.wallpapers.ui.photo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.wallpapers.R;
import com.example.wallpapers.adapter.PhotoAdapter;
import com.example.wallpapers.loadmore.EndlessRecyclerViewScrollListener;
import com.example.wallpapers.model.Photo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotoGalleryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerView;
    private ArrayList<Photo> mArrayList = new ArrayList<>();
    private SwipeRefreshLayout mSrlLayout;
    private ProgressDialog mProgressDialog;
    private ProgressBar progressBar;
    PhotoAdapter mAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    int page = 1;
    String id = "", title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        progressBar = findViewById(R.id.progressBar);


        mSrlLayout = (SwipeRefreshLayout) findViewById(R.id.srlLayout_PGA);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvList_PGA);
        mProgressDialog = new ProgressDialog(this);
        mSrlLayout.setOnRefreshListener(this);

        mRecyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new PhotoAdapter(mArrayList, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setAnimation(null);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.getRecycledViewPool().clear();

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                PhotoGalleryActivity.this.page++;
                getData(PhotoGalleryActivity.this.id, PhotoGalleryActivity.this.page);
            }
        });
        mArrayList.clear();
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                getData(id, page);
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
                getData(id, page);
                mSrlLayout.setRefreshing(false);
            }
        }, 2500);
    }

    private void getData(String id, int page) {
        AndroidNetworking.post("https://www.flickr.com/services/rest")
                .addBodyParameter("api_key", "71e2a9a70ac5d577d67e353e03938a96")
                .addBodyParameter("gallery_id", id)
                .addBodyParameter("extras", "views, media, path_alias, url_sq, url_t, url_s, url_q, url_m, url_n, url_z, url_c, url_l, url_o")
                .addBodyParameter("format", "json")
                .addBodyParameter("method", "flickr.galleries.getPhotos")
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
                            if (progressBar.isShown()) {
                                progressBar.setVisibility(View.GONE);
                            }
                            mSrlLayout.setRefreshing(false);
                            mAdapter.notifyDataSetChanged();
                            mAdapter.notifyItemRangeRemoved(0, mArrayList.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }


}
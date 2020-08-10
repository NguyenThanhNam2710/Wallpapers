package com.example.wallpapers.ui.favorites;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.wallpapers.R;
import com.example.wallpapers.adapter.PhotoAdapter;
import com.example.wallpapers.loadmore.EndlessRecyclerViewScrollListener;
import com.example.wallpapers.model.Photo;
import com.example.wallpapers.model.Photos;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FavoriteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerView;
    private ArrayList<Photo> mArrayList = new ArrayList<>();
    private SwipeRefreshLayout mSrlLayout;

    PhotoAdapter mAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    int page = 1;
    SweetAlertDialog mProgressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        mRecyclerView = root.findViewById(R.id.rvList_favorites);
        mSrlLayout = root.findViewById(R.id.srlLayout_favorites);

        mSrlLayout.setOnRefreshListener(this);
        mProgressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        mAdapter = new PhotoAdapter(mArrayList, getActivity());
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(null);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.getRecycledViewPool().clear();
        mArrayList.clear();

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mProgressDialog.setTitleText("Loading");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                getData_F(page);
                return null;
            }
        };
        asyncTask.execute();

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                FavoriteFragment.this.page++;
                getData_F(FavoriteFragment.this.page);
            }
        });


        return root;
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
                            if (mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }

                            Photos photos1 = new Gson().fromJson(photos.toString(), Photos.class);
                            int pagers = photos1.getPages();
                            Log.e("doInBackground()", page + ", " + pagers);
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
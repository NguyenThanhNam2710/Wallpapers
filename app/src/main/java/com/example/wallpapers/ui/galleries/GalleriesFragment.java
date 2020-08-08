package com.example.wallpapers.ui.galleries;

import android.app.ProgressDialog;
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
import com.example.wallpapers.adapter.GalleryAdapter;
import com.example.wallpapers.adapter.PhotoAdapter;
import com.example.wallpapers.loadmore.EndlessRecyclerViewScrollListener;
import com.example.wallpapers.model.Galleries_;
import com.example.wallpapers.model.Gallery;
import com.example.wallpapers.model.Photo;
import com.example.wallpapers.ui.favorites.FavoriteFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GalleriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSrlLayout;
    private ArrayList<Gallery> mArrayList = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    GalleryAdapter mAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    int page = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        mRecyclerView = root.findViewById(R.id.rvList_galleries);
        mSrlLayout = root.findViewById(R.id.srlLayout_galleries);
        mProgressDialog = new ProgressDialog(getActivity());
        mSrlLayout.setOnRefreshListener(this);
        mRecyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new GalleryAdapter(mArrayList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setAnimation(null);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.getRecycledViewPool().clear();

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                GalleriesFragment.this.page++;
                getData(GalleriesFragment.this.page);
            }
        });
        mArrayList.clear();
        AsyncTask asyncTask = new AsyncTask() {
//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//                mProgressDialog.setMessage("Loading ...");
//                mProgressDialog.setCancelable(false);
//                mProgressDialog.show();
//            }

            @Override
            protected Object doInBackground(Object[] objects) {
                getData(page);
                return null;
            }
        };
        asyncTask.execute();

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
                getData(page);
                mSrlLayout.setRefreshing(false);
            }
        }, 2500);
    }

    private void getData(int page) {
        AndroidNetworking.post("https://www.flickr.com/services/rest")
                .addBodyParameter("api_key", "71e2a9a70ac5d577d67e353e03938a96")
                .addBodyParameter("user_id", "187043301@N04")
                .addBodyParameter("extras", "views, media, path_alias, url_sq, url_t, url_s, url_q, url_m, url_n, url_z, url_c, url_l, url_o")
                .addBodyParameter("format", "json")
                .addBodyParameter("method", "flickr.galleries.getList")
                .addBodyParameter("nojsoncallback", "1")
                .addBodyParameter("per_page", "10")
                .addBodyParameter("page", String.valueOf(page)).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", response.toString() + "");
                        JSONObject galleries = null;
                        try {
                            galleries = response.getJSONObject("galleries");
                            Galleries_ galleries_ = new Gson().fromJson(galleries.toString(), Galleries_.class);
                            Log.e("galleries", galleries.toString() + "");
                            JSONArray gallery = galleries.getJSONArray("gallery");
                            Log.e("gallery_length", gallery.length() + "");
                            mArrayList.addAll(new Gson().fromJson(gallery.toString(), new TypeToken<ArrayList<Gallery>>() {
                            }.getType()));
                            Log.e("listImage_length", mArrayList.size() + "");
                            if (mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
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
                        Log.e("onError_GF", anError.getErrorBody());
                    }
                });

    }
}
package com.example.wallpapers;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.wallpapers.adapter.CommentAdapter;
import com.example.wallpapers.adapter.GalleryAdapter;
import com.example.wallpapers.model.Comment_;
import com.example.wallpapers.model.Gallery;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BottomSheet extends BottomSheetDialogFragment {
    String id;
    Context context;

    public BottomSheet(String id, Context context) {
        this.id = id;
        this.context = context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.row_add_item, container, false);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rvComment);
        AndroidNetworking.post("https://www.flickr.com/services/rest")
                .addBodyParameter("api_key", "71e2a9a70ac5d577d67e353e03938a96")
                .addBodyParameter("photo_id", id)
                .addBodyParameter("format", "json")
                .addBodyParameter("method", "flickr.photos.comments.getList")
                .addBodyParameter("nojsoncallback", "1").build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<Comment_> mArrayList = new ArrayList<>();

                            JSONObject comments = response.getJSONObject("comments");
                            Log.e("comments", comments.toString() + "");
                            JSONArray comment = comments.getJSONArray("comment");
                            Log.e("comment", comment.toString() + "");
                            mArrayList.addAll(new Gson().fromJson(comment.toString(), new TypeToken<ArrayList<Comment_>>() {
                            }.getType()));

                            Log.e("mArrayList", mArrayList.size() + "");
                            LinearLayoutManager staggeredGridLayoutManager = new LinearLayoutManager(context);
                            CommentAdapter mAdapter = new CommentAdapter(mArrayList, context);
                            mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("onError_FF", anError.getErrorBody());
                    }
                });
        return view;
    }


    private void getCommentAdd(String photoID) {

        AndroidNetworking.post("https://www.flickr.com/services/rest")
                .addBodyParameter("api_key", "71e2a9a70ac5d577d67e353e03938a96")
                .addBodyParameter("photo_id", photoID)
                .addBodyParameter("format", "json")
                .addBodyParameter("method", "flickr.photos.comments.getList")
                .addBodyParameter("nojsoncallback", "1")
                .addBodyParameter("per_page", "20")
                .addBodyParameter("page", "1").build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", response.toString() + "");
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("onError_FF", anError.getErrorBody());
                    }
                });
    }

    private void getComment(List<Comment_> photos, String photoID) {

        AndroidNetworking.post("https://www.flickr.com/services/rest")
                .addBodyParameter("api_key", "71e2a9a70ac5d577d67e353e03938a96")
                .addBodyParameter("photo_id", "4847537363")
                .addBodyParameter("format", "json")
                .addBodyParameter("method", "flickr.photos.comments.getList")
                .addBodyParameter("nojsoncallback", "1")
                .addBodyParameter("per_page", "20")
                .addBodyParameter("page", "1").build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject comments = response.getJSONObject("comments");
                            Log.e("comments", comments.toString() + "");
                            JSONArray comment = comments.getJSONArray("comment");
                            Log.e("comment", comment.toString() + "");
                            photos.addAll(new Gson().fromJson(comment.toString(), new TypeToken<ArrayList<Comment_>>() {
                            }.getType()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("error", e.getMessage() + "");
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("onError_FF", anError.getErrorBody());
                    }
                });
    }
}

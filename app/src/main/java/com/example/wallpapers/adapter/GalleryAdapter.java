package com.example.wallpapers.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.wallpapers.R;
import com.example.wallpapers.model.Gallery;
import com.example.wallpapers.ui.photo.PhotoGalleryActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private List<Gallery> listData;
    private Context context;

    ArrayList<Integer> list = new ArrayList<>();
    int so = 0;

    public GalleryAdapter(List<Gallery> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    public GalleryAdapter(List<Gallery> listData, Context context, ArrayList<Integer> list) {
        this.listData = listData;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.gallery_listview, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Gallery item = listData.get(position);

        holder.btnGallery.setText(item.getTitle().getContent() + " (" + item.getCountPhotos() + ")");
        holder.btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("id_gallery ", item.getGalleryId());
                Intent intent = new Intent(context.getApplicationContext(), PhotoGalleryActivity.class);
                intent.putExtra("id", item.getId());
                intent.putExtra("title", item.getTitle().getContent());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button btnGallery;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnGallery = (Button) itemView.findViewById(R.id.btnGallery);
        }
    }


    private int getSize_Gallery(String id) {
        AndroidNetworking.post("https://www.flickr.com/services/rest")
                .addBodyParameter("api_key", "71e2a9a70ac5d577d67e353e03938a96")
                .addBodyParameter("gallery_id", id)
                .addBodyParameter("extras", "views, media, path_alias, url_sq, url_t, url_s, url_q, url_m, url_n, url_z, url_c, url_l, url_o")
                .addBodyParameter("format", "json")
                .addBodyParameter("method", "flickr.galleries.getPhotos")
                .addBodyParameter("nojsoncallback", "1")
                .addBodyParameter("per_page", "10")
                .addBodyParameter("page", "0").build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", response.toString() + "");
                        try {
                            JSONObject photos = response.getJSONObject("photos");
                            Log.e("photos", photos.toString() + "");
                            JSONArray photo = photos.getJSONArray("photo");
                            Log.e("photo_length", photo.length() + "");
                            so = photo.length();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
        return so;
    }
}

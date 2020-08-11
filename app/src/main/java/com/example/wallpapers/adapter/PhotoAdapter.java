package com.example.wallpapers.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.wallpapers.R;
import com.example.wallpapers.model.Comment_;
import com.example.wallpapers.model.Photo;
import com.example.wallpapers.ui.photo.PhotoActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> implements Filterable {
    private List<Photo> listData;
    private Context context;

    public PhotoAdapter(List<Photo> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.photo_listview, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    ArrayList<Comment_> mArrayList = new ArrayList<>();
    int size = -1;

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Photo item = listData.get(position);
        Picasso.get().load(item.getUrlM()).into(holder.imageView);
        holder.tvPhoto_View.setText(item.getViews());
        String finalUrl = item.getUrlM();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("id_image ", item.getId());
                AndroidNetworking.post("https://www.flickr.com/services/rest")
                        .addBodyParameter("api_key", "71e2a9a70ac5d577d67e353e03938a96")
                        .addBodyParameter("photo_id", item.getId())
                        .addBodyParameter("format", "json")
                        .addBodyParameter("method", "flickr.photos.comments.getList")
                        .addBodyParameter("nojsoncallback", "1").build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject comments = response.getJSONObject("comments");
                                    Log.e("comments", comments.toString() + "");
                                    JSONArray comment = comments.getJSONArray("comment");
                                    Log.e("comment", comment.toString() + "");
                                    mArrayList.addAll(new Gson().fromJson(comment.toString(), new TypeToken<ArrayList<Comment_>>() {
                                    }.getType()));
                                    Log.e("mArrayList", mArrayList.size() + "");
                                    size = mArrayList.size();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    Intent intent = new Intent(context, PhotoActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("photoID", item.getId());
                                    bundle.putString("urlM", finalUrl);
                                    bundle.putString("title", item.getTitle());
                                    bundle.putString("views", item.getViews());
                                    bundle.putString("media", item.getMedia());
                                    bundle.putInt("comments", size);
                                    intent.putExtras(bundle);
                                    context.startActivity(intent);
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e("onError_FF", anError.getErrorBody());
                            }
                        });
                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Photo> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listData);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Photo item : listData) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listData.clear();
            listData.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView tvPhoto_View;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            tvPhoto_View = (TextView) itemView.findViewById(R.id.tvPhoto_View);
        }
    }

}

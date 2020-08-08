package com.example.wallpapers.adapter;

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
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallpapers.R;
import com.example.wallpapers.model.Photo;
import com.example.wallpapers.ui.photo.PhotoActivity;
import com.squareup.picasso.Picasso;

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
//
//        for (int i = 0; i < listData.size(); i++) {
//            Log.e("image[" + i + "]", listData.get(i).getUrlM());
//        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Photo item = listData.get(position);
        Picasso.get().load(item.getUrlM()).into(holder.imageView);
        holder.tvPhoto_View.setText(item.getViews());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("id_image ", item.getId());
                Intent intent = new Intent(context, PhotoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("urlM", item.getUrlM());
                bundle.putString("title", item.getTitle());
                intent.putExtras(bundle);
                context.startActivity(intent);
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

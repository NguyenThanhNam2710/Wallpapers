package com.example.wallpapers.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.wallpapers.R;
import com.example.wallpapers.model.Comment_;

import org.json.JSONObject;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment_> comment_list;
    private Context context;

    public CommentAdapter(List<Comment_> comment_list, Context context) {
        this.comment_list = comment_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_comment, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUserName.setText(comment_list.get(position).getAuthorname());
        holder.tvContent.setText(comment_list.get(position).getContent());
        holder.tvTime.setText(comment_list.get(position).getDatecreate());
    }

    @Override
    public int getItemCount() {
        return comment_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatarmgAvatar;
        TextView tvUserName;
        TextView tvContent;
        TextView tvTime;

        public ViewHolder(@NonNull View view) {
            super(view);
            imgAvatarmgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            tvContent = (TextView) view.findViewById(R.id.tvContent);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
        }
    }

}

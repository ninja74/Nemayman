package com.design.nemayman.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.design.nemayman.Models.ModPosts;
import com.design.nemayman.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.meness.Library.HtmlTextView.HtmlTextView;

public class AdRecycleComment extends RecyclerView.Adapter<AdRecycleComment.myViewHolder> {

    public Context context;
    public List<ModPosts> posts;

    public AdRecycleComment(Context context, List<ModPosts> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyc_comment, viewGroup, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, final int i) {

// comment
        myViewHolder.txtComment.setHtml(posts.get(i).commentTxt);

// comment author
        myViewHolder.txtNameComment.setText(posts.get(i).commentAuthorName);

// img
        Glide.with(context).load(posts.get(i).commentAuthorAvatarUrls)
                .placeholder(R.drawable.logonemayman)
                .error(R.drawable.logonemayman)
                .into(myViewHolder.imgThumbComment);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        public HtmlTextView txtComment;
        public TextView txtNameComment;
        public CircleImageView imgThumbComment;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            txtComment = itemView.findViewById(R.id.txtComment);
            txtNameComment = itemView.findViewById(R.id.txtNameComment);
            imgThumbComment = itemView.findViewById(R.id.imgThumbComment);

        }
    }

}

package com.design.nemayman.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.design.nemayman.Activity_Details_Nema;
import com.design.nemayman.Activity_Main_Nema;
import com.design.nemayman.Classes.checkInternet;
import com.design.nemayman.Models.ModPosts;
import com.design.nemayman.R;

import java.util.List;

import io.github.meness.Library.HtmlTextView.HtmlTextView;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class AdRecyclePosts extends RecyclerView.Adapter<AdRecyclePosts.myViewHolder> {

    public Context context;
    public List<ModPosts> posts;
    private checkInternet internet;

    public AdRecyclePosts(Context context, List<ModPosts> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyc, viewGroup, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, final int i) {

        internet = new checkInternet(context);

        try {
            // title
            myViewHolder.txtTitle.setText(posts.get(i).postTitle);

// Excerpt
            String strExcerpt = posts.get(i).postExcerpt + "";
            String strExcerpt2 = "";
// delete ادامه مطلب
            strExcerpt = strExcerpt.replace("<span class=\"space\">&nbsp;&nbsp;</span>&nbsp;ادامه مطلب&nbsp;", "");
            String strExcerptArr[] = strExcerpt.split(" ");

            for (int j = 0; j < 20; j++) strExcerpt2 += strExcerptArr[j] + " ";

            myViewHolder.txtExcerpt.setHtml(strExcerpt2 + "...");


// img
            Glide.with(context).load(posts.get(i).imgPostMedium)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher_round)
                    .into(myViewHolder.imgThumb);

// category
            myViewHolder.txtCategory.setText(posts.get(i).category);

// click

                myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (internet.CheckNetworkConnection()){
                            Intent intent = new Intent(context, Activity_Details_Nema.class);
                            intent.putExtra("idPost", posts.get(i).idPost +"");
                            intent.putExtra("postTitle", posts.get(i).postTitle +"");
                            intent.putExtra("postDescription", posts.get(i).postDescription +"");
                            intent.putExtra("urlImg", posts.get(i).postImgFullUrl +"");

                            context.startActivity(intent);
                        }else {
                            CheckNet();
                        }
                    }
                });
        }catch (Exception e){
            Log.e("errAd", e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgThumb;
        public TextView txtTitle, txtCategory;
        public HtmlTextView txtExcerpt;
        public CardView cardView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imgThumb = itemView.findViewById(R.id.imgThumb);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtExcerpt = itemView.findViewById(R.id.txtExcerpt);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }

    private void CheckNet(){
        PrettyDialog prettyDialog = new PrettyDialog(context);
        prettyDialog.setIcon(
                R.drawable.pdlg_icon_info,     // icon resource
                R.color.pdlg_color_red,      // icon tint
                new PrettyDialogCallback() {   // icon OnClick listener
                    @Override
                    public void onClick() {

                    }
                });
        prettyDialog.setTitle("خطا در ارتباط");
        prettyDialog.setMessage("لطفا ارتباط خود با اینترنت را چک نمایید");
        prettyDialog.show();
    }

}

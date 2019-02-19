package com.design.nemayman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.design.nemayman.Adapters.AdRecycleComment;
import com.design.nemayman.Adapters.AdRecyclePosts;
import com.design.nemayman.Classes.checkInternet;
import com.design.nemayman.Connects.ConComment;
import com.design.nemayman.Models.ModPosts;

import java.util.List;

import io.github.meness.Library.HtmlTextView.HtmlHttpImageGetter;
import io.github.meness.Library.HtmlTextView.HtmlTextView;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class Activity_Details_Nema extends AppCompatActivity {


    private String idPost = "", txtTitle = "", txtDec = "", urlImg = "";
    private TextView txtTitleDetails;
    private Button btnCommentDetails;
    private HtmlTextView txtDecDetails;
    private ImageView imgTitleDetails;
    private RecyclerView recyclerView;
    private checkInternet internet;
    private MaterialProgressBar progressLoadingComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_nema);
        findViews();
        defaultData();

        internet = new checkInternet(Activity_Details_Nema.this);
// title
        txtTitleDetails.setText(txtTitle);

// img title
        Glide.with(Activity_Details_Nema.this)
                .load(urlImg)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .into(imgTitleDetails);

// txtDec
        txtDecDetails.setHtml(txtDec, new HtmlHttpImageGetter(txtDecDetails));

// Comment
        btnCommentDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internet.CheckNetworkConnection()){
                    btnCommentDetails.setVisibility(View.GONE);
                    progressLoadingComment.setVisibility(View.VISIBLE);

                    String url = "http://nemayman.com/wp-json/wp/v2/comments?post=" + idPost;

                    ConComment conComment = new ConComment(Activity_Details_Nema.this, url);
                    conComment.getDataFromUrlComment(new ConComment.OnPostResponseComment() {
                        @Override
                        public void onPostResponse(List<ModPosts> response) {

                            if (response.size() == 0)
                                Toast.makeText(Activity_Details_Nema.this, "کامنتی وجود ندارد", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(Activity_Details_Nema.this, response.size() + " کامنت موجود است", Toast.LENGTH_SHORT).show();

                            recyclerView.setVisibility(View.VISIBLE);
                            progressLoadingComment.setVisibility(View.GONE);
                            AdRecycleComment adRecycleComment = new AdRecycleComment(Activity_Details_Nema.this, response);
                            recyclerView.setLayoutManager(new LinearLayoutManager(Activity_Details_Nema.this, LinearLayoutManager.VERTICAL, false));
                            recyclerView.setAdapter(adRecycleComment);

                        }
                    });
                }else {
                    CheckNet();
                }
            }
        });


    }

    private void defaultData() {

        idPost = getIntent().getStringExtra("idPost");
        txtTitle = getIntent().getStringExtra("postTitle");
        txtDec = getIntent().getStringExtra("postDescription");
        urlImg = getIntent().getStringExtra("urlImg");
        progressLoadingComment.setVisibility(View.GONE);

    }

    private void findViews() {
        txtTitleDetails = findViewById(R.id.txtTitleDetails);
        btnCommentDetails = findViewById(R.id.btnCommentDetails);
        txtDecDetails = findViewById(R.id.txtDecDetails);
        imgTitleDetails = findViewById(R.id.imgTitleDetails);
        recyclerView = findViewById(R.id.recyclerComment);
        progressLoadingComment = findViewById(R.id.progressLoadingComment);
    }


    private void CheckNet() {
        PrettyDialog prettyDialog = new PrettyDialog(this);
        prettyDialog.setIcon(
                R.drawable.pdlg_icon_info,     // icon resource
                R.color.pdlg_color_red,      // icon tint
                new PrettyDialogCallback() {   // icon OnClick listener
                    @Override
                    public void onClick() {
                        finish();
                    }
                });
        prettyDialog.setTitle("خطا در ارتباط");
        prettyDialog.setMessage("لطفا ارتباط خود با اینترنت را چک نمایید");
        prettyDialog.show();
    }
}

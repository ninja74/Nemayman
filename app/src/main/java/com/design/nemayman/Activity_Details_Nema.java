package com.design.nemayman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.design.nemayman.Adapters.AdRecycleComment;
import com.design.nemayman.Classes.checkInternet;
import com.design.nemayman.Connects.ConComment;
import com.design.nemayman.Models.ModPosts;
import com.design.nemayman.R;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.github.meness.Library.HtmlTextView.HtmlHttpImageGetter;
import io.github.meness.Library.HtmlTextView.HtmlTextView;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import life.sabujak.roundedbutton.RoundedButton;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class Activity_Details_Nema extends AppCompatActivity {


    private String idPost = "", txtTitle = "", txtDec = "", urlImg = "";
    private TextView txtTitleDetails;
    private HtmlTextView txtDecDetails;
    private ImageView imgTitleDetails;
    private RecyclerView recyclerView;
    private checkInternet internet;
    private MaterialProgressBar progressLoadingComment;
    private RoundedButton btnCommentDetails;

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
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_failed)
                .into(imgTitleDetails);

// txtDec
        txtDecDetails.setHtml(txtDec, new HtmlHttpImageGetter(txtDecDetails));


// Comment


        btnCommentDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internet.CheckNetworkConnection()) {
                    btnCommentDetails.setVisibility(View.GONE);
                    progressLoadingComment.setVisibility(View.VISIBLE);

                    String url = getString(R.string.urlDetailsComments) + idPost;

                    ConComment conComment = new ConComment(Activity_Details_Nema.this, url);
                    conComment.getDataFromUrlComment(new ConComment.OnPostResponseComment() {
                        @Override
                        public void onPostResponse(List<ModPosts> response) {

                            if (response.size() == 0)
                                Toasty.info(Activity_Details_Nema.this, getString(R.string.toastDetailsNoComment), Toast.LENGTH_SHORT, true).show();

                            recyclerView.setVisibility(View.VISIBLE);
                            progressLoadingComment.setVisibility(View.GONE);
                            AdRecycleComment adRecycleComment = new AdRecycleComment(Activity_Details_Nema.this, response);
                            recyclerView.setLayoutManager(new LinearLayoutManager(Activity_Details_Nema.this, LinearLayoutManager.VERTICAL, false));
                            recyclerView.setAdapter(adRecycleComment);

                        }
                    });
                } else {
                    checkNet();
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
        txtDecDetails = findViewById(R.id.txtDecDetails);
        imgTitleDetails = findViewById(R.id.imgTitleDetails);
        recyclerView = findViewById(R.id.recyclerComment);
        progressLoadingComment = findViewById(R.id.progressLoadingComment);
        btnCommentDetails = findViewById(R.id.btnCommentDetails);
    }

    private void checkNet() {
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
        prettyDialog.setTitle(getString(R.string.AlertCantConnect));
        prettyDialog.setMessage(getString(R.string.AlertCheckNetAgain));
        prettyDialog.show();
    }

}

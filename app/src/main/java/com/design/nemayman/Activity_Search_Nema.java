package com.design.nemayman;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.design.nemayman.Adapters.AdRecyclePosts;
import com.design.nemayman.Classes.checkInternet;
import com.design.nemayman.Connects.ConComment;
import com.design.nemayman.Connects.ConPosts;
import com.design.nemayman.Models.ModPosts;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class Activity_Search_Nema extends AppCompatActivity {

    private EditText edtSearch;
    private ImageView imgClear;
    private ImageView imgBack;
    private RecyclerView recyclerSearch;
    private LinearLayoutManager manager;
    private AdRecyclePosts postsAdapter;
    private List<ModPosts> data;
    private Boolean isScrolling = false;
    private int currentItem, totalItems, scrollOutItems;
    private int page = 1;
    private String url = "";
    private String txtToSearch = "";
    private MaterialProgressBar progressLoadingSearch;
    private MaterialProgressBar progressLoadingSearchFirst;

    private checkInternet internet;
    private Boolean boolCheckNet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_nema);

        findViews();
        defaults();

        internet = new checkInternet(Activity_Search_Nema.this);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!edtSearch.getText().toString().equals(""))
                    imgClear.setVisibility(View.VISIBLE);
                else
                    imgClear.setVisibility(View.GONE);
            }
        });


        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (internet.CheckNetworkConnection()) {
                        View view = Activity_Search_Nema.this.getCurrentFocus();

                        if (view != null) {
                            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        txtToSearch = "&&search=" + edtSearch.getText().toString();

                        setDataOnRec();
                        return true;
                    } else {
                        checkNet();
                    }
                }
                return false;
            }
        });


        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void defaults() {
        imgClear.setVisibility(View.GONE);
        progressLoadingSearch.setVisibility(View.GONE);
        url = getString(R.string.urlSearch) + "&&per_page=10&&page=";
    }

    private void findViews() {
        edtSearch = findViewById(R.id.edtSearch);
        imgClear = findViewById(R.id.imgClear);
        imgBack = findViewById(R.id.imgBack);
        recyclerSearch = findViewById(R.id.recyclerSearch);
        progressLoadingSearch = findViewById(R.id.progressLoadingSearch);
        progressLoadingSearchFirst = findViewById(R.id.progressLoadingSearchFirst);

    }

    private void setDataOnRec() {
        page = 1;

        if (page == 1) {
            progressLoadingSearch.setVisibility(View.GONE);
            progressLoadingSearchFirst.setVisibility(View.VISIBLE);
        } else {
            progressLoadingSearch.setVisibility(View.VISIBLE);
            progressLoadingSearchFirst.setVisibility(View.GONE);
        }


        data = new ArrayList<>();
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        try {

            ConPosts conPosts = new ConPosts(Activity_Search_Nema.this, url + page + txtToSearch);
            conPosts.getModPostsFromUrl(new ConPosts.OnPostResponse() {
                @Override
                public void onPostResponse(final List<ModPosts> response) {
                    if (response == null) {

                        progressLoadingSearch.setVisibility(View.GONE);
                        progressLoadingSearchFirst.setVisibility(View.GONE);
                        Toasty.info(Activity_Search_Nema.this, getString(R.string.toastSearchNothingFound), Toast.LENGTH_SHORT, true).show();


                    } else {

                        if (response.size() == 0)
                            Toasty.info(Activity_Search_Nema.this, getString(R.string.toastSearchNothingFound), Toast.LENGTH_SHORT, true).show();

                        for (int i = 0; i < response.size(); i++) {
                            data.add(response.get(i));
                        }

                        postsAdapter = new AdRecyclePosts(Activity_Search_Nema.this, data);
                        recyclerSearch.setAdapter(postsAdapter);
                        progressLoadingSearch.setVisibility(View.GONE);
                        progressLoadingSearchFirst.setVisibility(View.GONE);
                        recyclerSearch.setLayoutManager(manager);
                        recyclerSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }

                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                currentItem = manager.getChildCount();
                                totalItems = manager.getItemCount();
                                scrollOutItems = manager.findFirstVisibleItemPosition();

                                if (dy > 0) { // know scroll down
                                    if (isScrolling && (currentItem + scrollOutItems == totalItems) && response.size() == 10) {
                                        progressLoadingSearch.setVisibility(View.VISIBLE);
                                        isScrolling = false;
                                        if (!boolCheckNet) {
                                            page++;
                                        }

                                        ConPosts conPosts = new ConPosts(Activity_Search_Nema.this, url + page + txtToSearch);
                                        conPosts.getModPostsFromUrl(new ConPosts.OnPostResponse() {
                                            @Override
                                            public void onPostResponse(List<ModPosts> response) {

                                                if (internet.CheckNetworkConnection()) {
                                                    if (response == null) {
                                                        progressLoadingSearch.setVisibility(View.GONE);
                                                        Toasty.info(Activity_Search_Nema.this, getString(R.string.toastMainNoMorePosts), Toast.LENGTH_SHORT, true).show();

                                                    } else {

                                                        for (int i = 0; i < response.size(); i++) {
                                                            data.add(response.get(i));

                                                            postsAdapter.notifyDataSetChanged();
                                                            progressLoadingSearch.setVisibility(View.GONE);
                                                        }
                                                        boolCheckNet = false;
                                                    }
                                                } else {

                                                    checkNet();
                                                    progressLoadingSearch.setVisibility(View.GONE);
                                                    boolCheckNet = true;

                                                }
                                            }
                                        });

                                    }
                                }
                            }
                        });

                    }
                }
            });
        } catch (Exception e) {
            Log.e("err", e.getMessage());
        }

    }

    private void checkNet() {
        PrettyDialog prettyDialog = new PrettyDialog(Activity_Search_Nema.this);
        prettyDialog.setIcon(
                R.drawable.pdlg_icon_info,     // icon resource
                R.color.pdlg_color_red,      // icon tint
                new PrettyDialogCallback() {   // icon OnClick listener
                    @Override
                    public void onClick() {

                    }
                });
        prettyDialog.setTitle(getString(R.string.AlertCantConnect));
        prettyDialog.setMessage(getString(R.string.AlertCheckNetAgain));
        prettyDialog.show();
    }

}

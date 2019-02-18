package com.design.nemayman;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.design.nemayman.Connects.ConComment;
import com.design.nemayman.Connects.ConPosts;

import java.util.List;

public class Activity_Search_Nema extends AppCompatActivity {

    EditText edtSearch;
    ImageView imgClear;
    ImageView imgBack;
    RecyclerView recyclerSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_nema);

        findViews();
        defaults();


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

                    View view = Activity_Search_Nema.this.getCurrentFocus();

                    if (view != null) {
                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }


                    String url = "http://nemayman.com/wp-json/wp/v2/posts?per_page=10&&page=1&&_embed&&search="+edtSearch.getText().toString();



// -----------------------------------------------------------------------


                    return true;
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

    }

    private void findViews() {
        edtSearch = findViewById(R.id.edtSearch);
        imgClear = findViewById(R.id.imgClear);
        imgBack = findViewById(R.id.imgBack);
        recyclerSearch = findViewById(R.id.recyclerSearch);

    }
}

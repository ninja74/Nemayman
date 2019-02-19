package com.design.nemayman;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.design.nemayman.Adapters.AdRecyclePosts;
import com.design.nemayman.Connects.ConPosts;
import com.design.nemayman.Models.ModPosts;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import io.github.meness.Library.Tag.TagGroup;
import io.github.meness.Library.Utils.IntentUtility;
import io.github.meness.Library.Utils.Utility;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class Activity_Main_Nema extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private RecyclerView recyclerPosts;
    private Toolbar toolbar;
    private LinearLayoutManager manager;
    private AdRecyclePosts postsAdapter;
    private List<ModPosts> data;
    private Boolean isScrolling = false;
    private int currentItem, totalItems, scrollOutItems;
    private int page = 1;
    private String url = "";

    private AlertDialog alertDialogFilters;
    private String reqDate = "";
    private String dateBeforFilter = "";
    private String dateAfterFilter = "";
    private String CategoryFilter = "";
    private TextView txtDateToFilter;
    private TextView txtDateFromFilter;
    private TagGroup tagGroupFilter;
    private List<ModPosts> modPostsCategory;
    private String Txtsearch = "";

    private MaterialProgressBar progressLoading;
    private MaterialProgressBar progressLoadingFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nema);
        findViews();
        setSupportActionBar(toolbar);


        // navigationView ****************************************************************

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);


        // set Data on Recycler
        String url = "http://nemayman.com/wp-json/wp/v2/posts?_embed&&per_page=10&&page=";
        try {
            setDataOnRec(url);
        } catch (Exception e) {

        }

    }

// Get Data And set on RecyclerView

    private void setDataOnRec(final String urlMethod) {
        url = urlMethod;
        page = 19;
        progressLoading.setVisibility(View.VISIBLE);

        data = new ArrayList<>();
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        try {
            ConPosts conPosts = new ConPosts(Activity_Main_Nema.this, url + page + Txtsearch);

            conPosts.getModPostsFromUrl(new ConPosts.OnPostResponse() {
                @Override
                public void onPostResponse(final List<ModPosts> response) {

                    for (int i = 0; i < response.size(); i++) {
                        data.add(response.get(i));
                    }

                    postsAdapter = new AdRecyclePosts(Activity_Main_Nema.this, data);
                    recyclerPosts.setAdapter(postsAdapter);
                    progressLoading.setVisibility(View.GONE);
                    recyclerPosts.setLayoutManager(manager);
                    recyclerPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                if (isScrolling && (currentItem + scrollOutItems == totalItems)) {
                                    progressLoading.setVisibility(View.VISIBLE);
                                    isScrolling = false;
                                    page++;
                                    ConPosts conPosts = new ConPosts(Activity_Main_Nema.this, url + page + Txtsearch);
                                    conPosts.getModPostsFromUrl(new ConPosts.OnPostResponse() {
                                        @Override
                                        public void onPostResponse(List<ModPosts> response) {

                                            if (response == null){
                                                progressLoading.setVisibility(View.GONE);
                                                Toast.makeText(Activity_Main_Nema.this, "پست بیشتری وجود نداره", Toast.LENGTH_SHORT).show();
                                            }else {
                                                for (int i = 0; i < response.size(); i++) {
                                                    data.add(response.get(i));
                                                }
                                                postsAdapter.notifyDataSetChanged();
                                                progressLoading.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e("err", e.getMessage());
        }

    }

// Start AlertDialogFilter

    private void AlertDialogFilter() {


        AlertDialog.Builder builderFilter = new AlertDialog.Builder(Activity_Main_Nema.this);
        LinearLayout layoutFilter = (LinearLayout) getLayoutInflater().inflate(R.layout.item_filter, null, false);

        final TextView txtCategoriesFilter = layoutFilter.findViewById(R.id.txtCategoriesFilter);
        TextView txtDeleteFilter = layoutFilter.findViewById(R.id.txtDeleteFilter);
        TextView txtDismissFilter = layoutFilter.findViewById(R.id.txtDismissFilter);
        TextView txtDoFilter = layoutFilter.findViewById(R.id.txtDoFilter);

        txtDateToFilter = layoutFilter.findViewById(R.id.txtDateToFilter);
        txtDateFromFilter = layoutFilter.findViewById(R.id.txtDateFromFilter);

        tagGroupFilter = layoutFilter.findViewById(R.id.tagGroupFilter);
        progressLoadingFilter = layoutFilter.findViewById(R.id.progressLoadingFilter);

        progressLoadingFilter.setVisibility(View.VISIBLE);

// txt dismiss
        txtDismissFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogFilters.dismiss();
            }
        });

// txt Delete
        txtDeleteFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Txtsearch = "";
                String url = "http://nemayman.com/wp-json/wp/v2/posts?_embed&&per_page=10&&page=";
                setDataOnRec(url);
                txtDateFromFilter.setText(getString(R.string.txtDateFrom));
                txtDateToFilter.setText(getString(R.string.txtDateTo));
                alertDialogFilters.dismiss();
            }
        });

// txtDate From
        txtDateFromFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersianCalendar persianCalendar = new PersianCalendar();
                persianCalendar.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        Activity_Main_Nema.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
                datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
//                datePickerDialog.setThemeDark(true);
                reqDate = "txtdateBeforFilter";
            }
        });

// txtDate To
        txtDateToFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersianCalendar persianCalendar = new PersianCalendar();
                persianCalendar.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        Activity_Main_Nema.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
                datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
//                datePickerDialog.setThemeDark(true);
                reqDate = "txtDateTo";
            }
        });

// tagGP

        String urlCategory = "http://nemayman.com/wp-json/wp/v2/categories?per_page=100";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, urlCategory, new OkResListenerImg(), new ErrListenerImg());
        queue.add(request);

        tagGroupFilter.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String s) {

                txtCategoriesFilter.setText(getString(R.string.txtCategories) + " " + s);

                for (int y = 0; y < modPostsCategory.size(); y++) {
                    if (modPostsCategory.get(y).nameCategoryFilter.equals(s)) {
                        CategoryFilter = modPostsCategory.get(y).idCategoryFilter + "";
                        break;
                    }
                }

            }
        });

// txt Do filter
        txtDoFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Txtsearch = "";
                String url = "http://nemayman.com/wp-json/wp/v2/posts?_embed&&per_page=10&&page=";


                String symbol = "-";
                if (dateBeforFilter != null && dateAfterFilter != null) {
                    if (Utility.hasPermission(dateBeforFilter, dateAfterFilter, symbol)) {
                        Toast.makeText(getApplicationContext(), "بازه انتخابی اشتباه است", Toast.LENGTH_LONG).show();
                    } else {
                        if (!dateAfterFilter.equals(""))
                            Txtsearch += "&&after=" + dateAfterFilter;
                        if (!dateBeforFilter.equals(""))
                            Txtsearch += "&&before=" + dateBeforFilter;
                    }
                }


                if (!CategoryFilter.equals(""))
                    Txtsearch += "&&categories=" + CategoryFilter;

                setDataOnRec(url);
                alertDialogFilters.dismiss();


            }
        });


        builderFilter.setView(layoutFilter);
        alertDialogFilters = builderFilter.create();
        alertDialogFilters.show();


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        String symbol = "-";

        if (reqDate.equals("txtdateBeforFilter")) {
            dateAfterFilter = Utility.getGlobalData(year, monthOfYear + 1, dayOfMonth, symbol);
            txtDateFromFilter.setText(getString(R.string.txtDateFrom) + " " + date);
        } else {
            dateBeforFilter = Utility.getGlobalData(year, monthOfYear + 1, dayOfMonth, symbol);
            txtDateToFilter.setText(getString(R.string.txtDateTo) + " " + date);
        }


    }

    private class OkResListenerImg implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            JsonGetCategory(response.toString());
        }
    }

    private class ErrListenerImg implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {

            NetworkResponse response = error.networkResponse;
            if (error instanceof ServerError && response != null) {
                try {
                    String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    // Now you can use any deserializer to make sense of data
                    JSONObject obj = new JSONObject(res);
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
                    e2.printStackTrace();
                }
            }
        }
    }

    private void JsonGetCategory(String result) {

        modPostsCategory = new ArrayList<>();
        try {

            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                int id = object.getInt("id");
                String category = object.getString("name");

                ModPosts modPosts = new ModPosts();
                modPosts.idCategoryFilter = id;
                modPosts.nameCategoryFilter = category;

                modPostsCategory.add(modPosts);
            }

            List<String> listTagGP = new ArrayList<>();

            for (int j = 0; j < modPostsCategory.size(); j++)
                listTagGP.add(modPostsCategory.get(j).nameCategoryFilter);

            progressLoadingFilter.setVisibility(View.GONE);
            tagGroupFilter.setTags(null, listTagGP);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

// End AlertDialogFilter

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ourWebSite) {
            IntentUtility.browseWebsite(Activity_Main_Nema.this, "http://nemayman.com/");
        } else if (id == R.id.nav_resume) {
            IntentUtility.browseWebsite(Activity_Main_Nema.this, "http://nemayman.com/%d9%86%d9%85%d9%88%d9%86%d9%87-%da%a9%d8%a7%d8%b1%d9%87%d8%a7/");
        } else if (id == R.id.nav_teach) {
            IntentUtility.browseWebsite(Activity_Main_Nema.this, "http://nemayman.com/%d8%aa%d9%85%d8%a7%d8%b3-%d8%a8%d8%a7-%d9%85%d8%a7/#");
        } else if (id == R.id.nav_seo) {
            IntentUtility.browseWebsite(Activity_Main_Nema.this, "http://nemayman.com/category/learning/support/");
        } else if (id == R.id.nav_aboutUs) {
            IntentUtility.browseWebsite(Activity_Main_Nema.this, "http://nemayman.com/%d8%af%d8%b1%d8%a8%d8%a7%d8%b1%d9%87-%d9%86%d9%85%d8%a7%db%8c-%d9%85%d9%86/");
        } else if (id == R.id.nav_contactUs) {
            IntentUtility.sendEmail(Activity_Main_Nema.this, "support@nemayman.com", "درخواست پشتیبانی", "Send Email To Nemayman.com");
        } else if (id == R.id.nav_share) {
            IntentUtility.share(Activity_Main_Nema.this, "http://nemayman.com/", "طراحی وبسایت", "انتخاب کنید");
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity__main__nema, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionSearch) {

            Intent intent = new Intent(Activity_Main_Nema.this, Activity_Search_Nema.class);
            startActivity(intent);

            return true;
        } else if (id == R.id.actionFilter) {


            AlertDialogFilter();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void findViews() {
        recyclerPosts = findViewById(R.id.recyclerPosts);
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        progressLoading = findViewById(R.id.progressLoading);
    }

}

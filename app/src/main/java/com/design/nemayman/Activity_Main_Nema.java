package com.design.nemayman;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Toast;

import com.design.nemayman.Adapters.AdRecyclePosts;
import com.design.nemayman.Connects.ConPosts;
import com.design.nemayman.Models.ModPosts;

import java.util.ArrayList;
import java.util.List;

import io.github.meness.Library.Utils.IntentUtility;

public class Activity_Main_Nema extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    RecyclerView recyclerPosts;
    Toolbar toolbar;
    LinearLayoutManager manager;
    AdRecyclePosts postsAdapter;
    List<ModPosts> data;
    Boolean isScrolling = false;
    int currentItem, totalItems, scrollOutItems;
    String url;
    int page = 1;

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
        setDataOnRec();

    }

    private void setDataOnRec() {

        url = "http://nemayman.com/wp-json/wp/v2/posts?_embed&&per_page=10&&page=";
        data = new ArrayList<>();
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        try {
            ConPosts conPosts = new ConPosts(Activity_Main_Nema.this, url + page);

            conPosts.getModPostsFromUrl(new ConPosts.OnPostResponse() {
                @Override
                public void onPostResponse(List<ModPosts> response) {

                    for (int i = 0; i < response.size(); i++) {
                        data.add(response.get(i));
                    }

                    postsAdapter = new AdRecyclePosts(Activity_Main_Nema.this, data);
                    recyclerPosts.setAdapter(postsAdapter);
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

                            if (isScrolling && (currentItem + scrollOutItems == totalItems)) {
                                isScrolling = false;
                                page++;
                                ConPosts conPosts = new ConPosts(Activity_Main_Nema.this, url + page);
                                conPosts.getModPostsFromUrl(new ConPosts.OnPostResponse() {
                                    @Override
                                    public void onPostResponse(List<ModPosts> response) {

                                        Toast.makeText(Activity_Main_Nema.this, page+"", Toast.LENGTH_SHORT).show();

//                                        for (int i = 0; i < response.size(); i++) {
//                                            data.add(response.get(i));
//                                        }
//                                        postsAdapter.notifyDataSetChanged();


                                    }
                                });

                            }

                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e("err", e.getMessage());
        }

    }

    private void findViews() {
        recyclerPosts = findViewById(R.id.recyclerPosts);
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
}

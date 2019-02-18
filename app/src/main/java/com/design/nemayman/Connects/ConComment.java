package com.design.nemayman.Connects;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.design.nemayman.Models.ModPosts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConComment {


    public Context context;
    public String url;


    public ConComment(Context context, String url) {
        this.context = context;
        this.url = url;
    }


    public void getDataFromUrlComment(final OnPostResponseComment onPostResponse) {



        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        onPostResponse.onPostResponse(ParsingPostJSONComment(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("errAppWp", error.getMessage());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }


    private List<ModPosts> ParsingPostJSONComment(JSONArray response) {


        String commentTex = "null";
        String authorName = "null";
        String authorAvatarUrls = "null";


        try {
            List<ModPosts> posts = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {

                JSONObject object = response.getJSONObject(i);

// txtComment
                JSONObject objComment = object.getJSONObject("content");
                commentTex = objComment.getString("rendered");

// authorName
                authorName = object.getString("author_name");

// img
                JSONObject objImg = object.getJSONObject("author_avatar_urls");
                authorAvatarUrls = objImg.getString("96");



                ModPosts post = new ModPosts();
                post.commentTxt = commentTex;
                post.commentAuthorName = authorName;
                post.commentAuthorAvatarUrls = authorAvatarUrls;
                posts.add(post);
            }
            return posts;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public interface OnPostResponseComment {
        void onPostResponse(List<ModPosts> response);
    }


}


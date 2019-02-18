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

public class ConPosts {

    public Context context;
    public String url;


    public ConPosts(Context context, String url) {
        this.context = context;
        this.url = url;
    }


    public void getModPostsFromUrl(final OnPostResponse onPostResponse) {


        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        onPostResponse.onPostResponse(ParsingPostJSON(response));
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


    private List<ModPosts> ParsingPostJSON(JSONArray response) {

        int idPost = 0;
        int idPostMedia = 0;

        String postTitle = "null";
        String postDec = "null";
        String postExcerpt = "null";
        String postImgmediumUrl = "null";
        String postImgFullUrl = "null";
        String nameCat = "null";

        try {
            List<ModPosts> posts = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {

                JSONObject object = response.getJSONObject(i);
// id
                idPost = object.getInt("id");
                idPostMedia = object.getInt("featured_media");
// title
                JSONObject objTitle = object.getJSONObject("title");
                postTitle = objTitle.getString("rendered");
                postTitle = postTitle.replace("&#8211;", " - ");
                postTitle = postTitle.replace("&#8230;", "... ");
// dec
                JSONObject objDec = object.getJSONObject("content");
                postDec = objDec.getString("rendered");
// short dec
                JSONObject objexcerpt = object.getJSONObject("excerpt");
                postExcerpt = objexcerpt.getString("rendered");

                String objEmbedded = object.getString("_embedded");
                JSONObject object2 = new JSONObject(objEmbedded);
// pic
                JSONArray arrayWpFeaturedmedia = object2.getJSONArray("wp:featuredmedia");
                for (int j = 0; j < arrayWpFeaturedmedia.length(); j++) {
                    JSONObject objMedia = arrayWpFeaturedmedia.getJSONObject(j);
                    JSONObject objMediaDetails = objMedia.getJSONObject("media_details");
                    JSONObject objSizes = objMediaDetails.getJSONObject("sizes");
                    JSONObject objThumbnail = objSizes.getJSONObject("medium");
                    postImgmediumUrl = objThumbnail.getString("source_url");

                    JSONObject objFull = objSizes.getJSONObject("full");
                    postImgFullUrl = objFull.getString("source_url");

//                    postImgmediumUrl = objThumbnail.getString("source_url").replace("http://localhost:8080/", "http://192.168.56.1:8080/");
                }

                JSONArray arrayWpTerm = object2.getJSONArray("wp:term");

                JSONArray values = arrayWpTerm.getJSONArray(0);
                for (int w = 0; w < values.length(); w++) {
                    JSONObject objCat = values.getJSONObject(w);
                    nameCat = objCat.getString("name");
                }


                ModPosts post = new ModPosts();
                post.idPost = idPost;
                post.idPostMedia = idPostMedia;
                post.postTitle = postTitle;
                post.postDescription = postDec;
                post.postExcerpt = postExcerpt;
                post.imgPostMedium = postImgmediumUrl;
                post.category = nameCat;
                post.postImgFullUrl = postImgFullUrl;

                posts.add(post);
            }
            return posts;

        } catch (JSONException e) {
            Log.d("errAppWp", e.getMessage());
            return null;
        }
    }


    public interface OnPostResponse {
        void onPostResponse(List<ModPosts> response);
    }


}

package com.design.nemayman;

import android.app.Application;

import com.design.nemayman.Classes.TypefaceUtil;

public class BaseActivity_Nema extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/IRANSansMobile.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf

    }

}

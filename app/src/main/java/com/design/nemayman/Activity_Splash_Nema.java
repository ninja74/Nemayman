package com.design.nemayman;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.design.nemayman.Classes.checkInternet;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class Activity_Splash_Nema extends AppCompatActivity {

    private checkInternet internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_nema);

        internet = new checkInternet(Activity_Splash_Nema.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (internet.CheckNetworkConnection()){
                    Intent intent = new Intent(Activity_Splash_Nema.this, Activity_Main_Nema.class);
                    startActivity(intent);
                    finish();
                }else {

                }


            }

        }, 2000);

    }

    private void CheckNet() {
        PrettyDialog prettyDialog = new PrettyDialog(Activity_Splash_Nema.this);
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

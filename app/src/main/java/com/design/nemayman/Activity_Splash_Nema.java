package com.design.nemayman;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.design.nemayman.Classes.checkInternet;

import es.dmoral.toasty.Toasty;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class Activity_Splash_Nema extends AppCompatActivity {

    private checkInternet internet;
    private TextView txtTryAgainSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_nema);
        findViews();
        setdefault();
        internet = new checkInternet(Activity_Splash_Nema.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (internet.CheckNetworkConnection()) {
                    Intent intent = new Intent(Activity_Splash_Nema.this, Activity_Main_Nema.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toasty.error(Activity_Splash_Nema.this, getString(R.string.toastSplashCantConnect), Toast.LENGTH_SHORT, true).show();
                    txtTryAgainSplash.setVisibility(View.VISIBLE);
                }

            }

        }, 2000);

        txtTryAgainSplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internet.CheckNetworkConnection()) {
                    Intent intent = new Intent(Activity_Splash_Nema.this, Activity_Main_Nema.class);
                    startActivity(intent);
                    finish();
                } else
                    Toasty.error(Activity_Splash_Nema.this, getString(R.string.toastSplashCantConnect), Toast.LENGTH_SHORT, true).show();
            }
        });

    }

    private void setdefault() {
        txtTryAgainSplash.setVisibility(View.GONE);
    }

    private void findViews() {
        txtTryAgainSplash = findViewById(R.id.txtTryAgainSplash);
    }

    private void checkNet() {
        PrettyDialog prettyDialog = new PrettyDialog(Activity_Splash_Nema.this);
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

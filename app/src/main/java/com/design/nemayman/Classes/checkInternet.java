package com.design.nemayman.Classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class checkInternet {
    Context _context;

    public checkInternet(Context context)
    {
        _context=context;
    }

    public boolean CheckNetworkConnection()
    {
        ConnectivityManager manager=
                (ConnectivityManager)
                        _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo=manager.getActiveNetworkInfo();

        if(networkInfo!=null && networkInfo.isConnected())
            return true;
        return  false;
    }

}

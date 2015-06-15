package com.arcgis.uploadofflinedata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.Date;
import java.util.Timer;

/**
 * Created by EMonegiser on 2015/6/10.
 */
public class NetReceiver extends BroadcastReceiver{


    private final static String TAG="NetReceiver";

    public NetReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            Log.i(TAG,"NetReceiver onReceive");
            Timer timer = new Timer();
            timer.schedule(new QunXTask(context), new Date());
        }
    }
}

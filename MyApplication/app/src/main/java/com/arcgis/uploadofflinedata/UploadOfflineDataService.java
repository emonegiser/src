package com.arcgis.uploadofflinedata;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.arcgis.activity.DZZHAddActivity;
import com.arcgis.dao.DZZHDao;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.NetUtils;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pang congcong on 2015/5/24.
 */
public class UploadOfflineDataService  extends Service{

    private final String TAG="UploadService";

    DZZHDao dzzhDao=null;

    //全局变量存储位置
    private App MyApp;

    private NetReceiver mReceiver = new NetReceiver();

    private static IConnectState onGetConnectState;

    public static IConnectState getOnGetConnectState() {
        return onGetConnectState;
    }

    public void setOnGetConnectState(IConnectState onGetConnectState)
    {
        this.onGetConnectState = onGetConnectState;
    }

    private Binder binder = new MyBinder();

    public static boolean isContected = true;

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dzzhDao.close();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp=(App) this.getApplication();

        Log.i(TAG,"------------Register BroadcastReceiver-------------");
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 添加接收网络连接状态改变的Action
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyBinder extends Binder
    {
        public UploadOfflineDataService getService()
        {
            Log.i(TAG,"------------Bind UploadService-------------");

            return UploadOfflineDataService.this;
        }
    }


}

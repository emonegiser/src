package com.arcgis.httputil;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

/**
 * Created by mars on 2015/3/5.
 */
public class GPSUtil {

    //判断是否打开
    public static boolean isOpen(Context context){
        LocationManager locationManager= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean gps=false;
        boolean network=false;

        if(locationManager!=null){
            gps=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }

        if(gps || network){
            return true;
        }
        return false;
    }

    //强制打开GPS
    public static void openGPS(Context context) {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        context.sendBroadcast(intent);
    }

    public static void closeGPS(Context context) {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", false);
        context.sendBroadcast(intent);
    }
}

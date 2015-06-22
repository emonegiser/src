package com.arcgis.gpsservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.arcgis.httputil.App;
import com.arcgis.httputil.ToastUtil;

import java.text.DecimalFormat;
import java.util.Iterator;

public class GPSService extends Service {

    private LocationManager lm;
    private Location location;
    private static final String TAG = "GPSService";
    DecimalFormat df = new DecimalFormat("#.00000000");
    DecimalFormat dfAccu = new DecimalFormat("#.0000");

    //全局变量存储位置
    private App MyApp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp=(App) this.getApplication();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // 判断GPS是否正常启动
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ToastUtil.show(GPSService.this, "请开启GPS");
            //返回开启GPS导航设置界面
            //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //GPSService.this.startActivityForResult(intent, 0);
            return 0;
        }

        // 为获取地理位置信息时设置查询条件
        String bestProvider = lm.getBestProvider(getCriteria(), true); // 获取GPS信息

        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        location = lm.getLastKnownLocation(bestProvider);// 通过GPS获取位置

//        ToastUtil.show(this,"GPSService\n 经度:"+location.getLongitude()+"\n 纬度:"+location.getLatitude());
        if(MyApp!=null){
            if(location !=null){
                MyApp.setmGPSLat(String.valueOf(df.format(location.getLatitude())));
                MyApp.setmGPSLon(String.valueOf(df.format(location.getLongitude())));
                MyApp.setmGPSAccu(String.valueOf(dfAccu.format(location.getAccuracy())));
            }
        }

        // 监听状态
        lm.addGpsStatusListener(listener);
        // 绑定监听，有4个参数
        // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        // 参数2，位置信息更新周期，单位毫秒
        // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        // 参数4，监听
        // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
        // 1秒更新一次，或最小位移变化超过1米更新一次;
        // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread,在run中sleep(10000);然后执行handler.sendMessage(),更新位置

        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10*1000, 2  ,locationListener);
        }else{
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10*1000, 2,locationListener);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // 位置监听
    private LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {

            if(MyApp!=null){
                MyApp.setmGPSLat(String.valueOf(df.format(location.getLatitude())));
                MyApp.setmGPSLon(String.valueOf(df.format(location.getLongitude())));
                MyApp.setmGPSAccu(String.valueOf(dfAccu.format(location.getAccuracy())));
            }

             ToastUtil.show(GPSService.this,"onLocationChanged"+"\n"+"经度:"+location.getLongitude()+"\n"+"纬度:"+location.getLatitude()+"");

        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location = lm.getLastKnownLocation(provider);
            ToastUtil.show(GPSService.this,"onProviderEnabled"+"\n"+"GPS已开启");
            if(MyApp!=null){
                MyApp.setmGPSLat(String.valueOf(location.getLatitude()));
                MyApp.setmGPSLon(String.valueOf(location.getLongitude()));
                MyApp.setmGPSAccu(String.valueOf(location.getAccuracy()));
            }
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            ToastUtil.show(GPSService.this,"onProviderDisabled"+"\n"+"GPS禁用!");
        }

    };

    // 状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {

        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i(TAG, "卫星状态改变");
                    // 获取当前状态
                    GpsStatus gpsStatus = lm.getGpsStatus(null);
                    // 获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    // 创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();

                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    System.out.println("搜索到：" + count + "颗卫星");
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "定位启动");
                    ToastUtil.show(GPSService.this,"定位启动");
                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "定位结束");
                    ToastUtil.show(GPSService.this,"定位结束");
                    break;
            }
        };
    };

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(true);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
}

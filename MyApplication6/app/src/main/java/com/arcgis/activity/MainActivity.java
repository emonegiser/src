package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.arcgis.R;
import com.arcgis.activity.toolsUtil.UpdateManager;
import com.arcgis.emergency.ViewLoginActivity;
import com.arcgis.entity.DZZHYJEntity;
import com.arcgis.gpsservice.GPSService;
import com.arcgis.gpsservice.GPSStopThread;
import com.arcgis.httputil.App;
import com.arcgis.httputil.GPSUtil;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.arcgis.selectdao.DZSYSDICEntityDao;
import com.arcgis.selectentity.DZSYSDICEntity;
import com.esri.android.runtime.ArcGISRuntime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends Activity implements View.OnClickListener{

    LinearLayout linear01;
    LinearLayout linear02;
    LinearLayout linear03;
//    LinearLayout linear04;
    LinearLayout linear05;
//    LinearLayout linear06;
//    LinearLayout linear07;
//    LinearLayout linear08;
    LinearLayout linear09;
    LinearLayout linear10;
    LinearLayout linear11;
//    LinearLayout linear13;
//    LinearLayout linear14;

    private boolean isExits = false;
    private Timer timer;
    private TimerTask timerTask;

    private KsoapValidateHttp ksoap;
    //全局变量存储位置
    private App MyApp;

    //矿产类型
    List<String> kclx_list=new ArrayList<>();
    //乡镇
    List<String> xiangzhen_list=new ArrayList<>();
    List<String> XZName_list = new ArrayList<>();
    //村
    List<String> cun_list=new ArrayList<>();
    //处理状态
    List<String> clzt_list=new ArrayList<>();


    private DZSYSDICEntityDao dzsysdicEntityDao=null;
    private boolean isNetwork=false;

    private String Code=null;//版本号
    private UpdateManager mUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");

        linear01= (LinearLayout) findViewById(R.id.linear01);
        linear02= (LinearLayout) findViewById(R.id.linear02);
        linear03= (LinearLayout) findViewById(R.id.linear03);
//        linear04= (LinearLayout) findViewById(R.id.linear04);
        linear05= (LinearLayout) findViewById(R.id.linear05);
//        linear06= (LinearLayout) findViewById(R.id.linear06);
//        linear07= (LinearLayout) findViewById(R.id.linear07);
//        linear08= (LinearLayout) findViewById(R.id.linear08);
        linear09= (LinearLayout) findViewById(R.id.linear09);
        linear10= (LinearLayout) findViewById(R.id.linear10);
        linear11= (LinearLayout) findViewById(R.id.linear11);
//        linear13= (LinearLayout) findViewById(R.id.linear13);
//        linear14= (LinearLayout) findViewById(R.id.linear14);
        setProgressBarIndeterminateVisibility(true);

        isNetwork=NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);
        dzsysdicEntityDao=new DZSYSDICEntityDao(this);
        linear01.setOnClickListener(this);
        linear02.setOnClickListener(this);
        linear03.setOnClickListener(this);
//        linear04.setOnClickListener(this);
        linear05.setOnClickListener(this);
//        linear06.setOnClickListener(this);
//        linear07.setOnClickListener(this);
//        linear08.setOnClickListener(this);
        linear09.setOnClickListener(this);
        linear10.setOnClickListener(this);
        linear11.setOnClickListener(this);
//        linear13.setOnClickListener(this);
//        linear14.setOnClickListener(this);

            Getcode getcode=new Getcode();
        try {
            Code = getcode.execute().get(50, TimeUnit.SECONDS);
            if(Code!=null){
                mUpdateManager = new UpdateManager(this);
                mUpdateManager.checkUpdateInfo(Code);
            }else{
                ToastUtil.show(MainActivity.this, "获取版本号失败，暂时无法升级软件！");
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        if (isNetwork) {
            GetKCDataSync getDZDataSync=new GetKCDataSync();
            try {
                String DataRslt=getDZDataSync.execute().get(25, TimeUnit.SECONDS);
                if(DataRslt!=null && DataRslt.contains("&&")){
                    DZSYSDICEntity dzsysdicEntity=new DZSYSDICEntity();
                    xiangzhen_list.clear();
                    cun_list.clear();
                    clzt_list.clear();
                    kclx_list.clear();
                    String []RsltArr=DataRslt.split("&&");
                    if(RsltArr!=null && RsltArr.length>0){
                        int len=RsltArr.length;
                        for(int i=0;i<len;i++){
                            String dataElement=RsltArr[i];
                            JSONArray jsonArray = new JSONArray(dataElement);
                            for(int k=0;k<jsonArray.length();k++){
                                JSONObject o = (JSONObject) jsonArray.get(k);
                                if(o.has("XZQMC") && o.get("XZQMC")!=null){
                                    xiangzhen_list.add(o.get("XZQMC").toString());

                                    dzsysdicEntity.setName(o.get("XZQMC").toString());
                                    dzsysdicEntity.setType("XZ");

                                    if(dzsysdicEntityDao.isEntityExist(o.get("XZQMC").toString())){
                                    }else{
                                        dzsysdicEntityDao.add(dzsysdicEntity);
                                    }
                                }else if(o.has("CUNMC") && o.get("CUNMC")!=null){
                                    cun_list.add(o.get("CUNMC").toString());

                                    dzsysdicEntity.setName(o.get("CUNMC").toString());
                                    dzsysdicEntity.setType("CUN");

                                    if(dzsysdicEntityDao.isEntityExist(o.get("CUNMC").toString())){
                                    }else{
                                        dzsysdicEntityDao.add(dzsysdicEntity);
                                    }
                                }else if(o.has("M_TID") && o.has("NAME") && o.get("M_TID")!=null && o.get("NAME")!=null){
                                    kclx_list.add(o.get("NAME").toString());

                                    dzsysdicEntity.setName(o.get("NAME").toString());
                                    dzsysdicEntity.setType("KCLX");

                                    if(dzsysdicEntityDao.isEntityExist(o.get("NAME").toString())){
                                    }else{
                                        dzsysdicEntityDao.add(dzsysdicEntity);
                                    }
                                }else if(o.has("NAME") && o.has("ZT_VALUE") && o.get("NAME")!=null && o.get("ZT_VALUE")!=null){
                                    clzt_list.add(o.get("NAME").toString());

                                    dzsysdicEntity.setName(o.get("NAME").toString());
                                    dzsysdicEntity.setType("KCCLZT");

                                    if(dzsysdicEntityDao.isEntityExist(o.get("NAME").toString())){
                                    }else{
                                        dzsysdicEntityDao.add(dzsysdicEntity);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                ToastUtil.show(MainActivity.this, "连接服务器超时");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                GetXZName getXZ=new GetXZName();
                String xzStr = getXZ.execute().get(50, TimeUnit.SECONDS);
                XZName_list.clear();
                JSONArray jsonArray = new JSONArray(xzStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject o = (JSONObject) jsonArray.get(i);
                    if (o.has("Name") && o.get("Name")!=null){
                        XZName_list.add(o.getString("Name").toString());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

        }else{
            //没有网络，读取本地数据保存的本地
            xiangzhen_list.clear();
            cun_list.clear();
            clzt_list.clear();
            kclx_list.clear();

            List<DZSYSDICEntity> XZList =dzsysdicEntityDao.queryByType("XZ");
            for(DZSYSDICEntity dz:XZList){
                xiangzhen_list.add(dz.getName());
            }

            List<DZSYSDICEntity> CUNList =dzsysdicEntityDao.queryByType("CUN");
            for(DZSYSDICEntity dz:CUNList){
                cun_list.add(dz.getName());
            }

            List<DZSYSDICEntity> KCLXList =dzsysdicEntityDao.queryByType("KCLX");
            for(DZSYSDICEntity dz:KCLXList){
                kclx_list.add(dz.getName());
            }

            List<DZSYSDICEntity> KCCLZTList =dzsysdicEntityDao.queryByType("KCCLZT");
            for(DZSYSDICEntity dz:KCCLZTList){
                clzt_list.add(dz.getName());
            }

        }

        MyApp=(App) this.getApplication();
        if(xiangzhen_list!=null && xiangzhen_list.size()>0){
            xiangzhen_list.add(0,"全部");
            MyApp.setXiangzhen_list(xiangzhen_list);
        }
        if(cun_list!=null && cun_list.size()>0){
            cun_list.add(0,"全部");
            MyApp.setCun_list(cun_list);
        }
        if(clzt_list!=null && clzt_list.size()>0){
            MyApp.setClzt_list(clzt_list);
        }
        if(kclx_list!=null && kclx_list.size()>0){
            MyApp.setKclx_list(kclx_list);
        }
        if(XZName_list!=null&&XZName_list.size()>0){
            MyApp.setCx_list(XZName_list);
        }
    }

    public class GetXZName extends AsyncTask<String, Integer, String> {

        public GetXZName() {
            ksoap=new KsoapValidateHttp(MainActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String XZRslt=ksoap.WebGetXZName();
                if(XZRslt!=null){
                    return XZRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class GetKCDataSync extends AsyncTask<String, Integer, String> {

        String KCxz=null;
        String KCcun=null;
        String KClx=null;
        String KCclzt=null;
        ProgressDialog progressdialog=null;

        public GetKCDataSync() {
            ksoap=new KsoapValidateHttp(MainActivity.this);
            progressdialog=new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressdialog.setCancelable(true);
            progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressdialog.setMessage("数据获取中...");
            progressdialog.show();
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                KCxz=ksoap.WebGetTB_XZ();
                KCcun=ksoap.WebGetTB_CUN();
                KClx=ksoap.WebGetKCLX();
                KCclzt=ksoap.WebGetTB_MINECLZT();
                if(KCxz!=null && KCcun!=null && KClx!=null && KCclzt!=null){
                    return KCxz+"&&"+KCcun+"&&"+KClx+"&&"+KCclzt;
                }else{
                    return "NODATA";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(progressdialog!=null){
                progressdialog.dismiss();
            }
        }
    }

    //获取版本号
    public class Getcode extends AsyncTask<String, Integer, String> {

        public Getcode() {
            ksoap=new KsoapValidateHttp(MainActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String code=ksoap.WebGetVercode();
                if(code!=null){
                    return code;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "退出").setIcon(android.R.drawable.ic_delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                break;

            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("退出软件").setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
								Intent intentGPS = new Intent(MainActivity.this, GPSService.class);
								MainActivity.this.stopService(intentGPS);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.show();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dzsysdicEntityDao.close();
    }

    @Override
    public void onClick(View v) {
        int viewID=v.getId();
        switch (viewID){
            case R.id.linear01:
                Intent map1Intent=new Intent();
                map1Intent.setClass(MainActivity.this,XCRY.class);
                startActivity(map1Intent);
                break;
            case R.id.linear02:
                Intent map2Intent=new Intent();
                map2Intent.setClass(MainActivity.this,MainMap2Activity.class);
                startActivity(map2Intent);
                break;
            case R.id.linear03:
                Intent map3Intent=new Intent();
                map3Intent.setClass(MainActivity.this,MainMap3Activity.class);
                startActivity(map3Intent);
                break;
//            case R.id.linear04:
//                //卫片执法
//                Intent wpzfIntent=new Intent();
//                wpzfIntent.setClass(MainActivity.this,WPZFQueryActivity.class);
//                startActivity(wpzfIntent);
//                break;
            case R.id.linear05:
                Intent XcIntent=new Intent();
                XcIntent.setClass(MainActivity.this,XunChaSBActivity.class);
                startActivity(XcIntent);
                break;
//            case R.id.linear06:
//                //审批用地
//                Intent map4Intent=new Intent();
//                map4Intent.setClass(MainActivity.this,MainMap4Activity.class);
//                startActivity(map4Intent);
//                break;
//            case R.id.linear07:
//                //供应用地
//                Intent map7Intent=new Intent();
//                map7Intent.setClass(MainActivity.this,MainMap5Activity.class);
//                startActivity(map7Intent);
//                break;
//            case R.id.linear08:
//                //储备用地
//                Intent map8Intent=new Intent();
//                map8Intent.setClass(MainActivity.this,MainMap6Activity.class);
//                startActivity(map8Intent);
//                break;
            case R.id.linear09:
                //任务巡查
//                Intent map9Intent=new Intent();
//                map9Intent.setClass(MainActivity.this,XCRWQueryActivity.class);
//                startActivity(map9Intent);
                ToastUtil.show(MainActivity.this, "该模块正在开发完善中");
                break;
            case R.id.linear10:
                //应急指挥
                Intent map10Intent=new Intent();
                map10Intent.setClass(MainActivity.this,ViewLoginActivity.class);
                startActivity(map10Intent);
                break;
            case R.id.linear11:
                //系统设置
                Intent map11Intent=new Intent();
                map11Intent.setClass(MainActivity.this,SettingActivity.class);
                startActivity(map11Intent);
                break;
//            case R.id.linear13:
//                //地灾预警任务
//                Intent intentDzzhyj=new Intent();
//                intentDzzhyj.setClass(MainActivity.this,DZZHYJActivity.class);
//                startActivity(intentDzzhyj);
//                break;
//            case R.id.linear14:
//                //完善应急值守报警信息
//                Intent intentYj=new Intent();
//                intentYj.setClass(MainActivity.this,YJZSPJXXSActivity.class);
//                startActivity(intentYj);
//                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (!isExits) {
                ToastUtil.show(MainActivity.this,"再按一次退出");
                isExits = true;
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (isExits) {
                            isExits = false;
                        }
                    }
                };
                timer.schedule(timerTask, 3000);
            } else {
                GPSUtil.closeGPS(this);

                GPSStopThread gpsStopThread =new GPSStopThread(this);
                gpsStopThread.start();

                MyApp.StopGPS();

                finish();
            }
        }

        return false;
    }
}
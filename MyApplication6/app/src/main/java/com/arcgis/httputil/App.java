package com.arcgis.httputil;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.arcgis.R;
import com.arcgis.emergency.AppGlobal;
import com.arcgis.entity.CBYDEntity;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.entity.GYPZYDEntity;
import com.arcgis.entity.KCZYEntity;
import com.arcgis.entity.PZYDEntity;
import com.arcgis.entity.SBYDEntity;
import com.arcgis.entity.WPURLEntity;
import com.arcgis.entity.WPZFEntity;
import com.arcgis.entity.XCRWEntity;
import com.arcgis.gpsservice.GPSService;
import com.arcgis.gpsservice.GPSStartThread;
import com.arcgis.uploadofflinedata.IConnectState;
import com.arcgis.uploadofflinedata.UploadDZZHData;
import com.arcgis.uploadofflinedata.UploadDZZHinfoData;
import com.arcgis.uploadofflinedata.UploadOfflineDataService;
import com.esri.core.geometry.Point;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.showclear.sc_sip.SipContext;

//程序主入口
public class App extends Application {

    private final String AppLog="AppLog";

    //一下两个字段为绑定服务需要
    UploadOfflineDataService receiveMsgService;
    private boolean conncetState = true; // 记录当前连接状态，因为广播会接收所有的网络状态改变wifi/3g等等，所以需要一个标志记录当前状态
	
	//原始GPS信息
	private String mGPSLat;//维度Y
	private String mGPSLon;//经度X
	private String mGPSAccu;

    //地灾类型
    private List<String> DZType=new ArrayList<>();
    //稳定状态
    private List<String> DZWDName=new ArrayList<>();
    //地灾规模
    private List<String> DZScale=new ArrayList<>();
    //矿产类型
    List<String> kclx_list=new ArrayList<>();
    //乡镇
    List<String> xiangzhen_list=new ArrayList<>();
    //乡镇+编号
    List<String> cx_list = new ArrayList<>();
    //村
    List<String> cun_list=new ArrayList<>();
    //处理状态
    List<String> clzt_list=new ArrayList<>();
    //地质灾害点查询结果
    private List<DZZHEntity> DZZH_list=new ArrayList<>();
    //矿产资源查询结果
    private List<KCZYEntity> KCZY_list=new ArrayList<>();
    //矿产资源
    private List<Point> pointList=new ArrayList<>();
    //上报用地
    private List<Point> sbydpointList=new ArrayList<>();
    //批准用地
    private List<Point> PZYDpointList=new ArrayList<>();
    //供应用地
    private List<Point> GYYDpointList=new ArrayList<>();
    //储备用地
    private List<Point> CBYDpointList=new ArrayList<>();
    //获取上报用地报批批次
    private List<String> SBYD_BPPC_list=new ArrayList<>();
    //查询上报用地结果列表
    private List<SBYDEntity> SBYD_QUERY__list=new ArrayList<>();
    //查询批准用地结果列表
    private List<PZYDEntity> PZYD_QUERY_list=new ArrayList<>();
    //查询储备用地结果列表
    private List<CBYDEntity> CBYD_QUERY_list=new ArrayList<>();
    //供应用地供应方式
    private List<String> GYYD_GYFS_list=new ArrayList<>();
    //供应用地土地用途
    private List<String> GYYD_TDYT_list=new ArrayList<>();
    //查询供应用地结果列表
    private List<GYPZYDEntity> GYPZYD_QUERY_list=new ArrayList<>();
    //储备用地村信息
    private List<String> CBYDCUN_List=new ArrayList<>();
    //巡查任务
    List<XCRWEntity> XCRW_list=new ArrayList<>();
    //未完成巡查任务
    List<XCRWEntity> WWCXCRW_list=new ArrayList<>();
    //已完成巡查任务
    List<XCRWEntity> YWCXCRW_list=new ArrayList<>();
    //卫片执法
    List<WPZFEntity> WPZF_list=new ArrayList<>();
    //卫片URL列表
    List<WPURLEntity> WPURLEntity_list=new ArrayList<>();

    //存储Activity
    private List<Activity> Activity_List = new ArrayList<>();

    private static App instance;

    private Handler handler;

    @Override
	public void onCreate() {
		super.onCreate();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(0==msg.arg1){
                    ToastUtil.show(App.this, "请检查手机存储卡");
                }
            }
        };
        initImageLoader(this);//异步加载图片
        //
        AppGlobal.getInstance().setContext(this);
        AppGlobal.getInstance().setSipContext(new SipContext(this));
        // 启用消息服务
        AppGlobal.getInstance().getSipContext().enableMessageManager();
        //
        AppGlobal.getInstance().getSipContext().startup();

        InitDataSync initDataSync=new InitDataSync();
        initDataSync.execute();

        bind();//绑定服务
    }

    public static App getInstance(){
        if(null == instance){
            instance = new App();
        }
        return instance;
    }

    public App() {}

    //添加Activity到容器中
    public void addActivity(Activity activity){
        Activity_List.add(activity);
    }

    //遍历所有Activity并finish
    public void exit(){
        //关闭GPS
       // StopGPS();

        for(Activity activity:Activity_List){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    public class InitDataSync extends AsyncTask<String, Integer, String> {

        public InitDataSync() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... str) {
            //创建应用目录
            String state = Environment.getExternalStorageState();
            Log.i("APP","Environment状态："+state);

            if (state.equals(Environment.MEDIA_MOUNTED)) {
                //根目录
                String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp";
                //图片
                String imagesDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp/images";
                //视频
                String videoDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp/video";
                //数据库
                String dbDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp/db";
                //shape
                String shapeDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp/shape";

                File rootFile = new File(rootDir);
                if (!rootFile.exists()) {
                    rootFile.mkdirs();
                    Log.i("APP","创建根目录");
                }

                File imagesFile = new File(imagesDir);
                if (!imagesFile.exists()) {
                    imagesFile.mkdirs();
                }

                File videoFile = new File(videoDir);
                if (!videoFile.exists()) {
                    videoFile.mkdirs();
                }

                File dbFile = new File(dbDir);
                if (!dbFile.exists()) {
                    dbFile.mkdirs();
                }

                File shapeFile = new File(shapeDir);
                if (!shapeFile.exists()) {
                    shapeFile.mkdirs();
                }
            } else {
                Message msg=new Message();
                msg.arg1=0;
                handler.sendMessage(msg);
            }

            //初始化数据库
            InitDB(App.this);
            //初始化配置数据库
            InitConfDB(App.this);
            //启动GPS服务
            InitGPS();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public void StopGPS(){

        if(GPSUtil.isOpen(this)){
            GPSUtil.closeGPS(this);
        }
    }

    public void InitGPS(){

        //第一次启动
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        App.this.sendBroadcast(intent);

        //若没有启动，再次启动
        if(!GPSUtil.isOpen(this)){
            GPSUtil.openGPS(this);
        }

        SharedPreferences XCSB_INFO = this.getSharedPreferences("XCSB_INFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = XCSB_INFO.edit();
        editor.putString("ISON","true");
        editor.commit();

        GPSStartThread gpsStartThread=new GPSStartThread(this);
        gpsStartThread.start();

        Intent intentGPS = new Intent(this, GPSService.class);
        this.startService(intentGPS);
    }


    public void InitConfDB(Context context){

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String databasePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/BJApp/db";
            String databaseFilename = databasePath + "/"+ ConstantVar.BJ_CONF_DATABASE_FILENAME;
            File dir = new File(databasePath);

            //若路径不存在，则创建该路径
            if (!dir.exists()) {
                dir.mkdir();
            }

            //若文件不存在，则读取该文件,写到db路径下
            if (!(new File(databaseFilename)).exists()) {
                InputStream is = context.getResources().openRawResource(R.raw.confparam);
                File dbName = new File(databaseFilename);
                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(dbName);
                    byte[] buffer = new byte[1024];
                    int count = 0;

                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }

                    fos.flush();
                    fos.close();
                    is.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    ToastUtil.show(context, "找不到指定文件");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void InitDB(Context context){

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String databasePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/BJApp/db";
            String databaseFilename = databasePath + "/"+ ConstantVar.BJ_DATABASE_FILENAME;
            File dir = new File(databasePath);

            //若路径不存在，则创建该路径
            if (!dir.exists()) {
                dir.mkdir();
            }

            //若文件不存在，则读取该文件,写到db路径下
            if (!(new File(databaseFilename)).exists()) {
                InputStream is = context.getResources().openRawResource(R.raw.dzzh);
                File dbName = new File(databaseFilename);
                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(dbName);
                    byte[] buffer = new byte[1024];
                    int count = 0;

                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }

                    fos.flush();
                    fos.close();
                    is.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    ToastUtil.show(context, "找不到指定文件");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onTerminate() {
        AppGlobal.getInstance().getSipContext().shutdown();
        //
        super.onTerminate();
    }

    private void initImageLoader(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300, true, true, true))
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions).memoryCache(new WeakMemoryCache());
        ImageLoaderConfiguration config = builder.build();
        ImageLoader.getInstance().init(config);
    }

    /////////////////////////////////绑定服务/////////////////////////////////////////

    //服务连接
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            receiveMsgService = ((UploadOfflineDataService.MyBinder) service)
                    .getService();
            receiveMsgService.setOnGetConnectState(new IConnectState() { // 添加接口实例获取连接状态
                @Override
                public void GetState(boolean isConnected) {
                    if (conncetState !=isConnected ) { // 如果当前连接状态与广播服务返回的状态不同才进行通知显示
                        conncetState = isConnected;
                        if (conncetState) {// 已连接
                            Log.i(AppLog, "网络已连接");
                            UploadThread t=new UploadThread();
                            t.run();
                        } else {// 未连接
                            Log.i(AppLog,"网络已经断开");
                        }
                    }
                }
            });
        }
    };

    private void bind() {
        Intent intent = new Intent(App.this, UploadOfflineDataService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    public class  UploadThread extends Thread
    {
        public  void run()
        {
            UploadDZZHData uploadDZZHData=new UploadDZZHData(App.this);
            UploadDZZHinfoData uploadDZZHinfoData=new UploadDZZHinfoData(App.this);

            uploadDZZHData.QueryAndUploadDZZH();
            uploadDZZHinfoData.QueryAndUploadDZZHinfo();
        }
    }

    ///////////////////////////////////绑定服务代码结束///////////////////////////////////////

    public List<XCRWEntity> getWWCXCRW_list() {
        return WWCXCRW_list;
    }

    public void setWWCXCRW_list(List<XCRWEntity> WWCXCRW_list) {
        this.WWCXCRW_list = WWCXCRW_list;
    }

    public List<XCRWEntity> getYWCXCRW_list() {
        return YWCXCRW_list;
    }

    public void setYWCXCRW_list(List<XCRWEntity> YWCXCRW_list) {
        this.YWCXCRW_list = YWCXCRW_list;
    }

    public List<WPURLEntity> getWPURLEntity_list() {
        return WPURLEntity_list;
    }

    public void setWPURLEntity_list(List<WPURLEntity> WPURLEntity_list) {
        this.WPURLEntity_list = WPURLEntity_list;
    }

    public List<WPZFEntity> getWPZF_list() {
        return WPZF_list;
    }

    public void setWPZF_list(List<WPZFEntity> WPZF_list) {
        this.WPZF_list = WPZF_list;
    }

    public List<XCRWEntity> getXCRW_list() {
        return XCRW_list;
    }

    public void setXCRW_list(List<XCRWEntity> XCRW_list) {
        this.XCRW_list = XCRW_list;
    }

    public List<String> getCBYDCUN_List() {
        return CBYDCUN_List;
    }

    public void setCBYDCUN_List(List<String> CBYDCUN_List) {
        this.CBYDCUN_List = CBYDCUN_List;
    }

    public List<Point> getCBYDpointList() {
        return CBYDpointList;
    }

    public void setCBYDpointList(List<Point> CBYDpointList) {
        this.CBYDpointList = CBYDpointList;
    }

    public List<CBYDEntity> getCBYD_QUERY_list() {
        return CBYD_QUERY_list;
    }

    public void setCBYD_QUERY_list(List<CBYDEntity> CBYD_QUERY_list) {
        this.CBYD_QUERY_list = CBYD_QUERY_list;
    }

    public List<Point> getGYYDpointList() {
        return GYYDpointList;
    }

    public void setGYYDpointList(List<Point> GYYDpointList) {
        this.GYYDpointList = GYYDpointList;
    }

    public List<GYPZYDEntity> getGYPZYD_QUERY_list() {
        return GYPZYD_QUERY_list;
    }

    public void setGYPZYD_QUERY_list(List<GYPZYDEntity> GYPZYD_QUERY_list) {
        this.GYPZYD_QUERY_list = GYPZYD_QUERY_list;
    }

    public List<String> getGYYD_GYFS_list() {
        return GYYD_GYFS_list;
    }

    public void setGYYD_GYFS_list(List<String> GYYD_GYFS_list) {
        this.GYYD_GYFS_list = GYYD_GYFS_list;
    }

    public List<String> getGYYD_TDYT_list() {
        return GYYD_TDYT_list;
    }

    public void setGYYD_TDYT_list(List<String> GYYD_TDYT_list) {
        this.GYYD_TDYT_list = GYYD_TDYT_list;
    }

    public List<PZYDEntity> getPZYD_QUERY_list() {
        return PZYD_QUERY_list;
    }

    public void setPZYD_QUERY_list(List<PZYDEntity> PZYD_QUERY_list) {
        this.PZYD_QUERY_list = PZYD_QUERY_list;
    }

    public List<Point> getPZYDpointList() {
        return PZYDpointList;
    }

    public void setPZYDpointList(List<Point> PZYDpointList) {
        this.PZYDpointList = PZYDpointList;
    }

    public List<Point> getSbydpointList() {
        return sbydpointList;
    }

    public void setSbydpointList(List<Point> sbydpointList) {
        this.sbydpointList = sbydpointList;
    }

    public List<SBYDEntity> getSBYD_QUERY__list() {
        return SBYD_QUERY__list;
    }

    public void setSBYD_QUERY__list(List<SBYDEntity> SBYD_QUERY__list) {
        this.SBYD_QUERY__list = SBYD_QUERY__list;
    }

    public List<String> getSBYD_BPPC_list() {
        return SBYD_BPPC_list;
    }

    public void setSBYD_BPPC_list(List<String> SBYD_BPPC_list) {
        this.SBYD_BPPC_list = SBYD_BPPC_list;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    public List<KCZYEntity> getKCZY_list() {
        return KCZY_list;
    }

    public void setKCZY_list(List<KCZYEntity> KCZY_list) {
        this.KCZY_list = KCZY_list;
    }

    public List<DZZHEntity> getDZZH_list() {
        return DZZH_list;
    }

    public void setDZZH_list(List<DZZHEntity> DZZH_list) {
        this.DZZH_list = DZZH_list;
    }

    public List<String> getKclx_list() {
        return kclx_list;
    }

    public void setKclx_list(List<String> kclx_list) {
        this.kclx_list = kclx_list;
    }

    public List<String> getXiangzhen_list() {
        return xiangzhen_list;
    }

    public void setXiangzhen_list(List<String> xiangzhen_list) {
        this.xiangzhen_list = xiangzhen_list;
    }

    public List<String> getCx_list() {
        return cx_list;
    }

    public void setCx_list(List<String> cx_list) {
        this.cx_list = cx_list;
    }

    public List<String> getCun_list() {
        return cun_list;
    }

    public void setCun_list(List<String> cun_list) {
        this.cun_list = cun_list;
    }

    public List<String> getClzt_list() {
        return clzt_list;
    }

    public void setClzt_list(List<String> clzt_list) {
        this.clzt_list = clzt_list;
    }

    public List<String> getDZType() {
        return DZType;
    }

    public void setDZType(List<String> DZType) {
        this.DZType = DZType;
    }

    public List<String> getDZWDName() {
        return DZWDName;
    }

    public void setDZWDName(List<String> DZWDName) {
        this.DZWDName = DZWDName;
    }

    public List<String> getDZScale() {
        return DZScale;
    }

    public void setDZScale(List<String> DZScale) {
        this.DZScale = DZScale;
    }

    public String getmGPSLat() {
        return mGPSLat;
    }

    public void setmGPSLat(String mGPSLat) {
        this.mGPSLat = mGPSLat;
    }

    public String getmGPSLon() {
        return mGPSLon;
    }

    public void setmGPSLon(String mGPSLon) {
        this.mGPSLon = mGPSLon;
    }

    public String getmGPSAccu() {
        return mGPSAccu;
    }

    public void setmGPSAccu(String mGPSAccu) {
        this.mGPSAccu = mGPSAccu;
    }
}

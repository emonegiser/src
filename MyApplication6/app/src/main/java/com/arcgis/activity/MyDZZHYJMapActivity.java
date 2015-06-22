package com.arcgis.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.internal.widget.ActionBarContextView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.arcgis.R;
import com.arcgis.drawtool.DrawEvent;
import com.arcgis.drawtool.DrawEventListener;
import com.arcgis.drawtool.DrawTool;
import com.arcgis.drawtool.LatLonListener;
import com.arcgis.entity.DZZHYJEntity;
import com.arcgis.entity.LatLonPoint;
import com.arcgis.entity.YJSPXXSEntity;
import com.arcgis.gpsservice.GPSService;
import com.arcgis.httputil.App;
import com.arcgis.httputil.ConstantVar;
import com.arcgis.httputil.GPSUtil;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

public class MyDZZHYJMapActivity extends Activity implements DrawEventListener,LatLonListener,View.OnClickListener {

    MapView mMapView;
    private TextView backtextview;
    private TextView titletextview;

    ArcGISDynamicMapServiceLayer dynamicMapServiceLayer;
    //绘制图形层
    private GraphicsLayer drawLayer;
    private DrawTool drawTool;

    //全局变量存储位置
    private App MyApp;
    static MyDZZHYJMapActivity instance;
//    public static int TOADDZHD=1;

    private Graphic drawGraphic;
    private MarkerSymbol markerSymbol;

    private boolean isNetwork=false;
    private  Intent intent;
    private DZZHYJEntity entity;
    private YJSPXXSEntity entitys;

    private TextView mapImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.activity_dzzhyj_map_position);
        intent=getIntent();
        mMapView= (MapView) findViewById(R.id.map);
        backtextview= (TextView) findViewById(R.id.backtextview);
        titletextview= (TextView) findViewById(R.id.titletextview);
        if(intent.hasExtra("data")){
            entity = (DZZHYJEntity) intent.getSerializableExtra("data");
            titletextview.setText(entity.getZHMC());
        }
        if(intent.hasExtra("datas")){
            entitys = (YJSPXXSEntity) intent.getSerializableExtra("datas");
            titletextview.setText(entitys.getDi_type());
            Log.i("position_1",entitys.getE()+entitys.getN());
        }
        instance=MyDZZHYJMapActivity.this;
        App.getInstance().addActivity(this);
        isNetwork=NetUtils.isNetworkAvailable(this);

        backtextview.setOnClickListener(this);


        if (isNetwork) {
            dynamicMapServiceLayer = new ArcGISDynamicMapServiceLayer(ConstantVar.DZZHMAPURL);
            mMapView.removeLayer(0);
            mMapView.addLayer(dynamicMapServiceLayer);
        }else{
            Toast toast = Toast.makeText(this, R.string.offline_message, Toast.LENGTH_SHORT);
            toast.show();
        }

        // 在drawLayer上绘制几何图形
        this.drawLayer = new GraphicsLayer();
        this.mMapView.addLayer(this.drawLayer);
        this.drawTool = new DrawTool(this.mMapView,"MyPositionMapActivity");
        // 此类实现DawEventListener接口
        this.drawTool.addEventListener(this);
        MyApp=(App) this.getApplication();

        mapImg = (TextView)findViewById(R.id.mapImg);
        mapImg.setOnClickListener(this);
    }

    // 实现DrawEventListener中定义的方法
    public void handleDrawEvent(DrawEvent event) {
        // 将画好的图形（已经实例化了Graphic），添加到drawLayer中并刷新显示
        this.drawLayer.addGraphic(event.getDrawGraphic());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance=null;
    }
private String px="1.0",py="1.0";
    @Override
    protected void onResume() {
        super.onResume();

        if(entity!=null){

            //经度
             px=entity.getE();//MyApp.getmGPSLon();
            //纬度
             py=entity.getN();//MyApp.getmGPSLat();
//            Log.i("position_2",entitys.getE()+entitys.getN());
        }
        if(entitys!=null){
            //经度
             px=entitys.getE();//MyApp.getmGPSLon();
            //纬度
             py=entitys.getN();//MyApp.getmGPSLat();
            Log.i("position_2",entitys.getE()+entitys.getN());

        }

       if(Double.parseDouble(px)<=1.0|Double.parseDouble(py)<=1.0){
           Toast.makeText(MyDZZHYJMapActivity.this,"坐标不正确，无法定位",Toast.LENGTH_LONG).show();
           return;
       }
        if( px!=null && py!=null){
            ToastUtil.show(this,"经度:"+px+"\n"+"纬度:"+py);

            this.markerSymbol = new SimpleMarkerSymbol(Color.RED, 15,SimpleMarkerSymbol.STYLE.TRIANGLE);
            Point point = new Point();
            point.setXY(Double.valueOf(px), Double.valueOf(py));
            //GPS坐标转墨卡托
            Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326), SpatialReference.create(102100));
            this.drawGraphic = new Graphic(mapPoint, this.markerSymbol);
            this.drawLayer.addGraphic(drawGraphic);
            mMapView.centerAt(mapPoint,true);
        }
    }

    @Override
    public void handleLatLon(LatLonPoint pt) {
        isNetwork=NetUtils.isNetworkAvailable(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
//                if(requestCode== MyPositionMapActivity.TOADDZHD){
//                    String px=data.getStringExtra("PX");
//                    String py=data.getStringExtra("PY");
//                    this.mMapView.centerAndZoom(Double.valueOf(py),Double.valueOf(px),16);
//                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int viewID=v.getId();
        switch (viewID){
            case R.id.backtextview:
                MyDZZHYJMapActivity.this.finish();
                break;
            case R.id.mapImg:
                if (!isNetwork) break;
                if (mapImg.getText().equals("显示影像")) {
                    ArcGISTiledMapServiceLayer ImgMap = new ArcGISTiledMapServiceLayer(ConstantVar.IMAGEURL);
                    mMapView.addLayer(ImgMap, 1);
                    mapImg.setText("隐藏影像");
                    Toast toast = Toast.makeText(this, "显示影像", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Layer layer = mMapView.getLayerByURL(ConstantVar.IMAGEURL);
                    if (layer != null) {
                        mMapView.removeLayer(layer);
                        Toast toast = Toast.makeText(this, "隐藏影像", Toast.LENGTH_LONG);
                        toast.show();
                    }
                    mapImg.setText("显示影像");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MyDZZHYJMapActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public static MyDZZHYJMapActivity getInstance(){
        return instance;
    }
}
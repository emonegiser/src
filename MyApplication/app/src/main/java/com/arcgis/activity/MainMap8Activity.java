package com.arcgis.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcgis.R;
import com.arcgis.drawtool.DrawEvent;
import com.arcgis.drawtool.DrawEventListener;
import com.arcgis.drawtool.DrawTool;
import com.arcgis.drawtool.GetPolygonCoordsListener;
import com.arcgis.entity.MercatorEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.ConstantVar;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.FillSymbol;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;

import java.util.List;

//卫片执法使用
public class MainMap8Activity extends Activity implements DrawEventListener,GetPolygonCoordsListener,View.OnClickListener {

    MapView mMapView;
    ArcGISDynamicMapServiceLayer imageServiceLayer;
    boolean activeNetwork;
    //绘制图形层
    private GraphicsLayer drawLayer;
    private DrawTool drawTool;
    //全局变量存储位置
    private App MyApp;

    static MainMap8Activity instance;

    public static final int TOADDPAGE=3;

    private Graphic drawGraphic;
    private Graphic polygonGraphic;
    private Graphic textSymbolgp;
    //填充多边形
    private FillSymbol fillSymbol;
    //查询结果图层
    private GraphicsLayer polygonLayer;
    private MarkerSymbol markerSymbol;

    long lx=0l;
    long ly=0l;
    private boolean isNetwork=false;

    private ProgressDialog progressdialog;

    //调用webservice
    private KsoapValidateHttp ksoap;
    private LinearLayout bottomlinear;
    private Polygon polygon;
   private TextView mapImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map8);
        isNetwork=NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);
        mMapView = (MapView)findViewById(R.id.map);
        MyApp=(App) this.getApplication();
        instance=MainMap8Activity.this;

        activeNetwork = NetUtils.isNetworkAvailable(MainMap8Activity.this);
        if (activeNetwork) {
            SharedPreferences WPURL_INFO = getSharedPreferences("WPURL_INFO",MODE_PRIVATE);
            if(WPURL_INFO.getString("MAPURL",null)!=null){
                imageServiceLayer = new ArcGISDynamicMapServiceLayer(WPURL_INFO.getString("MAPURL",null),null);
            }else{
                imageServiceLayer = new ArcGISDynamicMapServiceLayer(ConstantVar.XZQHMAPURL,null);
            }
            mMapView.addLayer(imageServiceLayer);
        }else{
            Toast toast = Toast.makeText(this, R.string.offline_message, Toast.LENGTH_SHORT);
            toast.show();
        }

        bottomlinear= (LinearLayout) findViewById(R.id.bottomlinear);

        TextView backtextview = (TextView) findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);

        TextView titletextview = (TextView) findViewById(R.id.titletextview);
        titletextview.setText("查看卫片");

        mapImg = (TextView)findViewById(R.id.mapImg);
        mapImg.setOnClickListener(this);

        this.fillSymbol = new SimpleFillSymbol(Color.RED);
        this.fillSymbol.setAlpha(90);


        // 在drawLayer上绘制几何图形
        this.drawLayer = new GraphicsLayer();
        this.polygonLayer=new GraphicsLayer();
        this.mMapView.addLayer(this.drawLayer);
        this.mMapView.addLayer(this.polygonLayer);
        this.drawTool = new DrawTool(this.mMapView,"MainMap4Activity");
        // 此类实现DawEventListener接口
        this.drawTool.addEventListener(this);

        MyApp=(App) this.getApplication();

        if(MyApp.getmGPSLon()!=null &&!MyApp.getmGPSLon().equals("")&&MyApp.getmGPSLat()!=null&&!MyApp.getmGPSLat().equals(""))
        {
            ToastUtil.show(MainMap8Activity.this, "定位当前位置 经度：" + MyApp.getmGPSLon() + "  纬度：" + MyApp.getmGPSLat());
            SharedPreferences sp = MainMap8Activity.this.getSharedPreferences("LOGIN_INFO",  MainMap8Activity.this.MODE_PRIVATE);
            PictureMarkerSymbol locationSymbol = new PictureMarkerSymbol(this.getResources().getDrawable(R.drawable.location));
            Point point = new Point();
            point.setXY(Double.valueOf(MyApp.getmGPSLon()), Double.valueOf(MyApp.getmGPSLat()));
            //西安坐标转墨卡托
            Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326), SpatialReference.create(102100));
            this.drawGraphic = new Graphic(mapPoint,locationSymbol);
            this.drawLayer.addGraphic(drawGraphic);
            //设置点样式的颜色，大小和文本内容
            TextSymbol ts = new TextSymbol (14,sp.getString("NAME",null),Color.BLACK);
            Graphic gp = new Graphic(mapPoint,ts);
            //添加到图层中显示
            this.drawLayer.addGraphic(gp);
            mMapView.centerAt(mapPoint,true);
            mMapView.setScale(3000);
        }
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
    public static MainMap8Activity getInstance(){
        if(instance!=null){
            return instance;
        }
        return null;
    }


    @Override
    public void handleLatLon(List<MercatorEntity> LatLonPoint_List) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                if(requestCode== MainMap8Activity.TOADDPAGE){
                    this.markerSymbol = new SimpleMarkerSymbol(Color.RED, 15,SimpleMarkerSymbol.STYLE.TRIANGLE);
                    Point point = new Point();
                    point.setXY(lx, ly);
                    this.drawGraphic = new Graphic(point, this.markerSymbol);
                    this.drawLayer.addGraphic(drawGraphic);
                    mMapView.centerAt(point,true);
                }
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
                MainMap8Activity.this.finish();
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
    protected void onResume() {
        super.onResume();
        Intent intent=this.getIntent();

        if(intent!=null){
            String WPURLStr=intent.getStringExtra("WPURL");

        }
    }
}
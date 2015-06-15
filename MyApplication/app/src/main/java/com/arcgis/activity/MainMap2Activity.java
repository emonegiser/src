package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

//矿产资源
public class MainMap2Activity extends Activity implements DrawEventListener,GetPolygonCoordsListener
                                                            ,View.OnClickListener{

    MapView mMapView;
    ArcGISDynamicMapServiceLayer dynamicMapServiceLayer;
    private TextView titletextview;
    //绘制图形层
    private GraphicsLayer drawLayer;
    //查询结果图层
    private GraphicsLayer polygonLayer;
    private DrawTool drawTool;
    //全局变量存储位置
    private App MyApp;
    private KsoapValidateHttp ksoap;

    static MainMap2Activity instance;

    public static final int TOADDPAGE=1;

    //矿产面中心点
    long lx=0l;
    long ly=0l;

    private Graphic drawGraphic;
    private Graphic polygonGraphic;
    private Graphic textSymbolgp;
    private MarkerSymbol markerSymbol;
    //填充多边形
    private FillSymbol fillSymbol;

    private TextView backtextview;
    private TextView mapImg;
    private TextView addpointTextView;
    private TextView querypointTextView;
    private TextView clearTextView;
    private LinearLayout bottomlinear;
    private Polygon polygon;
    private boolean isNetwork=false;
    private ProgressDialog progressdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map2);

        mMapView = (MapView)findViewById(R.id.map);
        MyApp=(App) this.getApplication();
        instance=MainMap2Activity.this;
        ksoap=new KsoapValidateHttp(MainMap2Activity.this);
        App.getInstance().addActivity(this);

        backtextview= (TextView) findViewById(R.id.backtextview);//返回
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);//标题
        titletextview.setText("矿产资源");

//        mapImg = (TextView)findViewById(R.id.mapImg);//显示影像
//        mapImg.setOnClickListener(this);

        addpointTextView= (TextView) findViewById(R.id.addpointTextView2);
        addpointTextView.setOnClickListener(this);
        querypointTextView= (TextView) findViewById(R.id.querypointTextView2);
        querypointTextView.setOnClickListener(this);
        clearTextView= (TextView) findViewById(R.id.clearTextView2);
        clearTextView.setOnClickListener(this);
        bottomlinear= (LinearLayout) findViewById(R.id.bottomlinear);

        isNetwork= NetUtils.isNetworkAvailable(this);
        if (isNetwork) {
//            dynamicMapServiceLayer = new ArcGISDynamicMapServiceLayer(ConstantVar.XZQHMAPURL);
            ArcGISTiledMapServiceLayer ImgLayler =new ArcGISTiledMapServiceLayer(ConstantVar.IMAGEURL);
            mMapView.addLayer(ImgLayler);
        }else{
            Toast toast = Toast.makeText(this, R.string.offline_message, Toast.LENGTH_SHORT);
            toast.show();
        }
        this.fillSymbol = new SimpleFillSymbol(Color.RED);
        this.fillSymbol.setAlpha(90);

        // 在drawLayer上绘制几何图形
        this.drawLayer = new GraphicsLayer();
        this.polygonLayer=new GraphicsLayer();
        this.mMapView.addLayer(this.drawLayer);
        this.mMapView.addLayer(this.polygonLayer);
        this.drawTool = new DrawTool(this.mMapView,"MainMap2Activity");
        // 此类实现DawEventListener接口
        this.drawTool.addEventListener(this);

        //地图定位
        if(MyApp.getmGPSLon()!=null &&!MyApp.getmGPSLon().equals("")&&MyApp.getmGPSLat()!=null&&!MyApp.getmGPSLat().equals(""))
        {
            ToastUtil.show(MainMap2Activity.this, "定位当前位置 经度：" + MyApp.getmGPSLon() + "  纬度：" + MyApp.getmGPSLat());
            SharedPreferences sp = MainMap2Activity.this.getSharedPreferences("LOGIN_INFO",  MainMap2Activity.this.MODE_PRIVATE);
            PictureMarkerSymbol locationSymbol = new PictureMarkerSymbol(this.getResources().getDrawable(R.drawable.location));
            Point point = new Point();
            point.setXY(Double.valueOf(MyApp.getmGPSLon()), Double.valueOf(MyApp.getmGPSLat()));
            //西安坐标转墨卡托
            Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326), SpatialReference.create(102100));
            this.drawGraphic = new Graphic(mapPoint,locationSymbol);
            this.drawLayer.addGraphic(drawGraphic);
            //设置点样式的颜色，大小和文本内容
            TextSymbol ts = new TextSymbol(14,sp.getString("NAME",null),Color.BLACK);
            Graphic gp = new Graphic(mapPoint,ts);
            //添加到图层中显示
            this.drawLayer.addGraphic(gp);
            mMapView.centerAt(mapPoint,true);
//            mMapView.setScale(2256.994353);
            mMapView.zoomToScale(mapPoint,1128.497176);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MainMap2Activity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static MainMap2Activity getInstance(){
        if(instance!=null){
            return instance;
        }
        return null;
    }

    @Override
    public void handleLatLon(List<MercatorEntity> LatLonPoint_List) {

        progressdialog=new ProgressDialog(MainMap2Activity.this);
        progressdialog.setCancelable(true);
        progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdialog.setMessage("页面跳转中...");
        progressdialog.show();

        String coordStr="";
        DecimalFormat df=(DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(8);
        lx=0l;
        ly=0l;
        if(LatLonPoint_List!=null && LatLonPoint_List.size()>0){
            for(MercatorEntity llp:LatLonPoint_List){
                lx+=llp.getX();
                ly+=llp.getY();
                Point WGS84Point = (Point) GeometryEngine.project(new Point(llp.getX(), llp.getY()),
                        SpatialReference.create(102100), SpatialReference.create(4326));
                coordStr+=df.format(WGS84Point.getX())+" "+df.format(WGS84Point.getY())+";";
            }
            lx=lx/LatLonPoint_List.size();
            ly=ly/LatLonPoint_List.size();
            //跳转至矿产添加页面
            Intent toAddKC=new Intent();
            toAddKC.putExtra("COORDSTR",coordStr);
            toAddKC.setClass(MainMap2Activity.this,KCAddActivity.class);
            startActivityForResult(toAddKC, MainMap2Activity.TOADDPAGE);
        }
        progressdialog.dismiss();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                if(requestCode== MainMap2Activity.TOADDPAGE){
                    this.markerSymbol = new SimpleMarkerSymbol(Color.RED, 15, SimpleMarkerSymbol.STYLE.TRIANGLE);
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
                MainMap2Activity.this.finish();
                break;
            case R.id.addpointTextView2:
                drawTool.activate(DrawTool.POLYGON);
                ToastUtil.show(this, "在地图上点选一个多边形，双击结束");
                break;
            case R.id.querypointTextView2:
                Intent intent=new Intent();
                intent.setClass(this,KCZYQueryActivity.class);
                startActivity(intent);
                break;
            case R.id.clearTextView2:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMap2Activity.this);
                builder.setTitle("确认删除图形?");
                builder.setCancelable(false);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainMap2Activity.this.drawLayer.removeAll();
                        MainMap2Activity.this.drawTool.deactivate();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
//            case R.id.mapImg:
//                if (!isNetwork) break;
//                if (mapImg.getText().equals("显示影像")) {
//                    ArcGISTiledMapServiceLayer ImgMap = new ArcGISTiledMapServiceLayer(ConstantVar.IMAGEURL);
//                    mMapView.addLayer(ImgMap, 1);
//                    mapImg.setText("隐藏影像");
//                    Toast toast = Toast.makeText(this, "显示影像", Toast.LENGTH_LONG);
//                    toast.show();
//                } else {
//                    Layer layer = mMapView.getLayerByURL(ConstantVar.IMAGEURL);
//                    if (layer != null) {
//                        mMapView.removeLayer(layer);
//                        Toast toast = Toast.makeText(this, "隐藏影像", Toast.LENGTH_LONG);
//                        toast.show();
//                    }
//                    mapImg.setText("显示影像");
//                }
//                break;
            default:
                break;
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        Intent intent=this.getIntent();

        if(intent!=null){
            String from=intent.getStringExtra("FROM");
//            if(from !=null && from.equals("ALLPT")){
//                List<com.esri.core.geometry.Point> ptList=new ArrayList<com.esri.core.geometry.Point>();
//                List<KCZYEntity> KCZY_list=new ArrayList<KCZYEntity>();
//
//                if(MyApp.getDZZH_list()!=null && MyApp.getDZZH_list().size()>0){
//                    KCZY_list=MyApp.getKCZY_list();
//                    for(KCZYEntity dzzh:KCZY_list){
//                        ptList.add(new Point(Double.valueOf(dzzh.getPx()),Double.valueOf(dzzh.getPy())));
//                    }
//                }
//
//                if(ptList!=null && ptList.size()>0){
//                    for(Point p:ptList){
//
//                    }
//                }
//            }

            if(from !=null && from.equals("CENTERAT")){
                bottomlinear.setVisibility(View.GONE);
                polygon=new Polygon();
                if(MyApp.getPointList()!=null && MyApp.getPointList().size()>0){
                    List<Point> pl=MyApp.getPointList();
                    Point startPoint = null;
                    Point endPoint = null;

                    for(int i=1;i<pl.size();i++){
                        startPoint = pl.get(i-1);
                        endPoint = pl.get(i);
                        Line line = new Line();
                        line.setStart(startPoint);
                        line.setEnd(endPoint);
                        polygon.addSegment(line, false);
                    }
                    polygon.calculateArea2D();

//                    for(int i=0;i<pl.size();i++){
//                        if (startPoint == null) {
//                            startPoint = pl.get(0);
//                            polygon.startPath(pl.get(0));
//                        } else{
//                            polygon.lineTo(pl.get(i));
//                        }
//                    }
                }
                this.polygonGraphic = new Graphic(polygon, this.fillSymbol);
                this.polygonLayer.addGraphic(polygonGraphic);

                String px=intent.getStringExtra("PX");
                String py=intent.getStringExtra("PY");
                String TEXT=intent.getStringExtra("TEXT");
                if(px!=null && py!=null){
                    this.markerSymbol = new SimpleMarkerSymbol(Color.BLACK, 15, SimpleMarkerSymbol.STYLE.CROSS);
                    Point point = new Point();
                    point.setXY(Double.valueOf(px), Double.valueOf(py));
                    //西安坐标转墨卡托
                    Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(2359), SpatialReference.create(102100));
                    this.drawGraphic = new Graphic(mapPoint, this.markerSymbol);
                    this.drawLayer.addGraphic(drawGraphic);
                    //设置点样式的颜色，大小和文本内容
                    TextSymbol ts = new TextSymbol(14,TEXT,Color.BLACK);
                    textSymbolgp = new Graphic(mapPoint,ts);
                    //添加到图层中显示
                    this.polygonLayer.addGraphic(textSymbolgp);
                    mMapView.centerAt(mapPoint,true);
                }else{
                    ToastUtil.show(this, "该点无西安1980坐标坐标信息");
                }
            }
        }
    }
}
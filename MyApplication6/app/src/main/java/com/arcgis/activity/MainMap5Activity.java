package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.arcgis.selectdao.DZSYSDICEntityDao;
import com.arcgis.selectentity.DZSYSDICEntity;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISImageServiceLayer;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//供应用地使用
public class MainMap5Activity extends Activity implements DrawEventListener,GetPolygonCoordsListener,View.OnClickListener {

    MapView mMapView;
    ArcGISImageServiceLayer imageServiceLayer;
    //绘制图形层
    private GraphicsLayer drawLayer;
    private DrawTool drawTool;
    //全局变量存储位置
    private App MyApp;

    static MainMap5Activity instance;

    private TextView backtextview;
    private TextView titletextview;
    private TextView mapImg;

    //下部按钮
    private TextView addpointTextView;
    private TextView querypointTextView;
    private TextView clearTextView;
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

    //供应用地供应方式
    private List<String> GYYD_GYFS_list=new ArrayList<>();

    //供应用地土地用途
    private List<String> GYYD_TDYT_list=new ArrayList<>();

    private DZSYSDICEntityDao dzsysdicEntityDao=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map5);
        isNetwork=NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);
        mMapView = (MapView)findViewById(R.id.map);
        MyApp=(App) this.getApplication();
        instance=MainMap5Activity.this;

        dzsysdicEntityDao=new DZSYSDICEntityDao(this);

        if (isNetwork) {
            imageServiceLayer = new ArcGISImageServiceLayer(ConstantVar.IMAGEURL,null);
            mMapView.addLayer(imageServiceLayer);
        }else{
            Toast toast = Toast.makeText(this, R.string.offline_message, Toast.LENGTH_SHORT);
            toast.show();
        }


        addpointTextView= (TextView) findViewById(R.id.addpointTextView3);
        addpointTextView.setOnClickListener(this);
        querypointTextView= (TextView) findViewById(R.id.querypointTextView2);
        querypointTextView.setOnClickListener(this);
        clearTextView= (TextView) findViewById(R.id.clearTextView2);
        clearTextView.setOnClickListener(this);
        bottomlinear= (LinearLayout) findViewById(R.id.bottomlinear);

        backtextview= (TextView) findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("供应用地");

        mapImg = (TextView)findViewById(R.id.mapImg);
        mapImg.setOnClickListener(this);

        this.fillSymbol = new SimpleFillSymbol(Color.RED);
        this.fillSymbol.setAlpha(90);


        // 在drawLayer上绘制几何图形
        this.drawLayer = new GraphicsLayer();
        this.polygonLayer=new GraphicsLayer();
        this.mMapView.addLayer(this.drawLayer);
        this.mMapView.addLayer(this.polygonLayer);
        this.drawTool = new DrawTool(this.mMapView,"MainMap5Activity");
        // 此类实现DawEventListener接口
        this.drawTool.addEventListener(this);

        MyApp=(App) this.getApplication();

        if(isNetwork){
            DZSYSDICEntity dzsysdicEntity=new DZSYSDICEntity();
            //初始化供应方式
            GetGYFSDataSync getGYFSDataSync=new GetGYFSDataSync();
            try {
                String GYFSStr=getGYFSDataSync.execute().get(300, TimeUnit.SECONDS);
                if(GYFSStr!=null && !GYFSStr.isEmpty()){
                    GYYD_GYFS_list.clear();
                    JSONArray jsonArray = new JSONArray(GYFSStr);
                    for(int k=0;k<jsonArray.length();k++){
                        JSONObject o = (JSONObject) jsonArray.get(k);
                        if(o.has("NAME") && o.get("NAME")!=null){
                            GYYD_GYFS_list.add(o.get("NAME").toString());
                            dzsysdicEntity.setName(o.get("NAME").toString());
                            dzsysdicEntity.setType("GYYDGYFS");

                            if(!dzsysdicEntityDao.isEntityExist(o.get("NAME").toString())){
                                dzsysdicEntityDao.add(dzsysdicEntity);
                            }
                        }
                    }
                }else{
                    ToastUtil.show(MainMap5Activity.this,"服务器无返回值");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                ToastUtil.show(this,"连接服务器超时");
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.show(this,"解析JSON错误");
            }

            //初始化土地用途
            GetTDYTDataSync getTDYTDataSync=new GetTDYTDataSync();
            try {
                String TDYTStr=getTDYTDataSync.execute().get(300, TimeUnit.SECONDS);
                if(TDYTStr!=null && !TDYTStr.isEmpty()){
                    GYYD_TDYT_list.clear();
                    JSONArray jsonArray = new JSONArray(TDYTStr);
                    for(int k=0;k<jsonArray.length();k++){
                        JSONObject o = (JSONObject) jsonArray.get(k);
                        if(o.has("NAME") && o.get("NAME")!=null){
                            GYYD_TDYT_list.add(o.get("NAME").toString());
                            dzsysdicEntity.setName(o.get("NAME").toString());
                            dzsysdicEntity.setType("GYYDTDYT");

                            if(!dzsysdicEntityDao.isEntityExist(o.get("NAME").toString())){
                                dzsysdicEntityDao.add(dzsysdicEntity);
                            }
                        }
                    }
                }else{
                    ToastUtil.show(MainMap5Activity.this,"服务器无返回值");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                ToastUtil.show(this,"连接服务器超时");
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.show(this,"解析JSON错误");
            }
        }else{
            //没有网络从本地读取
            GYYD_GYFS_list.clear();
            GYYD_TDYT_list.clear();
            List<DZSYSDICEntity> GYYDGYFSList =dzsysdicEntityDao.queryByType("GYYDGYFS");
            for(DZSYSDICEntity dz:GYYDGYFSList){
                GYYD_GYFS_list.add(dz.getName());
            }
            List<DZSYSDICEntity> GYYDTDYTList =dzsysdicEntityDao.queryByType("GYYDTDYT");
            for(DZSYSDICEntity dz:GYYDTDYTList){
                GYYD_TDYT_list.add(dz.getName());
            }
        }

        if(GYYD_GYFS_list!=null && GYYD_GYFS_list.size()>0){
            MyApp.setGYYD_GYFS_list(null);
            MyApp.setGYYD_GYFS_list(GYYD_GYFS_list);
        }

        if(GYYD_TDYT_list!=null && GYYD_TDYT_list.size()>0){
            MyApp.setGYYD_TDYT_list(null);
            MyApp.setGYYD_TDYT_list(GYYD_TDYT_list);
        }
        if(MyApp.getmGPSLon()!=null &&!MyApp.getmGPSLon().equals("")&&MyApp.getmGPSLat()!=null&&!MyApp.getmGPSLat().equals(""))
        {
            ToastUtil.show( MainMap5Activity.this,"定位当前位置 经度："+MyApp.getmGPSLon()+"  纬度："+MyApp.getmGPSLat());
            SharedPreferences sp = MainMap5Activity.this.getSharedPreferences("LOGIN_INFO",  MainMap5Activity.this.MODE_PRIVATE);
            PictureMarkerSymbol locationSymbol = new PictureMarkerSymbol(this.getResources().getDrawable(R.drawable.location));
            Point point = new Point();
            point.setXY(Double.valueOf(MyApp.getmGPSLon()), Double.valueOf(MyApp.getmGPSLat()));
            //西安坐标转墨卡托
            Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326),SpatialReference.create(102100));
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

    public static MainMap5Activity getInstance(){
        if(instance!=null){
            return instance;
        }
        return null;
    }


    @Override
    public void handleLatLon(List<MercatorEntity> LatLonPoint_List) {

        progressdialog=new ProgressDialog(MainMap5Activity.this);
        progressdialog.setCancelable(true);
        progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdialog.setMessage("跳转至添加页面...");
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
            //跳转至供应用地添加页面
            Intent toAddKC=new Intent();
            toAddKC.putExtra("COORDSTR",coordStr);
            toAddKC.setClass(MainMap5Activity.this,GYPZYDAddActivity.class);
            startActivityForResult(toAddKC, MainMap5Activity.TOADDPAGE);
            //startActivity(toAddKC);

            progressdialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                if(requestCode== MainMap5Activity.TOADDPAGE){
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
                MainMap5Activity.this.finish();
                break;
            case R.id.addpointTextView3:
                drawTool.activate(DrawTool.POLYGON);
                ToastUtil.show(this,"在地图上点选一个多边形，双击结束");
                break;
            case R.id.querypointTextView2:
                Intent intent=new Intent();
                intent.setClass(this,GYPZYDQueryActivity.class);
                startActivity(intent);
                break;
            case R.id.clearTextView2:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMap5Activity.this);
                builder.setTitle("确认删除图形?");
                builder.setCancelable(false);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainMap5Activity.this.drawLayer.removeAll();
                        MainMap5Activity.this.drawTool.deactivate();
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


    //供应方式
    public class GetGYFSDataSync extends AsyncTask<String, Integer, String> {

        public GetGYFSDataSync() {
            ksoap=new KsoapValidateHttp(MainMap5Activity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetJSYDGDFS();
                if(AddRslt!=null){
                    return AddRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //土地用途
    public class GetTDYTDataSync extends AsyncTask<String, Integer, String> {

        public GetTDYTDataSync() {
            ksoap=new KsoapValidateHttp(MainMap5Activity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetTB_LANDUSE();
                if(AddRslt!=null){
                    return AddRslt;
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
                if(MyApp.getGYYDpointList()!=null && MyApp.getGYYDpointList().size()>0){
                    List<Point> pl=MyApp.getGYYDpointList();
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

                }
                this.polygonGraphic = new Graphic(polygon, this.fillSymbol);
                this.drawLayer.addGraphic(polygonGraphic);

                String px=intent.getStringExtra("PX");
                String py=intent.getStringExtra("PY");
                String TEXT=intent.getStringExtra("TEXT");
                if(px!=null && py!=null){
                    this.markerSymbol = new SimpleMarkerSymbol(Color.BLACK, 15,SimpleMarkerSymbol.STYLE.CROSS);
                    Point point = new Point();
                    point.setXY(Double.valueOf(px), Double.valueOf(py));
                    //WGS84坐标转墨卡托
                    Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326), SpatialReference.create(102100));
                    this.drawGraphic = new Graphic(mapPoint, this.markerSymbol);
                    this.drawLayer.addGraphic(drawGraphic);
                    //设置点样式的颜色，大小和文本内容
                    TextSymbol ts = new TextSymbol (14,TEXT,Color.BLACK);
                    textSymbolgp = new Graphic(mapPoint,ts);
                    //添加到图层中显示
                    this.polygonLayer.addGraphic(textSymbolgp);
                    mMapView.centerAt(mapPoint,true);
                }else{
                    ToastUtil.show(this,"该点无西安1980坐标信息");
                }
            }
        }
    }
}
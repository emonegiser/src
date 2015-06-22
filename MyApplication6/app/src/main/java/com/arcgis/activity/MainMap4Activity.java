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

//批准用地使用
public class MainMap4Activity extends Activity implements DrawEventListener,GetPolygonCoordsListener,View.OnClickListener {

    MapView mMapView;
    ArcGISImageServiceLayer imageServiceLayer;
    //绘制图形层
    private GraphicsLayer drawLayer;
    private DrawTool drawTool;
    //全局变量存储位置
    private App MyApp;

    static MainMap4Activity instance;

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

    List<String> list_BPPC=new ArrayList<>();
    private LinearLayout bottomlinear;
    private Polygon polygon;
    private DZSYSDICEntityDao dzsysdicEntityDao=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map4);
        App.getInstance().addActivity(this);

        mMapView = (MapView)findViewById(R.id.map);
        MyApp=(App) this.getApplication();
        instance=MainMap4Activity.this;
        dzsysdicEntityDao=new DZSYSDICEntityDao(this);

        isNetwork= NetUtils.isNetworkAvailable(this);
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
        titletextview.setText("批准用地");

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

        //获取报批批次
        if(isNetwork){
            DZSYSDICEntity dzsysdicEntity=new DZSYSDICEntity();
            GetBPPCDataSync getBPPCDataSync=new GetBPPCDataSync();
            try {
                String bppcStr=getBPPCDataSync.execute().get(300, TimeUnit.SECONDS);
                if(bppcStr!=null && !bppcStr.isEmpty()){
                    list_BPPC.clear();
                    JSONArray jsonArray = new JSONArray(bppcStr);
                    for(int k=0;k<jsonArray.length();k++){
                        JSONObject o = (JSONObject) jsonArray.get(k);
                        if(o.has("PC_NAME") && o.get("PC_NAME")!=null){
                            list_BPPC.add(o.get("PC_NAME").toString());

                            dzsysdicEntity.setName(o.get("PC_NAME").toString());
                            dzsysdicEntity.setType("PZYDBPPC");

                            if(!dzsysdicEntityDao.isEntityExist(o.get("PC_NAME").toString())){
                                dzsysdicEntityDao.add(dzsysdicEntity);
                            }
                        }
                    }
                }else{
                    ToastUtil.show(MainMap4Activity.this,"服务器无返回值");
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
            list_BPPC.clear();
            List<DZSYSDICEntity> SBYDBPPCList =dzsysdicEntityDao.queryByType("PZYDBPPC");
            for(DZSYSDICEntity dz:SBYDBPPCList){
                list_BPPC.add(dz.getName());
            }
        }

        if(list_BPPC!=null && list_BPPC.size()>0){
            list_BPPC.add(0,"全部");
            MyApp.setSBYD_BPPC_list(null);
            MyApp.setSBYD_BPPC_list(list_BPPC);
        }

        if(MyApp.getmGPSLon()!=null &&!MyApp.getmGPSLon().equals("")&&MyApp.getmGPSLat()!=null&&!MyApp.getmGPSLat().equals(""))
        {
            ToastUtil.show( MainMap4Activity.this,"定位当前位置 经度："+MyApp.getmGPSLon()+"  纬度："+MyApp.getmGPSLat());
            SharedPreferences sp = MainMap4Activity.this.getSharedPreferences("LOGIN_INFO",  MainMap4Activity.this.MODE_PRIVATE);
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
    public static MainMap4Activity getInstance(){
        if(instance!=null){
            return instance;
        }
        return null;
    }


    @Override
    public void handleLatLon(List<MercatorEntity> LatLonPoint_List) {
        if(!isNetwork){
            ToastUtil.show(this,"请检查网络连接");
            return;
        }
        progressdialog=new ProgressDialog(MainMap4Activity.this);
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
            //跳转至矿产添加页面
            Intent toAddKC=new Intent();
            toAddKC.putExtra("COORDSTR",coordStr);
            toAddKC.setClass(MainMap4Activity.this,PZYDAddActivity.class);
            startActivityForResult(toAddKC, MainMap4Activity.TOADDPAGE);
            //startActivity(toAddKC);

            progressdialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                if(requestCode== MainMap4Activity.TOADDPAGE){
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
                MainMap4Activity.this.finish();
                break;
            case R.id.addpointTextView3:
                drawTool.activate(DrawTool.POLYGON);
                ToastUtil.show(this,"在地图上点选一个多边形，双击结束");
                break;
            case R.id.querypointTextView2:
                Intent intent=new Intent();
                intent.setClass(this,PZYDQueryActivity.class);
                startActivity(intent);
                break;
            case R.id.clearTextView2:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMap4Activity.this);
                builder.setTitle("确认删除图形?");
                builder.setCancelable(false);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainMap4Activity.this.drawLayer.removeAll();
                        MainMap4Activity.this.drawTool.deactivate();
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

    public class GetBPPCDataSync extends AsyncTask<String, Integer, String> {

        public GetBPPCDataSync() {
            ksoap=new KsoapValidateHttp(MainMap4Activity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGettb_jsydbp();
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
                if(MyApp.getPZYDpointList()!=null && MyApp.getPZYDpointList().size()>0){
                    List<Point> pl=MyApp.getPZYDpointList();
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
                    this.markerSymbol = new SimpleMarkerSymbol(Color.BLACK, 15,SimpleMarkerSymbol.STYLE.SQUARE);
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
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
import com.arcgis.entity.DZZHYJEntity;
import com.arcgis.entity.MercatorEntity;
import com.arcgis.entity.XCRWEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.ConstantVar;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISImageServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnLongPressListener;
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

//巡查任务
public class MainMap7Activity extends Activity implements DrawEventListener,
        GetPolygonCoordsListener,View.OnClickListener {

    MapView mMapView;
    ArcGISImageServiceLayer imageServiceLayer;
    boolean activeNetwork;
    //绘制图形层
    private GraphicsLayer drawLayer;
    private DrawTool drawTool;
    //全局变量存储位置
    private App MyApp;

    static MainMap7Activity instance;

    private TextView backtextview;
    private TextView titletextview;
//    private TextView mapImg;

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

    XCRWEntity xcrwEntity=null;
    private String RWLX=null;
    private DZZHYJEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map7);
        App.getInstance().addActivity(this);

        mMapView = (MapView)findViewById(R.id.map);
        MyApp=(App) this.getApplication();
        instance=MainMap7Activity.this;

        activeNetwork = NetUtils.isNetworkAvailable(MainMap7Activity.this);
        if (activeNetwork) {
            ArcGISTiledMapServiceLayer ImgLayler =new ArcGISTiledMapServiceLayer(ConstantVar.IMAGEURL);
            mMapView.addLayer(ImgLayler);
        }else{
            Toast toast = Toast.makeText(this, R.string.offline_message, Toast.LENGTH_SHORT);
            toast.show();
        }


        bottomlinear= (LinearLayout) findViewById(R.id.bottomlinear);

        backtextview= (TextView) findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("巡查任务");

//        mapImg = (TextView)findViewById(R.id.mapImg);
//        mapImg.setOnClickListener(this);

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
            ToastUtil.show( MainMap7Activity.this,"定位当前位置 经度："+MyApp.getmGPSLon()+"  纬度："+MyApp.getmGPSLat());
            SharedPreferences sp = MainMap7Activity.this.getSharedPreferences("LOGIN_INFO",  MainMap7Activity.this.MODE_PRIVATE);
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
            mMapView.setScale(100);


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
    public static MainMap7Activity getInstance(){
        if(instance!=null){
            return instance;
        }
        return null;
    }


    @Override
    public void handleLatLon(List<MercatorEntity> LatLonPoint_List) {
        isNetwork=NetUtils.isNetworkAvailable(this);
//        if(!isNetwork){
//            ToastUtil.show(this,"请检查网络连接");
//            return;
//        }
//        progressdialog=new ProgressDialog(MainMap6Activity.this);
//        progressdialog.setCancelable(true);
//        progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressdialog.setMessage("跳转至添加页面...");
//        progressdialog.show();
//
//        String coordStr="";
//        DecimalFormat df=(DecimalFormat) NumberFormat.getInstance();
//        df.setMaximumFractionDigits(8);
//        lx=0l;
//        ly=0l;
//        if(LatLonPoint_List!=null && LatLonPoint_List.size()>0){
//            for(MercatorEntity llp:LatLonPoint_List){
//                lx+=llp.getX();
//                ly+=llp.getY();
//                Point WGS84Point = (Point) GeometryEngine.project(new Point(llp.getX(), llp.getY()),
//                        SpatialReference.create(102100), SpatialReference.create(4326));
//                coordStr+=df.format(WGS84Point.getX())+" "+df.format(WGS84Point.getY())+";";
//            }
//            lx=lx/LatLonPoint_List.size();
//            ly=ly/LatLonPoint_List.size();
//            //跳转至矿产添加页面
//            Intent toAddKC=new Intent();
//            toAddKC.putExtra("COORDSTR",coordStr);
//            toAddKC.setClass(MainMap6Activity.this,PZYDAddActivity.class);
//            startActivityForResult(toAddKC, MainMap6Activity.TOADDPAGE);
//            //startActivity(toAddKC);
//
//            progressdialog.dismiss();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                if(requestCode== MainMap7Activity.TOADDPAGE){
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
                MainMap7Activity.this.finish();
                break;
            default:
                break;
        }

    }

//    public class GetCBYDCUNDataSync extends AsyncTask<String, Integer, String> {
//
//        public GetCBYDCUNDataSync() {
//            ksoap=new KsoapValidateHttp();
//        }
//
//        @Override
//        protected String doInBackground(String... str) {
//            try {
//                String AddRslt=ksoap.WebGetLandReserveCun();
//                if(AddRslt!=null){
//                    return AddRslt;
//                }else{
//                    return null;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }



    @Override
    protected void onResume() {
        super.onResume();
        Intent intent=this.getIntent();

        if(intent!=null){

//            String from=intent.getStringExtra("FROM");
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
//
//            if(from !=null && from.equals("CENTERAT")){
//                polygon=new Polygon();
//                if(MyApp.getCBYDpointList()!=null && MyApp.getCBYDpointList().size()>0){
//                    List<Point> pl=MyApp.getCBYDpointList();
//                    Point startPoint = null;
//                    Point endPoint = null;
//
//                    for(int i=1;i<pl.size();i++){
//                        startPoint = pl.get(i-1);
//                        endPoint = pl.get(i);
//                        Line line = new Line();
//                        line.setStart(startPoint);
//                        line.setEnd(endPoint);
//                        polygon.addSegment(line, false);
//                    }
//
//                }
//                this.polygonGraphic = new Graphic(polygon, this.fillSymbol);
//                this.drawLayer.addGraphic(polygonGraphic);
            String px;
            String py;
            px=intent.getStringExtra("PX");
            py=intent.getStringExtra("PY");
            String TEXT=intent.getStringExtra("TEXT");
            xcrwEntity= (XCRWEntity) intent.getSerializableExtra("XCRW");
            RWLX=intent.getStringExtra("RWLX");
            entity = (DZZHYJEntity) intent.getSerializableExtra("Data");
            if(entity!=null){
                px=entity.getX();
                py=entity.getY();
            }
            if(px!=null && py!=null){
                this.markerSymbol = new SimpleMarkerSymbol(Color.RED, 15,SimpleMarkerSymbol.STYLE.CIRCLE);
                Point point = new Point();
                point.setXY(Double.valueOf(px), Double.valueOf(py));
                //WGS84坐标转墨卡托
                Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(2359), SpatialReference.create(102100));
                this.drawGraphic = new Graphic(mapPoint, this.markerSymbol);
                this.drawLayer.addGraphic(drawGraphic);
                //设置点样式的颜色，大小和文本内容
                TextSymbol ts = new TextSymbol (14,TEXT,Color.BLACK);
                textSymbolgp = new Graphic(mapPoint,ts);
                //添加到图层中显示
                this.drawLayer.addGraphic(textSymbolgp);
                mMapView.centerAt(mapPoint,true);
            }else{
                ToastUtil.show(this,"该点无西安1980坐标信息");
            }

            mMapView.setOnLongPressListener(new OnLongPressListener() {
                @Override
                public boolean onLongPress(float x, float y) {
                    Point pt = mMapView.toMapPoint(new Point(x, y));
                    int[] id = drawLayer.getGraphicIDs(x, y, 20);
                    if (id != null) {
                        if (xcrwEntity != null) {
                            Intent toMapIntent = new Intent();
                            toMapIntent.setClass(MainMap7Activity.this, DZZHRWEditActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("XCRW", xcrwEntity);
                            toMapIntent.putExtras(bundle);
                            startActivity(toMapIntent);
                        }
                        else if (RWLX.equals("YJ")){
                            Intent toMapIntent2 = new Intent();
                            toMapIntent2.putExtra("Data",entity);
                            toMapIntent2.setClass(MainMap7Activity.this, DZZHYJDetailActivity.class);
                            startActivity(toMapIntent2);
                        }
                        else{ToastUtil.show(MainMap7Activity.this,"任务信息出错");}
                    }
                    return false;
                }
            });

        }
    }
}
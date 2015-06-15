package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.arcgis.R;
import com.arcgis.drawtool.DrawEvent;
import com.arcgis.drawtool.DrawEventListener;
import com.arcgis.drawtool.DrawTool;
import com.arcgis.drawtool.GetPolygonCoordsListener;
import com.arcgis.drawtool.GetPolylineCoordsListener;
import com.arcgis.entity.MercatorEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.ConstantVar;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.arcgis.selectdao.DZSYSDICEntityDao;
import com.arcgis.selectentity.DZSYSDICEntity;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISImageServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.Proximity2DResult;
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

//地质灾害辅助线面绘制页
public class Map1FZ extends Activity implements DrawEventListener,GetPolygonCoordsListener,GetPolylineCoordsListener,View.OnClickListener {

    private final String tag="FeatureEdit";

    ArcGISFeatureLayer fLayer=null;
    GraphicsLayer gLayer = null;
    public static int GraphicID=-1;//gLayer中指定Graphic的id
    Point pointClicked;
    Graphic graphic=null;

    private String flag=null;
    private String TEXT=null;
    private String px=null;
    private String py=null;
    private String type=null;
    String coordStr="";

    MapView mMapView;
    ArcGISImageServiceLayer imageServiceLayer;
    //绘制图形层
    private GraphicsLayer drawLayer=null;
    private DrawTool drawTool;
    //全局变量存储位置
    private App MyApp;

    static Map1FZ instance;

    private TextView backtextview;
    private TextView titletextview;
//    private TextView mapImg;

    //下部按钮
    private TextView fzfinish;
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
        setContentView(R.layout.fz_map1);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);

        mMapView = (MapView)findViewById(R.id.map);

        MyApp=(App) this.getApplication();
        instance=Map1FZ.this;

        dzsysdicEntityDao=new DZSYSDICEntityDao(this);

        if (isNetwork) {
//            imageServiceLayer = new ArcGISImageServiceLayer(ConstantVar.IMAGEURL,null);
            ArcGISTiledMapServiceLayer ImgLayler =new ArcGISTiledMapServiceLayer(ConstantVar.IMAGEURL);
            mMapView.addLayer(ImgLayler);
        }else{
            Toast toast = Toast.makeText(this, R.string.offline_message, Toast.LENGTH_SHORT);
            toast.show();
        }

        fzfinish= (TextView) findViewById(R.id.fzfinish);
        fzfinish.setOnClickListener(this);
        clearTextView= (TextView) findViewById(R.id.clearTextView);
        clearTextView.setOnClickListener(this);
//
        backtextview= (TextView) findViewById(R.id.backtextview); //返回
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);//编号
        titletextview.setText("添加辅助几何图形");

//        mapImg =(TextView)findViewById(R.id.mapImg);//显示影像
//        mapImg.setOnClickListener(this);

        this.fillSymbol = new SimpleFillSymbol(Color.RED);
        this.fillSymbol.setAlpha(90);

        // 在drawLayer上绘制几何图形
        this.drawLayer = new GraphicsLayer();
        this.polygonLayer=new GraphicsLayer();
        this.mMapView.addLayer(this.drawLayer);
        this.mMapView.addLayer(this.polygonLayer);
        this.drawTool = new DrawTool(this.mMapView,"Map1FZ");
        // 此类实现DawEventListener接口
        this.drawTool.addEventListener(this);

        MyApp=(App) this.getApplication();

        //获取报批批次
//        if(isNetwork){
//            DZSYSDICEntity dzsysdicEntity=new DZSYSDICEntity();
//            GetBPPCDataSync getBPPCDataSync=new GetBPPCDataSync();
//            try {
//                String bppcStr=getBPPCDataSync.execute().get(300, TimeUnit.SECONDS);
//                if(bppcStr!=null && !bppcStr.isEmpty()){
//                    list_BPPC.clear();
//                    JSONArray jsonArray = new JSONArray(bppcStr);
//                    for(int k=0;k<jsonArray.length();k++){
//                        JSONObject o = (JSONObject) jsonArray.get(k);
//                        if(o.has("PC_NAME") && o.get("PC_NAME")!=null){
//                            list_BPPC.add(o.get("PC_NAME").toString());
//
//                            dzsysdicEntity.setName(o.get("PC_NAME").toString());
//                            dzsysdicEntity.setType("SBYDBPPC");
//
//                            if(!dzsysdicEntityDao.isEntityExist(o.get("PC_NAME").toString())){
//                                dzsysdicEntityDao.add(dzsysdicEntity);
//                            }
//                        }
//                    }
//                }else{
//                    ToastUtil.show(Map1FZ.this, "服务器无返回值");
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (TimeoutException e) {
//                e.printStackTrace();
//                ToastUtil.show(this, "连接服务器超时");
//            } catch (JSONException e) {
//                e.printStackTrace();
//                ToastUtil.show(this, "解析JSON错误");
//            }
//
//        }else{
//            //没有网络从本地读取
//            list_BPPC.clear();
//            List<DZSYSDICEntity> SBYDBPPCList =dzsysdicEntityDao.queryByType("SBYDBPPC");
//            for(DZSYSDICEntity dz:SBYDBPPCList){
//                list_BPPC.add(dz.getName());
//            }
//        }

//        if(list_BPPC!=null && list_BPPC.size()>0){
//            list_BPPC.add(0,"全部");
//            MyApp.setSBYD_BPPC_list(null);
//            MyApp.setSBYD_BPPC_list(list_BPPC);
//        }

        //GPS缩放
//        if(MyApp.getmGPSLon()!=null &&!MyApp.getmGPSLon().equals("")&&MyApp.getmGPSLat()!=null&&!MyApp.getmGPSLat().equals(""))
//        {
//            ToastUtil.show(Map1FZ.this, "定位当前位置 经度：" + MyApp.getmGPSLon() + "  纬度：" + MyApp.getmGPSLat());
//            SharedPreferences sp = Map1FZ.this.getSharedPreferences("LOGIN_INFO",  Map1FZ.this.MODE_PRIVATE);
//            PictureMarkerSymbol locationSymbol = new PictureMarkerSymbol(this.getResources().getDrawable(R.drawable.location));
//            Point point = new Point();
//            point.setXY(Double.valueOf(MyApp.getmGPSLon()), Double.valueOf(MyApp.getmGPSLat()));
//            //西安坐标转墨卡托
//            Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326), SpatialReference.create(102100));
//            this.drawGraphic = new Graphic(mapPoint,locationSymbol);
//            this.drawLayer.addGraphic(drawGraphic);
//            //设置点样式的颜色，大小和文本内容
//            TextSymbol ts = new TextSymbol(14,sp.getString("NAME",null),Color.BLACK);
//            Graphic gp = new Graphic(mapPoint,ts);
//            //添加到图层中显示
//            this.drawLayer.addGraphic(gp);
//            mMapView.centerAt(mapPoint,true);
//            mMapView.setScale(3000);
//        }

        //<-------------------------在线编辑设置部分代码开始------------------------------------>
        //地图单击事件，撤销编辑
        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float x, float y) {
                // convert event into screen click
                pointClicked = mMapView.toMapPoint(x, y);
            }
        });

        //地图长按事件
        mMapView.setOnLongPressListener(new OnLongPressListener() {
            @Override
            public boolean onLongPress(float x, float y) {
                Point pt = mMapView.toMapPoint(new Point(x, y));
                int[] id = drawLayer.getGraphicIDs(x, y, 20);

                if (id != null) {
                    GraphicID = id[0];
                    graphic = drawLayer.getGraphic(GraphicID);
                    drawLayer.updateGraphic(GraphicID, graphic);

                    if (TEXT!=null&&TEXT.equals("plon")){
                    //选中ID为GraphicID的graphic后再绑定地图触摸事件
                        mMapView.setOnTouchListener(new MyMapOnTouchListener(Map1FZ.this, mMapView));
                    }
                    if (TEXT!=null&&TEXT.equals("line")){
                        mMapView.setOnTouchListener(new MyMapOnTouchListenerline(Map1FZ.this, mMapView));
                    }
                } else {
                    Log.i(tag, "未找到Graphic");
                }

                return true;
            }

        });

        //<-------------------------在线编辑设置部分代码结束------------------------------------>
    }

    // 实现DrawEventListener中定义的方法
    public void handleDrawEvent(DrawEvent event) {
        // 将画好的图形（已经实例化了Graphic），添加到drawLayer中并刷新显示
        this.drawLayer.addGraphic(event.getDrawGraphic());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dzsysdicEntityDao.close();
        instance=null;
    }
    public static Map1FZ getInstance(){
        if(instance!=null){
            return instance;
        }
        return null;
    }


    @Override
    public void handleLatLon(List<MercatorEntity> LatLonPoint_List) {




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
            ToastUtil.show(Map1FZ.this, coordStr);

            lx=lx/LatLonPoint_List.size();
            ly=ly/LatLonPoint_List.size();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                if(requestCode== Map1FZ.TOADDPAGE){
                    this.markerSymbol = new SimpleMarkerSymbol(Color.RED, 15, SimpleMarkerSymbol.STYLE.TRIANGLE);
                    Point point = new Point();
                    point.setXY(lx, ly);
                    this.drawGraphic = new Graphic(point, this.markerSymbol);
                    this.drawLayer.addGraphic(drawGraphic);
                    mMapView.centerAt(point, true);
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
            case R.id.fzfinish:
                new AlertDialog.Builder(Map1FZ.this).setTitle("是否修改绘制区域?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(TEXT!=null&&TEXT.equals("plon")){
                                GraphicID=drawTool.activate(DrawTool.POLYGON);//-----------------------确定graphicID
                                Log.i(tag,"GraphicID:"+GraphicID);
                                ToastUtil.show(Map1FZ.this, "在地图上点选一个多边形，双击结束");}
                                if(TEXT!=null&&TEXT.equals("line")){
                                    GraphicID=drawTool.activate(DrawTool.POLYLINE);//-----------------------确定graphicID
                                    Log.i(tag,"GraphicID:"+GraphicID);
                                    ToastUtil.show(Map1FZ.this, "在地图上点选一条折线，双击结束");
                                }
                            }
                        }).setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(coordStr!="") {
                            progressdialog = new ProgressDialog(Map1FZ.this);
                            progressdialog.setCancelable(true);
                            progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressdialog.setMessage("跳转至添加页面...");
                            progressdialog.show();
                            //跳转回添加属性页
                            Intent toMapIntent = new Intent();
                            if (flag.equals("DZZHAddActivity")) {
                                toMapIntent.setClass(Map1FZ.this, DZZHAddActivity.class);
                            }
                            if (flag.equals("DZZHinfoActivity"))
                            {
                                toMapIntent.setClass(Map1FZ.this, DZZHinfoActivity.class);
                            }
                            toMapIntent.putExtra("PXY", coordStr);
                            toMapIntent.putExtra("TYPE",type);
                            setResult(RESULT_OK, toMapIntent);
                            Map1FZ.this.finish();
                            progressdialog.dismiss();
                        }
                        else {
                            ToastUtil.show(Map1FZ.this, "请先绘制完正确图形后再提交！");
                        }
                    }
                }).show();
                break;
//            case R.id.addpointTextView2:
//                GraphicID=drawTool.activate(DrawTool.POLYGON);//-----------------------确定graphicID
//                Log.i(tag,"GraphicID:"+GraphicID);
//                ToastUtil.show(this, "在地图上点选一个多边形，双击结束");
//
//                break;
            case R.id.backtextview:
                Map1FZ.this.finish();
                break;
            case R.id.clearTextView:
                AlertDialog.Builder builder = new AlertDialog.Builder(Map1FZ.this);
                builder.setTitle("确认删除图形?");
                builder.setCancelable(false);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Map1FZ.this.drawLayer.removeAll();
                        Map1FZ.this.drawTool.deactivate();
                        if (TEXT!=null&&TEXT.equals("plon")){
                            GraphicID=drawTool.activate(DrawTool.POLYGON);//-----------------------确定graphicID
                            Log.i(tag,"GraphicID:"+GraphicID);
                            ToastUtil.show(Map1FZ.this, "在地图上点选一个多边形，双击结束");
                        }
                        if (TEXT!=null&&TEXT.equals("line")){
                            GraphicID=drawTool.activate(DrawTool.POLYLINE);//-----------------------确定graphicID
                            Log.i(tag,"GraphicID:"+GraphicID);
                            ToastUtil.show(Map1FZ.this, "在地图上点选一条折线，双击结束");
                        }
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

//    public class GetBPPCDataSync extends AsyncTask<String, Integer, String> {
//
//        public GetBPPCDataSync() {
//            ksoap=new KsoapValidateHttp(Map1FZ.this);
//        }
//
//        @Override
//        protected String doInBackground(String... str) {
//            try {
//                String AddRslt=ksoap.WebGettb_jsydbp();
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

//            if(from !=null && from.equals("CENTERAT")){
//                bottomlinear.setVisibility(View.GONE);
//                polygon=new Polygon();
//                if(MyApp.getSbydpointList()!=null && MyApp.getSbydpointList().size()>0){
//                    List<Point> pl=MyApp.getSbydpointList();
//                    Point startPoint = null;
//                    Point endPoint = null;
//                    for(int i=1;i<pl.size();i++){
//                        startPoint = pl.get(i-1);
//                        endPoint = pl.get(i);
//                        Line line = new Line();
//                        line.setStart(startPoint);
//                        line.setEnd(endPoint);
//                        polygon.addSegment(line, false);
//                    }
//                    //polygon.calculateArea2D();
//
//                }
//                this.polygonGraphic = new Graphic(polygon, this.fillSymbol);
//             this.drawLayer.addGraphic(polygonGraphic);

            flag=intent.getStringExtra("Flag");
            type=intent.getStringExtra("TYPE");
            px=intent.getStringExtra("PX");
            py=intent.getStringExtra("PY");
            TEXT=intent.getStringExtra("TX");
            if(px!=null && py!=null){
                this.markerSymbol = new SimpleMarkerSymbol(Color.BLACK, 15, SimpleMarkerSymbol.STYLE.CROSS);
                Point point = new Point();
                point.setXY(Double.valueOf(px), Double.valueOf(py));
                //WGS84坐标转墨卡托
                Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326), SpatialReference.create(102100));
                this.drawGraphic = new Graphic(mapPoint, this.markerSymbol);
                this.drawLayer.addGraphic(drawGraphic);
                //设置点样式的颜色，大小和文本内容
                TextSymbol ts = new TextSymbol(14,TEXT,Color.RED);
                textSymbolgp = new Graphic(mapPoint,ts);
                //添加到图层中显示
                this.polygonLayer.addGraphic(textSymbolgp);
                mMapView.centerAt(mapPoint,true);
                mMapView.setScale(200);
            }else{
                ToastUtil.show(this, "该点无西安1980坐标信息");
            }

            if (TEXT!=null&&TEXT.equals("plon")){
                GraphicID=drawTool.activate(DrawTool.POLYGON);//-----------------------确定graphicID
                Log.i(tag,"GraphicID:"+GraphicID);
                ToastUtil.show(this, "在地图上点选一个多边形，双击结束");
            }
            if (TEXT!=null&&TEXT.equals("line")){
                GraphicID=drawTool.activate(DrawTool.POLYLINE);//-----------------------确定graphicID
                Log.i(tag,"GraphicID:"+GraphicID);
                ToastUtil.show(this, "在地图上点选一条折线，双击结束");
            }
        }
    }



//自定义地图点击事件
//在这个类中，GraphicID和GraphicLayer均使用全局变量
public class MyMapOnTouchListener extends MapOnTouchListener {
    private Context context=null;
    private MapView map=null;

    private Graphic g=drawLayer.getGraphic(GraphicID);
    private Polygon pg=(Polygon)g.getGeometry();
    private MercatorEntity latLonPointEdit=null;

    private int editIndex = -1;

    public MyMapOnTouchListener(Context context, MapView map)
    {
        super(context, map) ;
        this. context = context;
        this. map = map;
        Log.i(tag,"------执行MyMapOnTouchListener构造函数------");
    }

    @Override
    public boolean onDragPointerMove(MotionEvent from,MotionEvent to) throws  NullPointerException
    {
//            ToastUtil.show(MainMap3Activity.this,"执行了onDragPointerMove方法,ID:"+GraphicID);

        if(g==null)
        {
            Log.i(tag,"FeatureEdit----gs  is  null!");
            return false;
        }else
        {
            Log.i(tag, "FeatureEdit----gs  is  not null!!!!");
        }

        if (editIndex<0) {//此时需要获取编辑的节点序号
            if(from!=null)
            {
                Point ptClick = map.toMapPoint(from.getX(),from.getY() );
                Proximity2DResult pr= GeometryEngine.getNearestVertex(pg, ptClick);
                editIndex = pr.getVertexIndex();
                Log.i(tag,"获取editIndex="+editIndex);
            }
            else
            {
                ToastUtil.show(Map1FZ.this, "请选择点进行编辑");
            }
        }

        if(g!=null && editIndex>=0) {
            Point ptTo = map. toMapPoint(to. getX(), to.getY()) ;
            pg.setPoint(editIndex, ptTo);//改变指定节点的坐标
            Log.i(tag,"改变指定节点的坐标");
        }
        drawLayer.updateGraphic(GraphicID, pg);
        return true;
    }

    @Override
    public boolean onDragPointerUp( MotionEvent from, MotionEvent to) {
        Log.i(tag, "重置editindex");
        editIndex = -1;
        if (drawLayer.getGraphic(GraphicID)!=null) {
            drawLayer.updateGraphic(GraphicID, g);
        }
        else
        {
            Log.i(tag,"此时无可刷新的图层");
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent point) {
//          编辑完成，双击多边形，根据ID号来识别graphic，然后将graphic转换成polygon，进而读取各个点的坐标，
//          添加到list<MercatorEntity>中，执行handlelatlon方法，传递参数到添加上报报工地页面
        latLonPointEdit=new MercatorEntity();
        List<MercatorEntity> LatLonPoint_List=new ArrayList<MercatorEntity>();

        Geometry geometry= GeometryEngine.simplify(g.getGeometry(), map.getSpatialReference()) ;
        if (geometry!=null) {
            String geomytry2json = GeometryEngine.geometryToJson(map.getSpatialReference(), geometry);

            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(geomytry2json);
            com.alibaba.fastjson.JSONArray jsonring = jsonObject.getJSONArray("rings");
            com.alibaba.fastjson.JSONArray jsonArray = jsonring.getJSONArray(0);

            //polygon有ring组成，每个ring的起点和终点是重合的，需要去掉一个重复点的记录
            for (int i = 0; i < jsonArray.size()-1; i++) {
                latLonPointEdit=new MercatorEntity();//每次循环都要实例化这个类，实例化的类的属性赋值后不嫩再修改
                double PointX = jsonArray.getJSONArray(i).getBigDecimal(0).doubleValue();
                double PointY = jsonArray.getJSONArray(i).getBigDecimal(1).doubleValue();
                latLonPointEdit.setX(PointX);
                latLonPointEdit.setY(PointY);
                LatLonPoint_List.add(latLonPointEdit);
            }
            handleLatLon(LatLonPoint_List);
        }
        else {
            ToastUtil.show(Map1FZ.this, "图层不存在");
        }
        return super.onDoubleTap(point);
    }
}

    //自定义地图点击事件
    //在这个类中，GraphicID和GraphicLayer均使用全局变量
    public class MyMapOnTouchListenerline extends MapOnTouchListener {
        private Context context=null;
        private MapView map=null;

        private Graphic g=drawLayer.getGraphic(GraphicID);
        private Polyline lg=(Polyline)g.getGeometry();
        private MercatorEntity latLonPointEdit=null;

        private int editIndex = -1;

        public MyMapOnTouchListenerline(Context context, MapView map)
        {
            super(context, map) ;
            this. context = context;
            this. map = map;
            Log.i(tag,"------执行MyMapOnTouchListener构造函数------");
        }

        @Override
        public boolean onDragPointerMove(MotionEvent from,MotionEvent to) throws  NullPointerException
        {
//            ToastUtil.show(MainMap3Activity.this,"执行了onDragPointerMove方法,ID:"+GraphicID);

            if(g==null)
            {
                Log.i(tag,"FeatureEdit----gs  is  null!");
                return false;
            }else
            {
                Log.i(tag, "FeatureEdit----gs  is  not null!!!!");
            }

            if (editIndex<0) {//此时需要获取编辑的节点序号
                if(from!=null)
                {
                    Point ptClick = map.toMapPoint(from.getX(),from.getY() );
                    Proximity2DResult pr= GeometryEngine.getNearestVertex(lg, ptClick);
                    editIndex = pr.getVertexIndex();
                    Log.i(tag,"获取editIndex="+editIndex);
                }
                else
                {
                    ToastUtil.show(Map1FZ.this, "请选择点进行编辑");
                }
            }

            if(g!=null && editIndex>=0) {
                Point ptTo = map. toMapPoint(to. getX(), to.getY()) ;
                lg.setPoint(editIndex, ptTo);//改变指定节点的坐标
                Log.i(tag,"改变指定节点的坐标");
            }
            drawLayer.updateGraphic(GraphicID, lg);
            return true;
        }

        @Override
        public boolean onDragPointerUp( MotionEvent from, MotionEvent to) {
            Log.i(tag, "重置editindex");
            editIndex = -1;
            if (drawLayer.getGraphic(GraphicID)!=null) {
                drawLayer.updateGraphic(GraphicID, g);
            }
            else
            {
                Log.i(tag,"此时无可刷新的图层");
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent point) {
//          编辑完成，双击多边形，根据ID号来识别graphic，然后将graphic转换成polygon，进而读取各个点的坐标，
//          添加到list<MercatorEntity>中，执行handlelatlon方法，传递参数到添加上报报工地页面
            latLonPointEdit=new MercatorEntity();
            List<MercatorEntity> LatLonPoint_List=new ArrayList<MercatorEntity>();

            Geometry geometry= GeometryEngine.simplify(g.getGeometry(), map.getSpatialReference()) ;
            if (geometry!=null) {
                String geomytry2json = GeometryEngine.geometryToJson(map.getSpatialReference(), geometry);

                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(geomytry2json);
                com.alibaba.fastjson.JSONArray jsonring = jsonObject.getJSONArray("paths");
                com.alibaba.fastjson.JSONArray jsonArray = jsonring.getJSONArray(0);

                for (int i = 0; i < jsonArray.size()-1; i++) {
                    latLonPointEdit=new MercatorEntity();//每次循环都要实例化这个类，实例化的类的属性赋值后不嫩再修改
                    double PointX = jsonArray.getJSONArray(i).getBigDecimal(0).doubleValue();
                    double PointY = jsonArray.getJSONArray(i).getBigDecimal(1).doubleValue();
                    latLonPointEdit.setX(PointX);
                    latLonPointEdit.setY(PointY);
                    LatLonPoint_List.add(latLonPointEdit);
                }
                handleLatLon(LatLonPoint_List);
            }
            else {
                ToastUtil.show(Map1FZ.this, "图层不存在");
            }
            return super.onDoubleTap(point);
        }
    }
}
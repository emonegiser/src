package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arcgis.R;
import com.arcgis.drawtool.DrawEvent;
import com.arcgis.drawtool.DrawEventListener;
import com.arcgis.drawtool.DrawTool;
import com.arcgis.drawtool.LatLonListener;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.entity.LatLonPoint;
import com.arcgis.entity.XCRWEntity;
import com.arcgis.gpsservice.Gps;
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
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainMap1Activity extends Activity implements DrawEventListener,LatLonListener,View.OnClickListener {

    MapView mMapView;
    private TextView backtextview;
    private TextView titletextview;

//    Spinner spinnerdzzhrw=null;
    ArrayAdapter<String> spinnerdzzhrwAdapter;

    ArcGISDynamicMapServiceLayer dynamicMapServiceLayer;
    //绘制图形层
    private GraphicsLayer drawLayer;
    private DrawTool drawTool;
    private KsoapValidateHttp ksoap;
    //地灾类型
    private List<String> DZType=new ArrayList<>();
    //稳定状态
    private List<String> DZWDName=new ArrayList<>();
    //地灾规模
    private List<String> DZScale=new ArrayList<>();
    //全局变量存储位置
    private App MyApp;
    static MainMap1Activity instance;
    public static int TOADDZHD=1;

    private Graphic drawGraphic;
    private MarkerSymbol markerSymbol;
    private GraphicsLayer queryTempLayer;

//    private TextView newrwTextView;//新建任务
    private TextView addpointTextView;  //添加点按钮
//    private TextView querypointTextView;//查询按钮
    private TextView clearTextView;     //清除按钮
    private LinearLayout bottomlinear;

    private boolean isNetwork=false;
    private DZSYSDICEntityDao dzsysdicEntityDao=null;
    private String Pid=null;
//    private XCRWEntity mXCRWEntity;
//    private List<XCRWEntity> XCRW_list=new ArrayList<XCRWEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.activity_map1);
        ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");

        Gps.Type=Gps.ZHDXC;

        mMapView= (MapView) findViewById(R.id.map);

        backtextview= (TextView) findViewById(R.id.backtextview);//返回
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.titletextview);//标题
        titletextview.setText("地质灾害");

//        RwBtn = (TextView)findViewById(R.id.RwBtn);//任务按钮
//        RwBtn.setOnClickListener(this);

//        newrwTextView=(TextView)findViewById(R.id.newrwTextView);
        addpointTextView= (TextView) findViewById(R.id.addpointTextView);
//        querypointTextView= (TextView) findViewById(R.id.querypointTextView);
        clearTextView= (TextView) findViewById(R.id.clearTextView);
        bottomlinear= (LinearLayout) findViewById(R.id.bottomlinear);

        instance=MainMap1Activity.this;
        App.getInstance().addActivity(this);
        isNetwork=NetUtils.isNetworkAvailable(this);//判断是否有网络
        dzsysdicEntityDao=new DZSYSDICEntityDao(this);

//        newrwTextView.setOnClickListener(this);
        addpointTextView.setOnClickListener(this);
//        querypointTextView.setOnClickListener(this);
        clearTextView.setOnClickListener(this);

//        spinnerdzzhrw = (Spinner) findViewById(R.id.spinner);
//        ArrayList<String> zhlx=new ArrayList<String>();
//        zhlx.add("选择任务:");
//        zhlx.add("普通任务");
//        zhlx.add("预警任务");
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,zhlx);
//        spinnerdzzhrw.setAdapter(adapter);
//        spinnerdzzhrw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String str = parent.getItemAtPosition(position).toString();
//                if(str.equals("普通任务")){
//                    isNetwork= NetUtils.isNetworkAvailable(MainMap1Activity.this);
//                    if(!isNetwork){
//                        ToastUtil.show(MainMap1Activity.this,"请检查网络连接");
//                        return;
//                    }
//                    XCRW_list.clear();
//                    /**
//                     * [{"ID":21,"RWBH":"130676043871617756","SENDER_ID":"zj","RECEIVER_ID":"2323",
//                     * "SENDTIME":"\/Date(1423065600000)\/","TASKTITLE":"werwe","TASKCONTENT":"erter",
//                     * "X":35550397.04379700,"Y":3054022.68020308,"E":105.51047862,"N":27.59821317,"TASKAddress":"亮岩镇,飞轮村",
//                     * "TASKFILES":"","RESULTCONTENT":"","RESULTFILES":"arcengine10.1开发总结.docx","STATE":"已完成",
//                     * "COMPLETETIME":"2015/2/6 17:43:10","TASKTYPE":"灾害点巡查"}]
//                     */
//                    if(isNetwork){
//
//                        GetXCRWDataSync getXCRWDataSync=new GetXCRWDataSync(Pid);
//                        try {
//                            String dataRslt=getXCRWDataSync.execute().get(20, TimeUnit.SECONDS);
//                            if(dataRslt!=null && dataRslt.contains("")){
//                                JSONArray jsonArray = new JSONArray(dataRslt);
//                                for(int k=0;k<jsonArray.length();k++){
//                                    JSONObject o = (JSONObject) jsonArray.get(k);
//                                    mXCRWEntity=new XCRWEntity();
//                                    if(o.has("SENDTIME") && o.get("SENDTIME")!=null){
//                                        mXCRWEntity.setSENDTIME(o.get("SENDTIME").toString());
//                                    }
//                                    if(o.has("TASKTYPE") && o.get("TASKTYPE")!=null){
//                                        mXCRWEntity.setTASKTYPE(o.get("TASKTYPE").toString());
//                                    }
//                                    if(o.has("COMPLETETIME") && o.get("COMPLETETIME")!=null){
//                                        mXCRWEntity.setCOMPLETETIME(o.get("COMPLETETIME").toString());
//                                    }
//                                    if(o.has("STATE") && o.get("STATE")!=null){
//                                        mXCRWEntity.setSTATE(o.get("STATE").toString());
//                                    }
//                                    if(o.has("RESULTFILES") && o.get("RESULTFILES")!=null){
//                                        mXCRWEntity.setRESULTFILES(o.get("RESULTFILES").toString());
//                                    }
//                                    if(o.has("RESULTCONTENT") && o.get("RESULTCONTENT")!=null){
//                                        mXCRWEntity.setRESULTCONTENT(o.get("RESULTCONTENT").toString());
//                                    }
//                                    if(o.has("TASKFILES") && o.get("TASKFILES")!=null){
//                                        mXCRWEntity.setTASKFILES(o.get("TASKFILES").toString());
//                                    }
//                                    if(o.has("TASKAddress") && o.get("TASKAddress")!=null){
//                                        mXCRWEntity.setTASKAddress(o.get("TASKAddress").toString());
//                                    }
//                                    if(o.has("N") && o.get("N")!=null){
//                                        mXCRWEntity.setN(o.get("N").toString());
//                                    }
//                                    if(o.has("E") && o.get("E")!=null){
//                                        mXCRWEntity.setE(o.get("E").toString());
//                                    }
//                                    if(o.has("TASKCONTENT") && o.get("TASKCONTENT")!=null){
//                                        mXCRWEntity.setTASKCONTENT(o.get("TASKCONTENT").toString());
//                                    }
//                                    if(o.has("TASKTITLE") && o.get("TASKTITLE")!=null){
//                                        mXCRWEntity.setTASKTITLE(o.get("TASKTITLE").toString());
//                                    }
//                                    if(o.has("RECEIVER_ID") && o.get("RECEIVER_ID")!=null){
//                                        mXCRWEntity.setRECEIVER_ID(o.get("RECEIVER_ID").toString());
//                                    }
//                                    if(o.has("SENDER_ID") && o.get("SENDER_ID")!=null){
//                                        mXCRWEntity.setSENDER_ID(o.get("SENDER_ID").toString());
//                                    }
//                                    if(o.has("RWBH") && o.get("RWBH")!=null){
//                                        mXCRWEntity.setRWBH(o.get("RWBH").toString());
//                                    }
//                                    if(o.has("ID") && o.get("ID")!=null){
//                                        mXCRWEntity.setID(o.get("ID").toString());
//                                    }
//                                    if(o.has("X") && o.get("X")!=null){
//                                        mXCRWEntity.setX(o.get("X").toString());
//                                    }
//                                    if(o.has("Y") && o.get("Y")!=null){
//                                        mXCRWEntity.setY(o.get("Y").toString());
//                                    }
//                                    XCRW_list.add(mXCRWEntity);
//
//                                }
//                            }else{
//                                ToastUtil.show(MainMap1Activity.this,"服务器返回值为空");
//                            }
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        } catch (TimeoutException e) {
//                            e.printStackTrace();
//                            ToastUtil.show(MainMap1Activity.this,"连接服务器超时");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            ToastUtil.show(MainMap1Activity.this,"解析JSON错误");
//                        }
//                    }else{
//                        ToastUtil.show(MainMap1Activity.this,"网络异常，无法查询");
//                    }
//
//                    if(XCRW_list!=null && XCRW_list.size()>0){
//                        MyApp.setXCRW_list(null);
//                        MyApp.setXCRW_list(XCRW_list);
//                        Intent toListShowIntent=new Intent();
//                        toListShowIntent.setClass(MainMap1Activity.this,DZZHRWListActivity.class);
//                        startActivity(toListShowIntent);
//                    }else{
//                        ToastUtil.show(MainMap1Activity.this,"没有该巡查数据");
//                    }
//                }else if (str.equals("预警任务")){
//                    Intent toListShowIntent=new Intent();
//                    toListShowIntent.setClass(MainMap1Activity.this,DZZHYJActivity.class);
//                    startActivity(toListShowIntent);
//                }else{
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        SharedPreferences LOGIN_INFO = getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        Pid = LOGIN_INFO.getString("PID",null);

        if (isNetwork) {
            ArcGISTiledMapServiceLayer ImgLayler =new ArcGISTiledMapServiceLayer(ConstantVar.IMAGEURL);
//            dynamicMapServiceLayer = new ArcGISDynamicMapServiceLayer(ConstantVar.DZZHMAPURL);
            mMapView.removeLayer(0);
            mMapView.addLayer(ImgLayler);
        }else{
            Toast toast = Toast.makeText(this, R.string.offline_message, Toast.LENGTH_SHORT);
            toast.show();
        }

        // 在drawLayer上绘制几何图形
        this.drawLayer = new GraphicsLayer();
        this.mMapView.addLayer(this.drawLayer);
        this.drawTool = new DrawTool(this.mMapView,"MainMap1Activity");
        // 此类实现DawEventListener接口
        this.drawTool.addEventListener(this);

        //初始化地址灾害规模、类型、名称
        ksoap=new KsoapValidateHttp(this);
        GetDZDataSync getDZDataSync=new GetDZDataSync();
        try {
            if(isNetwork){
                DZSYSDICEntity dzsysdicEntity=new DZSYSDICEntity();
                String DataRslt=getDZDataSync.execute().get(15, TimeUnit.SECONDS);
                if(DataRslt!=null && DataRslt.contains("&&")){
                    DZWDName.clear();
                    DZScale.clear();
                    DZType.clear();
                    String []RsltArr=DataRslt.split("&&");
                    if(RsltArr!=null && RsltArr.length>0){
                        int len=RsltArr.length;
                        for(int i=0;i<len;i++){
                            String dataElement=RsltArr[i];
                            JSONArray jsonArray = new JSONArray(dataElement);
                            for(int k=0;k<jsonArray.length();k++){
                                JSONObject o = (JSONObject) jsonArray.get(k);
                                if(o.has("DZ_WDNAME") && o.get("DZ_WDNAME")!=null){
                                    DZWDName.add(o.get("DZ_WDNAME").toString());
                                    dzsysdicEntity.setName(o.get("DZ_WDNAME").toString());
                                    dzsysdicEntity.setType("DZWDZT");

                                    if(dzsysdicEntityDao.isEntityExist(o.get("DZ_WDNAME").toString())){
                                    }else{
                                        dzsysdicEntityDao.add(dzsysdicEntity);
                                    }

                                }else if(o.has("S_NAME") && o.get("S_NAME")!=null){
                                    DZScale.add(o.get("S_NAME").toString());
                                    dzsysdicEntity.setName(o.get("S_NAME").toString());
                                    dzsysdicEntity.setType("DZGM");

                                    if(dzsysdicEntityDao.isEntityExist(o.get("S_NAME").toString())){
                                    }else {
                                        dzsysdicEntityDao.add(dzsysdicEntity);
                                    }
                                }else if(o.has("ID") && o.has("NAME") && o.get("ID")!=null && o.get("NAME")!=null){
                                    DZType.add(o.get("NAME").toString());
                                    dzsysdicEntity.setName(o.get("NAME").toString());
                                    dzsysdicEntity.setType("DZTYPE");

                                    if(dzsysdicEntityDao.isEntityExist(o.get("NAME").toString())){
                                    }else {
                                        dzsysdicEntityDao.add(dzsysdicEntity);
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                //没有网络，读取本地数据保存的本地
                DZWDName.clear();
                DZScale.clear();
                DZType.clear();

                List<DZSYSDICEntity> DZWDZTList =dzsysdicEntityDao.queryByType("DZWDZT");
                for(DZSYSDICEntity dz:DZWDZTList){
                    DZWDName.add(dz.getName());
                }

                List<DZSYSDICEntity> DZGMList =dzsysdicEntityDao.queryByType("DZGM");
                for(DZSYSDICEntity dz:DZGMList){
                    DZScale.add(dz.getName());
                }

                List<DZSYSDICEntity> DZTYPEList =dzsysdicEntityDao.queryByType("DZTYPE");
                for(DZSYSDICEntity dz:DZTYPEList){
                    DZType.add(dz.getName());
                }
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            ToastUtil.show(MainMap1Activity.this,"连接服务器超时");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyApp=(App) this.getApplication();

        if(DZWDName!=null && DZWDName.size()>0){
            MyApp.setDZWDName(DZWDName);
        }
        if(DZScale!=null && DZScale.size()>0){
            MyApp.setDZScale(DZScale);
        }
        if(DZType!=null && DZType.size()>0){
            MyApp.setDZType(DZType);
        }
        if(MyApp.getmGPSLon()!=null &&!MyApp.getmGPSLon().equals("")&&MyApp.getmGPSLat()!=null&&!MyApp.getmGPSLat().equals(""))
        {
            ToastUtil.show( MainMap1Activity.this,"定位当前位置 经度："+MyApp.getmGPSLon()+"  纬度："+MyApp.getmGPSLat());
            SharedPreferences sp = MainMap1Activity.this.getSharedPreferences("LOGIN_INFO",  MainMap1Activity.this.MODE_PRIVATE);
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
//            mMapView.zoomToScale(mapPoint,3000);

            mMapView.centerAt(mapPoint,true);
            mMapView.setScale(1128.497176);
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
        dzsysdicEntityDao.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        spinnerdzzhrw.setSelection(0);
        Intent intent=this.getIntent();
        if(intent!=null){
            String from=intent.getStringExtra("FROM");
            if(from !=null && from.equals("ALLPT")){
                //List<com.esri.core.geometry.Point> ptList=new ArrayList<com.esri.core.geometry.Point>();
                List<DZZHEntity> DZZH_list=new ArrayList<DZZHEntity>();
                if(MyApp.getDZZH_list()!=null && MyApp.getDZZH_list().size()>0){
                    DZZH_list=MyApp.getDZZH_list();
                    for(DZZHEntity dzzh:DZZH_list){
                        //ptList.add(new Point(Double.valueOf(dzzh.getPx()),Double.valueOf(dzzh.getPy())));
                        String px =dzzh.getX();
                        String py = dzzh.getY();
                        String TEXT = dzzh.getDZPTBH();
                        if(px!=null && py!=null){
                            this.markerSymbol = new SimpleMarkerSymbol(Color.RED, 15,SimpleMarkerSymbol.STYLE.TRIANGLE);
                            Point point = new Point();
                            point.setXY(Double.valueOf(px.replace(",","")), Double.valueOf(py.replace(",","")));
                            //西安坐标转墨卡托
                            Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(2359),SpatialReference.create(102100));
                            this.drawGraphic = new Graphic(mapPoint, this.markerSymbol);
                            this.drawLayer.addGraphic(drawGraphic);
                            //设置点样式的颜色，大小和文本内容
                            TextSymbol ts = new TextSymbol (14,TEXT,Color.BLACK);
                            Graphic gp = new Graphic(mapPoint,ts);
                            //添加到图层中显示
                            this.drawLayer.addGraphic(gp);
                            mMapView.zoomToScale(mapPoint,100);
                        }else{
                            ToastUtil.show(this,"该点无西安1980坐标信息");
                        }
                    }
                }
            }
            if(from !=null && from.equals("CENTERAT")){
                bottomlinear.setVisibility(View.GONE);
                String px=intent.getStringExtra("PX");
                String py=intent.getStringExtra("PY");
                String TEXT=intent.getStringExtra("TEXT");
                if(px!=null && py!=null){
                    this.markerSymbol = new SimpleMarkerSymbol(Color.RED, 15,SimpleMarkerSymbol.STYLE.TRIANGLE);
                    Point point = new Point();
                    point.setXY(Double.valueOf(px.replace(",","")), Double.valueOf(py.replace(",","")));
                    //西安坐标转墨卡托
                    Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(2359),SpatialReference.create(102100));
                    this.drawGraphic = new Graphic(mapPoint, this.markerSymbol);
                    this.drawLayer.addGraphic(drawGraphic);
                    //设置点样式的颜色，大小和文本内容
                    TextSymbol ts = new TextSymbol (14,TEXT,Color.BLACK);
                    Graphic gp = new Graphic(mapPoint,ts);
                    //添加到图层中显示
                    this.drawLayer.addGraphic(gp);
                    mMapView.centerAt(mapPoint,true);
                }else{
                    ToastUtil.show(this,"该点无西安1980坐标信息");
                }
            }
        }
    }


    @Override
    public void handleLatLon(LatLonPoint pt) {

        isNetwork=NetUtils.isNetworkAvailable(this);

        if(pt!=null){
            if(isNetwork){
                //定位所在乡镇
                GetXZDataSync getXZDataSync=new GetXZDataSync(pt.getX()+"",pt.getY()+"");
                try {
                    String dwRslt=getXZDataSync.execute().get(200, TimeUnit.SECONDS);
                    if(dwRslt!=null && dwRslt.length()>0){
                        Intent AddPointIntent=new Intent(MainMap1Activity.this, DZZHAddActivity.class);
                        AddPointIntent.putExtra("PX",pt.getX());
                        AddPointIntent.putExtra("PY",pt.getY());
                        AddPointIntent.putExtra("XZ",dwRslt);
                        startActivityForResult(AddPointIntent,MainMap1Activity.TOADDZHD);
                        //startActivity(AddPointIntent);
                    }else{
                        ToastUtil.show(this,"服务器返回为空,无法获取该点所属乡镇");
                        Intent AddPointIntent=new Intent(MainMap1Activity.this, DZZHAddActivity.class);
                        AddPointIntent.putExtra("PX",pt.getX());
                        AddPointIntent.putExtra("PY",pt.getY());
                        startActivityForResult(AddPointIntent,MainMap1Activity.TOADDZHD);
                    }

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                } catch (TimeoutException e1) {
                    e1.printStackTrace();
                }
            }else{
                ToastUtil.show(this,"网络异常,无法获取该点所属乡镇");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                if(requestCode==MainMap1Activity.TOADDZHD){
                    String px=data.getStringExtra("PX");
                    String py=data.getStringExtra("PY");
                    this.mMapView.centerAndZoom(Double.valueOf(py.replace(",","")),Double.valueOf(px.replace(",","")),18);
                }else  if (requestCode==2){
                    String px=data.getStringExtra("PX");
                    String py=data.getStringExtra("PY");
                    this.mMapView.centerAndZoom(Double.valueOf(py),Double.valueOf(px),18);
                }else{
                    ToastUtil.show(this,"无法通过返回坐标定位");
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
                //onKeyDown(KeyEvent.KEYCODE_BACK, null);
                MainMap1Activity.this.finish();
                break;
            case R.id.addpointTextView:
                new AlertDialog.Builder(MainMap1Activity.this).setTitle("是否以当前坐标作为灾害位置?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (MyApp.getmGPSLon() != null && !MyApp.getmGPSLon().equals("") && MyApp.getmGPSLat() != null && !MyApp.getmGPSLat().equals(""))
                                {
                                    Point pt = new Point();
                                    pt.setXY(Double.valueOf(MyApp.getmGPSLon()), Double.valueOf(MyApp.getmGPSLat()));
                                    //地理坐标转西安坐标
                                    // Point pt = (Point) GeometryEngine.project(point, SpatialReference.create(4326),SpatialReference.create(2359));
                                    //定位所在乡镇
                                    GetXZDataSync getXZDataSync = new GetXZDataSync(pt.getX() + "", pt.getY() + "");
                                    try {
                                        String dwRslt = getXZDataSync.execute().get(15, TimeUnit.SECONDS);
                                        if (dwRslt != null && dwRslt.length() > 0) {
                                            Intent AddPointIntent = new Intent(MainMap1Activity.this, DZZHAddActivity.class);
//                                            String [] result=dwRslt.split(";");
                                            AddPointIntent.putExtra("PX", pt.getX());
                                            AddPointIntent.putExtra("PY", pt.getY());
                                            AddPointIntent.putExtra("XZ", dwRslt);
                                            startActivityForResult(AddPointIntent, MainMap1Activity.TOADDZHD);
                                        } else {
                                            ToastUtil.show(MainMap1Activity.this, "服务器返回为空,无法获取该点所属乡镇");
                                            Intent AddPointIntent = new Intent(MainMap1Activity.this, DZZHAddActivity.class);
                                            AddPointIntent.putExtra("PX", pt.getX());
                                            AddPointIntent.putExtra("PY", pt.getY());
                                            startActivityForResult(AddPointIntent, MainMap1Activity.TOADDZHD);
                                        }
                                    } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                    } catch (ExecutionException e1) {
                                        e1.printStackTrace();
                                    } catch (TimeoutException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                ToastUtil.show(MainMap1Activity.this, "无法定位当前坐标!");
                            }
                        }).setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drawTool.activate(DrawTool.POINT);
                        ToastUtil.show(MainMap1Activity.this, "在地图上选择一个点");
                    }
                }).show();
                break;
//            case R.id.newrwTextView:
//                if (MyApp.getmGPSLon() != null && !MyApp.getmGPSLon().equals("") && MyApp.getmGPSLat() != null && !MyApp.getmGPSLat().equals("")) {
//                    Point pt = new Point();
//                    pt.setXY(Double.valueOf(MyApp.getmGPSLon()), Double.valueOf(MyApp.getmGPSLat()));
//                    //定位所在乡镇
//                    GetXZDataSync getXZDataSync = new GetXZDataSync(pt.getX() + "", pt.getY() + "");
//                    try {
//                        String dwRslt = getXZDataSync.execute().get(15, TimeUnit.SECONDS);
//                        if (dwRslt != null && dwRslt.length() > 0) {
//                            Intent AddPointIntent = new Intent(MainMap1Activity.this, DZZHNEWRWActivity.class);
//                            String [] result=dwRslt.split(";");
//                            AddPointIntent.putExtra("PX", pt.getX());
//                            AddPointIntent.putExtra("PY", pt.getY());
//                            AddPointIntent.putExtra("RWBH",result[0]);
//                            AddPointIntent.putExtra("XZ", result[1]);
//                            startActivityForResult(AddPointIntent, 2);
//                        } else {
//                            ToastUtil.show(MainMap1Activity.this, "服务器返回为空,无法获取该点所属乡镇");
//                            Intent AddPointIntent = new Intent(MainMap1Activity.this, DZZHNEWRWActivity.class);
//                            AddPointIntent.putExtra("PX", pt.getX());
//                            AddPointIntent.putExtra("PY", pt.getY());
//                            startActivityForResult(AddPointIntent, 2);
//                        }
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    } catch (ExecutionException e1) {
//                        e1.printStackTrace();
//                    } catch (TimeoutException e1) {
//                        e1.printStackTrace();
//                    }
//                }else{
//                    ToastUtil.show(MainMap1Activity.this, "无法定位当前坐标!");
//                    Intent tointent=new Intent();
//                    tointent.setClass(this,DZZHNEWRWActivity.class);
//                    startActivity(tointent);
//
//                }
//                break;
//            case R.id.querypointTextView:
//                Intent intent=new Intent();
//                intent.setClass(this,DZZHQueryActivity.class);
//                startActivity(intent);
//                break;
            case R.id.clearTextView:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMap1Activity.this);
                builder.setTitle("确认删除图形?");
                builder.setCancelable(false);
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainMap1Activity.this.drawLayer.removeAll();
                        MainMap1Activity.this.drawTool.deactivate();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MainMap1Activity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //定位所在乡镇
    public class GetXZDataSync extends AsyncTask<String, Integer, String> {

        String px=null;
        String py=null;

        public GetXZDataSync() {
        }

        public GetXZDataSync(String px, String py) {
            ksoap=new KsoapValidateHttp(MainMap1Activity.this);
            this.px = px;
            this.py = py;
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String posRslt=ksoap.WebGetXZByXY(this.px,this.py);
                if(posRslt!=null){
                    return posRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //获取灾害稳定状态、规模、类型
    public class GetDZDataSync extends AsyncTask<String, Integer, String> {

        String DZName=null;
        String DZScale=null;
        String DZType=null;

        @Override
        protected String doInBackground(String... str) {
            try {
                DZName=ksoap.WebGetDZ_WDNAME();
                DZScale=ksoap.WebGetTB_SCALE();
                DZType=ksoap.WebGetTB_DZZHTYPE();
                if(DZName!=null && DZScale!=null && DZType!=null){
                    return DZName+"&&"+DZScale+"&&"+DZType;
                }else{
                    return "NODATA";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //获取移动端需执行的任务
    public class GetXCRWDataSync extends AsyncTask<String, Integer, String> {
        String pid;
//        String rwxz;
//        String rwlx;
//        String zt;
//        String rwrq;

        public GetXCRWDataSync(String pid) {
            ksoap=new KsoapValidateHttp(MainMap1Activity.this );
            this.pid=pid;
//            this.rwxz=rwxz;
//            this.rwlx=rwlx;
//            this.zt=zt;
//            this.rwrq=rwrq;
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetInspectionMission2(pid);
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


    public static MainMap1Activity getInstance(){
        return instance;
    }
}
package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arcgis.R;
import com.arcgis.adapter.DZZHYJAlertListAdapter;
import com.arcgis.adapter.DZZHYJListAdapter;
import com.arcgis.drawtool.DrawTool;
import com.arcgis.entity.DZZHYJEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.ConstantVar;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.esri.android.map.Layer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.Point;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2015/4/4.
 */
public class DZZHYJActivity extends Activity implements View.OnClickListener{
    //全局变量存储位置
    private App MyApp;
    private ListView listView;
    private DZZHYJListAdapter adapter;
    private String arrayType[];
    private TextView mback;
private boolean isNetwork=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzzhyj);
        //实例化全局变量
        MyApp=(App) this.getApplication();
        listView = (ListView) findViewById(R.id.listView);

        mback= (TextView) findViewById(R.id.backtextview);//返回
        mback.setOnClickListener(this);

        final  Calendar calendar= Calendar.getInstance();
        adapter = new DZZHYJListAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent DetailIntent = new Intent();
                DetailIntent.setClass(DZZHYJActivity.this, MainMap7Activity.class);
                DetailIntent.putExtra("Data", dataList.get(position));
                DetailIntent.putExtra("RWLX","YJ");
                startActivity(DetailIntent);
                dataList.get(position);
            }
        });
        Resources res = getResources();
        arrayType = res.getStringArray(R.array.dzzh_type_str);

        initData();
    }

    ArrayList<Map<String, String>> arraylist =new ArrayList<Map<String, String>>();
    private DZZHYJAlertListAdapter itemAapter = null;
    private void alert() {

        if (arraylist.size() > 0) {
            arraylist.clear();
        }
        final Dialog dialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflaterDl = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.alert_zhlx_type, null);
        ListView list = (ListView) layout.findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long arg3) {
                dialog.dismiss();
            }
        });

        itemAapter = new DZZHYJAlertListAdapter(this);
        list.setAdapter(itemAapter);

        for (int i = 0; i < arrayType.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("item", arrayType[i]);
            arraylist.add(map);
        }
        itemAapter.addData(arraylist);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.getWindow().setContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private ArrayList<DZZHYJEntity> dataList = new ArrayList<>();

    /**
     * 查询预警任务信息
     * **/
    private void initData() {

        if(dataList.size()>0){
            dataList.clear();
        }
        isNetwork= NetUtils.isNetworkAvailable(DZZHYJActivity.this);
        if(!isNetwork){
            ToastUtil.show(DZZHYJActivity.this,"请检查网络连接");
            return;
        }
        if(isNetwork) {
            GetDZZHYJDATA getDZDataSync = new GetDZZHYJDATA();
            try {
                String rsltStr = getDZDataSync.execute().get(50, TimeUnit.SECONDS);
                if (rsltStr == null) {
                    adapter.addData(null);
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(rsltStr);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        DZZHYJEntity entity = new DZZHYJEntity();
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        entity.setBJRQ(object.getString("BJRQ"));
                        entity.setBZ(object.getString("BZ"));
                        entity.setE(object.getString("E"));
                        entity.setID(object.getString("ID"));
                        if (object.getString("IMAGES") == null || object.getString("IMAGES").equals("")) {
                            entity.setIMAGES("NO");
                        } else {
                            entity.setIMAGES(object.getString("IMAGES"));
                        }
                        entity.setN(object.getString("N"));
                        entity.setTB_DisasterWarn_ID(object.getString("TB_DisasterWarn_ID"));
                        entity.setX(object.getString("X"));
                        entity.setY(object.getString("Y"));
                        entity.setZHDJ(object.getString("ZHDJ"));
                        entity.setXXWZ(object.getString("XXWZ"));
                        entity.setZHMC(object.getString("ZHMC"));
                        dataList.add(entity);
                    }
                    adapter.addData(dataList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (rsltStr != null) {
                    Log.i("json", rsltStr);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }


    //调用webservice
    private KsoapValidateHttp ksoap;

    public class GetDZZHYJDATA extends AsyncTask<String, Integer, String> {
//        private String szxz;
        public GetDZZHYJDATA() {
//            this.szxz = szxz;
            ksoap = new KsoapValidateHttp(DZZHYJActivity.this);
        }
        @Override
        protected String doInBackground(String... str) {
            try {
                Log.i("net", "do");
                String AddRslt = ksoap.GetDzzhyjdata();
                if (AddRslt != null) {
                    return AddRslt;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String getTime() {
        long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new Date(time);
        Log.i("time", format.format(d1));
        return format.format(d1);
    }
    @Override
    public void onClick(View v) {
        int viewID=v.getId();
        switch (viewID){
            case R.id.backtextview:
                DZZHYJActivity.this.finish();
                break;
//            case R.id.search:
//                initData(spinnerSSXZ.getSelectedItem().toString().substring(0,spinnerSSXZ.getSelectedItem().toString().indexOf("[")));
//                break;
            default:
                break;
        }
    }
}

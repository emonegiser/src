package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Entity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcgis.R;
import com.arcgis.adapter.DZZHYJAlertListAdapter;
import com.arcgis.adapter.DZZHYJListAdapter;
import com.arcgis.adapter.YJZSPJXXListAdapter;
import com.arcgis.entity.DZZHYJEntity;
import com.arcgis.entity.YJSPXXEntity;
import com.arcgis.entity.YJSPXXSEntity;
import com.arcgis.httputil.KsoapValidateHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2015/4/4.
 */
public class YJZSPJXXSActivity extends Activity {
    private ListView listView;
    private YJZSPJXXListAdapter adapter;
    private EditText mType, mAddress;
    private TextView  mTime;
    private String arrayType[];
    private Button mbtn;
    private TextView mback;
    private TextView mAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yjzspjxx);
        listView = (ListView) findViewById(R.id.listView);
        mbtn= (Button) findViewById(R.id.search);
        mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData(mType.getText().toString().trim(),mAddress.getText().toString().trim());

            }
        });
        mType = (EditText) findViewById(R.id.type);
        mAdd= (TextView) findViewById(R.id.addTextview);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentYj=new Intent();
                intentYj.setClass(YJZSPJXXSActivity.this,YJZSPJXXActivity.class);
                startActivity(intentYj);
            }
        });
        mType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert();
            }
        });
        mTime = (TextView) findViewById(R.id.time);
       final  Calendar calendar= Calendar.getInstance();
        mTime.setText(calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1) +"/"+calendar.get(Calendar.DATE));
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePicker = new DatePickerDialog(YJZSPJXXSActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(DZZHYJActivity.this, year + "year " + (monthOfYear + 1) + "month " + dayOfMonth + "day", Toast.LENGTH_SHORT).show();
                     mTime.setText(year+"/"+(monthOfYear+1)+"/"+dayOfMonth);
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) , calendar.get(Calendar.DATE));
                datePicker.show();

            }
        });
        mAddress = (EditText) findViewById(R.id.address);
        adapter = new YJZSPJXXListAdapter(this);
        listView.setAdapter(adapter);
        mback= (TextView) findViewById(R.id.backtextview);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent DetailIntent = new Intent();
                DetailIntent.setClass(YJZSPJXXSActivity.this,YJZSPJXXDetailActivity.class);
                DetailIntent.putExtra("Data", dataList.get(position));
                startActivity(DetailIntent);
            }
        });

        Resources res = getResources();
        arrayType = res.getStringArray(R.array.dzzh_type_str);
        initData(mType.getText().toString().trim(),mAddress.getText().toString().trim());
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
                mType.setText(arrayType[positon]);
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

    private ArrayList<YJSPXXSEntity> dataList = new ArrayList<>();

    private void initData(String type,String address) {

        if(dataList.size()>0){
            dataList.clear();
        }
        if(type==null) type="";
        GeYJZSDATA getDZDataSync = new GeYJZSDATA(type, mTime.getText().toString().trim(),address);
    try {
            String rsltStr = getDZDataSync.execute().get(50, TimeUnit.SECONDS);
         if(rsltStr==null){
             adapter.addData(null);
             return;
         }
            try {
                JSONArray jsonArray = new JSONArray(rsltStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                   YJSPXXSEntity entity = new YJSPXXSEntity();
                    /// <param name="X">double经度</param>
                    /// <param name="Y">double纬度</param>
                    /// <param name="di_type">灾害类型</param>
                    /// <param name="di_add">地址</param>
                    /// <param name="di_Casualty">伤亡人数</param>
                    /// <param name="di_EconomicLoss">直接损失</param>
                    /// <param name="di_Relocate">转移人数</param>
                    /// <param name="di_IndirectLoss">间接损失</param>
                    /// <param name="remark">备注</param>

                    //  [{"id":"9","X":"23234.54450000","Y":"23445.44400000","E":"23234.54450000","N":"23445.44400000","di_Type":"3",
                    // "di_Add":"2","di_Casualty":"4","di_EconomicLoss":"5.00","di_Relocate":"6","di_IndirectLoss":"7.00","di_State":"0",
                    // "di_DgId":"0","di_Time":"2015/4/6 18:29:00","remark":"8"},{"
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    entity.setX(object.getString("X"));
                    entity.setY(object.getString("Y"));
                    entity.setDi_Relocate(object.getString("di_Relocate"));
                    entity.setDi_type(object.getString("di_Type"));
                    entity.setRemark(object.getString("remark"));
                    entity.setDi_Casualty(object.getString("di_Casualty"));
                    entity.setDi_Add(object.getString("di_Add"));
                    entity.setDi_IndirectLoss(object.getString("di_IndirectLoss"));
                    entity.setDi_EconomicLoss(object.getString("di_EconomicLoss"));
                    entity.setDi_Time(object.getString("di_Time"));
                    entity.setE(object.getString("E"));
                    entity.setN(object.getString("N"));
                    entity.setId(object.getString("id"));
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


    //调用webservice
    private KsoapValidateHttp ksoap;

    public class GeYJZSDATA extends AsyncTask<String, Integer, String> {
        private String name;
        private String time;
        private String address;
        public GeYJZSDATA(String name, String time,String address) {
            this.name = name;
            this.time = time;
            this.address=address;
            ksoap = new KsoapValidateHttp(YJZSPJXXSActivity.this);
        }


        @Override
        protected String doInBackground(String... str) {
            try {
                Log.i("net", "do");
                String AddRslt = ksoap.GetDzYJZSdata(name, time, address);
                if (AddRslt != null) {
                    return AddRslt;
                } else {
                    //Toast.makeText(YJZSPJXXSActivity.this,"无信息",Toast.LENGTH_LONG).show();
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
}

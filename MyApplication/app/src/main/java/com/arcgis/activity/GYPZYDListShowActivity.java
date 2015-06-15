package com.arcgis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.adapter.GYPZYDListAdapter;
import com.arcgis.entity.GYPZYDEntity;
import com.arcgis.httputil.App;

import java.util.ArrayList;
import java.util.List;

//地质灾害
public class GYPZYDListShowActivity extends Activity implements View.OnClickListener {

    private TextView mapBtn=null;
    private TextView backBtn=null;
    private TextView textTitle=null;
    private ListView showList=null;
    private GYPZYDListAdapter gypzydListAdapter=null;

    //全局变量存储位置
    private App MyApp;
    private List<GYPZYDEntity> GYPZYD_list=new ArrayList<GYPZYDEntity>();
    View header;//顶部
    public static final int TOMAP=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listshowpzyd);
        App.getInstance().addActivity(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        backBtn= (TextView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        textTitle= (TextView) findViewById(R.id.NavigateTitle);
        textTitle.setText("供应用地显示列表");


        showList= (ListView) findViewById(R.id.showList);
        header=inflater.inflate(R.layout.pzyd_header_layout, null);
        showList.addHeaderView(header);
        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    return;
                }
                GYPZYDEntity gypzydEntity=GYPZYD_list.get(position-1);
                if(gypzydEntity!=null){
                    //WGS84坐标系
                    String px=gypzydEntity.getX();
                    String py=gypzydEntity.getY();
                    Intent toMapIntent=new Intent();
                    toMapIntent.setClass(GYPZYDListShowActivity.this,GYPZYDDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("GYYD",gypzydEntity);
                    toMapIntent.putExtras(bundle);
                    startActivity(toMapIntent);
                }
            }
        });

        MyApp=(App) this.getApplication();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            GYPZYDListShowActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch(viewId){
            case R.id.mapBtn:
                Intent toMap1Intent=new Intent();
                toMap1Intent.setClass(GYPZYDListShowActivity.this,MainMap5Activity.class);
                toMap1Intent.putExtra("FROM", "ALLPT");
                startActivity(toMap1Intent);
                break;
            case R.id.backBtn:
                GYPZYDListShowActivity.this.finish();
                break;
            default:
                break;
        }
    }

    private void showListView(List<GYPZYDEntity> GYPZYD_list) {
        if (gypzydListAdapter == null) {
            showList = (ListView) findViewById(R.id.showList);
            gypzydListAdapter = new GYPZYDListAdapter(GYPZYD_list,this);
            showList.setAdapter(gypzydListAdapter);
        } else {
            gypzydListAdapter.onDateChange(GYPZYD_list);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MyApp.getGYPZYD_QUERY_list()!=null && MyApp.getGYPZYD_QUERY_list().size()>0){
            this.GYPZYD_list=MyApp.getGYPZYD_QUERY_list();
            showListView(GYPZYD_list);
        }
    }
}
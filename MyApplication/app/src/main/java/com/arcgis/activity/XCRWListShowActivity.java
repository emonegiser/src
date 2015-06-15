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
import com.arcgis.adapter.XCRWListAdapter;
import com.arcgis.entity.XCRWEntity;
import com.arcgis.httputil.App;

import java.util.ArrayList;
import java.util.List;

//巡查任务
public class XCRWListShowActivity extends Activity implements View.OnClickListener {

    private TextView mapBtn=null;
    private TextView backBtn=null;
    private TextView textTitle=null;
    private ListView showList=null;
    private XCRWListAdapter xcrwListAdapter=null;

    //全局变量存储位置
    private App MyApp;
    private List<XCRWEntity> XCRW_list=new ArrayList<>();
    View header;//顶部
    public static final int TOMAP=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listshowxcrw);
        App.getInstance().addActivity(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setVisibility(View.GONE);

        backBtn= (TextView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        textTitle= (TextView) findViewById(R.id.NavigateTitle);
        textTitle.setText("巡查任务显示列表");


        showList= (ListView) findViewById(R.id.showList);
        header=inflater.inflate(R.layout.pzyd_header_layout, null);
        showList.addHeaderView(header);
        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    return;
                }
                XCRWEntity xcrwEntity=XCRW_list.get(position-1);
                if(xcrwEntity!=null){
                    //WGS84坐标系
                    String px=xcrwEntity.getX();
                    String py=xcrwEntity.getY();
                    Intent toMapIntent=new Intent();
                    toMapIntent.setClass(XCRWListShowActivity.this,XCRWDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("XCRW",xcrwEntity);
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
            XCRWListShowActivity.this.finish();
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
                toMap1Intent.setClass(XCRWListShowActivity.this,MainMap7Activity.class);
                toMap1Intent.putExtra("FROM", "ALLPT");
                startActivity(toMap1Intent);
                break;
            case R.id.backBtn:
                XCRWListShowActivity.this.finish();
                break;
            default:
                break;
        }
    }

    private void showListView(List<XCRWEntity> XCRW_list) {
        if (xcrwListAdapter == null) {
            showList = (ListView) findViewById(R.id.showList);
            xcrwListAdapter = new XCRWListAdapter(XCRW_list,this);
            showList.setAdapter(xcrwListAdapter);
        } else {
            xcrwListAdapter.onDateChange(XCRW_list);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MyApp.getXCRW_list()!=null && MyApp.getXCRW_list().size()>0){
            this.XCRW_list=MyApp.getXCRW_list();
            showListView(XCRW_list);
        }

        Intent intent=getIntent();
        if(intent !=null){
            String mFlag=intent.getStringExtra("FLAG");
            if(mFlag.equals("YES")){
                textTitle.setText("已完成任务显示列表");
                if(MyApp.getYWCXCRW_list()!=null && MyApp.getYWCXCRW_list().size()>0){
                    this.XCRW_list=MyApp.getYWCXCRW_list();
                    showListView(XCRW_list);
                }
            }else if(mFlag.equals("NO")){
                textTitle.setText("未完成任务显示列表");
                if(MyApp.getWWCXCRW_list()!=null && MyApp.getWWCXCRW_list().size()>0){
                    this.XCRW_list=MyApp.getWWCXCRW_list();
                    showListView(XCRW_list);
                }
            }
        }
    }
}
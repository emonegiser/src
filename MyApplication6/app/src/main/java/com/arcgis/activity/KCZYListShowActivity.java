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
import com.arcgis.adapter.KCListAdapter;
import com.arcgis.entity.KCZYEntity;
import com.arcgis.httputil.App;

import java.util.ArrayList;
import java.util.List;

//地质灾害
public class KCZYListShowActivity extends Activity implements View.OnClickListener {

    private TextView mapBtn=null;
    private TextView backBtn=null;
    private ListView showList=null;
    private KCListAdapter kcListAdapter=null;

    //全局变量存储位置
    private App MyApp;
    private List<KCZYEntity> KCZY_list=new ArrayList<KCZYEntity>();
    View header;//顶部
    public static final int TOMAP=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listshowkczy);
        App.getInstance().addActivity(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        backBtn= (TextView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        showList= (ListView) findViewById(R.id.showList);
        header=inflater.inflate(R.layout.header_layout, null);
        showList.addHeaderView(header);
        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    return;
                }
                KCZYEntity kczyEntity=KCZY_list.get(position-1);
                if(kczyEntity!=null){
                    //西安1980坐标系
                    String px=kczyEntity.getPx();
                    String py=kczyEntity.getPy();
                    Intent toMapIntent=new Intent();
                    toMapIntent.setClass(KCZYListShowActivity.this,KCZYDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("KCZY",kczyEntity);
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
            KCZYListShowActivity.this.finish();
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
                toMap1Intent.setClass(KCZYListShowActivity.this,MainMap2Activity.class);
                toMap1Intent.putExtra("FROM", "ALLPT");
                startActivity(toMap1Intent);
                break;
            case R.id.backBtn:
                KCZYListShowActivity.this.finish();
                break;
            default:
                break;
        }
    }

    private void showListView(List<KCZYEntity> KCZY_list) {
        if (kcListAdapter == null) {
            showList = (ListView) findViewById(R.id.showList);
            kcListAdapter = new KCListAdapter(KCZY_list,this);
            showList.setAdapter(kcListAdapter);
        } else {
            kcListAdapter.onDateChange(KCZY_list);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MyApp.getKCZY_list()!=null && MyApp.getKCZY_list().size()>0){
            this.KCZY_list=MyApp.getKCZY_list();
            showListView(KCZY_list);
        }
    }
}
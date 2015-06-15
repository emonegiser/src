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
import com.arcgis.adapter.DZListAdapter;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.httputil.App;

import java.util.ArrayList;
import java.util.List;


public class DZZHListShowActivity extends Activity implements View.OnClickListener {

    private TextView mapBtn=null;
    private TextView backBtn=null;
    private ListView showList=null;
    private DZListAdapter dzListAdapter=null;

    //全局变量存储位置
    private App MyApp;
    private List<DZZHEntity> DZZH_list=new ArrayList<DZZHEntity>();
    View header;//顶部
    //private CenterAtListener centerAtListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listshowdzzh);
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
                DZZHEntity dzzhEntity=DZZH_list.get(position-1);
                if(dzzhEntity!=null){
                    String px=dzzhEntity.getX();
                    String py=dzzhEntity.getY();
                    Intent toMap1Intent=new Intent();
                    toMap1Intent.setClass(DZZHListShowActivity.this,DZZHDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DZZH",dzzhEntity);
                    toMap1Intent.putExtras(bundle);
                    startActivity(toMap1Intent);
                }
            }
        });

        MyApp=(App) this.getApplication();
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
                toMap1Intent.setClass(DZZHListShowActivity.this,MainMap1Activity.class);
                toMap1Intent.putExtra("FROM", "ALLPT");
                startActivity(toMap1Intent);
                break;
            case R.id.backBtn:
                DZZHListShowActivity.this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            DZZHListShowActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void showListView(List<DZZHEntity> DZZH_list) {
        if (dzListAdapter == null) {
            showList = (ListView) findViewById(R.id.showList);
            dzListAdapter = new DZListAdapter(DZZH_list,this);
            showList.setAdapter(dzListAdapter);
        } else {
            dzListAdapter.onDateChange(DZZH_list);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MyApp.getDZZH_list()!=null && MyApp.getDZZH_list().size()>0){
            this.DZZH_list=MyApp.getDZZH_list();
            showListView(DZZH_list);
        }
    }
}
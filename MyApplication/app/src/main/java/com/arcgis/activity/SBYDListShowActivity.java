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
import com.arcgis.adapter.SBYDListAdapter;
import com.arcgis.entity.SBYDEntity;
import com.arcgis.httputil.App;

import java.util.ArrayList;
import java.util.List;

//地质灾害
public class SBYDListShowActivity extends Activity implements View.OnClickListener {

    private TextView mapBtn=null;
    private TextView backBtn=null;
    private TextView textTitle=null;
    private ListView showList=null;
    private SBYDListAdapter sbydListAdapter=null;

    //全局变量存储位置
    private App MyApp;
    private List<SBYDEntity> SBYD_list=new ArrayList<SBYDEntity>();
    View header;//顶部
    public static final int TOMAP=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listshowsbyd);
        App.getInstance().addActivity(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        backBtn= (TextView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        textTitle= (TextView) findViewById(R.id.NavigateTitle);
        textTitle.setText("上报用地显示列表");


        showList= (ListView) findViewById(R.id.showList);
        header=inflater.inflate(R.layout.sbyd_header_layout, null);
        showList.addHeaderView(header);
        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    return;
                }
                SBYDEntity kczyEntity=SBYD_list.get(position-1);
                if(kczyEntity!=null){
                    //WGS84坐标系
                    String px=kczyEntity.getX();
                    String py=kczyEntity.getY();
                    Intent toMapIntent=new Intent();
                    toMapIntent.setClass(SBYDListShowActivity.this,SBYDDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SBYD",kczyEntity);
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
            SBYDListShowActivity.this.finish();
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
                toMap1Intent.setClass(SBYDListShowActivity.this,MainMap3Activity.class);
                toMap1Intent.putExtra("FROM", "ALLPT");
                startActivity(toMap1Intent);
                break;
            case R.id.backBtn:
                SBYDListShowActivity.this.finish();
                break;
            default:
                break;
        }
    }

    private void showListView(List<SBYDEntity> SBYD_list) {
        if (sbydListAdapter == null) {
            showList = (ListView) findViewById(R.id.showList);
            sbydListAdapter = new SBYDListAdapter(SBYD_list,this);
            showList.setAdapter(sbydListAdapter);
        } else {
            sbydListAdapter.onDateChange(SBYD_list);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MyApp.getSBYD_QUERY__list()!=null && MyApp.getSBYD_QUERY__list().size()>0){
            this.SBYD_list=MyApp.getSBYD_QUERY__list();
            showListView(SBYD_list);
        }
    }
}
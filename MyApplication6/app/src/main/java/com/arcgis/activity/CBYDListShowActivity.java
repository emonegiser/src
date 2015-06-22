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
import com.arcgis.adapter.CBYDListAdapter;
import com.arcgis.entity.CBYDEntity;
import com.arcgis.httputil.App;

import java.util.ArrayList;
import java.util.List;

//储备用地列表
public class CBYDListShowActivity extends Activity implements View.OnClickListener {

    private TextView mapBtn=null;
    private TextView backBtn=null;
    private TextView textTitle=null;
    private ListView showList=null;
    private CBYDListAdapter cbydListAdapter=null;

    //全局变量存储位置
    private App MyApp;
    private List<CBYDEntity> CBYD_list=new ArrayList<>();
    View header;//顶部
    public static final int TOMAP=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listshowcbyd);
        App.getInstance().addActivity(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        backBtn= (TextView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        textTitle= (TextView) findViewById(R.id.NavigateTitle);
        textTitle.setText("批准用地显示列表");


        showList= (ListView) findViewById(R.id.showList);
        header=inflater.inflate(R.layout.pzyd_header_layout, null);
        showList.addHeaderView(header);
        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    return;
                }
                CBYDEntity cbydEntity=CBYD_list.get(position-1);
                if(cbydEntity!=null){
                    //WGS84坐标系
                    String px=cbydEntity.getX();
                    String py=cbydEntity.getY();
                    Intent toMapIntent=new Intent();
                    toMapIntent.setClass(CBYDListShowActivity.this,CBYDDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CBYD",cbydEntity);
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
            CBYDListShowActivity.this.finish();
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
                toMap1Intent.setClass(CBYDListShowActivity.this,MainMap6Activity.class);
                toMap1Intent.putExtra("FROM", "ALLPT");
                startActivity(toMap1Intent);
                break;
            case R.id.backBtn:
                CBYDListShowActivity.this.finish();
                break;
            default:
                break;
        }
    }

    private void showListView(List<CBYDEntity> CBYD_list) {
        if (cbydListAdapter == null) {
            showList = (ListView) findViewById(R.id.showList);
            cbydListAdapter = new CBYDListAdapter(CBYD_list,this);
            showList.setAdapter(cbydListAdapter);
        } else {
            cbydListAdapter.onDateChange(CBYD_list);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MyApp.getCBYD_QUERY_list()!=null && MyApp.getCBYD_QUERY_list().size()>0){
            this.CBYD_list=MyApp.getCBYD_QUERY_list();
            showListView(CBYD_list);
        }
    }
}
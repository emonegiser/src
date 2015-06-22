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
import com.arcgis.adapter.WPZFListAdapter;
import com.arcgis.entity.WPZFEntity;
import com.arcgis.httputil.App;

import java.util.ArrayList;
import java.util.List;

//卫片执法
public class WPZFListShowActivity extends Activity implements View.OnClickListener {

    private TextView mapBtn=null;
    private TextView backBtn=null;
    private TextView textTitle=null;
    private ListView showList=null;
    private WPZFListAdapter wpzfListAdapter=null;

    //全局变量存储位置
    private App MyApp;
    private List<WPZFEntity> WPZF_list=new ArrayList<WPZFEntity>();
    View header;//顶部
    public static final int TOMAP=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listshowwpzf);
        App.getInstance().addActivity(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        //mapBtn.setOnClickListener(this);
        mapBtn.setVisibility(View.GONE);

        backBtn= (TextView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        textTitle= (TextView) findViewById(R.id.NavigateTitle);
        textTitle.setText("卫片执法显示列表");


        showList= (ListView) findViewById(R.id.showList);
        header=inflater.inflate(R.layout.pzyd_header_layout, null);
        showList.addHeaderView(header);
        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    return;
                }
                WPZFEntity wpzfEntity=WPZF_list.get(position-1);
                if(wpzfEntity!=null){
                    //WGS84坐标系
                    //String px=wpzfEntity.getX();
                    //String py=wpzfEntity.getY();
                    Intent toMapIntent=new Intent();
                    toMapIntent.setClass(WPZFListShowActivity.this,WPZFDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("WPZF",wpzfEntity);
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
            WPZFListShowActivity.this.finish();
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
                toMap1Intent.setClass(WPZFListShowActivity.this,MainMap8Activity.class);
                toMap1Intent.putExtra("FROM", "ALLPT");
                startActivity(toMap1Intent);
                break;
            case R.id.backBtn:
                WPZFListShowActivity.this.finish();
                break;
            default:
                break;
        }
    }

    private void showListView(List<WPZFEntity> WPZF_list) {
        if (wpzfListAdapter == null) {
            showList = (ListView) findViewById(R.id.showList);
            wpzfListAdapter = new WPZFListAdapter(WPZF_list,this);
            showList.setAdapter(wpzfListAdapter);
        } else {
            wpzfListAdapter.onDateChange(WPZF_list);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MyApp.getWPZF_list()!=null && MyApp.getWPZF_list().size()>0){
            this.WPZF_list=MyApp.getWPZF_list();
            showListView(WPZF_list);
        }
    }
}
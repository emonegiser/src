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
import com.arcgis.adapter.WPZFURLListAdapter;
import com.arcgis.entity.WPURLEntity;
import com.arcgis.httputil.App;

import java.util.ArrayList;
import java.util.List;

//卫片执法
public class WPURLListShowActivity extends Activity implements View.OnClickListener {

    private TextView mapBtn=null;
    private TextView backBtn=null;
    private TextView textTitle=null;
    private ListView showList=null;
    private WPZFURLListAdapter wpzfurlListAdapter=null;

    //全局变量存储位置
    private App MyApp;
    private List<WPURLEntity> WPZFURL_list=new ArrayList<WPURLEntity>();
    View header;//顶部
    public static final int TOMAP=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listshowwpzfurl);
        App.getInstance().addActivity(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        //mapBtn.setOnClickListener(this);
        mapBtn.setVisibility(View.GONE);

        backBtn= (TextView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        textTitle= (TextView) findViewById(R.id.NavigateTitle);
        textTitle.setText("卫片显示列表");


        showList= (ListView) findViewById(R.id.showList);
        header=inflater.inflate(R.layout.pzyd_header_layout, null);
        showList.addHeaderView(header);
        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    return;
                }
                WPURLEntity wpzfurlEntity=WPZFURL_list.get(position-1);
                if(wpzfurlEntity!=null){

                    Intent toMapIntent=new Intent();
                    toMapIntent.setClass(WPURLListShowActivity.this,WPURLDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("WPZFURL",wpzfurlEntity);
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
            WPURLListShowActivity.this.finish();
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
                toMap1Intent.setClass(WPURLListShowActivity.this,MainMap8Activity.class);
                toMap1Intent.putExtra("FROM", "ALLPT");
                startActivity(toMap1Intent);
                break;
            case R.id.backBtn:
                WPURLListShowActivity.this.finish();
                break;
            default:
                break;
        }
    }

    private void showListView(List<WPURLEntity> WPZFURL_list) {
        if (wpzfurlListAdapter == null) {
            showList = (ListView) findViewById(R.id.showList);
            wpzfurlListAdapter = new WPZFURLListAdapter(WPZFURL_list,this);
            showList.setAdapter(wpzfurlListAdapter);
        } else {
            wpzfurlListAdapter.onDateChange(WPZFURL_list);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MyApp.getWPURLEntity_list()!=null && MyApp.getWPURLEntity_list().size()>0){
            this.WPZFURL_list=MyApp.getWPURLEntity_list();
            showListView(WPZFURL_list);
        }
    }
}
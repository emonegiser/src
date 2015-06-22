package com.arcgis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.WPURLEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

//卫片执法
public class WPURLDetailActivity extends Activity implements View.OnClickListener{

    private TextView backtextview;
    private TextView titletextview;
    private TextView mapBtn;

    //卫片乡镇
    TextView TextViewwpxz=null;
    //卫片地图服务
    TextView TextViewwpdtfw=null;
    //卫片附件
    TextView TextViewwpfj=null;
    //所属日期
    TextView TextViewssrq= null;
    //卫片备注
    TextView TextViewrwbz=null;

    private WPURLEntity mWPURLEntity;

    //调用webservice
    private KsoapValidateHttp ksoap;

    private String kcbh;
    DecimalFormat df=null;
    //全局变量存储位置
    private App MyApp;

    private String px=null;
    private String py=null;
    private boolean isNetwork=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wpzfurl_detail);
        App.getInstance().addActivity(this);
        isNetwork= NetUtils.isNetworkAvailable(this);

        backtextview=(TextView)findViewById(R.id.backBtn);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.NavigateTitle);
        titletextview.setText("卫片详情");
        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setText("查看 >");
        mapBtn.setOnClickListener(this);

        df=(DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(8);

        MyApp=(App) this.getApplication();


        TextViewwpxz= (TextView) this.findViewById(R.id.TextViewwpxz);
        TextViewwpdtfw= (TextView) this.findViewById(R.id.TextViewwpdtfw);
        TextViewrwbz= (TextView) this.findViewById(R.id.TextViewrwbz);
        TextViewwpfj= (TextView) this.findViewById(R.id.TextViewwpfj);
        TextViewssrq= (TextView) this.findViewById(R.id.TextViewssrq);
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
                //查看卫片
                Intent toEditIntent=new Intent();
                toEditIntent.setClass(WPURLDetailActivity.this,MainMap8Activity.class);
                startActivity(toEditIntent);
                break;
            case R.id.backBtn:
                WPURLDetailActivity.this.finish();
                break;
            case R.id.downloadFJBtn:
                //下载附件
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            WPURLDetailActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = this.getIntent();
        if(intent!=null){
            mWPURLEntity= (WPURLEntity) intent.getSerializableExtra("WPZFURL");
            if(mWPURLEntity!=null){
                TextViewwpxz.setText(mWPURLEntity.getXZ());
                TextViewwpdtfw.setText(mWPURLEntity.getMAPURL());
                TextViewrwbz.setText(mWPURLEntity.getREMARK());
                TextViewwpfj.setText(mWPURLEntity.getFILES());
                TextViewssrq.setText(mWPURLEntity.getDATE());
            }

            SharedPreferences sp = WPURLDetailActivity.this.getSharedPreferences("WPURL_INFO", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("MAPURL", mWPURLEntity.getMAPURL().trim());
            editor.commit();
        }
    }
}
package com.arcgis.activity;

import android.app.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.activity.toolsUtil.ProShowUtil;
import com.arcgis.entity.YJSPXXEntity;
import com.arcgis.httputil.KsoapValidateHttp;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2015/4/6.
 */
public class YJZSPJXXActivity extends Activity implements View.OnClickListener{

    private TextView mtext1,mtext2,mtext3,mtext4,mtext5,mtext6,mtext7;
    private Button mMap,mOK;
    private TextView mback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activtity_yjzs);
        mtext1= (TextView) findViewById(R.id.address);
        mtext2= (TextView) findViewById(R.id.type);
        mtext3= (TextView) findViewById(R.id.num);
        mtext4= (TextView) findViewById(R.id.lose);
        mtext5= (TextView) findViewById(R.id.pNum);
        mtext6= (TextView) findViewById(R.id.lose2);
        mtext7= (TextView) findViewById(R.id.mark);
        mMap= (Button) findViewById(R.id.btnMap);
        mOK= (Button) findViewById(R.id.btnOk);
        mback= (TextView) findViewById(R.id.backtextview);
        mback.setOnClickListener(this);
        mMap.setOnClickListener(this);
        mOK.setOnClickListener(this);
        /// <summary>
        /// 完善应急值守报警信息
        /// </summary>
        /// <param name="X">double经度</param>
        /// <param name="Y">double纬度</param>
        /// <param name="di_type">灾害类型</param>
        /// <param name="di_add">地址</param>
        /// <param name="di_Casualty">伤亡人数</param>
        /// <param name="di_EconomicLoss">直接损失</param>
        /// <param name="di_Relocate">转移人数</param>
        /// <param name="di_IndirectLoss">间接损失</param>
        /// <param name="remark">备注</param>
        /// <returns></returns>

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnMap:

                break;
            case R.id.btnOk:
                YJSPXXEntity entity=new YJSPXXEntity();
                entity.setX("0");
                entity.setY("0");
                entity.setDi_add(mtext1.getText().toString().trim());
                entity.setDi_Casualty(mtext3.getText().toString().trim());
                entity.setDi_EconomicLoss(mtext4.getText().toString().trim());
                entity.setDi_IndirectLoss(mtext6.getText().toString().trim());
                entity.setRemark(mtext7.getText().toString().trim());
                entity.setDi_type(mtext2.getText().toString().trim());
                entity.setDi_Relocate(mtext5.getText().toString().trim());
                ProShowUtil.showLoading(YJZSPJXXActivity.this,"数据上传中...");
                GetAddYJZSPJXXDATA getDZDataSync = new GetAddYJZSPJXXDATA(entity);
                try {
                    String rsltStr = getDZDataSync.execute().get(50, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.backtextview:
                finish();
                break;
        }

    }
    private KsoapValidateHttp ksoap;

    public class GetAddYJZSPJXXDATA extends AsyncTask<String, Integer, String> {
       private YJSPXXEntity entity;
        public GetAddYJZSPJXXDATA(YJSPXXEntity entitys) {
            entity=entitys;
            ksoap = new KsoapValidateHttp(YJZSPJXXActivity.this);
            Log.i("net", "init");
        }


        @Override
        protected String doInBackground(String... str) {
            try {
                Log.i("net", "do");
                String AddRslt = ksoap.AddYJZSdata(entity);
                if (AddRslt != null) {
                    ProShowUtil.closeLoading();
                    return AddRslt;

                } else {
                    ProShowUtil.closeLoading();
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

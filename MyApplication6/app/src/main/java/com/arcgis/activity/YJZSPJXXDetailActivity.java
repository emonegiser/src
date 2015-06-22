package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.activity.toolsUtil.ACache;
import com.arcgis.activity.toolsUtil.FileUtils;
import com.arcgis.activity.toolsUtil.FormatTools;
import com.arcgis.activity.toolsUtil.ProShowUtil;
import com.arcgis.entity.DZZHYJEntity;
import com.arcgis.entity.DZZHYJImageEntity;
import com.arcgis.entity.YJSPXXSEntity;
import com.arcgis.httputil.KsoapValidateHttp;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2015/4/4.
 */
public class YJZSPJXXDetailActivity extends Activity {
    private TextView mLon;
    private TextView mLat,mPostion;
    private TextView back;
    private EditText maddress, mnum,pnum,mlose,mlose2, mmark, mname;
    private Intent intent;
    private YJSPXXSEntity entity;
    private Button changeBtn;
    private  TextView title;

    private ACache mAcache;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yjzspjxx_detail);
        intent = getIntent();
        if (intent.hasExtra("Data")) {
            entity = (YJSPXXSEntity) intent.getSerializableExtra("Data");
        }
        mLon = (TextView) findViewById(R.id.lon);
        mLat = (TextView) findViewById(R.id.lat);
        maddress = (EditText) findViewById(R.id.address);
        mnum = (EditText) findViewById(R.id.num);
        pnum = (EditText) findViewById(R.id.pnum);
        mmark = (EditText) findViewById(R.id.mark);
        mname = (EditText) findViewById(R.id.name);
        mlose=(EditText) findViewById(R.id.lose);
        mlose2=(EditText) findViewById(R.id.lose2);
        title= (TextView) findViewById(R.id.titletextview);
        mPostion=(TextView)findViewById(R.id.myPositon);
        mPostion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent1=new Intent(YJZSPJXXDetailActivity.this,MyDZZHYJMapActivity.class);
                intent1.putExtra("datas",entity);
                Log.i("postion_",entity.getE());
                startActivity(intent1);
            }
        });
        back=(TextView)findViewById(R.id.backtextview);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YJZSPJXXDetailActivity.this.finish();
            }
        });
        changeBtn = (Button) findViewById(R.id.complete);
        changeBtn.setOnClickListener(myListener);

        title.setText(entity.getDi_type());
        inintData();
        mAcache = ACache.get(this);
    }


    View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         if (v.getId() == R.id.complete) {

               AlertDialog.Builder alertDialog=new AlertDialog.Builder(YJZSPJXXDetailActivity.this);
                alertDialog.setTitle("温馨提示");
                alertDialog.setMessage("点击确定，完成修改？");
                alertDialog.setNegativeButton("取消",null);
                alertDialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProShowUtil.showLoading(YJZSPJXXDetailActivity.this,"数据修改中...");
                        entity.setDi_Add(maddress.getText().toString().trim());
                        entity.setDi_type(mname.getText().toString().trim());
                        entity.setDi_EconomicLoss(mlose.getText().toString().trim());
                        entity.setDi_IndirectLoss(mlose2.getText().toString().trim());
                        entity.setDi_Relocate(pnum.getText().toString().trim());
                        entity.setRemark(mmark.getText().toString().trim());
                        entity.setId(entity.getId());
                        entity.setE(entity.getE());
                        entity.setN(entity.getN());
                        ChangYJZSDATA getDZDataSync = new ChangYJZSDATA(entity);
                        try {
                            String rsltStr = getDZDataSync.execute().get(50, TimeUnit.SECONDS);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                  // dialo
                });
              alertDialog.show();

            }
        }
    };
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
    private void inintData() {
        mLat.setText(entity.getX());
        mLon.setText(entity.getY());
        maddress.setText(entity.getDi_Add());
        mname.setText(entity.getDi_type());
        mmark.setText(entity.getRemark());
        mnum.setText(entity.getDi_Casualty());
        pnum.setText(entity.getDi_Relocate());
        mlose.setText(entity.getDi_EconomicLoss());
        mlose2.setText(entity.getDi_IndirectLoss());
    }


    //调用webservice
    private KsoapValidateHttp ksoap;

    public class ChangYJZSDATA extends AsyncTask<String, Integer, String> {
        private YJSPXXSEntity entity1;

        public ChangYJZSDATA(YJSPXXSEntity entity1) {
            this.entity1 = entity1;
            ksoap = new KsoapValidateHttp(YJZSPJXXDetailActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {

                String AddRslt = ksoap.UpdateYJZSdata(entity1);
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

    private String getTime() {
        long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new Date(time);
        Log.i("time", format.format(d1));
        return format.format(d1);
    }


}

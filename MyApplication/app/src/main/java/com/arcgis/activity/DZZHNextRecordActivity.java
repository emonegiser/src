package com.arcgis.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arcgis.R;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.entity.DZZHinfoEntity;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DZZHNextRecordActivity extends Activity {

    TextView mTVNext;//下一条记录按钮
    TextView mBack;
    TextView LookupforDetail;

    TextView TextViewzb=null;
    TextView TextViewdzbh=null;
    TextView TextViewxxdz=null;

    TextView editTextscjcsj=null;
    TextView editTextbcjcshj=null;
    TextView editTextwyl=null;
    TextView textViewJyqk=null;
    TextView editTextczwt=null;
    TextView editTextclyj=null;
    TextView editTextcljg=null;

    TextView editTextfzzrr=null;
    TextView editTextfzrdh=null;
    TextView editTextjczrr=null;
    TextView editTextjcrdh=null;
    TextView editTextbz=null;

    TableRow row=null;

    ProgressDialog progressDialog;

    String bh;//灾害点编号

    private String id;
    private String filename;

    KsoapValidateHttp ksoap=null;

    JSONArray mRecordArray;
    int SerialNumber=0;//mRecordArray中的对象编号

    DZZHEntity mDZZHEntity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzzhnext_record);

        mTVNext=(TextView)findViewById(R.id.TVNextRecord);
        mBack=(TextView)findViewById(R.id.backtextview);
        LookupforDetail=(TextView)findViewById(R.id.Lookuptextview);

        TextViewzb= (TextView) this.findViewById(R.id.editTextxzb);
        TextViewdzbh= (TextView) this.findViewById(R.id.editTextdzbh);
        TextViewxxdz= (TextView) this.findViewById(R.id.TextViewdz);

        editTextscjcsj= (TextView) this.findViewById(R.id.editTextscjcsj);
        editTextbcjcshj= (TextView) this.findViewById(R.id.editTextbcjcsj);
        editTextwyl= (TextView) this.findViewById(R.id.editTextwyl);

        textViewJyqk=(TextView)this.findViewById(R.id.TextViewJyqk);

        editTextczwt= (TextView) this.findViewById(R.id.editTextczwt);
        editTextclyj= (TextView) this.findViewById(R.id.editTextclyj);
        editTextcljg= (TextView) this.findViewById(R.id.editTextcljg);

        editTextfzzrr= (TextView) this.findViewById(R.id.editTextfzzrr);
        editTextfzrdh= (TextView) this.findViewById(R.id.editTextfzrdh);
        editTextjczrr= (TextView) this.findViewById(R.id.editTextjczrr);
        editTextjcrdh= (TextView) this.findViewById(R.id.editTextjcrdh);
        editTextbz= (TextView) this.findViewById(R.id.editTextbz);

        row=(TableRow)this.findViewById(R.id.row);

        Intent intent=this.getIntent();
        mDZZHEntity=(DZZHEntity)intent.getSerializableExtra("DZZH");
        mRecordArray=JSON.parseArray(intent.getStringExtra("RECORDS"));
        bh= mDZZHEntity.getDZPTBH();

        TextViewzb.setText(mDZZHEntity.getX() + " " + mDZZHEntity.getY());
        TextViewdzbh.setText(bh);
        TextViewxxdz.setText(mDZZHEntity.getXQ()+" "+mDZZHEntity.getXZH()+" "+mDZZHEntity.getCUN()+" "+mDZZHEntity.getZU());

        String LfileName=mDZZHEntity.getPicture();

        if(!(LfileName.equals(""))) {
            //循环下载图片
            String[] picture = LfileName.split(";");
            for (String FileName : picture) {
                id = mDZZHEntity.getDZPTBH();
                filename = FileName;
                //调用webservice下载图片
                DZDownLoad DownLoad = new DZDownLoad();
                try {
                    String DLRslt = DownLoad.execute().get(200, TimeUnit.SECONDS);
                    if (DLRslt != null) {
                        Bitmap DLpicture = base64ToBitmap(DLRslt);
                        ImageView imageView = new ImageView(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(200, 200);  // , 1是可选写的
                        lp.setMargins(0, 0, 0, 0);
                        imageView.setLayoutParams(lp);
                        imageView.setImageBitmap(DLpicture);
                        row.addView(imageView);
                    }
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }

        mTVNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SerialNumber++;
                FillTextviews(SerialNumber);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DZZHNextRecordActivity.this.finish();
            }
        });

        LookupforDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DZZHEntity dzzhEntity=mDZZHEntity;
                if(dzzhEntity!=null){

                    progressDialog=new ProgressDialog(DZZHNextRecordActivity.this);
                    progressDialog.setCancelable(true);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("页面跳转中...");
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();

                    String px=dzzhEntity.getX();
                    String py=dzzhEntity.getY();
                    Intent toMap1Intent=new Intent();
                    toMap1Intent.setClass(DZZHNextRecordActivity.this,DZZHDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DZZH",dzzhEntity);
                    toMap1Intent.putExtras(bundle);
                    startActivity(toMap1Intent);
                    progressDialog.dismiss();
                }
                else
                    ToastUtil.show(DZZHNextRecordActivity.this,"无法传递数据");
            }
        });
    }


    private void FillTextviews(int number)
    {
        if (number<mRecordArray.size()) {
            JSONObject mRecordObject = mRecordArray.getJSONObject(number);
            if(number<mRecordArray.size()-1) {
                editTextscjcsj.setText(mRecordArray.getJSONObject(number+1).getString("JCSJ"));
            }
            else
            {
                editTextscjcsj.setText("");
            }
            editTextbcjcshj.setText(mRecordObject.getString("JCSJ"));
            textViewJyqk.setText(mRecordObject.getString("JYQK"));
            editTextwyl.setText(mRecordObject.getString("WYL"));
            editTextczwt.setText(mRecordObject.getString("CZWT"));
            editTextclyj.setText(mRecordObject.getString("CLYJ"));
            editTextcljg.setText(mRecordObject.getString("CLJG"));
            editTextfzzrr.setText(mRecordObject.getString("FZZRR"));
            editTextfzrdh.setText(mRecordObject.getString("FZZRRPHONE"));
            editTextjczrr.setText(mRecordObject.getString("JCZRR"));
            editTextjcrdh.setText(mRecordObject.getString("JCZRRPHONE"));
            editTextbz.setText(mRecordObject.getString("CONTENTS"));
        }
        else
            ToastUtil.show(DZZHNextRecordActivity.this,"这是最后一条记录了");
    }



    /** 
          * base64转为bitmap 
          * @param base64Data 
          * @return 
          */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public class DZDownLoad extends AsyncTask<String, Integer, String> {

        public DZDownLoad() {
            if (ksoap!=null) {
                ksoap = new KsoapValidateHttp(DZZHNextRecordActivity.this);
            }
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String functionName = "DZZH";
                String DLRslt=ksoap.SysDownLoadPic(id, functionName, filename);
                if(DLRslt!=null){
                    return DLRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

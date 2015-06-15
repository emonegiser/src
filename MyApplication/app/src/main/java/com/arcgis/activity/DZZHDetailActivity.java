package com.arcgis.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DZZHDetailActivity extends Activity implements View.OnClickListener{

    TextView TextViewxzb= null;
    TextView TextViewyzb= null;
    TextView TextViewdzbh=null;
    TextView TextViewssxq= null;
    TextView TextViewssxz= null;
    TextView TextViewsscs= null;
    TextView TextViewsszu= null;
    TextView TextViewdname= null;
    TextView TextViewdzlx= null;
    TextView TextViewgm= null;
    TextView TextViewgmdj= null;
    TextView TextViewwxdx= null;
    TextView TextViewwxhs= null;
    TextView TextViewwxrk= null;
    TextView TextViewjjss= null;
    TextView TextViewxqdj= null;
    TextView TextViewcfsj= null;
    TextView TextViewyxys= null;
    TextView TextViewfzzrr= null;
    TextView TextViewfzrdh= null;
    TextView TextViewjczrr= null;
    TextView TextViewjcrdh= null;
    TextView TextViewdjsj= null;
    TextView TextViewcqcs= null;
    TextView TextViewbz= null;

    TextView TextViewtp=null;
    TextView TextViewsp =null;

    private KsoapValidateHttp ksoap;
    private TextView backtextview;
    private TextView titletextview;
    private TextView mapBtn;

    private DZZHEntity mDZZHEntity;
    private String id;
    private String functionName="DZZH";
    private String filename;
    private String LfileName;
    private TableRow row;

    //全局变量存储位置
    private App MyApp;

    private String px;
    private String py;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dzzh_detail);
        App.getInstance().addActivity(this);
        MyApp=(App) this.getApplication();

        backtextview= (TextView) findViewById(R.id.backBtn); //返回
        backtextview.setOnClickListener(this);

        mapBtn= (TextView) findViewById(R.id.mapBtn);//地图定位
        mapBtn.setOnClickListener(this);

        row = (TableRow) findViewById(R.id.row);//放图片的表格

        TextViewxzb= (TextView) this.findViewById(R.id.editTextxzb);
        TextViewyzb= (TextView) this.findViewById(R.id.editTextyzb);
        TextViewdzbh= (TextView) this.findViewById(R.id.editTextdzbh);
        TextViewssxq= (TextView) this.findViewById(R.id.editTextssxq);
        TextViewssxz= (TextView) this.findViewById(R.id.editTextssxz);
        TextViewsscs= (TextView) this.findViewById(R.id.editTextsscs);
        TextViewsszu= (TextView) this.findViewById(R.id.editTextssz);
        TextViewdname= (TextView) this.findViewById(R.id.editTextdzmc);
        TextViewdzlx= (TextView) this.findViewById(R.id.spinnerdzlx);
        TextViewgm= (TextView) this.findViewById(R.id.editTextdzgm);
        TextViewgmdj= (TextView) this.findViewById(R.id.textViewgmdj);
        TextViewdzbh= (TextView) this.findViewById(R.id.editTextdzbh);
        TextViewwxdx = (TextView)this.findViewById(R.id.editTextwxdx);
        TextViewwxhs= (TextView) this.findViewById(R.id.editTextwxhs);
        TextViewwxrk= (TextView) this.findViewById(R.id.editTextwxrk);
        TextViewjjss= (TextView) this.findViewById(R.id.editTextjjss);
        TextViewxqdj= (TextView) this.findViewById(R.id.spinnerxqdj);
        TextViewcfsj= (TextView) this.findViewById(R.id.editTextcfsj);
        TextViewyxys= (TextView) this.findViewById(R.id.editTextyxys);
        TextViewfzzrr= (TextView) this.findViewById(R.id.editTextfzzrr);
        TextViewfzrdh= (TextView) this.findViewById(R.id.editTextfzrdh);
        TextViewjczrr= (TextView) this.findViewById(R.id.editTextjczrr);
        TextViewjcrdh= (TextView) this.findViewById(R.id.editTextjcrdh);
        TextViewdjsj= (TextView) this.findViewById(R.id.editTextdjsj);
        TextViewcqcs= (TextView) this.findViewById(R.id.editTextcqcs);
        TextViewbz= (TextView) this.findViewById(R.id.editTextbz);

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
                toMap1Intent.setClass(DZZHDetailActivity.this,MainMap1Activity.class);
                toMap1Intent.putExtra("FROM","CENTERAT");
                toMap1Intent.putExtra("PX",TextViewxzb.getText().toString());
                toMap1Intent.putExtra("PY",TextViewyzb.getText().toString());
                toMap1Intent.putExtra("TEXT",TextViewdzbh.getText().toString());
                startActivity(toMap1Intent);
                break;
            case R.id.backBtn:
                DZZHDetailActivity.this.finish();
                break;
            case R.id.TVNext:

                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            DZZHDetailActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = this.getIntent();
        if(intent!=null){
            mDZZHEntity=(DZZHEntity)intent.getSerializableExtra("DZZH");
            if(mDZZHEntity!=null){
                TextViewxzb.setText(mDZZHEntity.getX());
                TextViewyzb.setText(mDZZHEntity.getY());
                TextViewdzbh.setText(mDZZHEntity.getDZPTBH());
                TextViewssxq.setText(mDZZHEntity.getXQ());
                TextViewssxz.setText(mDZZHEntity.getXZH());
                TextViewsscs.setText(mDZZHEntity.getCUN());
                TextViewsszu.setText(mDZZHEntity.getZU());
                TextViewdname.setText(mDZZHEntity.getDNAME());
                TextViewdzlx.setText(mDZZHEntity.getDZTYPE());
                TextViewgm.setText(mDZZHEntity.getGM());
                TextViewgmdj.setText(mDZZHEntity.getGMDJ());
                TextViewwxdx.setText(mDZZHEntity.getWXDX());
                TextViewwxhs.setText(mDZZHEntity.getWXHS());
                TextViewwxrk.setText(mDZZHEntity.getWXRK());
                TextViewjjss.setText(mDZZHEntity.getQZJJSS());
                TextViewxqdj.setText(mDZZHEntity.getXQDJ());

                TextViewcfsj.setText(mDZZHEntity.getCSFSSJ());
                TextViewyxys.setText(mDZZHEntity.getYXYS());
                TextViewfzzrr.setText(mDZZHEntity.getFZZRNAME());
                TextViewfzrdh.setText(mDZZHEntity.getFZZRTEL());

                TextViewjczrr.setText(mDZZHEntity.getJCZRNAME());
                TextViewjcrdh.setText(mDZZHEntity.getJCZRTEL());
                TextViewdjsj.setText(mDZZHEntity.getDJRKYEAR());
                TextViewcqcs.setText(mDZZHEntity.getNCCS());
                TextViewbz.setText(mDZZHEntity.getBZ());
                // this.px=mDZZHEntity.getPx()
                LfileName=mDZZHEntity.getPicture();

                if(LfileName!=null) {
                    //循环下载图片
                    String[] picture = LfileName.split(";");
                    if (picture != null) {
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
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        }
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
            ksoap=new KsoapValidateHttp(DZZHDetailActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
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
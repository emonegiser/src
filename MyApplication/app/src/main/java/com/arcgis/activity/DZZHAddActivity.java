package com.arcgis.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.dao.DZZHDao;
import com.arcgis.drawtool.DrawTool;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.DateUtil;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

import org.java_websocket.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class DZZHAddActivity extends Activity implements View.OnClickListener{

    TextView textViewxzb= null;
//    TextView textViewyzb= null;
    EditText editTextdzbh=null;
    EditText editTextssxq= null;
    EditText editTextssxz= null;
    EditText editTextsscs= null;
    EditText editTextssz= null;
    EditText editTextdzmc= null;
    EditText editTextdzgm= null;
    EditText editTextwxdx= null;
    EditText editTextwxhs= null;
    EditText editTextwxrk= null;
    EditText editTextjjss= null;
    EditText editTextcfsj= null;
    EditText editTextyxys=null;
    EditText editTextfzzrr=null;
    EditText editTextfzrdh=null;
    EditText editTextjczrr=null;
    EditText editTextjcrdh=null;
    EditText editTextdjsj =null;
    EditText editTextcqcs =null;
    EditText editTextbz=null;

    Spinner spinnerdzlx= null;
    Spinner spinnergmdj= null;
    Spinner spinnerxqdj= null;

    private TextView backtextview;
    private TextView titletextview;

    private Button OkBtn=null;
    private Button BackBtn=null;
    private Button zpBtn=null;
    private Button spBtn=null;
    private Button lineBtn=null;
    private Button yxpolygonBtn=null;
    private Button bxpolygonBtn=null;
    private String id = "";
    private String type = "DZZH";

    //调用webservice
    private KsoapValidateHttp ksoap;

    ArrayAdapter<String> spinnerDzlxAdapter;
    ArrayAdapter<String> spinnergmdjAdapter;
    ArrayAdapter<String> spinnerxqdjAdapter;

    //全局变量存储位置
    private App MyApp;

    private String dzbhType="滑坡";
    private String coordStr=null;//辅助面坐标串
    private String fztype=null;//f辅助面类型

    String x=null;
    String y=null;
    String dzptbh=null;
    String xq=null;
    String xzh=null;
    String cun=null;
    String zu=null;
    String dname=null;
    String dztype=null;
    String gm=null;
    String gmdj=null;
    String wxdx=null;
    String wxhs=null;
    String wxrk=null;
    String qzjjss=null;
    String xqdj=null;
    String csfssj=null;
    String yxys=null;
    String fzzrname=null;
    String fzzrtel=null;
    String jczrname=null;
    String jczrtel=null;
    String djrkyear=null;
    String nccs=null;
    String bz=null;

    SimpleDateFormat format=null;
    DecimalFormat df=null;
    private boolean isNetwork=false;
    private DZZHDao dzzhDao=null;
//    String coordsStr=null;//辅助面坐标串

    //存储照片视频文件
    private File savePhototFile=null;
    private File saveVideoFile=null;
    //视频照片路径
    TextView TextViewsp=null;

    //上传数据流
    byte[] photobs = null;
    byte[] photobs2 = null;
    byte[] videobs = null;
    String pictureName="";//全部相片名字符串
    String photoName = null;//单张相片名
    String videoName = null;
    //上传人及地灾编号、备注
    String objId=null ;
    String UpPerson=null;
    String remark="";


    ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_adddzzh);
        App.getInstance().addActivity(this);

        OkBtn= (Button) findViewById(R.id.OkBtn);//保存
        OkBtn.setOnClickListener(this);

        BackBtn= (Button) findViewById(R.id.BackBtn);//取消
        BackBtn.setOnClickListener(this);

        zpBtn= (Button) findViewById(R.id.zpBtn);//拍照
        zpBtn.setOnClickListener(this);

        spBtn= (Button) findViewById(R.id.spBtn);//视频
        spBtn.setOnClickListener(this);

        lineBtn=(Button) findViewById(R.id.lineBtn);//添加辅助线
        lineBtn.setOnClickListener(this);

        yxpolygonBtn=(Button) findViewById(R.id.yxpolygonBtn);//添加影响范围面
        yxpolygonBtn.setOnClickListener(this);

        bxpolygonBtn=(Button) findViewById(R.id.bxpolygonBtn);//添加避险区域面
        bxpolygonBtn.setOnClickListener(this);

        backtextview= (TextView) findViewById(R.id.backtextview);//返回
        backtextview.setOnClickListener(this);

        titletextview=(TextView) findViewById(R.id.titletextview);//标题
        titletextview.setText("添加地质灾害点");

        TextViewsp= (TextView) findViewById(R.id.TextViewsp);

        textViewxzb= (TextView) this.findViewById(R.id.editTextxzb);
//        textViewyzb= (TextView) this.findViewById(R.id.editTextyzb);
        editTextdzbh= (EditText) this.findViewById(R.id.editTextdzbh);
        editTextssxq= (EditText) this.findViewById(R.id.editTextssxq);
        editTextssxz= (EditText) this.findViewById(R.id.editTextssxz);
        editTextsscs= (EditText) this.findViewById(R.id.editTextsscs);
        editTextssz = (EditText)this.findViewById(R.id.editTextssz);
        editTextdzmc = (EditText)this.findViewById(R.id.editTextdzmc);
        editTextdzgm = (EditText)this.findViewById(R.id.editTextdzgm);
        editTextwxdx = (EditText)this.findViewById(R.id.editTextwxdx);
        editTextwxhs = (EditText)this.findViewById(R.id.editTextwxhs);
        editTextwxrk = (EditText)this.findViewById(R.id.editTextwxrk);
        editTextjjss = (EditText)this.findViewById(R.id.editTextjjss);
        editTextcfsj = (EditText)this.findViewById(R.id.editTextcfsj);
        editTextyxys = (EditText)this.findViewById(R.id.editTextyxys);
        editTextfzzrr = (EditText)this.findViewById(R.id.editTextfzzrr);
        editTextfzrdh = (EditText)this.findViewById(R.id.editTextfzrdh);
        editTextjczrr = (EditText)this.findViewById(R.id.editTextjczrr);
        editTextjcrdh = (EditText)this.findViewById(R.id.editTextjcrdh);
        editTextdjsj = (EditText)this.findViewById(R.id.editTextdjsj);
        editTextcqcs = (EditText)this.findViewById(R.id.editTextcqcs);
        editTextbz = (EditText)this.findViewById(R.id.editTextbz);
        row = (TableRow) findViewById(R.id.row);//放图片的表格

        spinnerdzlx= (Spinner) this.findViewById(R.id.spinnerdzlx);
        spinnergmdj= (Spinner) this.findViewById(R.id.spinnergmdj);
        spinnerxqdj= (Spinner) this.findViewById(R.id.spinnerxqdj);

        //实例化全局变量
        MyApp=(App) this.getApplication();

        df=(DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(8);

        spinnerDzlxAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, MyApp.getDZType());
        spinnerDzlxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerdzlx.setAdapter(spinnerDzlxAdapter);
        spinnerdzlx.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DZZHAddActivity.this.dzbhType = parent.getItemAtPosition(position).toString();
                bingdzbh();//绑定地灾编号
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnergmdjAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,MyApp.getDZScale());
        spinnergmdjAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnergmdj.setAdapter(spinnergmdjAdapter);

        spinnerxqdjAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, MyApp.getDZScale());
        spinnerxqdjAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerxqdj.setAdapter(spinnerxqdjAdapter);

        dzzhDao=new DZZHDao(DZZHAddActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dzzhDao.close();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        switch(viewId){
            //保存
            case R.id.OkBtn:
                isNetwork= NetUtils.isNetworkAvailable(this);//判断是否有网络
                dzptbh = editTextdzbh.getText().toString();
                xq = editTextssxq.getText().toString();
                xzh = editTextssxz.getText().toString();
                cun = editTextsscs.getText().toString();
                zu = editTextssz.getText().toString();
                dname = editTextdzmc.getText().toString();
                gm = editTextdzgm.getText().toString();
                wxdx = editTextwxdx.getText().toString();
                wxhs = editTextwxhs.getText().toString();
                wxrk = editTextwxrk.getText().toString();
                qzjjss = editTextjjss.getText().toString();
                csfssj = editTextcfsj.getText().toString();
                yxys = editTextyxys.getText().toString();
                fzzrname = editTextfzzrr.getText().toString();
                fzzrtel = editTextfzrdh.getText().toString();
                jczrname = editTextjczrr.getText().toString();
                jczrtel = editTextjcrdh.getText().toString();
                djrkyear = editTextdjsj.getText().toString();
                nccs = editTextcqcs.getText().toString();
                bz = editTextbz.getText().toString();
                dztype = spinnerdzlx.getSelectedItem().toString();
                gmdj = spinnergmdj.getSelectedItem().toString();
                xqdj = spinnerxqdj.getSelectedItem().toString();
                if(isNetwork){
                    progressdialog=new ProgressDialog(MainMap1Activity.getInstance());
                    progressdialog.setCancelable(false);
                    progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressdialog.setMessage("保存中...");
                    progressdialog.setIndeterminate(true);
                    progressdialog.show();

                    String [] XY=textViewxzb.getText().toString().split(";");
                    x=XY[0];
                    y=XY[1];
//                    y=textViewyzb.getText().toString();


                    if(coordStr!=null) {
                        //保存辅助面
                        AddPolygon addPolygon = new AddPolygon(dzptbh, coordStr, type);
                        try {
                            String datapolygon = addPolygon.execute().get(25, TimeUnit.SECONDS);
                            if (datapolygon.equals("1")) {
                                ToastUtil.show(DZZHAddActivity.this, "信息保存中...");
                            } else {
                                ToastUtil.show(DZZHAddActivity.this, "信息保存失败");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                            ToastUtil.show(DZZHAddActivity.this, "连接服务器超时");
                        }
                    }
                    //添加属性信息
                    GetDZDataSync getDZDataSync=new GetDZDataSync();
                    try {
                        String dataRslt=getDZDataSync.execute().get(25, TimeUnit.SECONDS);
                        if(dataRslt!=null){
                            if(dataRslt.equals("保存失败!")){
                                ToastUtil.show(this,dataRslt+"数据将被保存至本地数据库");
                                DZZHEntity dzzhEntity=new DZZHEntity();
                                dzzhEntity.setX(x);
                                dzzhEntity.setY(y);
                                dzzhEntity.setDZPTBH(dzptbh);
                                dzzhEntity.setXQ(xq);
                                dzzhEntity.setXZH(xzh);
                                dzzhEntity.setCUN(cun);
                                dzzhEntity.setZU(zu);
                                dzzhEntity.setDNAME(dname);
                                dzzhEntity.setDZTYPE(dztype);
                                dzzhEntity.setGM(gm);
                                dzzhEntity.setGMDJ(gmdj);
                                dzzhEntity.setWXDX(wxdx);
                                dzzhEntity.setWXHS(wxhs);
                                dzzhEntity.setWXRK(wxrk);
                                dzzhEntity.setQZJJSS(qzjjss);
                                dzzhEntity.setXQDJ(xqdj);
                                dzzhEntity.setCSFSSJ(csfssj);
                                dzzhEntity.setYXYS(yxys);
                                dzzhEntity.setFZZRNAME(fzzrname);
                                dzzhEntity.setFZZRTEL(fzzrtel);
                                dzzhEntity.setJCZRNAME(jczrname);
                                dzzhEntity.setJCZRTEL(jczrtel);
                                dzzhEntity.setDJRKYEAR(djrkyear);
                                dzzhEntity.setNCCS(nccs);
                                dzzhEntity.setBZ(bz);
                                //插入数据
                                dzzhDao.add(dzzhEntity);
                                //跳转到地图页面
                                Intent toMapIntent=new Intent();
                                toMapIntent.setClass(this,MainMap1Activity.class);
                                toMapIntent.putExtra("PX",x);
                                toMapIntent.putExtra("PY",y);
                                setResult(RESULT_OK,toMapIntent);
                                DZZHAddActivity.this.finish();
                            }else if(dataRslt.equals("保存成功!")){
                                ToastUtil.show(this,dataRslt);
                                Intent toMapIntent=new Intent();
                                toMapIntent.setClass(this,MainMap1Activity.class);
                                toMapIntent.putExtra("PX",x);
                                toMapIntent.putExtra("PY",y);
                                //public static final int RESULT_OK = -1;
                                setResult(RESULT_OK,toMapIntent);
                                DZZHAddActivity.this.finish();
                            }
                        }
                        else{
                            ToastUtil.show(this,"请检查网络连接,该数据点被存入本地数据库");
                            DZZHEntity dzzhEntity=new DZZHEntity();
                            dzzhEntity.setX(x);
                            dzzhEntity.setY(y);
                            dzzhEntity.setDZPTBH(dzptbh);
                            dzzhEntity.setXQ(xq);
                            dzzhEntity.setXZH(xzh);
                            dzzhEntity.setCUN(cun);
                            dzzhEntity.setZU(zu);
                            dzzhEntity.setDNAME(dname);
                            dzzhEntity.setDZTYPE(dztype);
                            dzzhEntity.setGM(gm);
                            dzzhEntity.setGMDJ(gmdj);
                            dzzhEntity.setWXDX(wxdx);
                            dzzhEntity.setWXHS(wxhs);
                            dzzhEntity.setWXRK(wxrk);
                            dzzhEntity.setQZJJSS(qzjjss);
                            dzzhEntity.setXQDJ(xqdj);
                            dzzhEntity.setCSFSSJ(csfssj);
                            dzzhEntity.setYXYS(yxys);
                            dzzhEntity.setFZZRNAME(fzzrname);
                            dzzhEntity.setFZZRTEL(fzzrtel);
                            dzzhEntity.setJCZRNAME(jczrname);
                            dzzhEntity.setJCZRTEL(jczrtel);
                            dzzhEntity.setDJRKYEAR(djrkyear);
                            dzzhEntity.setNCCS(nccs);
                            dzzhEntity.setBZ(bz);
                            //插入数据
                            dzzhDao.add(dzzhEntity);
                            //跳转到地图页面
                            Intent toMapIntent=new Intent();
                            toMapIntent.setClass(this,MainMap1Activity.class);
                            toMapIntent.putExtra("PX",x);
                            toMapIntent.putExtra("PY",y);
                            setResult(RESULT_OK,toMapIntent);
                            DZZHAddActivity.this.finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        ToastUtil.show(DZZHAddActivity.this,"连接服务器超时");
                    }

                    if(videobs==null && photobs==null){
                        return;
                    }

                    if(arrayPath!=null){
                        //执行方法，循环上传并保存多张相片
                        for (String photoPath:arrayPath) {
                            String photoName = photoPath.substring(photoPath.lastIndexOf("/")+1,photoPath.length());
                            AddDZVediosPicturesSync addKCVediosPicturesSync = new AddDZVediosPicturesSync(id, type, photoName, photoPath);//上传相片
                            try {
                                String rslt = addKCVediosPicturesSync.execute().get(2000, TimeUnit.SECONDS);
                                if (rslt != null) {
                                    if (rslt.equals(photoName)) {
                                        pictureName+=photoName+";";
                                    }else
                                    {
                                        ToastUtil.show(this, "图片保存失败");
                                    }

                                } else {
                                    ToastUtil.show(this, "服务器返回值为空");
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            }

                        }


                        SharedPreferences sp = DZZHAddActivity.this.getSharedPreferences("LOGIN_INFO",  DZZHAddActivity.this.MODE_PRIVATE);
                        objId = sp.getString("PID",null);
                        UpPerson = sp.getString("NAME",null);

                        if (pictureName!=null&&!pictureName.equals(""))
                        {
                            AddDZPicture addDZpicture=new AddDZPicture();//保存相片
                            try {
                                String AddRslt = addDZpicture.execute().get(2000,TimeUnit.SECONDS);
                                if (AddRslt!=null){
                                    ToastUtil.show(this,AddRslt);
                                }else{
                                    ToastUtil.show(this, "图片保存失败");
                                }
                            }catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            }

                        }

                    }else{ToastUtil.show(this, "本地图片路径未正常存储");}
                }else{
                    ToastUtil.show(this,"请检查网络连接,该数据点被存入本地数据库");
                    DZZHEntity dzzhEntity=new DZZHEntity();
                    dzzhEntity.setX(x);
                    dzzhEntity.setY(y);
                    dzzhEntity.setDZPTBH(dzptbh);
                    dzzhEntity.setXQ(xq);
                    dzzhEntity.setXZH(xzh);
                    dzzhEntity.setCUN(cun);
                    dzzhEntity.setZU(zu);
                    dzzhEntity.setDNAME(dname);
                    dzzhEntity.setDZTYPE(dztype);
                    dzzhEntity.setGM(gm);
                    dzzhEntity.setGMDJ(gmdj);
                    dzzhEntity.setWXDX(wxdx);
                    dzzhEntity.setWXHS(wxhs);
                    dzzhEntity.setWXRK(wxrk);
                    dzzhEntity.setQZJJSS(qzjjss);
                    dzzhEntity.setXQDJ(xqdj);
                    dzzhEntity.setCSFSSJ(csfssj);
                    dzzhEntity.setYXYS(yxys);
                    dzzhEntity.setFZZRNAME(fzzrname);
                    dzzhEntity.setFZZRTEL(fzzrtel);
                    dzzhEntity.setJCZRNAME(jczrname);
                    dzzhEntity.setJCZRTEL(jczrtel);
                    dzzhEntity.setDJRKYEAR(djrkyear);
                    dzzhEntity.setNCCS(nccs);
                    dzzhEntity.setBZ(bz);
                    //插入数据
                    dzzhDao.add(dzzhEntity);
                    //跳转到地图页面
                    Intent toMapIntent=new Intent();
                    toMapIntent.setClass(this,MainMap1Activity.class);
                    toMapIntent.putExtra("PX",x);
                    toMapIntent.putExtra("PY",y);
                    setResult(RESULT_OK,toMapIntent);
                    DZZHAddActivity.this.finish();
                }
                progressdialog.dismiss();
                break;
            case R.id.backtextview:
                DZZHAddActivity.this.finish();
                break;
            case R.id.BackBtn:
                DZZHAddActivity.this.finish();
                break;
            case R.id.lineBtn:
                Intent dzzhIntent0=new Intent();
                dzzhIntent0.setClass(DZZHAddActivity.this,Map1FZ.class);
                Bundle bundle0=new Bundle();
                bundle0.putString("Flag","DZZHAddActivity");
                bundle0.putString("TX","line");
                bundle0.putString("TYPE","裂缝位置线");
                bundle0.putString("PX",x);
                bundle0.putString("PY",y);
                dzzhIntent0.putExtras(bundle0);
                startActivityForResult(dzzhIntent0,4);
                break;
            case R.id.yxpolygonBtn:
                Intent dzzhIntent=new Intent();
                dzzhIntent.setClass(DZZHAddActivity.this,Map1FZ.class);
                Bundle bundle=new Bundle();
                bundle.putString("Flag","DZZHAddActivity");
                bundle.putString("TX","plon");
                bundle.putString("TYPE","影响范围");
                bundle.putString("PX",x);
                bundle.putString("PY",y);
                dzzhIntent.putExtras(bundle);
                startActivityForResult(dzzhIntent,4);
                break;
            case R.id.bxpolygonBtn:
                Intent dzzhIntent2=new Intent();
                dzzhIntent2.setClass(DZZHAddActivity.this,Map1FZ.class);
                Bundle bundle2=new Bundle();
                bundle2.putString("Flag","DZZHAddActivity");
                bundle2.putString("TX","plon");
                bundle2.putString("TYPE","避险区域");
                bundle2.putString("PX",x);
                bundle2.putString("PY",y);
                dzzhIntent2.putExtras(bundle2);
                startActivityForResult(dzzhIntent2,4);

                break;
            case R.id.zpBtn:
                String photoPath= Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp/images";
                File photoDir = new File(photoPath);
                if (!photoDir.exists()) {
                    photoDir.mkdirs();
                }
                savePhototFile = new File(photoPath, "DZZH"+formatter.format(date) + ".jpg");
                photoName="DZZH"+formatter.format(date) + ".jpg";

                savePhototFile.delete();
                if (!savePhototFile.exists()) {
                    try {
                        savePhototFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(savePhototFile));
                startActivityForResult(photoIntent, 1);
                break;
            case R.id.spBtn:
                String videoPath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp/video";
                File videoDir = new File(videoPath);
                if (!videoDir.exists()) {
                    videoDir.mkdirs();
                }

                saveVideoFile = new File(videoPath,"DZZH"+formatter.format(date) + ".avi");
                videoName="DZZH"+formatter.format(date) + ".avi";

                saveVideoFile.delete();
                if (!saveVideoFile.exists()) {
                    try {
                        saveVideoFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveVideoFile));
                startActivityForResult(videoIntent, 2);
            default:
                break;
        }
    }

    private TableRow row;
    String filepath;
    ArrayList<String> arrayName=new ArrayList<String>();
    ArrayList<String> arrayPath=new ArrayList<String>();
    //此回传方法中执行相片压缩
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(1==requestCode && RESULT_OK==resultCode){

            FileInputStream photoIn = null;
            try {
                if (savePhototFile != null) {
                    photoIn = new FileInputStream(savePhototFile);
                }
                photobs = new byte[photoIn.available()];
                photoIn.read(photobs);
                photoIn.close();
                filepath = savePhototFile.toString();

                arrayName.add(photoName);
                arrayPath.add(filepath);

                Bitmap bm = getSmallBitmap(filepath);
                FileOutputStream b = null;
                b = new FileOutputStream(savePhototFile.toString());
                // 把数据写入文件...10 是压缩率，表示压缩90%; 如果不压缩是100，表示压缩率为0
                boolean iscompress = bm.compress(Bitmap.CompressFormat.JPEG, 20, b);
                if (iscompress) {
                    ImageView imageView = new ImageView(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(200, 200);  // , 1是可选写的
                    lp.setMargins(0, 0, 0, 0);
                    imageView.setLayoutParams(lp);
                    imageView.setImageBitmap(bm);
                    row.addView(imageView);

                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(2==requestCode && RESULT_OK==resultCode){

            FileInputStream videoIn = null;
            try {
                if(saveVideoFile!=null){
                    videoIn = new FileInputStream(saveVideoFile);
                }
                videobs = new byte[videoIn.available()];
                TextViewsp.setText(saveVideoFile.toString());
                videoIn.read(videobs);

                videoIn.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(4==requestCode && RESULT_OK==resultCode){
            Bundle b=data.getExtras();
            coordStr=b.getString("PXY");
            fztype=b.getString("TYPE");

        }
    }

    // Bitmap转换成byte[]
    public byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    //通过路劲压缩图片
    public static Bitmap getSmallBitmap (String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    //上传图片
    public class AddDZVediosPicturesSync extends AsyncTask<String, Integer, String> {
        //    byte[] vediobyte;
//        String vedioname;
//        String videobsBase64;
        String photobsBase64;
        String id;
        String type;
        ArrayList<String> arrayNmae=new ArrayList<String>();
        ArrayList<String> arrayPath=new ArrayList<String>();
        String photoName;
        String photoPath;
        byte[] photobs2;

        public AddDZVediosPicturesSync(String id,String type,String photoName,String photoPath) {
            this.id=id;
            this.type=type;
            this.photoPath=photoPath;
            this.photoName=photoName;
            ksoap=new KsoapValidateHttp(DZZHAddActivity.this);
            photobs2=Bitmap2Bytes(BitmapFactory.decodeFile(photoPath));
            this.arrayNmae=arrayNmae;
            this.arrayPath=arrayPath;
//            this.vedioname=vedioname;
//            this.picturebyte=picture;
//            this.picname=picname;
            this.photoName=photoName;
            this.photobs2=photobs2;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... str) {

            try {
//
//                if(vediobyte!=null){
//                    videobsBase64= Base64.encodeBytes(vediobyte);
//                }
//
                if(photobs2!=null){
                    photobsBase64= Base64.encodeBytes(photobs2);
                }

//                if(videobsBase64==null){
//                    videobsBase64="";
//                }
                if(photobsBase64==null){
                    photobsBase64="";
                }

                String AddRslt=ksoap.DZWebUploadVediosPictures(id,type,photoName, photobsBase64);
                if(AddRslt!=null){
                    return AddRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(progressdialog!=null){
                progressdialog.dismiss();
            }
        }
    }
    //添加属性信息
    public class GetDZDataSync extends AsyncTask<String, Integer, String> {

        public GetDZDataSync() {
            ksoap=new KsoapValidateHttp(DZZHAddActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebAddBJS_DZZH_PT(x,y,dzptbh,xq,xzh,cun,zu,dname,dztype,gm,gmdj,wxdx,wxhs,wxrk,
                        qzjjss,xqdj,csfssj,yxys,fzzrname,fzzrtel,jczrname,jczrtel,djrkyear,nccs,bz);
                if(AddRslt!=null){
                    return AddRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(progressdialog!=null){
                progressdialog.dismiss();
            }
        }
    }
    //保存图片
    public class AddDZPicture extends AsyncTask<String, Integer, String> {

        public AddDZPicture() {
            ksoap=new KsoapValidateHttp(DZZHAddActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                objId=dzptbh;
                String AddRslt=ksoap.SysPicAdd(pictureName,UpPerson,objId,remark);
                if(AddRslt!=null){
                    return AddRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(progressdialog!=null){
                progressdialog.dismiss();
            }
        }
    }

    public class QueryDisByIdd extends AsyncTask<String, Integer, String> {

        public QueryDisByIdd() {
            ksoap=new KsoapValidateHttp(DZZHAddActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                objId=dzptbh;
                String iddRslt=ksoap.QueryDisByIdd(dzbhType);
                if(iddRslt!=null){
                    return iddRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(progressdialog!=null){
                progressdialog.dismiss();
            }
        }
    }

    //定位所在村社
    public class GetXZDataSyCS extends AsyncTask<String, Integer, String> {

        String px=null;
        String py=null;

        public GetXZDataSyCS() {
        }

        public GetXZDataSyCS(String px, String py) {
            ksoap=new KsoapValidateHttp(DZZHAddActivity.this);
            this.px = px;
            this.py = py;
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String posRslt=ksoap.WebGetCSByXY(this.px,this.py);
                if(posRslt!=null){
                    return posRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //保存辅助面
    public  class AddPolygon extends AsyncTask<String, Integer, String>{
        String dzptbh;
        String coordstr;
        String type;

        public AddPolygon(String dzptbh,String coordstr,String type) {
            ksoap=new KsoapValidateHttp(DZZHAddActivity.this );
            this.coordstr=coordstr;
            this.dzptbh=dzptbh;
            this.type=type;
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRsltp=ksoap.AddPolygon(dzptbh,coordstr,type);
                if(AddRsltp!=null){
                    return AddRsltp;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            DZZHAddActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onResume() {
        super.onResume();
        String XZ=null;

        Intent fromMapIntent=this.getIntent();
        if(fromMapIntent!=null){
//            coordsStr= fromMapIntent.getStringExtra("COORDSTR");
            this.x= fromMapIntent.getDoubleExtra("PX", 0.0f)+"";
            this.y= fromMapIntent.getDoubleExtra("PY", 0.0f)+"";
            if(x !=null &&y!=null) {
                Point Point2359 = (Point) GeometryEngine.project(new Point(fromMapIntent.getDoubleExtra("PX", 0.0f),
                                fromMapIntent.getDoubleExtra("PY", 0.0f)),
                        SpatialReference.create(4326),
                        SpatialReference.create(2359));
                XZ = fromMapIntent.getStringExtra("XZ");
                textViewxzb.setText(df.format(Point2359.getX())+";"+df.format(Point2359.getY()));//标注
//                textViewyzb.setText(df.format(Point2359.getY()));
//                this.x=df.format(Point2359.getX()).toString();
//                this.y=df.format(Point2359.getY()).toString();
                //定位所在村社
                GetXZDataSyCS getXZDataCS = new GetXZDataSyCS(x + "", y + "");
                try {
                    String dwRslt = getXZDataCS.execute().get(200, TimeUnit.SECONDS);
                    if (dwRslt != null && dwRslt.length() > 0) {
                        editTextsscs.setText(dwRslt);
                        editTextsscs.setText(dwRslt);
                    } else {
                        ToastUtil.show(this, "未成功获取所在村社,请手动输入");
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                } catch (TimeoutException e1) {
                    e1.printStackTrace();
                }
                if (XZ != null) {
                    editTextssxz.setText(XZ);
                    editTextdzmc.setText(XZ);
                } else {
                    ToastUtil.show(this, "未成功获取所在乡镇,请手动输入");
                }
            }
//            if(coordsStr !=null) {
//
//            }

        }

        format = new SimpleDateFormat("yyyy");
        String dataStr = format.format(new Date());
        editTextcfsj.setText(dataStr);
        editTextdjsj.setText(dataStr);
        bingdzbh();//绑定地灾编号
    }

    public void bingdzbh(){
        String typeName = "";
        String bh ="";
        QueryDisByIdd DisIdd=new QueryDisByIdd();//设置地灾编号
        try {
            String IddRslt = DisIdd.execute().get(25,TimeUnit.SECONDS);
            if (IddRslt!=null){
                int idd = Integer.valueOf(IddRslt)+1;
                if (idd<10){
                    bh = "00"+idd;
                }else if(idd>9&&idd<100){
                    bh = "0"+idd;
                }else {
                    bh=""+idd;
                }
                switch (dzbhType)
                {
                    case "滑坡":
                        typeName="hp";
                        break;
                    case "崩塌":
                        typeName="bt";
                        break;
                    case "泥石流":
                        typeName="nsl";
                        break;
                    case "地面塌陷":
                        typeName="dmtx";
                        break;
                    case "地裂缝":
                        typeName="dlf";
                        break;
                    case "地面沉降":
                        typeName="dmcj";
                        break;
                    case "危岩体":
                        typeName="wyt";
                        break;
                    case "采空塌陷":
                        typeName="cktx";
                        break;
                    case "地震":
                        typeName="dz";
                        break;
                    case "火山":
                        typeName="hs";
                        break;
                    case "潜在滑坡":
                        typeName="qzhp";
                        break;
                }
                editTextdzbh.setText("qxg-"+typeName+"-"+bh);
                //ToastUtil.show(this,IddRslt);
            }else{
                ToastUtil.show(this, "获取编号失败");
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 日期选择器
     *
     *
     */
    // private  static
    public static class  DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d("OnDateSet", "select year:" + year + ";month:" + month + ";day:" + day);
            String m="";
            String d="";
            if(month<10){
                m="0"+(month+1)+"";
            }else{
                m=month+"";
            }

            if(day<10){
                d="0"+day+"";
            }else{
                d=day+"";
            }
            // this.editTextdjsj.setText(year+"-"+m+"-"+d);
        }
    }

    private String loadPhoneStatus(){
        TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceNo=phoneMgr.getDeviceId();
        Log.i("deviceNo", deviceNo);
        return deviceNo;
    }
}
package com.arcgis.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.arcgis.R;
import com.arcgis.dao.DZZHDao;
import com.arcgis.dao.DZZHinfoDao;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.entity.DZZHinfoEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DZZHinfoActivity extends Activity implements View.OnClickListener{

    private final String TAG="DZZHinfoActivity";

    TextView TextViewzb=null;
    TextView TextViewdzbh=null;
    TextView TextViewxxdz=null;

    EditText editTextscjcsj=null;
    EditText editTextbcjcshj=null;
    EditText editTextwyl=null;
    EditText editTextczwt=null;
    EditText editTextclyj=null;
    EditText editTextcljg=null;

    EditText editTextfzzrr=null;
    EditText editTextfzrdh=null;
    EditText editTextjczrr=null;
    EditText editTextjcrdh=null;
    EditText editTextbz=null;

    Spinner spinnerjyqk=null;
    ArrayAdapter<String> spinnerjyqkAdapter=null;
    private final static String[] jyqkArrays={"无降雨","小雨","中雨","大雨","特大雨"};

    Button lineBtn=null;
    Button polygonBtn=null;
    Button zpBtn=null;
    Button spBtn=null;
    Button BackBtn=null;
    Button OkBtn=null;

    private Button yxpolygonBtn=null;
    private Button bxpolygonBtn=null;

    TextView QueryRecords;

    private TableRow row;

    private String Xzb="";
    private String Yzb="";

    private DZZHEntity mDZZHEntity;
    private DZZHinfoEntity mdzzhinfoEntity;

    private String id;
    private String functionName="DZZH";
    private String filename;
    private String LfileName;

    private boolean isNetwork=false;

    private KsoapValidateHttp ksoap;
    private TextView backtextview;
    private TextView LookupforDetail;

    SimpleDateFormat format=null;
    DecimalFormat df=null;

    private DZZHDao dzzhDao=null;
    private DZZHinfoDao dzzHinfoDao=null;

    String coordStr=null;//辅助面坐标串
    private String fztype=null;//f辅助面类型

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
    String UpPerson=null;
    String remark="";

    ProgressDialog progressdialog;

    private List<DZZHEntity> DZZH_list=new ArrayList<DZZHEntity>();

    //全局变量存储位置
    private App MyApp;

    private String dzbhType="滑坡";

    String objId=null ;
    String bh;//灾害点编号
    String ddr;//带队人
    String scry;//随从人员

    String scjcsj;//上次监测数据
    String bcjcsj;//本次监测数据
    String jyqk;//降雨情况
    String wyl;//位移量
    String czwt;//存在问题
    String clyj;//处理意见
    String cljg;//处理结果

    String fzzrr;//防灾责任人
    String fzzrrTel;//防灾责任人电话
    String jczrr;//监测责任人
    String jczzrTel;//监测责任人电话
    String xcms;//巡查描述
    String xcFiles;//巡查文件

    String type="DZZH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzzh_info);

        App.getInstance().addActivity(this);
        MyApp=(App) this.getApplication();

        backtextview= (TextView) findViewById(R.id.backtextview); //返回
        backtextview.setOnClickListener(this);

        LookupforDetail=(TextView)findViewById(R.id.Lookuptextview);
        LookupforDetail.setOnClickListener(this);

        QueryRecords=(TextView)findViewById(R.id.TVQueryRecords);//查看巡查记录按钮点击事件放在这里
        QueryRecords.setOnClickListener(this);

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

        TextViewzb= (TextView) this.findViewById(R.id.editTextxzb);
        TextViewdzbh= (TextView) this.findViewById(R.id.editTextdzbh);
        TextViewxxdz= (TextView) this.findViewById(R.id.TextViewdz);

        editTextscjcsj= (EditText) this.findViewById(R.id.editTextscjcsj);
        editTextbcjcshj= (EditText) this.findViewById(R.id.editTextbcjcsj);
        editTextwyl= (EditText) this.findViewById(R.id.editTextwyl);
        editTextczwt= (EditText) this.findViewById(R.id.editTextczwt);
        editTextclyj= (EditText) this.findViewById(R.id.editTextclyj);
        editTextcljg= (EditText) this.findViewById(R.id.editTextcljg);

        editTextfzzrr= (EditText) this.findViewById(R.id.editTextfzzrr);
        editTextfzrdh= (EditText) this.findViewById(R.id.editTextfzrdh);
        editTextjczrr= (EditText) this.findViewById(R.id.editTextjczrr);
        editTextjcrdh= (EditText) this.findViewById(R.id.editTextjcrdh);
        editTextbz= (EditText) this.findViewById(R.id.editTextbz);

        spinnerjyqk=(Spinner)this.findViewById(R.id.spinnerjyqk);
        spinnerjyqkAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,jyqkArrays);
        spinnerjyqkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerjyqk.setAdapter(spinnerjyqkAdapter);

        row=(TableRow)this.findViewById(R.id.row);

        dzzHinfoDao=new DZZHinfoDao(DZZHinfoActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = this.getIntent();
        if(intent!=null){
            mDZZHEntity=(DZZHEntity)intent.getSerializableExtra("DZZH");
            if(mDZZHEntity!=null){

                Xzb=mDZZHEntity.getX();
                Yzb=mDZZHEntity.getY();

                TextViewzb.setText(mDZZHEntity.getX()+" "+mDZZHEntity.getY());
                TextViewdzbh.setText(mDZZHEntity.getDZPTBH());
                TextViewxxdz.setText(mDZZHEntity.getXQ()+" "+mDZZHEntity.getXZH()+" "+mDZZHEntity.getCUN()+" "+mDZZHEntity.getZU());

                editTextfzzrr.setText(mDZZHEntity.getFZZRNAME());
                editTextfzrdh.setText(mDZZHEntity.getFZZRTEL());
                editTextjczrr.setText(mDZZHEntity.getJCZRNAME());
                editTextjcrdh.setText(mDZZHEntity.getJCZRTEL());
                editTextbz.setText(mDZZHEntity.getBZ());

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
                            } catch (InterruptedException | ExecutionException e) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        int viewId=v.getId();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        switch(viewId) {

            case  R.id.TVQueryRecords:
                final AsyQueryRecordsByBH Task=new AsyQueryRecordsByBH();
                Task.execute(mDZZHEntity.getDZPTBH());
                break;
            //保存
            case R.id.OkBtn:

                bh=TextViewdzbh.getText().toString();
                SharedPreferences sp = DZZHinfoActivity.this.getSharedPreferences("LOGIN_INFO",  DZZHinfoActivity.this.MODE_PRIVATE);
                objId = sp.getString("PID", null);

                scjcsj=editTextscjcsj.getText().toString();
                bcjcsj=editTextbcjcshj.getText().toString();
                jyqk=spinnerjyqk.getSelectedItem().toString();
                wyl=editTextwyl.getText().toString();
                czwt=editTextczwt.getText().toString();
                clyj=editTextclyj.getText().toString();
                cljg=editTextcljg.getText().toString();

                fzzrr=editTextfzzrr.getText().toString();
                fzzrrTel=editTextfzrdh.getText().toString();
                jczrr=editTextjczrr.getText().toString();
                jczzrTel=editTextjcrdh.getText().toString();
                xcms=editTextbz.getText().toString();//巡查描述就是这里的备注
                isNetwork= NetUtils.isNetworkAvailable(this);//判断是否有网络
                if(isNetwork){
                    progressdialog=new ProgressDialog(DZZHinfoActivity.this);
                    progressdialog.setCancelable(true);
                    progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressdialog.setMessage("保存中...");
                    progressdialog.setIndeterminate(true);
                    progressdialog.show();

                    //保存辅助面
                    if(coordStr!=null) {
                        //保存辅助面
                        AddPolygon addPolygon = new AddPolygon(bh, coordStr);
                        try {
                            String datapolygon = addPolygon.execute().get(25, TimeUnit.SECONDS);
                            if (datapolygon.equals("1")) {
                                ToastUtil.show(DZZHinfoActivity.this, "信息保存中...");
                            } else {
                                ToastUtil.show(DZZHinfoActivity.this, "信息保存失败");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                            ToastUtil.show(DZZHinfoActivity.this, "连接服务器超时");
                        }
                    }

                    if(arrayPath!=null){

                        //执行方法，循环上传并保存多张相片
                        for (String photoPath:arrayPath) {
                            if(videobs==null && photobs==null){
                                xcFiles="";
                                break;
                            }
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

                        xcFiles=pictureName;

                        SharedPreferences sharedPreferences = DZZHinfoActivity.this.getSharedPreferences("LOGIN_INFO",  DZZHinfoActivity.this.MODE_PRIVATE);
                        objId = sharedPreferences.getString("PID",null);
                        UpPerson = sharedPreferences.getString("NAME",null);

                        ddr=sharedPreferences.getString("FZR","");
                        scry=sharedPreferences.getString("XCRY","");

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

                    GetDZDataSync getDZDataSync=new GetDZDataSync();
                    try {
                        String dataRslt=getDZDataSync.execute().get(25, TimeUnit.SECONDS);
                        if(dataRslt!=null){
                            if(dataRslt.equals("保存失败!")){
                                Log.i(TAG, "-----------------------DZZHinfoActivity can't be Saved-------------------------");

                                ToastUtil.show(this, dataRslt + "数据将被保存至本地数据库");

                                DZZHinfoEntity dzzHinfoEntity=new DZZHinfoEntity();
                                dzzHinfoEntity.setObjId(objId);
                                dzzHinfoEntity.setBh(bh);
                                dzzHinfoEntity.setDdr(ddr);
                                dzzHinfoEntity.setScry(scry);
                                dzzHinfoEntity.setScjcsj(scjcsj);
                                dzzHinfoEntity.setBcjcsj(bcjcsj);
                                dzzHinfoEntity.setJyqk(jyqk);
                                dzzHinfoEntity.setWyl(wyl);
                                dzzHinfoEntity.setCzwt(czwt);
                                dzzHinfoEntity.setClyj(clyj);
                                dzzHinfoEntity.setCljg(cljg);
                                dzzHinfoEntity.setFzzrr(fzzrr);
                                dzzHinfoEntity.setFzzrrTel(fzzrrTel);
                                dzzHinfoEntity.setJczrr(jczrr);
                                dzzHinfoEntity.setJczzrTel(jczzrTel);
                                dzzHinfoEntity.setXcms(xcms);
                                dzzHinfoEntity.setXcFiles(xcFiles);

                                dzzHinfoDao.add(dzzHinfoEntity);
                                //插入数据
//                                dzzhDao.add(dzzhEntity);
                                //跳转到地图页面
//                                Intent toMapIntent=new Intent();
//                                toMapIntent.setClass(this,MainMap1Activity.class);
//                                toMapIntent.putExtra("PX",x);
//                                toMapIntent.putExtra("PY",y);
//                                setResult(RESULT_OK,toMapIntent);
                                progressdialog.dismiss();
                                DZZHinfoActivity.this.finish();
                            }else if(dataRslt.equals("保存成功!")){
                                ToastUtil.show(this, dataRslt);
//                                toMapIntent.putExtra("PX",x);
//                                toMapIntent.putExtra("PY",y);
                                //public static final int RESULT_OK = -1;
                                Log.i(TAG,"-----------------------DZZHinfoActivity has been Saved-------------------------");
                                progressdialog.dismiss();
                                DZZHinfoActivity.this.finish();
                            }
                        }
                        else
                        {
                            ToastUtil.show(this, "保存失败，数据将被保存至本地数据库");

                            DZZHinfoEntity dzzHinfoEntity=new DZZHinfoEntity();
                            dzzHinfoEntity.setObjId(objId);
                            dzzHinfoEntity.setBh(bh);
                            dzzHinfoEntity.setDdr(ddr);
                            dzzHinfoEntity.setScry(scry);
                            dzzHinfoEntity.setScjcsj(scjcsj);
                            dzzHinfoEntity.setBcjcsj(bcjcsj);
                            dzzHinfoEntity.setJyqk(jyqk);
                            dzzHinfoEntity.setWyl(wyl);
                            dzzHinfoEntity.setCzwt(czwt);
                            dzzHinfoEntity.setClyj(clyj);
                            dzzHinfoEntity.setCljg(cljg);
                            dzzHinfoEntity.setFzzrr(fzzrr);
                            dzzHinfoEntity.setFzzrrTel(fzzrrTel);
                            dzzHinfoEntity.setJczrr(jczrr);
                            dzzHinfoEntity.setJczzrTel(jczzrTel);
                            dzzHinfoEntity.setXcms(xcms);
                            dzzHinfoEntity.setXcFiles(xcFiles);


                            dzzHinfoDao.add(dzzHinfoEntity);
                            progressdialog.dismiss();
                            DZZHinfoActivity.this.finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        ToastUtil.show(DZZHinfoActivity.this,"连接服务器超时");
                    }

                }else{
                    ToastUtil.show(this,"请检查网络连接,该数据点被存入本地数据库");

                    DZZHinfoEntity dzzHinfoEntity=new DZZHinfoEntity();
                    dzzHinfoEntity.setObjId(objId);
                    dzzHinfoEntity.setBh(bh);
                    dzzHinfoEntity.setDdr(ddr);
                    dzzHinfoEntity.setScry(scry);
                    dzzHinfoEntity.setScjcsj(scjcsj);
                    dzzHinfoEntity.setBcjcsj(bcjcsj);
                    dzzHinfoEntity.setJyqk(jyqk);
                    dzzHinfoEntity.setWyl(wyl);
                    dzzHinfoEntity.setCzwt(czwt);
                    dzzHinfoEntity.setClyj(clyj);
                    dzzHinfoEntity.setCljg(cljg);
                    dzzHinfoEntity.setFzzrr(fzzrr);
                    dzzHinfoEntity.setFzzrrTel(fzzrrTel);
                    dzzHinfoEntity.setJczrr(jczrr);
                    dzzHinfoEntity.setJczzrTel(jczzrTel);
                    dzzHinfoEntity.setXcms(xcms);
                    dzzHinfoEntity.setXcFiles(xcFiles);

                    dzzHinfoDao.add(dzzHinfoEntity);

                    //跳转到地图页面
                    Intent toMapIntent=new Intent();
                    toMapIntent.setClass(this,MainMap1Activity.class);
//                    toMapIntent.putExtra("PX",x);
//                    toMapIntent.putExtra("PY",y);
                    setResult(RESULT_OK,toMapIntent);
                    DZZHinfoActivity.this.finish();
                }
                break;
            case R.id.backtextview:
                DZZHinfoActivity.this.finish();
                break;
            case  R.id.BackBtn:
                DZZHinfoActivity.this.finish();
                break;
            case  R.id.Lookuptextview:
                if(mDZZHEntity!=null){
                    progressdialog=new ProgressDialog(DZZHinfoActivity.this);
                    progressdialog.setCancelable(true);
                    progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressdialog.setMessage("页面跳转中...");
                    progressdialog.setIndeterminate(true);
                    progressdialog.show();

                    String px=mDZZHEntity.getX();
                    String py=mDZZHEntity.getY();
                    Intent toMap1Intent = new Intent();
                    toMap1Intent.setClass(DZZHinfoActivity.this, DZZHDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DZZH",mDZZHEntity);
                    toMap1Intent.putExtras(bundle);
                    startActivity(toMap1Intent);
                    progressdialog.dismiss();
                }
                else
                    ToastUtil.show(DZZHinfoActivity.this,"无法传递数据");
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
                break;
            case R.id.lineBtn:
                Intent dzzhIntent0=new Intent();
                dzzhIntent0.setClass(DZZHinfoActivity.this,Map1FZ.class);
                Bundle bundle0=new Bundle();
                bundle0.putString("Flag","DZZHAddActivity");
                bundle0.putString("TX","line");
                bundle0.putString("TYPE","裂缝位置线");
                bundle0.putString("PX",Xzb);
                bundle0.putString("PY",Xzb);
                dzzhIntent0.putExtras(bundle0);
                startActivityForResult(dzzhIntent0,4);
                break;
            case R.id.yxpolygonBtn:
                Intent dzzhIntent=new Intent();
                dzzhIntent.setClass(DZZHinfoActivity.this,Map1FZ.class);
                Bundle bundle=new Bundle();
                bundle.putString("Flag","DZZHAddActivity");
                bundle.putString("TX","plon");
                bundle.putString("TYPE","影响范围");
                bundle.putString("PX",Xzb);
                bundle.putString("PY",Yzb);
                dzzhIntent.putExtras(bundle);
                startActivityForResult(dzzhIntent,4);
                break;
            case R.id.bxpolygonBtn:
                Intent dzzhIntent2=new Intent();
                dzzhIntent2.setClass(DZZHinfoActivity.this,Map1FZ.class);
                Bundle bundle2=new Bundle();
                bundle2.putString("Flag","DZZHAddActivity");
                bundle2.putString("TX","plon");
                bundle2.putString("TYPE","避险区域");
                bundle2.putString("PX",Xzb);
                bundle2.putString("PY",Yzb);
                dzzhIntent2.putExtras(bundle2);
                startActivityForResult(dzzhIntent2,4);

                break;
            default:
                break;
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
            ksoap=new KsoapValidateHttp(DZZHinfoActivity.this);
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
            ksoap=new KsoapValidateHttp(DZZHinfoActivity.this);
            photobs2=Bitmap2Bytes(BitmapFactory.decodeFile(photoPath));

            this.photoName=photoName;

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
                    photobsBase64= org.java_websocket.util.Base64.encodeBytes(photobs2);
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
    //保存辅助面
    public  class AddPolygon extends AsyncTask<String, Integer, String>{
        String dzptbh;
        String coordstr;

        public AddPolygon(String dzptbh,String coordstr) {
            ksoap=new KsoapValidateHttp(DZZHinfoActivity.this );
            this.coordstr=coordstr;
            this.dzptbh=dzptbh;
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
    //保存属性
    public class GetDZDataSync extends AsyncTask<String, Integer, String> {

        public GetDZDataSync() {
            ksoap=new KsoapValidateHttp(DZZHinfoActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebAddBJS_DZZH_PT2(objId,bh,ddr,scry,fzzrr,fzzrrTel,jczrr,jczzrTel,xcms,xcFiles,scjcsj,
                bcjcsj,jyqk,wyl,czwt,clyj,cljg);
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
            ksoap=new KsoapValidateHttp(DZZHinfoActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                objId=bh;
                String AddRslt=ksoap.SysPicAdd(pictureName, UpPerson, objId, remark);
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

    class AsyQueryRecordsByBH extends AsyncTask<String,Integer,String>
    {

        public AsyQueryRecordsByBH() {
            super();
            ksoap=new KsoapValidateHttp(DZZHinfoActivity.this);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressdialog=new ProgressDialog(DZZHinfoActivity.this);
            progressdialog.setCancelable(false);
            progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressdialog.setMessage("正在查询...");
            progressdialog.setIndeterminate(true);
            progressdialog.show();
        }

        @Override
        protected void onPostExecute(String mRecords) {
            super.onPostExecute(mRecords);
            progressdialog.dismiss();

            if (mRecords!=null) {
                Intent intent=new Intent();
                intent.setClass(DZZHinfoActivity.this, DZZHNextRecordActivity.class);
                Bundle recorbundle = new Bundle();
                recorbundle.putSerializable("DZZH", mDZZHEntity);
                recorbundle.putString("RECORDS", mRecords);
                intent.putExtras(recorbundle);
                startActivity(intent);
            }
            else
                ToastUtil.show(DZZHinfoActivity.this,"该灾害点目前没有巡查数据");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String mRecords;//查询到的记录该地质灾害编号下的所有巡查记录的JSON字符串
                mRecords=ksoap.WebDownloadXCRecords(mDZZHEntity.getDZPTBH());
                return mRecords;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

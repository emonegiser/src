package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.dao.KCZYDao;
import com.arcgis.entity.KCZYEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;

import org.java_websocket.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KCAddActivity extends Activity implements View.OnClickListener{

    TextView editTextzb= null;
    EditText editTextkqbh= null;
    EditText editTextname=null;
static     EditText editTextclsj= null;
    //矿产储量
    EditText editTextkccl= null;
    EditText kcdm= null;
    EditText editTextkcmj= null;
    EditText editTextkcfzr= null;
    EditText editTextlxdh= null;
    EditText editTextbz=null;

    Spinner spinnerkclx= null;
    Spinner spinnerclzt= null;
    EditText spinnerssc= null;
    Spinner spinnerhfx=null;
    EditText spinnerssxz=null;

    private TextView backtextview;
    private TextView titletextview;

    private Button OkBtn=null;
    private Button BackBtn=null;

    private String id = "";
    private String type = "KC";

    //调用webservice
    private KsoapValidateHttp ksoap;

    ArrayAdapter<String> spinnerkclxAdapter;
    ArrayAdapter<String> spinnerclztAdapter;
    ArrayAdapter<String> spinnerhfxAdapter;

    //全局变量存储位置
    private App MyApp;

    String mineid=null;
    String minename=null;
    String minetype=null;
    String tel=null;
    String bexz=null;
    String becun=null;
    String minevol=null;
    String minearea=null;
    String mineclzt=null;
    String inlaw=null;
    String handletime=null;
    String handleremarks=null;
    String XY=null;
    String address=null;
    String man=null;

    SimpleDateFormat format=null;
    private boolean isNetwork=false;
    private KCZYDao kczyDao=null;

    //存储照片视频文件
    private File savePhototFile=null;
    private File saveVideoFile=null;
    //视频照片路径
    TextView TextViewsp=null;
    TextView TextViewzp=null;

    //上传数据流
    byte[] photobs = null;
    byte[] videobs = null;
    String pictureName="";//全部相片名字符串
    String photoName = null;//单张相片名
    String videoName = null;

    //上传人及地灾编号、备注
    String objId=null ;
    String UpPerson=null;
    String remark="";

    Button zpBtn=null;
    Button spBtn=null;

    ProgressDialog progressdialog;

    private TableRow row=null;
    String filepath;
    ArrayList<String> arrayName=new ArrayList<String>();
    ArrayList<String> arrayPath=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addkc);
        App.getInstance().addActivity(this);
        BackBtn= (Button) findViewById(R.id.BackBtn);
        BackBtn.setOnClickListener(this);

        OkBtn= (Button) findViewById(R.id.OkBtn);
        OkBtn.setOnClickListener(this);

        TextViewsp= (TextView) findViewById(R.id.TextViewsp);

        backtextview= (TextView) findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);
        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("添加矿产资源");

        zpBtn= (Button) findViewById(R.id.zpBtn);
        zpBtn.setOnClickListener(this);

        spBtn= (Button) findViewById(R.id.spBtn);
        spBtn.setOnClickListener(this);

        //矿区坐标
        editTextzb= (TextView) this.findViewById(R.id.editTextzb);
        editTextzb.setOnClickListener(this);
        //矿区编号
        editTextkqbh= (EditText) this.findViewById(R.id.editTextkqbh);
        //处理时间
        editTextclsj= (EditText) this.findViewById(R.id.editTextclsj);
        editTextclsj.setOnClickListener(this);
        //矿产名称
        editTextname= (EditText) this.findViewById(R.id.editTextname);
        //矿产储量
        editTextkccl= (EditText) this.findViewById(R.id.editTextkccl);
        //地名
        kcdm= (EditText) this.findViewById(R.id.kcdm);
        //矿产面积
        editTextkcmj= (EditText) this.findViewById(R.id.editTextkcmj);
        //负责人
        editTextkcfzr= (EditText) this.findViewById(R.id.editTextkcfzr);
        //联系电话
        editTextlxdh= (EditText) this.findViewById(R.id.editTextlxdh);
        //备注
        editTextbz= (EditText) this.findViewById(R.id.editTextbz);

        //矿产类型
        spinnerkclx= (Spinner) this.findViewById(R.id.spinnerkclx);
        //处理状态
        spinnerclzt= (Spinner) this.findViewById(R.id.spinnerclzt);
        //所属村
        spinnerssc= (EditText) this.findViewById(R.id.spinnerssc);
        //所属乡镇
        spinnerssxz=(EditText)this.findViewById(R.id.spinnerssxz);
        //合法性
        spinnerhfx= (Spinner) this.findViewById(R.id.spinnerhfx);

        row = (TableRow) findViewById(R.id.row);//放图片的表格

        MyApp=(App) this.getApplication();

        //矿产类型
        spinnerkclxAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, MyApp.getKclx_list());
        spinnerkclxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerkclx.setAdapter(spinnerkclxAdapter);

        //处理状态
        spinnerclztAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,MyApp.getClzt_list());
        spinnerclztAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerclzt.setAdapter(spinnerclztAdapter);

        //合法性
        List<String> hfx_list=new ArrayList<>();
        hfx_list.add("合法");
        hfx_list.add("非法");
        spinnerhfxAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, hfx_list);
        spinnerhfxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerhfx.setAdapter(spinnerhfxAdapter);

        kczyDao=new KCZYDao(KCAddActivity.this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            KCAddActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        kczyDao.close();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        switch(viewId){
            //保存
            case R.id.OkBtn:
                isNetwork = NetUtils.isNetworkAvailable(this);

                mineid=editTextkqbh.getText().toString();
                minename=editTextname.getText().toString();
                minetype=spinnerkclx.getSelectedItem().toString();
                tel=editTextlxdh.getText().toString();
                bexz=spinnerssxz.getText().toString();
                becun=spinnerssc.getText().toString();
                address=kcdm.getText().toString();
                minevol=editTextkccl.getText().toString();
                minearea=editTextkcmj.getText().toString();
                mineclzt=spinnerclzt.getSelectedItem().toString();
                inlaw=spinnerhfx.getSelectedItem().toString();
                handletime=editTextclsj.getText().toString();
                handleremarks=editTextbz.getText().toString();
                XY=editTextzb.getText().toString();
                man=editTextkcfzr.getText().toString();

                if(minename == null || minename.isEmpty()){
                    ToastUtil.show(this,"请输入矿产名称!");
                    return;
                }

                if(isNetwork){

                    progressdialog=new ProgressDialog(this);
                    progressdialog.setCancelable(true);
                    progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressdialog.setMessage("保存中...");
                    progressdialog.setIndeterminate(true);
                    progressdialog.show();

                    //网络正常
                    AddKCDataSync addDZDataSync=new AddKCDataSync();
                    try {
                        String dataRslt=addDZDataSync.execute().get(5000, TimeUnit.MILLISECONDS);
                        if(dataRslt!=null){
                            if(dataRslt.equals("1")){
                                dataRslt="保存成功!";
                                ToastUtil.show(this,dataRslt);
                                Intent toMapIntent=new Intent();
                                toMapIntent.setClass(this,MainMap2Activity.class);
                                setResult(RESULT_OK,toMapIntent);
                                KCAddActivity.this.finish();
                            }else if(dataRslt.equals("0")){
                                dataRslt="保存失败!";
                                ToastUtil.show(this,dataRslt+"数据将被保存至本地");
                                //保存
                                KCZYEntity kczyEntity=new KCZYEntity();
                                kczyEntity.setKcNo(mineid);
                                kczyEntity.setKcName(minename);
                                kczyEntity.setKcType(minetype);
                                kczyEntity.setPhone(tel);
                                kczyEntity.setSsxz(bexz);
                                kczyEntity.setSscun(becun);
                                kczyEntity.setXxwz(address);
                                kczyEntity.setKccl(minevol);
                                kczyEntity.setKcmj(minearea);
                                kczyEntity.setClzt(mineclzt);
                                kczyEntity.setSFHF(inlaw);
                                kczyEntity.setAddtime(handletime);
                                kczyEntity.setBz(handleremarks);
                                kczyEntity.setCoords(XY);
                                kczyEntity.setJianceren(man);
                                kczyDao.add(kczyEntity);

                                Intent toMapIntent=new Intent();
                                toMapIntent.setClass(this,MainMap2Activity.class);
                                setResult(RESULT_OK,toMapIntent);
                                KCAddActivity.this.finish();

                            }

                        }else{
                            ToastUtil.show(this,"服务器返回值为空");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        ToastUtil.show(KCAddActivity.this,"连接服务器超时");
                    }

                    if(videobs==null && photobs==null){
                        return;
                    }

                    if(arrayPath!=null){
                        //执行方法，循环上传并保存多张相片
                        for (String photoPath:arrayPath) {
                            String photoName = photoPath.substring(photoPath.lastIndexOf("/")+1,photoPath.length());
                            AddKCVediosPicturesSync addKCVediosPicturesSync = new AddKCVediosPicturesSync(id,type , photoName, photoPath);//上传相片
                            try {
                                String rslt = addKCVediosPicturesSync.execute().get(2000, TimeUnit.SECONDS);
                                if (rslt != null) {
                                    if (rslt.equals(photoName)) {
                                        pictureName+=photoName+";";
                                    }else
                                    {
                                        ToastUtil.show(this, "图片上传失败");
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


                        SharedPreferences sp = this.getSharedPreferences("LOGIN_INFO",  this.MODE_PRIVATE);
                        objId = sp.getString("PID",null);
                        UpPerson = sp.getString("NAME",null);

                        if (pictureName!=null&&!pictureName.equals(""))
                        {
                            AddKCPicture addDZpicture=new AddKCPicture();//保存相片
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
                    ToastUtil.show(KCAddActivity.this,"网络异常，该数据被保存在本地数据库");
                    //保存
                    KCZYEntity kczyEntity=new KCZYEntity();
                    kczyEntity.setKcNo(mineid);
                    kczyEntity.setKcName(minename);
                    kczyEntity.setKcType(minetype);
                    kczyEntity.setPhone(tel);
                    kczyEntity.setSsxz(bexz);
                    kczyEntity.setSscun(becun);
                    kczyEntity.setXxwz(address);
                    kczyEntity.setKccl(minevol);
                    kczyEntity.setKcmj(minearea);
                    kczyEntity.setClzt(mineclzt);
                    kczyEntity.setSFHF(inlaw);
                    kczyEntity.setAddtime(handletime);
                    kczyEntity.setBz(handleremarks);
                    kczyEntity.setCoords(XY);
                    kczyEntity.setJianceren(man);
                    kczyDao.add(kczyEntity);

                    Intent toMapIntent=new Intent();
                    toMapIntent.setClass(this,MainMap2Activity.class);
                    setResult(RESULT_OK,toMapIntent);
                    KCAddActivity.this.finish();
                }

                break;
            case R.id.BackBtn:
                KCAddActivity.this.finish();
                break;
            case R.id.editTextclsj:
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(), "datePicker");
                break;
            case R.id.backtextview:
                KCAddActivity.this.finish();
                break;
            case R.id.editTextzb:
                String coords=editTextzb.getText().toString();
                LayoutInflater factory = LayoutInflater.from(KCAddActivity.this);
                View view = factory.inflate(R.layout.activity_zbdetail, null);
                TextView editTextzbdetail = (TextView) view.findViewById(R.id.editTextzbdetail);
                editTextzbdetail.setText(coords);
                AlertDialog.Builder builder = new AlertDialog.Builder(KCAddActivity.this);
                builder.setTitle("坐标详情");
                builder.setView(view);
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;

            case R.id.zpBtn:
                String photoPath= Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp/images";
                File photoDir = new File(photoPath);
                if (!photoDir.exists()) {
                    photoDir.mkdirs();
                }

                savePhototFile = new File(photoPath, "KC"+formatter.format(date) + ".jpg");
                photoName="KC"+formatter.format(date) + ".jpg";

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

                saveVideoFile = new File(videoPath,"KC"+formatter.format(date) + ".avi");
                videoName="KC"+formatter.format(date) + ".avi";

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
                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(saveVideoFile));
                startActivityForResult(videoIntent, 2);
            default:
                break;
        }
    }

    public class AddKCDataSync extends AsyncTask<String, Integer, String> {

        public AddKCDataSync() {
            ksoap=new KsoapValidateHttp(KCAddActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebAddMinieral(mineid,minename, minetype,man,tel,bexz,becun,
                        minevol,minearea,mineclzt,inlaw,handletime,handleremarks,XY,address);
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
    }

    public class AddKCVediosPicturesSync extends AsyncTask<String, Integer, String> {

        String ckbh;
        byte[] vediobyte;
        String vedioname;
        byte[] picturebyte;
        String photoName;
        String photoPath;
        String type;
        String videobsBase64;
        String photobsBase64;


        public AddKCVediosPicturesSync(String ckbh,String type,String photoName,String photoPath) {
            ksoap=new KsoapValidateHttp(KCAddActivity.this);
            this.ckbh=ckbh;
            this.type=type;
            this.photoPath=photoPath;
            this.photoName=photoName;
            picturebyte=Bitmap2Bytes(BitmapFactory.decodeFile(photoPath));

        }

        @Override
        protected String doInBackground(String... str) {
            try {

                if(vediobyte!=null){
                    videobsBase64= Base64.encodeBytes(vediobyte);
                }

                if(picturebyte!=null){
                    photobsBase64= Base64.encodeBytes(picturebyte);
                }

                if(videobsBase64==null){
                    videobsBase64="";
                }
                if(photobsBase64==null){
                    photobsBase64="";
                }
                String AddRslt=ksoap.DZWebUploadVediosPictures(ckbh,type,photoName,photobsBase64);
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
    }



    @Override
    protected void onResume() {
        super.onResume();
        String coordsStr=null;

        Intent fromMapIntent=this.getIntent();
        if(fromMapIntent!=null){
            coordsStr= fromMapIntent.getStringExtra("COORDSTR");
            editTextzb.setText(coordsStr);
        }

        //处理时间
        format = new SimpleDateFormat("yyyy-MM-dd");
        String dataStr = format.format(new Date());
        editTextclsj.setText(dataStr);

        //产生地灾编号
        Random r=new Random();
        String random=System.currentTimeMillis()+"";
        String dzbh="KC"+dataStr.replaceAll("-","")+this.loadPhoneStatus().substring(2,5)+random.substring(8)+r.nextInt(10)+"";
        editTextkqbh.setText(dzbh);

        isNetwork = NetUtils.isNetworkAvailable(this);
        //调用接口获取面积信息
        if(isNetwork){
            GetAreaDataSync getAreaDataSync=new GetAreaDataSync(coordsStr);
            try {
                String rsltStr=getAreaDataSync.execute().get(5000,TimeUnit.MILLISECONDS);
                if(rsltStr!=null && !rsltStr.isEmpty()){
                    JSONArray jsonArray = new JSONArray(rsltStr);
                    for(int k=0;k<jsonArray.length();k++){
                        JSONObject o = (JSONObject) jsonArray.get(k);
                        if(o.has("CUN") && o.get("CUN")!=null){
                            spinnerssxz.setText(o.get("CUN").toString());
                        }
                        if(o.has("ZHEN") && o.get("ZHEN")!=null){
                            spinnerssc.setText(o.get("ZHEN").toString());
                        }
                        if(o.has("MIANJI") && o.get("MIANJI")!=null){
                            editTextkcmj.setText(o.get("MIANJI").toString());
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                ToastUtil.show(this,"连接服务器超时");
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.show(this,"解析JSON错误");
            }
        }else{
            ToastUtil.show(this,"无法获取服务器数据，请检查网络");
        }
    }

    public class GetAreaDataSync extends AsyncTask<String, Integer, String> {

        private String coords;
        public GetAreaDataSync(String coords) {
            this.coords=coords;
            ksoap=new KsoapValidateHttp(KCAddActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetAreaAddress(coords);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 日期选择器
     */
    public static  class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
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

            editTextclsj.setText(year+"-"+m+"-"+d);
        }
    }

    private String loadPhoneStatus(){
        TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceNo=phoneMgr.getDeviceId();
        Log.i("deviceNo", deviceNo);
        return deviceNo;
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

    public class AddKCPicture extends AsyncTask<String, Integer, String> {

        public AddKCPicture() {
            ksoap=new KsoapValidateHttp(KCAddActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                objId=mineid;
                String AddRslt=ksoap.KCPicAdd(pictureName,UpPerson,objId,remark);
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
}
package com.arcgis.activity;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.dao.DZZHDao;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;

import org.java_websocket.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class YJZHPhotoActivity extends Activity implements View.OnClickListener{

    //调用webservice
    private KsoapValidateHttp ksoap;
    private TableRow row;
    private DZZHDao dzzhDao=null;
    private boolean isNetwork=false;
    private Button OkBtn=null;
    private Button zpBtn=null;
    private TextView BackBtn=null;

    //上传数据流
    byte[] photobs = null;
    byte[] photobs2 = null;
    byte[] videobs = null;
    private String id = "";
    private String type = "YJZH";

    //视频照片路径
    TextView TextViewsp=null;
    String filepath;
    String pictureName="";//全部相片名字符串
    String photoName = null;//单张相片名
    String videoName = null;
    //上传人及地灾编号、备注
    String objId=null ;
    String UpPerson=null;
    String remark="";
    //存储照片视频文件
    private File savePhototFile=null;
    private File saveVideoFile=null;

    ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photoyjzh);
        App.getInstance().addActivity(this);

        SharedPreferences sp = YJZHPhotoActivity.this.getSharedPreferences("LOGIN_INFO",  YJZHPhotoActivity.this.MODE_PRIVATE);
        objId = sp.getString("PID",null);
        UpPerson = sp.getString("NAME",null);

        row = (TableRow) findViewById(R.id.row);//放图片的表格
        zpBtn= (Button) findViewById(R.id.zpBtn);//拍照
        zpBtn.setOnClickListener(this);
        OkBtn= (Button) findViewById(R.id.OkBtn);//保存
        OkBtn.setOnClickListener(this);
        BackBtn= (TextView) findViewById(R.id.backtextview);//取消
        BackBtn.setOnClickListener(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //dzzhDao.close();
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
                if(isNetwork){
//                    progressdialog=new ProgressDialog(YJZHPhotoActivity.getInstance());
//                    progressdialog.setCancelable(true);
//                    progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                    progressdialog.setMessage("保存中...");
//                    progressdialog.setIndeterminate(true);
//                    progressdialog.show();


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



                        if (pictureName!=null&&!pictureName.equals(""))
                        {
                            AddDZPicture addDZpicture=new AddDZPicture();//保存相片
                            try {
                                String AddRslt = addDZpicture.execute().get(2000,TimeUnit.SECONDS);
                                if (AddRslt!=null){
                                    ToastUtil.show(this, AddRslt);
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

                    }else{
                        ToastUtil.show(this, "本地图片路径未正常存储");}
                }else{
                    ToastUtil.show(this, "请检查网络连接,该数据点被存入本地数据库");
                }
                break;
            case R.id.BackBtn:
                YJZHPhotoActivity.this.finish();
                break;
            case R.id.zpBtn:
                String photoPath= Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp/images";
                File photoDir = new File(photoPath);
                if (!photoDir.exists()) {
                    photoDir.mkdirs();
                }
                savePhototFile = new File(photoPath, "YJZH"+formatter.format(date) + ".jpg");
                photoName="YJZH"+formatter.format(date) + ".jpg";

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

                saveVideoFile = new File(videoPath,"YJZH"+formatter.format(date) + ".avi");
                videoName="YJZH"+formatter.format(date) + ".avi";

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
            ksoap=new KsoapValidateHttp(YJZHPhotoActivity.this);
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



    public class AddDZPicture extends AsyncTask<String, Integer, String> {

        public AddDZPicture() {
            ksoap=new KsoapValidateHttp(YJZHPhotoActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {

                String AddRslt=ksoap.SysPicAddYJ(objId, UpPerson, pictureName);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            YJZHPhotoActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onResume() {
        super.onResume();
        String XZ=null;
//        Intent fromMapIntent=this.getIntent();
//        if(fromMapIntent!=null){
//            x = fromMapIntent.getDoubleExtra("PX", 0.0f)+"";
//            y = fromMapIntent.getDoubleExtra("PY", 0.0f)+"";
//            Point Point2359 = (Point) GeometryEngine.project(new Point(fromMapIntent.getDoubleExtra("PX", 0.0f),
//                            fromMapIntent.getDoubleExtra("PY", 0.0f)),
//                    SpatialReference.create(4326),
//                    SpatialReference.create(2359));
//            XZ=fromMapIntent.getStringExtra("XZ");
//            textViewxzb.setText(df.format(Point2359.getX()));
//            textViewyzb.setText(df.format(Point2359.getY()));
//            //定位所在村社
//            GetXZDataSyCS getXZDataCS=new GetXZDataSyCS(x+"",y+"");
//            try {
//                String dwRslt=getXZDataCS.execute().get(200, TimeUnit.SECONDS);
//                if(dwRslt!=null && dwRslt.length()>0){
//                    editTextsscs.setText(dwRslt); editTextsscs.setText(dwRslt);
//                }else{
//                    ToastUtil.show(this,"未成功获取所在村社,请手动输入");
//                }
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            } catch (ExecutionException e1) {
//                e1.printStackTrace();
//            } catch (TimeoutException e1) {
//                e1.printStackTrace();
//            }
//            if(XZ!=null){
//                editTextssxz.setText(XZ);
//                editTextdzmc.setText(XZ);
//            }else{
//                ToastUtil.show(this,"未成功获取所在乡镇,请手动输入");
//            }
//        }
//
//        format = new SimpleDateFormat("yyyy");
//        String dataStr = format.format(new Date());
//        editTextcfsj.setText(dataStr);
//        editTextdjsj.setText(dataStr);
//        bingdzbh();//绑定地灾编号
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


    private String loadPhoneStatus(){
        TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceNo=phoneMgr.getDeviceId();
        Log.i("deviceNo", deviceNo);
        return deviceNo;
    }
}
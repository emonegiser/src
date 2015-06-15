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
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.dao.SBYDDao;
import com.arcgis.entity.SBYDEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


//原来是添加上报用地
//更名为建设用地管理
public class SBYDAddActivity extends Activity implements View.OnClickListener{

    //地块坐标
    TextView editTextzb= null;
    //地块编号
    EditText editTextkqbh= null;
    //地块面积
    EditText editTextmj=null;
    //所属村
    EditText spinnerssc= null;
    //所属乡镇
    EditText spinnerssxz=null;
    //报送时间
    static EditText editTextbssj= null;
    //报批批次
    Spinner spinnerbppc=null;
    //征地补偿费用
    EditText editTextzdbcfy= null;
    //地上附着物补偿费用
    EditText editTextfzwbcfy=null;

    private Button BackBtn=null;
    private Button OkBtn=null;

    private TextView backtextview;
    private TextView titletextview;

    //调用webservice
    private KsoapValidateHttp ksoap;

    //报批批次
    ArrayAdapter<String> spinnerbppcAdapter;

    //全局变量存储位置
    private App MyApp;

    //

    String XY=null;
    String bh=null;
    String mj=null;
    String xz=null;
    String cun=null;
    String tdzsmc=null;
    String zdbcfy=null;
    String dsfzwbcf=null;
    String bssj=null;

    SimpleDateFormat format=null;
    boolean isNetwork=false;
    ProgressDialog progressdialogYANZHEN;
    AlertDialog.Builder AddAlertDialog;
    ProgressDialog progressdialogSave=null;
    SBYDDao sbydDao=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addsbyd);
        App.getInstance().addActivity(this);

        BackBtn= (Button) findViewById(R.id.BackBtn);
        BackBtn.setOnClickListener(this);

        OkBtn= (Button) findViewById(R.id.OkBtn);
        OkBtn.setOnClickListener(this);

        backtextview= (TextView) findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);
        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("添加上报用地");

        sbydDao=new SBYDDao(this);

        //地块坐标
        editTextzb= (TextView) this.findViewById(R.id.editTextzb);
        editTextzb.setOnClickListener(this);
        //矿区编号
        editTextkqbh= (EditText) this.findViewById(R.id.editTextkqbh);
        //所属村
        spinnerssc= (EditText) this.findViewById(R.id.spinnerssc);
        //所属乡镇
        spinnerssxz=(EditText)this.findViewById(R.id.spinnerssxz);
        //地块面积
        editTextmj= (EditText) findViewById(R.id.editTextmj);
        //报送时间
        editTextbssj= (EditText) findViewById(R.id.editTextbssj);
        editTextbssj.setOnClickListener(this);

        spinnerbppc= (Spinner) findViewById(R.id.spinnerbppc);

        //附着物补偿费用
        editTextfzwbcfy= (EditText) findViewById(R.id.editTextfzwbcfy);
        //征地补偿费用
        editTextzdbcfy= (EditText) findViewById(R.id.editTextzdbcfy);

        MyApp=(App) this.getApplication();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            SBYDAddActivity.this.finish();
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
            //分析占压
            case R.id.OkBtn:
                isNetwork= NetUtils.isNetworkAvailable(this);

                XY=editTextzb.getText().toString();
                bh=editTextkqbh.getText().toString();
                mj=editTextmj.getText().toString();
                xz=spinnerssxz.getText().toString();
                cun=spinnerssc.getText().toString();
                tdzsmc=spinnerbppc.getSelectedItem().toString();
                zdbcfy=editTextzdbcfy.getText().toString();
                dsfzwbcf=editTextfzwbcfy.getText().toString();
                bssj=editTextbssj.getText().toString();

                if(bh ==null || bh.isEmpty()){
                    ToastUtil.show(this,"地块编号不能为空!");
                    return;
                }

                AddAlertDialog = new AlertDialog.Builder(SBYDAddActivity.this);


                progressdialogYANZHEN=new ProgressDialog(AddAlertDialog.getContext());
                progressdialogYANZHEN.setCancelable(true);
                progressdialogYANZHEN.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressdialogYANZHEN.setMessage("获取占压分析结果...");
                progressdialogYANZHEN.setIndeterminate(true);
                progressdialogYANZHEN.show();

                //验证是否符合添加条件
                if(isNetwork){
                    IsSBYDDataSync isSBYDDataSync=new IsSBYDDataSync(XY,bh,mj,xz,cun);
                    try {
                        String dataRslt=isSBYDDataSync.execute().get(300, TimeUnit.SECONDS);
                        if(dataRslt!=null && !dataRslt.isEmpty()){
                            LayoutInflater factory = LayoutInflater.from(SBYDAddActivity.this);
                            View view = factory.inflate(R.layout.activity_zbdetail, null);
                            TextView editTextzbdetail = (TextView) view.findViewById(R.id.editTextzbdetail);
                            editTextzbdetail.setText(dataRslt);
                            AddAlertDialog.setTitle("土地占压分析结果");
                            AddAlertDialog.setView(view);
                            AddAlertDialog.setCancelable(true);
                            AddAlertDialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressdialogSave=new ProgressDialog(AddAlertDialog.getContext());
                                    progressdialogSave.setCancelable(true);
                                    progressdialogSave.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progressdialogSave.setMessage("保存上报用地...");
                                    progressdialogSave.setIndeterminate(true);
                                    progressdialogSave.show();

                                    isNetwork= NetUtils.isNetworkAvailable(SBYDAddActivity.this);

                                    if(isNetwork){
                                        //有网络
                                        AddSBYDDataSync addSBYDDataSync=new AddSBYDDataSync(XY,bh,mj,xz,cun,
                                                tdzsmc,zdbcfy,dsfzwbcf,bssj);
                                        try {
                                            String addRslt=addSBYDDataSync.execute().get(300,TimeUnit.SECONDS);
                                            if(addRslt!=null){
                                                if(addRslt.equals("1")){
                                                    addRslt="保存成功!";
                                                    ToastUtil.show(SBYDAddActivity.this,addRslt);
                                                    Intent toMapIntent=new Intent();
                                                    toMapIntent.setClass(SBYDAddActivity.this,MainMap3Activity.class);
                                                    setResult(RESULT_OK,toMapIntent);
                                                    SBYDAddActivity.this.finish();
                                                }else if(addRslt.equals("0")){
                                                    addRslt="保存失败!";
                                                    ToastUtil.show(SBYDAddActivity.this,addRslt+"数据将被保存至本地");

                                                    SBYDEntity sbydEntity=new SBYDEntity();

                                                    sbydEntity.setZDBCFY(zdbcfy);
                                                    sbydEntity.setDKMJ(mj);
                                                    sbydEntity.setCoords(XY);
                                                    sbydEntity.setBH(bh);
                                                    sbydEntity.setXZ(xz);
                                                    sbydEntity.setCUN(cun);
                                                    sbydEntity.setDSFZWBCF(dsfzwbcf);
                                                    sbydEntity.setBSSJ(bssj);
                                                    sbydEntity.setTDZSMC(tdzsmc);
                                                    sbydDao.add(sbydEntity);
                                                }
                                            }else{
                                                ToastUtil.show(SBYDAddActivity.this,"服务器无返回值");
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        } catch (TimeoutException e) {
                                            e.printStackTrace();
                                            ToastUtil.show(SBYDAddActivity.this,"连接服务器超时");
                                        }
                                        dialog.dismiss();
                                    }else{
                                        ToastUtil.show(SBYDAddActivity.this,"当前无网络，该数据被保存至本地数据库");

                                        SBYDEntity sbydEntity=new SBYDEntity();

                                        sbydEntity.setZDBCFY(zdbcfy);
                                        sbydEntity.setDKMJ(mj);
                                        sbydEntity.setCoords(XY);
                                        sbydEntity.setBH(bh);
                                        sbydEntity.setXZ(xz);
                                        sbydEntity.setCUN(cun);
                                        sbydEntity.setDSFZWBCF(dsfzwbcf);
                                        sbydEntity.setBSSJ(bssj);
                                        sbydEntity.setTDZSMC(tdzsmc);
                                        sbydDao.add(sbydEntity);
                                    }
                                }
                            });
                            AddAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AddAlertDialog.show();

                        }else{
                            ToastUtil.show(this,"服务器无返回值");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        ToastUtil.show(SBYDAddActivity.this,"线程中断异常");
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        ToastUtil.show(SBYDAddActivity.this,"线程执行异常");
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        ToastUtil.show(SBYDAddActivity.this,"连接服务器超时");
                    }
                }else{
                    ToastUtil.show(this,"当前无网络，无法获取分析结果，请稍后再试");
                }

                break;
            case R.id.BackBtn:
                SBYDAddActivity.this.finish();
                break;
            case R.id.editTextbssj:
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(), "datePicker");
                break;
            case R.id.backtextview:
                SBYDAddActivity.this.finish();
                break;
            case R.id.editTextzb:
                String coords=editTextzb.getText().toString();
                LayoutInflater factory = LayoutInflater.from(SBYDAddActivity.this);
                View view = factory.inflate(R.layout.activity_zbdetail, null);
                TextView editTextzbdetail = (TextView) view.findViewById(R.id.editTextzbdetail);
                editTextzbdetail.setText(coords);
                AlertDialog.Builder builder = new AlertDialog.Builder(SBYDAddActivity.this);
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
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String coordsStr=null;

        //从MainMap3Activity中获取信息
        Intent fromMapIntent=this.getIntent();
        if(fromMapIntent!=null){
            editTextzb.setText("");
            coordsStr= fromMapIntent.getStringExtra("COORDSTR");
            editTextzb.setText(coordsStr);
        }

        //处理时间
        format = new SimpleDateFormat("yyyy-MM-dd");
        String dataStr = format.format(new Date());
        editTextbssj.setText(dataStr);

        isNetwork= NetUtils.isNetworkAvailable(SBYDAddActivity.this);
        //调用接口获取村信息
        if(isNetwork){
            GetAreaDataSync getAreaDataSync=new GetAreaDataSync(coordsStr);
            try {
                String rsltStr=getAreaDataSync.execute().get(300,TimeUnit.SECONDS);
                if(rsltStr!=null && !rsltStr.isEmpty()){
                    JSONArray jsonArray = new JSONArray(rsltStr);
                    //[{"MIANJI":"2258420.02005503","CUN":"大兰村","BH":"9353819","ZHEN":"流仓桥办事处"}]
                    for(int k=0;k<jsonArray.length();k++){
                        JSONObject o = (JSONObject) jsonArray.get(k);
                        if(o.has("MIANJI") && o.get("MIANJI")!=null){
                            editTextmj.setText(o.get("MIANJI").toString());
                        }
                        if(o.has("CUN") && o.get("CUN")!=null){
                            spinnerssc.setText(o.get("CUN").toString());
                        }
                        if(o.has("BH") && o.get("BH")!=null){
                            editTextkqbh.setText(o.get("BH").toString());
                        }
                        if(o.has("ZHEN") && o.get("ZHEN")!=null){
                            spinnerssxz.setText(o.get("ZHEN").toString());
                        }
                    }
                }else{
                    ToastUtil.show(SBYDAddActivity.this,"服务器无返回值");
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
            ToastUtil.show(this,"当前无网络无法获取上报用地位置信息");
        }


        spinnerbppcAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, MyApp.getSBYD_BPPC_list());
        spinnerbppcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbppc.setAdapter(spinnerbppcAdapter);
    }

    public class GetAreaDataSync extends AsyncTask<String, Integer, String> {

        private String coords;
        public GetAreaDataSync(String coords) {
            this.coords=coords;
            ksoap=new KsoapValidateHttp(SBYDAddActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebGetBHIAreaZhenCun(coords);
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

    public class IsSBYDDataSync extends AsyncTask<String, Integer, String> {

        String XY=null;
        String bh=null;
        String mj=null;
        String xz=null;
        String cun=null;

        public IsSBYDDataSync(String XY, String bh, String mj, String xz, String cun) {
            this.XY = XY;
            this.bh = bh;
            this.mj = mj;
            this.xz = xz;
            this.cun = cun;
            ksoap=new KsoapValidateHttp(SBYDAddActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebLandDecisionAnalysis(XY, bh, mj, xz, cun, tdzsmc,zdbcfy,dsfzwbcf,bssj);
                if(AddRslt!=null){
                    Log.i("WebLandDecisionAnalysis",AddRslt.toString());
                    return AddRslt;

                }else{
                    Log.i("WebLandDecisionAnalysis","未返回任何值");
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressdialogYANZHEN.dismiss();
            super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


    public class AddSBYDDataSync extends AsyncTask<String, Integer, String> {

        String XY=null;
        String bh=null;
        String mj=null;
        String xz=null;
        String cun=null;
        String tdzsmc=null;
        String zdbcfy=null;
        String dsfzwbcf=null;
        String bssj=null;


        public AddSBYDDataSync(String XY, String bh, String mj, String xz, String cun,String tdzsmc,
                               String zdbcfy,String dsfzwbcf,String bssj) {
            this.XY = XY;
            this.bh = bh;
            this.mj = mj;
            this.xz = xz;
            this.cun = cun;
            this.tdzsmc=tdzsmc;
            this.zdbcfy=zdbcfy;
            this.dsfzwbcf=dsfzwbcf;
            this.bssj=bssj;
            ksoap=new KsoapValidateHttp(SBYDAddActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... str) {

            try {
                String AddRslt=ksoap.WebAddLandReported(XY, bh, mj, xz, cun, tdzsmc, zdbcfy, dsfzwbcf,bssj);
                if(AddRslt!=null){
                    Log.i("-WebAddLandReported-",AddRslt.toString());
                    return AddRslt;
                }else{
                    Log.i("-WebAddLandReported-","返回值为空");
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressdialogSave.dismiss();
            super.onPostExecute(s);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private String loadPhoneStatus(){
        TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceNo=phoneMgr.getDeviceId();
        Log.i("deviceNo", deviceNo);
        return deviceNo;
    }

    /**
     * 日期选择器
     */
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
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

            editTextbssj.setText(year+"-"+m+"-"+d);
        }
    }
}
package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.arcgis.dao.PZYDDao;
import com.arcgis.entity.PZYDEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PZYDAddActivity extends Activity implements View.OnClickListener{

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
    //批准时间
    static EditText editTextpzsj= null;
    //两公告一登记时间
    static EditText editTextlggydjsj= null;
    //土地征收名称
    Spinner spinnertdzsmc=null;
    //土地征收文号
    EditText EditTexttdzswh=null;
    //征地补偿费用
    EditText editTextzdbcfy= null;
    //地上附着物补偿费用
    EditText editTextfzwbcfy=null;
    //是否纳入政府储备
    Spinner spinnersfnrzfcb=null;

    private Button BackBtn=null;
    private Button OkBtn=null;

    private TextView backtextview;
    private TextView titletextview;

    //调用webservice
    private KsoapValidateHttp ksoap;

    //报批批次
    ArrayAdapter<String> spinnerbppcAdapter;
    //是否纳入政府储备
    ArrayAdapter<String> spinnersfnrzfcbAdapter;

    //全局变量存储位置
    private App MyApp;

    //坐标,编号,镇,村,地块面积,土地征收名称,
    // 土地征收文号,批准时间,两公告一登记,
    // 地面附着补偿费用,是否纳入政府储备：是、否

    String XY=null;
    String bh=null;
    String dkmj=null;
    String xz=null;
    String cun=null;
    String tdzsmc=null;
    String tdzswh=null;
    String lggydjsj=null;
    String pzsj=null;
    String zdbcfy=null;
    String dsfzwbcf=null;
    String sfnrzfcb=null;

    SimpleDateFormat format=null;
    private boolean isNetwork=false;
    ProgressDialog progressdialog;
    PZYDDao pzydDao=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addpzyd);
        App.getInstance().addActivity(this);
        isNetwork= NetUtils.isNetworkAvailable(this);
        pzydDao=new PZYDDao(this);

        BackBtn= (Button) findViewById(R.id.BackBtn);
        BackBtn.setOnClickListener(this);

        OkBtn= (Button) findViewById(R.id.OkBtn);
        OkBtn.setOnClickListener(this);

        backtextview= (TextView) findViewById(R.id.backtextview);
        backtextview.setOnClickListener(this);
        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("添加批准用地");

        //地块坐标
        editTextzb= (TextView) this.findViewById(R.id.editTextzb);
        editTextzb.setOnClickListener(this);
        //地块编号
        editTextkqbh= (EditText) this.findViewById(R.id.editTextkqbh);
        //所属村
        spinnerssc= (EditText) this.findViewById(R.id.spinnerssc);
        //所属乡镇
        spinnerssxz=(EditText)this.findViewById(R.id.spinnerssxz);
        //地块面积
        editTextmj= (EditText) findViewById(R.id.editTextmj);
        //土地征收名称
        spinnertdzsmc= (Spinner) findViewById(R.id.spinnertdzsmc);
        //土地征收文号
        EditTexttdzswh= (EditText) findViewById(R.id.EditTexttdzswh);
        //是否纳入储备
        spinnersfnrzfcb= (Spinner) findViewById(R.id.spinnersfnrzfcb);
        //批准时间
        editTextpzsj= (EditText) findViewById(R.id.editTextpzsj);
        editTextpzsj.setOnClickListener(this);
        //两公告一登记时间
        editTextlggydjsj= (EditText) findViewById(R.id.editTextlggydjsj);
        editTextlggydjsj.setOnClickListener(this);

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
            PZYDAddActivity.this.finish();
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
            //添加
            case R.id.OkBtn:

                isNetwork= NetUtils.isNetworkAvailable(this);

                XY=editTextzb.getText().toString();
                bh=editTextkqbh.getText().toString();
                dkmj=editTextmj.getText().toString();
                xz=spinnerssxz.getText().toString();
                cun=spinnerssc.getText().toString();
                tdzsmc=spinnertdzsmc.getSelectedItem().toString();
                tdzswh=EditTexttdzswh.getText().toString();
                zdbcfy=editTextzdbcfy.getText().toString();
                dsfzwbcf=editTextfzwbcfy.getText().toString();
                pzsj=editTextpzsj.getText().toString();
                lggydjsj=editTextlggydjsj.getText().toString();
                sfnrzfcb=spinnersfnrzfcb.getSelectedItem().toString();

                if(bh ==null || bh.isEmpty()){
                    ToastUtil.show(this,"地块编号不能为空!");
                    return;
                }

                //添加批准用地
                if(isNetwork){
                    PZYDADDDataSync AddPZYDSync=new PZYDADDDataSync(XY, bh, xz, cun, dkmj, tdzsmc, tdzswh,pzsj,
                            lggydjsj, zdbcfy, dsfzwbcf, sfnrzfcb);
                    try {
                        String addRslt=AddPZYDSync.execute().get(300, TimeUnit.SECONDS);
                        if(addRslt!=null && !addRslt.isEmpty()){
                            if(addRslt.equals("1")){
                                addRslt="保存成功!";
                                ToastUtil.show(PZYDAddActivity.this,addRslt);
                                Intent toMapIntent=new Intent();
                                toMapIntent.setClass(PZYDAddActivity.this,MainMap4Activity.class);
                                setResult(RESULT_OK,toMapIntent);
                                PZYDAddActivity.this.finish();
                            }else if(addRslt.equals("0")){
                                addRslt="保存失败!";
                                ToastUtil.show(PZYDAddActivity.this,addRslt+"该数据被保存至本地数据库");

                                PZYDEntity pzydEntity=new PZYDEntity();
                                pzydEntity.setZDBCFY(zdbcfy);
                                pzydEntity.setDKMJ(dkmj);
                                pzydEntity.setCoords(XY);
                                pzydEntity.setBH(bh);
                                pzydEntity.setXZ(xz);
                                pzydEntity.setCUN(cun);
                                pzydEntity.setTDZSWH(tdzswh);
                                pzydEntity.setDSFZWBCFY(dsfzwbcf);
                                pzydEntity.setTDZSMC(tdzsmc);
                                pzydEntity.setPZSJ(pzsj);
                                pzydEntity.setLGGYDJSJ(lggydjsj);
                                pzydEntity.setSFNRZFCB(sfnrzfcb);
                                pzydDao.add(pzydEntity);
                            }
                        }else{
                            ToastUtil.show(this,"服务器无返回值");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        ToastUtil.show(PZYDAddActivity.this,"线程中断异常");
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        ToastUtil.show(PZYDAddActivity.this,"线程执行异常");
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        ToastUtil.show(PZYDAddActivity.this,"连接服务器超时");
                    }
                }else{
                    ToastUtil.show(this,"当前无网络,该数据被保存至本地数据库");

                    PZYDEntity pzydEntity=new PZYDEntity();
                    pzydEntity.setZDBCFY(zdbcfy);
                    pzydEntity.setDKMJ(dkmj);
                    pzydEntity.setCoords(XY);
                    pzydEntity.setBH(bh);
                    pzydEntity.setXZ(xz);
                    pzydEntity.setCUN(cun);
                    pzydEntity.setTDZSWH(tdzswh);
                    pzydEntity.setDSFZWBCFY(dsfzwbcf);
                    pzydEntity.setTDZSMC(tdzsmc);
                    pzydEntity.setPZSJ(pzsj);
                    pzydEntity.setLGGYDJSJ(lggydjsj);
                    pzydEntity.setSFNRZFCB(sfnrzfcb);
                    pzydDao.add(pzydEntity);
                }
                break;
            case R.id.BackBtn:
                PZYDAddActivity.this.finish();
                break;
            case R.id.editTextpzsj:
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(), "datePicker");
                break;
            case R.id.editTextlggydjsj:
                DatePickerFragment2 datePicker2 = new DatePickerFragment2();
                datePicker2.show(getFragmentManager(), "datePicker");
                break;
            case R.id.backtextview:
                PZYDAddActivity.this.finish();
                break;
            case R.id.editTextzb:
                String coords=editTextzb.getText().toString();
                LayoutInflater factory = LayoutInflater.from(PZYDAddActivity.this);
                View view = factory.inflate(R.layout.activity_zbdetail, null);
                TextView editTextzbdetail = (TextView) view.findViewById(R.id.editTextzbdetail);
                editTextzbdetail.setText(coords);
                AlertDialog.Builder builder = new AlertDialog.Builder(PZYDAddActivity.this);
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

        Intent fromMapIntent=this.getIntent();
        if(fromMapIntent!=null){
            coordsStr= fromMapIntent.getStringExtra("COORDSTR");
            editTextzb.setText(coordsStr);
        }

        //处理时间
        format = new SimpleDateFormat("yyyy-MM-dd");
        String dataStr = format.format(new Date());
        editTextpzsj.setText(dataStr);
        editTextlggydjsj.setText(dataStr);

        isNetwork= NetUtils.isNetworkAvailable(this);

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
                    ToastUtil.show(PZYDAddActivity.this,"服务器无返回值");
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
            ToastUtil.show(this,"当前无网络无法获取批准用地位置信息");
        }


        spinnerbppcAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, MyApp.getSBYD_BPPC_list());
        spinnerbppcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertdzsmc.setAdapter(spinnerbppcAdapter);

        List<String> isZFCB_List=new ArrayList<String>();
        isZFCB_List.add("是");
        isZFCB_List.add("否");
        spinnersfnrzfcbAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, isZFCB_List);
        spinnersfnrzfcbAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersfnrzfcb.setAdapter(spinnersfnrzfcbAdapter);
    }

    public class GetAreaDataSync extends AsyncTask<String, Integer, String> {
        private String coords;
        public GetAreaDataSync(String coords) {
            this.coords=coords;
            ksoap=new KsoapValidateHttp(PZYDAddActivity.this);
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

    public class PZYDADDDataSync extends AsyncTask<String, Integer, String> {

        String XY=null;
        String bh=null;
        String dkmj=null;
        String xz=null;
        String cun=null;
        String tdzsmc=null;
        String tdzswh=null;
        String lggydjsj=null;
        String pzsj=null;
        String zdbcfy=null;
        String dsfzwbcf=null;
        String sfnrzfcb=null;

        ProgressDialog progressdialogSave=null;

        public PZYDADDDataSync(String XY,String bh,String xz,String cun,String dkmj,String tdzsmc,String tdzswh,
                               String pzsj,String lggydjsj,String zdbcfy,String sdfzwbcf,String sfnrzfcb) {
            this.XY = XY;
            this.bh = bh;
            this.dkmj = dkmj;
            this.xz = xz;
            this.cun = cun;
            this.tdzsmc=tdzsmc;
            this.zdbcfy=zdbcfy;
            this.tdzswh=tdzswh;
            this.lggydjsj=lggydjsj;
            this.pzsj=pzsj;
            this.sfnrzfcb=sfnrzfcb;
            this.dsfzwbcf=sdfzwbcf;
            ksoap=new KsoapValidateHttp(PZYDAddActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressdialogSave=new ProgressDialog(PZYDAddActivity.this);
            progressdialogSave.setCancelable(true);
            progressdialogSave.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressdialogSave.setMessage("保存批准用地...");
            progressdialogSave.setIndeterminate(true);
            progressdialogSave.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebAddPZYD( XY, bh, xz, cun, dkmj, tdzsmc, tdzswh,pzsj,
                                                lggydjsj, zdbcfy, dsfzwbcf, sfnrzfcb);
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
            progressdialogSave.dismiss();
            super.onPostExecute(s);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


//    private String loadPhoneStatus(){
//        TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
//        String deviceNo=phoneMgr.getDeviceId();
//        Log.i("deviceNo", deviceNo);
//        return deviceNo;
//    }

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

            editTextpzsj.setText(year+"-"+m+"-"+d);
        }
    }


    public static class DatePickerFragment2 extends DialogFragment implements DatePickerDialog.OnDateSetListener {
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

            editTextlggydjsj.setText(year+"-"+m+"-"+d);
        }
    }
}
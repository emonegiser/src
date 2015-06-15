package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.arcgis.R;
import com.arcgis.dao.XCRYDAO;
import com.arcgis.entity.XCRYEntity;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.github.nkzawa.socketio.client.On;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XCRY extends Activity {

    private String TAG="XCRY";
    private final static int HM_INIT=1;
    private final static int HM_END=2;
    private final static int HM_ERR=3;
    private final static int HM_FAIL=4;

    XCRYDAO xcrydao=null;

    KsoapValidateHttp ksoap=null;

    private TextView mBack;
    private TextView mTextViewFzr;
    private TextView mTextViewXcry;
    private TextView mTextViewOtherXcry;
    private TextView mUpdate;

    private Button mBtnAdd;
    private Button mBtnClear;
    private Button mBtnSubmit;

    private Spinner mSpinnerBm;
    ArrayAdapter<String> mSpinnerBmAdapter=null;
    private final static String[] Bm={"局机关/乡镇","市西办国土所"};

    String mBM;
    List<String> Xcrys=null;

    ArrayList<Integer> MultiChoiceID = new ArrayList<Integer>();

    SharedPreferences xcryPref=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xcry);

        xcrydao=new XCRYDAO(XCRY.this);

        ksoap=new KsoapValidateHttp(XCRY.this);

        mTextViewFzr=(TextView)findViewById(R.id.editTextfzr);
        mTextViewXcry=(TextView)findViewById(R.id.TextViewXcry);
        mTextViewOtherXcry=(TextView)findViewById(R.id.TextViewOtherXcry);
        mBack=(TextView)findViewById(R.id.backtextview);
        mUpdate=(TextView)findViewById(R.id.updatedata);

        mBtnAdd=(Button)findViewById(R.id.btnAdd);
        mBtnClear=(Button)findViewById(R.id.btnClear);
        mBtnSubmit=(Button)findViewById(R.id.btnSubmit);

        mSpinnerBm=(Spinner)findViewById(R.id.spinnerBM);
        mSpinnerBmAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Bm);
        mSpinnerBmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerBm.setAdapter(mSpinnerBmAdapter);


        xcryPref=getSharedPreferences("XCRY",MODE_PRIVATE);
        final SharedPreferences.Editor editor=xcryPref.edit();

        mTextViewFzr.setText(xcryPref.getString("FZR", ""));
        mTextViewXcry.setText(xcryPref.getString("XCRY", ""));
        mTextViewOtherXcry.setText(xcryPref.getString("OTHERXCRY", ""));

        //绑定Spinner控件选择事件
        mSpinnerBm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBM = parent.getItemAtPosition(position).toString();
                Xcrys = xcrydao.QueryXcrysByBm(mBM);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //点击添加巡查人员方法
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Xcrys = xcrydao.QueryXcrysByBm(mBM);
                if (Xcrys!=null) {
                    Object[] objs = Xcrys.toArray();
                    final String[] str = Xcrys.toArray(new String[Xcrys.size()]);

                    final boolean[] arrayFruitSelected = new boolean[str.length];

                    Dialog alertDialog = new AlertDialog.Builder(XCRY.this).
                            setTitle("选择巡查人员").
                            setIcon(null)
                            .setMultiChoiceItems(str, null, new DialogInterface.OnMultiChoiceClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    arrayFruitSelected[which] = isChecked;
                                }
                            }).
                                    setPositiveButton("确认", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            StringBuilder stringBuilder = new StringBuilder();
                                            for (int i = 0; i < arrayFruitSelected.length; i++) {
                                                if (arrayFruitSelected[i]) {
                                                    stringBuilder.append(str[i]).append("  ");
                                                }
                                            }
                                            String XcryStrings = stringBuilder.toString();
                                            mTextViewXcry.append(XcryStrings);
                                            Toast.makeText(XCRY.this, XcryStrings, Toast.LENGTH_SHORT).show();
                                        }
                                    }).
                                    setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                        }
                                    }).
                                    create();
                    alertDialog.show();
                }
                else
                    ToastUtil.show(XCRY.this,"没有"+mBM+"的巡查人员");
            }
        });

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("FZR", mTextViewFzr.getText().toString());
                editor.putString("XCRY", mTextViewXcry.getText().toString());
                editor.putString("OTHERXCRY", mTextViewOtherXcry.getText().toString());
                editor.commit();

                Dialog alertDialog = new AlertDialog.Builder(XCRY.this).
                        setTitle("巡查人员名单").
                        setIcon(null)
                        .setMessage("负责人：" + mTextViewFzr.getText().toString() +
                                "\n巡查人员：" + mTextViewXcry.getText().toString() +
                                "\n其他巡查人员：" + mTextViewOtherXcry.getText().toString()).
                                setPositiveButton("确认", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setClass(XCRY.this, MainDZZHActivity.class);
                                        startActivity(intent);
                                    }
                                }).
                                setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                    }
                                }).
                                create();
                alertDialog.show();
            }
        });

        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextViewXcry.setText("");
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XCRY.this.finish();
            }
        });

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateThread().start();
            }
        });
    }

    Handler mhandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case HM_INIT:
                    ToastUtil.show(XCRY.this,"最新版巡查人员名单正在下载");
                    break;
                case HM_END:
                    ToastUtil.show(XCRY.this,"名单下载完毕");
                    break;

                case HM_FAIL:
                    ToastUtil.show(XCRY.this,"下载失败，请重新点击更新数据");
                    break;
                default:
                    break;
            }
        }
    };

    class UpdateThread extends Thread
    {
        public void run()
        {
            Log.i(TAG,"The thread is running");

            //删除TB_XCRY中的所有记录
            xcrydao.deleteAll();
            //下载
            String XCRYJsonStrings=null;
            if (NetUtils.isNetworkAvailable(XCRY.this)) {
                try {
                    mhandler.obtainMessage(HM_INIT, null).sendToTarget();
                    XCRYJsonStrings = ksoap.WebDownloadXCRYTable();
                    if (XCRYJsonStrings == null)
                        return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                mhandler.obtainMessage(HM_ERR).sendToTarget();
            }

            JSONArray jsonArray=(JSONArray)JSON.parse(XCRYJsonStrings);
            for (int i=0;i<jsonArray.size() ;i++ ) {
                Log.i(TAG,"Prase the Json"+ i);
                com.alibaba.fastjson.JSONObject jsonObject=jsonArray.getJSONObject(i);
                String XCBH=jsonObject.getString("XCBH");
                String BM=jsonObject.getString("BM");
                String TEL=jsonObject.getString("TEL");
                String NAME=jsonObject.getString("NAME");

                XCRYEntity xcryEntity=new XCRYEntity();
                xcryEntity.setBm(BM);
                xcryEntity.setRybh(XCBH);
                xcryEntity.setName(NAME);
                xcryEntity.setTel(TEL);

                //执行存储
                xcrydao.add(xcryEntity);
            }
            if(xcrydao.queryAll()!=null) {
                mhandler.obtainMessage(HM_END, null).sendToTarget();
            }
            else
            {
                mhandler.obtainMessage(HM_FAIL, null).sendToTarget();
            }
        }
    }
}

package com.arcgis.emergency;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.httputil.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.showclear.sc_sip.SipContext;
import cn.showclear.sc_sip.SipRegistrationEventArgs;
import cn.showclear.sc_sip.SipSession;


public class ViewLoginActivity extends Activity implements View.OnClickListener {
    private static final String TAG = ViewLoginActivity.class.getCanonicalName();

    private EditText userEdit;
    private EditText passEdit;
    private EditText hostEdit;
    private EditText portEdit;
    private EditText dataServerEdit;
    private AutoCompleteTextView autoTextview;

//    private Spinner userSpinner;

    private TextView titletextview;
    private TextView backtextview;

    private Button okButton;
    ArrayAdapter<String> userSpinnerAdapter=null;
    List<String> userName_list=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_yj);
        //
       // userEdit = (EditText) findViewById(R.id.user);
        passEdit = (EditText) findViewById(R.id.pass);
        hostEdit = (EditText) findViewById(R.id.host);
        portEdit = (EditText) findViewById(R.id.port);

        dataServerEdit = (EditText) findViewById(R.id.data_server);
//        userSpinner= (Spinner) findViewById(R.id.userSpinner);

        autoTextview= (AutoCompleteTextView) findViewById(R.id.autoTextview);
        initAutoComplete("RECORD",autoTextview);

        titletextview= (TextView) findViewById(R.id.titletextview);
        titletextview.setText("应急指挥");

        userName_list=new ArrayList<>();
        userName_list.add("7901");
        userName_list.add("7902");
        userName_list.add("7903");
        userName_list.add("7905");
        userName_list.add("7906");
//        userName_list.add("6005");
//        userName_list.add("6006");
//        userName_list.add("6007");
//        userName_list.add("6008");
//        userName_list.add("6009");
//        userName_list.add("6010");
        userSpinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, userName_list);
        userSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        userSpinner.setAdapter(userSpinnerAdapter);

        backtextview= (TextView) findViewById(R.id.backtextview);
        backtextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewLoginActivity.this.finish();
            }
        });
        //
        okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkClicked();
            }
        });
        //
        registeSipBroadcast();
    }

    private void initAutoComplete(String field,AutoCompleteTextView auto) {
        String[] hisArrays=new String[]{};
        SharedPreferences sp = getSharedPreferences("INPUT_RECORD", 0);
        SharedPreferences.Editor speditor=sp.edit();

        if(null==sp.getString("RECORD",null)){
            speditor.putString("RECORD", "7901,7902,7903,7905,7906");
            speditor.commit();
        }

        String longhistory = sp.getString("RECORD", null);
        if(longhistory!=null){
            hisArrays = longhistory.split(",");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, hisArrays);
        //只保留最近的50条的记录
        if(hisArrays!=null && hisArrays.length > 50){
            String[] newArrays = new String[50];
            System.arraycopy(hisArrays, 0, newArrays, 0, 50);
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, newArrays);
        }
        auto.setAdapter(adapter);
        auto.setDropDownHeight(450);
        auto.setThreshold(1);
//        auto.setHeight(200);
        //auto.setCompletionHint("最近的5条记录");
        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
    }



    private void saveHistory(String field,AutoCompleteTextView auto) {
        String text = auto.getText().toString();
        SharedPreferences sp = getSharedPreferences("INPUT_RECORD", 0);
        String longhistory = sp.getString(field, null);
        if (longhistory!=null && !longhistory.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(longhistory);
            sb.insert(0, text + ",");
            sp.edit().putString("RECORD", sb.toString()).commit();
        }
    }

    @Override
    protected void onDestroy() {
        unregistSipBroadcast();
        //
        super.onDestroy();
    }

    private void refreshViews() {
        final SipContext sipContext = AppGlobal.getInstance().getSipContext();
        final SipSession.ConnectionState connectionState = sipContext.getRegistrationState();
        if (connectionState == SipSession.ConnectionState.CONNECTING
                || connectionState == SipSession.ConnectionState.TERMINATING) {
            okButton.setText("取消");
        } else {
            okButton.setText("登录");
            if (sipContext.isRegistered()) {
                finish();
            }
        }
    }

    private void onOkClicked() {

        saveHistory("RECORD",autoTextview);

        final SipContext sipContext = AppGlobal.getInstance().getSipContext();
        if(null==sipContext){
            return;
        }

        if(sipContext.getRegistrationState()==null){
            ToastUtil.show(this, "请重新登录");
            return;
        }
        final SipSession.ConnectionState connectionState = sipContext.getRegistrationState();

        if (connectionState == SipSession.ConnectionState.CONNECTING
                || connectionState == SipSession.ConnectionState.TERMINATING) {
            sipContext.stopStack(); // 取消操作
        } else {
            //String user = userEdit.getText().toString();
            //String user =userSpinner.getSelectedItem().toString();
            String user = autoTextview.getText().toString();
            String pass = passEdit.getText().toString();
            String host = hostEdit.getText().toString();
            int port = Integer.parseInt(portEdit.getText().toString());
            String dataServer = dataServerEdit.getText().toString();

            if(sipContext.isRegistered()){
                //ToastUtil.show(this,"已经登录");
                startActivity(new Intent(ViewLoginActivity.this, MainActivity.class));
                this.finish();
            }else{
                // 设置数据服务URL
                sipContext.setDataServerUrl(dataServer);

                // 执行登录操作
                boolean islogin=sipContext.loginToSip(user, pass, host, port);
//                if(islogin){
//                    startActivity(new Intent(ViewLoginActivity.this, MainActivity.class));
//                    this.finish();
//                }
            }
        }
        //
    }

    private BroadcastReceiver sipBroadCastRecv;

    private void registeSipBroadcast() {
        sipBroadCastRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                // Registration Event
                if(SipRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)){
                    SipRegistrationEventArgs args = intent.getParcelableExtra(SipRegistrationEventArgs.EXTRA_EMBEDDED);
                    if(args == null){
                        Log.e(TAG, "Invalid event args");
                        return;
                    }
                    switch(args.getEventType()){
                        case REGISTRATION_OK: // 注册成功
                            ToastUtil.show(ViewLoginActivity.this, "登录成功");
                            startActivity(new Intent(ViewLoginActivity.this, MainActivity.class));
                            ViewLoginActivity.this.finish();
                            break;
                        case REGISTRATION_INPROGRESS: // 正在注册中
                            ToastUtil.show(ViewLoginActivity.this, "正在注册中");
                            break;
                        case REGISTRATION_NOK: // 注册失败
                            ToastUtil.show(ViewLoginActivity.this, "注册失败");
                            break;
                        case UNREGISTRATION_OK: // 注销成功
                            ToastUtil.show(ViewLoginActivity.this, "注销成功");
                            break;
                        case UNREGISTRATION_INPROGRESS: // 正在注销中
                            ToastUtil.show(ViewLoginActivity.this, "正在注销中");
                            break;
                        case UNREGISTRATION_NOK: // 注销失败
                            ToastUtil.show(ViewLoginActivity.this, "注销失败");
                            break;
                        default:
                            refreshViews();
                            break;
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SipRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
        registerReceiver(sipBroadCastRecv, intentFilter);
    }

    private void unregistSipBroadcast() {
        if (sipBroadCastRecv != null) {
            unregisterReceiver(sipBroadCastRecv);
            sipBroadCastRecv = null;
        }
    }

    @Override
    public void onClick(View v) {

    }
}

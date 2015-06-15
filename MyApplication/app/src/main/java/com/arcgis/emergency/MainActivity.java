package com.arcgis.emergency;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.httputil.ToastUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import cn.showclear.sc_sip.SipContext;
import cn.showclear.sc_sip.SipMediaType;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private SipContext sipContext;

    private TextView currentUserLabel;
    private Button exitButton;
    private ImageButton videoCallButton;
    private ImageButton voiceCallButton;
    private ImageButton pocButton;
    private ImageButton messagesButton;

//    private Spinner telSpinner;
    private TextView  TextViewYYHJ;
    private TextView  TextViewSPHJ;
    private TextView  TextViewYYDJ;
    private TextView  TextViewXXFS;
    private TextView current_position;

    private AutoCompleteTextView autoTextview;

    ArrayAdapter<String> telSpinnerAdapter=null;
    List<String> userName_list=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_yj);
//        telSpinner= (Spinner) findViewById(R.id.telSpinner);
        TextViewYYHJ= (TextView) findViewById(R.id.TextViewYYHJ);
        TextViewSPHJ= (TextView) findViewById(R.id.TextViewSPHJ);
        TextViewYYDJ= (TextView) findViewById(R.id.TextViewYYDJ);
        TextViewXXFS= (TextView) findViewById(R.id.TextViewXXFS);
        current_position= (TextView) findViewById(R.id.current_position);

        autoTextview= (AutoCompleteTextView) findViewById(R.id.autoTextview);
        initAutoComplete("RECORD",autoTextview);

        //
        userName_list=new ArrayList<>();

        userName_list.add("998");
        userName_list.add("0998");
        userName_list.add("997");
        userName_list.add("0997");

        userName_list.add("7901");
        userName_list.add("7902");
        userName_list.add("7903");
        userName_list.add("7905");
        userName_list.add("7906");

//        telSpinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, userName_list);
//        telSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        telSpinner.setAdapter(telSpinnerAdapter);

        currentUserLabel = (TextView) findViewById(R.id.current_user);
        exitButton = (Button) findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExitButtonClicked();
            }
        });

        //视频呼叫
        videoCallButton = (ImageButton) findViewById(R.id.video_call);
        videoCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVideoCallButtonClicked();
            }
        });
        TextViewSPHJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVideoCallButtonClicked();
            }
        });


        //语音呼叫
        voiceCallButton = (ImageButton) findViewById(R.id.voice_call);
        voiceCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVoiceCallButtonClicked();
            }
        });

        TextViewYYHJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVoiceCallButtonClicked();
            }
        });

        //语音对讲
        pocButton = (ImageButton) findViewById(R.id.poc);
        pocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPocButtonClicked();
            }
        });

        TextViewYYDJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPocButtonClicked();
            }
        });

        //消息发送
        messagesButton = (ImageButton) findViewById(R.id.messages);
        messagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MessagesActivity.class));
            }
        });

        TextViewXXFS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MessagesActivity.class));
            }
        });
        //
        sipContext = AppGlobal.getInstance().getSipContext();
        //
        if (!sipContext.isRegistered()) {
            startActivity(new Intent(this, ViewLoginActivity.class));
            this.finish();
        }
        //

        current_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyPositionMapActivity.class));
            }
        });
        System.out.println(makeMd5ToLong("hello"));
    }


    private void initAutoComplete(String field,AutoCompleteTextView auto) {
        SharedPreferences sp = getSharedPreferences("CALL_RECORD", 0);
        SharedPreferences.Editor speditor=sp.edit();

        if(null==sp.getString("RECORD",null)){
            speditor.putString("RECORD", "998,0998,997,0997,7901,7902,7903,7905,7906");
            speditor.commit();
        }

        String longhistory = sp.getString("RECORD", null);
        String[]  hisArrays = longhistory.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, hisArrays);
        //只保留最近的50条的记录
        if(hisArrays.length > 50){
            String[] newArrays = new String[50];
            System.arraycopy(hisArrays, 0, newArrays, 0, 50);
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, newArrays);
        }
        auto.setAdapter(adapter);
        auto.setDropDownHeight(450);
        auto.setThreshold(1);
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
        SharedPreferences sp = getSharedPreferences("CALL_RECORD", 0);
        String longhistory = sp.getString(field, null);
        if (!longhistory.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(longhistory);
            sb.insert(0, text + ",");
            sp.edit().putString("RECORD", sb.toString()).commit();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //
        if (sipContext.isRegistered()) {
            currentUserLabel.setText("当前登录用户：" + sipContext.getLoginUser());
        } else {
            currentUserLabel.setText("当前未登录");
        }
    }

    private void onPocButtonClicked() {
        // 发起语音对讲
        //String tel = telEdit.getText().toString();
        //String tel = telSpinner.getSelectedItem().toString();
        String tel=autoTextview.getText().toString();
        saveHistory("RECORD",autoTextview);
        if (TextUtils.isEmpty(tel)) {
            return;
        }
        if(!sipContext.getLoginUser().equals(tel)){
            sipContext.makePoC(tel);
        }else{
            ToastUtil.show(this, "限制呼叫本机号码");
        }
    }

    private void onVoiceCallButtonClicked() {
        // 发起音频呼叫
        makeCall(SipMediaType.Audio);
    }

    private void onVideoCallButtonClicked() {
        // 发起视频呼叫
        makeCall(SipMediaType.AudioVideo);
    }

    private void makeCall(SipMediaType type) {
        //String tel = telEdit.getText().toString();
        //String tel = telSpinner.getSelectedItem().toString();
        String tel=autoTextview.getText().toString();
        saveHistory("RECORD",autoTextview);
        if (TextUtils.isEmpty(tel)) {
            return;
        }
        if(!sipContext.getLoginUser().equals(tel)){
            sipContext.makeCall(tel, type);
        }else{
            ToastUtil.show(this, "限制呼叫本机号码");
        }
    }

    private void onExitButtonClicked() {
        // 退出
        sipContext.logoutSip();
//        sipContext.shutdown();
        MainActivity.this.finish();
    }


    private static long makeMd5ToLong(String value) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] data =  md5.digest(value.getBytes());
            if (data == null) return value.hashCode();
            long ret = 0;
            for (int i = 0, n = Math.min(data.length, 8); i < n; i++) {
                long b = data[i] & 0xFF;
                ret = ret | (b << i);
            }
            return ret;
        } catch (NoSuchAlgorithmException e) {
            return value.hashCode();
        }
    }
}

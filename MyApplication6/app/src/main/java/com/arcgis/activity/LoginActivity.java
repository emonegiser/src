package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.arcgis.R;
import com.arcgis.dao.LoginDao;
import com.arcgis.entity.LoginEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoginActivity extends Activity implements View.OnClickListener{

    private EditText UidEditText=null;
    private EditText PwdEditText=null;
    private Button login_btn=null;
    private TextView syscongTextView=null;

    private boolean isExits = false;
    private Timer timer;
    private TimerTask timerTask;
    private KsoapValidateHttp ksoap;
    private ProgressDialog progressdialog;
    private CheckBox keepPsd;

    private LoginDao mLogindao;

    //全局变量存储位置
    private App MyApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_main);

        UidEditText= (EditText) findViewById(R.id.UidEditText);
        PwdEditText= (EditText) findViewById(R.id.PwdEditText);
        login_btn= (Button) findViewById(R.id.login_btn);
        syscongTextView= (TextView) findViewById(R.id.syscongTextView);
        keepPsd= (CheckBox) findViewById(R.id.keepPsd);

        syscongTextView.setOnClickListener(this);
        App.getInstance().addActivity(this);

        login_btn.setOnClickListener(this);

        SharedPreferences LOGIN_INFO = getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        if(LOGIN_INFO.getString("NAME",null)!=null&&LOGIN_INFO.getString("PWD",null)!=null){
            UidEditText.setText(LOGIN_INFO.getString("NAME",null));
        }
        if(LOGIN_INFO.getBoolean("ISCHECKED",false))
        {
            PwdEditText.setText(LOGIN_INFO.getString("PWD",null));
            keepPsd.setChecked(true);
        }
        MyApp=(App) this.getApplication();

        mLogindao=new LoginDao(LoginActivity.this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressdialog!=null){
            progressdialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch(viewId){
            case R.id.login_btn:
                String uid=UidEditText.getText().toString().trim();
                String pwd=PwdEditText.getText().toString().trim();

                if(uid.isEmpty()){
                    ToastUtil.show(this,"请输入用户名!");
                    return;
                }

                if(pwd.isEmpty()){
                    ToastUtil.show(this,"请输入密码!");
                    return;
                }

                progressdialog=new ProgressDialog(LoginActivity.this);
                progressdialog.setCancelable(true);
                progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressdialog.setMessage("登录中...");
                progressdialog.show();

                if(NetUtils.isNetworkAvailable(LoginActivity.this)){

                    if(uid!=null && pwd!=null && !uid.isEmpty() && !pwd.isEmpty()){
                        LoginSync loginTask = new LoginSync(uid,pwd);
                        String islogin=null;
                        try {
                            islogin=loginTask.execute().get(200, TimeUnit.SECONDS);
                            if(islogin!=null){
                                //解析JSON
                                JSONArray jsonArray = new JSONArray(islogin);
                                JSONObject o = (JSONObject) jsonArray.get(0);

                                SharedPreferences sp = LoginActivity.this.getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();

                                if (o.has("pname") && o.getString("pname") != null) {
                                    editor.putString("NAME", o.getString("pname"));
                                }
                                if (o.has("PID") && o.getString("PID") != null) {
                                    editor.putString("PID", o.getString("PID"));
                                }
                                editor.putString("NAME", uid);
                                editor.putString("PWD",pwd);
                                editor.putBoolean("ISCHECKED", keepPsd.isChecked());
                                editor.commit();
                                //跳转至首页
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();

                            }else{
                                //检查用户名密码
                                ToastUtil.show(LoginActivity.this,"输入的用户名或密码错误!");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            ToastUtil.show(LoginActivity.this,"线程执行异常");
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                            ToastUtil.show(LoginActivity.this,"连接服务器超时");
                            LoginActivity.this.finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else{
                        ToastUtil.show(LoginActivity.this,"请输入用户名和密码");
                        progressdialog.hide();
                    }
                }else{
                    //没有网络
                    SharedPreferences sp = LoginActivity.this.getSharedPreferences("KEEP_PWD", Context.MODE_PRIVATE);
                    String username=sp.getString("NAME",null);
                    String password=sp.getString("PWD",null);
                    //验证保存在本地的用户名和密码
                    if(username!=null && username.equals(uid) && password!=null && password.equals(pwd)){
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }else{
                        ToastUtil.show(LoginActivity.this,"无法从本地登陆,请链接网络");
                    }
                    if(progressdialog!=null){
                        progressdialog.dismiss();
                    }
                }

                break;
            case R.id.syscongTextView:
                startActivity(new Intent(this,ConfActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "退出").setIcon(android.R.drawable.ic_delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                break;
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("退出软件").setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.show();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (!isExits) {
                ToastUtil.show(LoginActivity.this, "再按一次退出");
                isExits = true;
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (isExits) {
                            isExits = false;
                        }
                    }
                };
                timer.schedule(timerTask, 3000);
            } else {
                this.finish();
                if(progressdialog!=null){
                    progressdialog.dismiss();
                }
            }
        }

        return false;
    }

    public class LoginSync extends AsyncTask<String, Integer, String> {

        private String uid;
        private String pwd;

        public LoginSync(String uid,String pwd) {
            this.uid = uid;
            this.pwd = pwd;
            ksoap=new KsoapValidateHttp(LoginActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                //rsltLogin.contains("pname") && rsltLogin.contains("PID")

                String rsltLogin=ksoap.WebGetLoginUserInfo(uid,pwd);
                if(rsltLogin!=null && !rsltLogin.isEmpty() ){
                    return rsltLogin;
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
    protected void onResume() {
        super.onResume();
        SharedPreferences SPPwd = LoginActivity.this.getSharedPreferences("KEEP_PWD", Context.MODE_PRIVATE);

        if(SPPwd.getBoolean("ISCHECKED",false)){
            //选取记住密码
            UidEditText.setText(SPPwd.getString("NAME",null));
            PwdEditText.setText(SPPwd.getString("PWD",null));
            keepPsd.setChecked(true);
        }
    }

    class getLoginUsertableThread extends Thread
    {

        public void run()
        {
            if(ksoap==null)
            {
                ksoap=new KsoapValidateHttp(LoginActivity.this);
            }
            String Accounts=null;
            try {
                Accounts=ksoap.WebgetLoginUsertable();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Accounts!=null) {
                mLogindao.deleteAll();
                com.alibaba.fastjson.JSONArray jsonArray = JSON.parseArray(Accounts);
                for (int i=0;i<jsonArray.size();i++)
                {
                    LoginEntity mLoginEntity=new LoginEntity();
                    mLoginEntity.setObjID(jsonArray.getJSONObject(i).getString("pid"));
                    mLoginEntity.setPassword(jsonArray.getJSONObject(i).getString("ppwd"));

                    mLogindao.add(mLoginEntity);
                }
            }
        }
    }

}
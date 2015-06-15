package com.arcgis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.httputil.App;
import com.arcgis.httputil.ConstantVar;


public class ConfActivity extends Activity implements View.OnClickListener{

    private EditText EditTextLogin=null;
    private EditText EditTextDZZH=null;
    private EditText EditTextKCZY=null;
    private EditText EditTextSBYD=null;
    private EditText EditTextPZYD=null;
    private EditText EditTextGYYD=null;
    private EditText EditTextCBYD=null;
    private EditText EditTextXCRW=null;
    private EditText EditTextWPZF=null;
    private EditText EditTextXZQH=null;
    private EditText EditTextSDYX=null;
    private EditText EditTextDZZHYJ=null;
    private EditText EditTextYJZS=null;
    private EditText EditTextTPSC=null;
    private EditText EditTextYJZH=null;
    private TextView backBtn=null;
    private Button ok_btn=null;

    //全局变量存储位置
    private App MyApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.conf_main);
        App.getInstance().addActivity(this);

        EditTextLogin= (EditText) findViewById(R.id.EditTextLogin);
        EditTextDZZH= (EditText) findViewById(R.id.EditTextDZZH);
        EditTextKCZY= (EditText) findViewById(R.id.EditTextKCZY);
        EditTextSBYD= (EditText) findViewById(R.id.EditTextSBYD);
        EditTextPZYD= (EditText) findViewById(R.id.EditTextPZYD);
        EditTextGYYD= (EditText) findViewById(R.id.EditTextGYYD);
        EditTextCBYD= (EditText) findViewById(R.id.EditTextCBYD);
        EditTextXCRW= (EditText) findViewById(R.id.EditTextXCRW);
        EditTextWPZF= (EditText) findViewById(R.id.EditTextWPZF);
        EditTextXZQH= (EditText) findViewById(R.id.EditTextXZQH);
        EditTextSDYX= (EditText) findViewById(R.id.EditTextSDYX);
        EditTextDZZHYJ=(EditText) findViewById(R.id.EditTextDZZHYJ);
        EditTextYJZS= (EditText) findViewById(R.id.EditTextYJZS);
        EditTextTPSC= (EditText) findViewById(R.id.EditTextTPSC);
        EditTextYJZH= (EditText) findViewById(R.id.EditTextYJZH);

        backBtn= (TextView) findViewById(R.id.backBtn);
        ok_btn= (Button) findViewById(R.id.ok_btn);
        
        ok_btn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        MyApp=(App) this.getApplication();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        switch(viewId){
            case R.id.ok_btn:

                AlertDialog dlg = new AlertDialog.Builder(this)
                        .setCancelable(true).setTitle("提醒").setMessage("是否保存？")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String login=EditTextLogin.getText().toString().trim();
                                String dzzh=EditTextDZZH.getText().toString().trim();
                                String kczy=EditTextKCZY.getText().toString().trim();
                                String sbyd=EditTextSBYD.getText().toString().trim();
                                String pzyd=EditTextPZYD.getText().toString().trim();
                                String gyyd=EditTextGYYD.getText().toString().trim();
                                String cbyd=EditTextCBYD.getText().toString().trim();
                                String xcrw=EditTextXCRW.getText().toString().trim();
                                String wpzf=EditTextWPZF.getText().toString().trim();
                                String xzqh=EditTextXZQH.getText().toString().trim();
                                String sdyx=EditTextSDYX.getText().toString().trim();
                                String dzzhyj=EditTextDZZHYJ.getText().toString().trim();
                                String yjzspjxx=EditTextYJZS.getText().toString().trim();
                                String tpsc=EditTextTPSC.getText().toString().trim();
                                String yjzh=EditTextYJZH.getText().toString().trim();

                                SharedPreferences CONFSYS_INFO = ConfActivity.this.getSharedPreferences("CONFSYS_INFO",MODE_PRIVATE);
                                SharedPreferences.Editor editor = CONFSYS_INFO.edit();
                                editor.putString("LOGIN",login);
                                editor.putString("DZZH",dzzh);
                                editor.putString("KCZY",kczy);
                                editor.putString("SBYD",sbyd);
                                editor.putString("PZYD",pzyd);
                                editor.putString("GYYD",gyyd);
                                editor.putString("CBYD",cbyd);
                                editor.putString("XCRW",xcrw);
                                editor.putString("WPZF",wpzf);
                                editor.putString("XZQH",xzqh);
                                editor.putString("SDYX",sdyx);
                                editor.putString("URL",dzzhyj);
                                editor.putString("ZSURL",yjzspjxx);
                                editor.putString("YJZH",yjzh);
                                editor.putString("TPSC",tpsc);
                                editor.commit();

                                if(CONFSYS_INFO.getString("WPZF",null)!=null && CONFSYS_INFO.getString("WPZF",null).length()>0){
                                    ConstantVar.WPZFURL=CONFSYS_INFO.getString("WPZF","");
                                }

                                if(CONFSYS_INFO.getString("XCRW",null)!=null && CONFSYS_INFO.getString("XCRW",null).length()>0){
                                    ConstantVar.XCRWURL=CONFSYS_INFO.getString("XCRW","");
                                }

                                if(CONFSYS_INFO.getString("CBYD",null)!=null && CONFSYS_INFO.getString("CBYD",null).length()>0){
                                    ConstantVar.CBYDURL=CONFSYS_INFO.getString("CBYD","");
                                }

                                if(CONFSYS_INFO.getString("GYYD",null)!=null && CONFSYS_INFO.getString("GYYD",null).length()>0){
                                    ConstantVar.GYYDURL=CONFSYS_INFO.getString("GYYD","");
                                }

                                if(CONFSYS_INFO.getString("PZYD",null)!=null && CONFSYS_INFO.getString("PZYD",null).length()>0){
                                    ConstantVar.PZYDURL=CONFSYS_INFO.getString("PZYD","");
                                }

                                if(CONFSYS_INFO.getString("SBYD",null)!=null && CONFSYS_INFO.getString("SBYD",null).length()>0){
                                    ConstantVar.SBYDURL=CONFSYS_INFO.getString("SBYD","");
                                }

                                if(CONFSYS_INFO.getString("KCZY",null)!=null && CONFSYS_INFO.getString("KCZY",null).length()>0){
                                    ConstantVar.KCURL=CONFSYS_INFO.getString("KCZY","");
                                }

                                if(CONFSYS_INFO.getString("DZZH",null)!=null && CONFSYS_INFO.getString("DZZH",null).length()>0){
                                    ConstantVar.DZZHQUERYURL=CONFSYS_INFO.getString("DZZH","");
                                }
                                if(CONFSYS_INFO.getString("LOGIN",null)!=null && CONFSYS_INFO.getString("LOGIN",null).length()>0){
                                    ConstantVar.LOGINURL=CONFSYS_INFO.getString("LOGIN","");
                                }

                                //地图
                                if(CONFSYS_INFO.getString("XZQH",null)!=null && CONFSYS_INFO.getString("XZQH",null).length()>0){
                                    ConstantVar.DZZHMAPURL=CONFSYS_INFO.getString("XZQH","");
                                }
                                if(CONFSYS_INFO.getString("XZQH",null)!=null && CONFSYS_INFO.getString("XZQH",null).length()>0){
                                    ConstantVar.XZQHMAPURL=CONFSYS_INFO.getString("XZQH","");
                                }
                                if(CONFSYS_INFO.getString("SDYX",null)!=null && CONFSYS_INFO.getString("SDYX",null).length()>0){
                                    ConstantVar.IMAGEURL=CONFSYS_INFO.getString("SDYX","");
                                }
                                if(CONFSYS_INFO.getString("URL",null)!=null && CONFSYS_INFO.getString("URL",null).length()>0){
                                    ConstantVar.DZZHYJ=CONFSYS_INFO.getString("URL","");
                                }
                                if(CONFSYS_INFO.getString("ZSURL",null)!=null && CONFSYS_INFO.getString("ZSURL",null).length()>0){
                                    ConstantVar.YJZS=CONFSYS_INFO.getString("ZSURL","");
                                }
                                if(CONFSYS_INFO.getString("YJZH",null)!=null&&CONFSYS_INFO.getString("YJZH",null).length()>0){
                                    ConstantVar.PicAddYJ = CONFSYS_INFO.getString("YJZH","");
                                }
                                if(CONFSYS_INFO.getString("TPSC",null)!=null&&CONFSYS_INFO.getString("TPSC",null).length()>0){
                                    ConstantVar.UPLOAD = CONFSYS_INFO.getString("TPSC","");
                                }

                                dialog.dismiss();
                                ConfActivity.this.finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.backBtn:
                ConfActivity.this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            ConfActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences CONFSYS_INFO = ConfActivity.this.getSharedPreferences("CONFSYS_INFO",MODE_PRIVATE);

        if(CONFSYS_INFO.getString("WPZF",null)!=null && CONFSYS_INFO.getString("WPZF",null).length()>0){
            EditTextWPZF.setText(CONFSYS_INFO.getString("WPZF",""));
        }

        if(CONFSYS_INFO.getString("XCRW",null)!=null && CONFSYS_INFO.getString("XCRW",null).length()>0){
            EditTextXCRW.setText(CONFSYS_INFO.getString("XCRW",""));
        }

        if(CONFSYS_INFO.getString("CBYD",null)!=null && CONFSYS_INFO.getString("CBYD",null).length()>0){
            EditTextCBYD.setText(CONFSYS_INFO.getString("CBYD",""));
        }

        if(CONFSYS_INFO.getString("GYYD",null)!=null && CONFSYS_INFO.getString("GYYD",null).length()>0){
            EditTextGYYD.setText(CONFSYS_INFO.getString("GYYD",""));
        }

        if(CONFSYS_INFO.getString("PZYD",null)!=null && CONFSYS_INFO.getString("PZYD",null).length()>0){
            EditTextPZYD.setText(CONFSYS_INFO.getString("PZYD",""));
        }

        if(CONFSYS_INFO.getString("SBYD",null)!=null && CONFSYS_INFO.getString("SBYD",null).length()>0){
            EditTextSBYD.setText(CONFSYS_INFO.getString("SBYD",""));
        }

        if(CONFSYS_INFO.getString("KCZY",null)!=null && CONFSYS_INFO.getString("KCZY",null).length()>0){
            EditTextKCZY.setText(CONFSYS_INFO.getString("KCZY",""));
        }

        if(CONFSYS_INFO.getString("DZZH",null)!=null && CONFSYS_INFO.getString("DZZH",null).length()>0){
            EditTextDZZH.setText(CONFSYS_INFO.getString("DZZH",""));
        }
        if(CONFSYS_INFO.getString("LOGIN",null)!=null && CONFSYS_INFO.getString("LOGIN",null).length()>0){
            EditTextLogin.setText(CONFSYS_INFO.getString("LOGIN",""));
        }

        //地图
        if(CONFSYS_INFO.getString("XZQH",null)!=null && CONFSYS_INFO.getString("XZQH",null).length()>0){
            EditTextXZQH.setText(CONFSYS_INFO.getString("XZQH",""));
        }

        if(CONFSYS_INFO.getString("SDYX",null)!=null && CONFSYS_INFO.getString("SDYX",null).length()>0){
            EditTextSDYX.setText(CONFSYS_INFO.getString("SDYX",""));
        }

        if(CONFSYS_INFO.getString("URL",null)!=null && CONFSYS_INFO.getString("URL",null).length()>0){
            EditTextDZZHYJ.setText(CONFSYS_INFO.getString("URL",""));
        }
        if(CONFSYS_INFO.getString("ZSURL",null)!=null && CONFSYS_INFO.getString("ZSURL",null).length()>0){
            EditTextYJZS.setText(CONFSYS_INFO.getString("ZSURL",""));
        }
        if(CONFSYS_INFO.getString("YJZH",null)!=null&&CONFSYS_INFO.getString("YJZH",null).length()>0){
            EditTextYJZH.setText(CONFSYS_INFO.getString("YJZH",""));
        }
        if(CONFSYS_INFO.getString("TPSC",null)!=null&&CONFSYS_INFO.getString("TPSC",null).length()>0){
            EditTextTPSC.setText(CONFSYS_INFO.getString("TPSC",""));
        }
    }
}
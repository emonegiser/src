package com.arcgis.emergency;

import android.app.Application;

import cn.showclear.sc_sip.SipContext;

/**
 * 程序主入口
 * Created by jiangwj on 2015/1/27.
 */
public class PoCSampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //
        AppGlobal.getInstance().setContext(this);
        AppGlobal.getInstance().setSipContext(new SipContext(this));
        // 启用消息服务
        AppGlobal.getInstance().getSipContext().enableMessageManager();
        //
        AppGlobal.getInstance().getSipContext().startup();
        //
    }

    @Override
    public void onTerminate() {
        AppGlobal.getInstance().getSipContext().shutdown();
        //
        super.onTerminate();
    }

}

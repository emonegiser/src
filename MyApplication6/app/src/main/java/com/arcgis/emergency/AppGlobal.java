package com.arcgis.emergency;

import android.content.Context;

import cn.showclear.sc_sip.SipContext;

/**
 * Created by jiangwj on 2015/1/27.
 */
public class AppGlobal {

    private static AppGlobal _instance = new AppGlobal();

    public static AppGlobal getInstance() {
        return _instance;
    }

    private Context context = null;

    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    private SipContext sipContext;

    public SipContext getSipContext() {
        return sipContext;
    }
    public void setSipContext(SipContext sipContext) {
        this.sipContext = sipContext;
    }

}

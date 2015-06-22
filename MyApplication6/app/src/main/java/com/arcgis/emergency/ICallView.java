package com.arcgis.emergency;

import android.content.Intent;

import cn.showclear.sc_sip.SipSession;

/**
 *
 * Created by jiangwj on 2015/1/27.
 */
public interface ICallView {
    void updateView(Intent intent);
    void updateView();
    void updateTimeView();
    void setSession(SipSession session);
}

package com.arcgis.uploadofflinedata;

import android.content.Context;
import android.util.Log;

import com.arcgis.httputil.NetUtils;

import java.util.TimerTask;

/**定时判断是否联网
 * Created by EMonegiser on 2015/6/10.
 */
class QunXTask extends TimerTask
{
    private final static String TAG="QunXTask";

    private Context context;

     UploadOfflineDataService receiveMsgService;

    public QunXTask(Context context) {
        super();
        this.context=context;
    }

    @Override
    public void run() {

        Log.i(TAG, "QunXtask Runs");


        if (NetUtils.isNetworkAvailable(context))
        {
            UploadOfflineDataService.isContected = true;
        }
        else
        {
            UploadOfflineDataService.isContected = false;
        }

        if (UploadOfflineDataService.getOnGetConnectState() != null)
        {
            UploadOfflineDataService.getOnGetConnectState().GetState(UploadOfflineDataService.isContected); // 通知网络状态改变
            Log.i(TAG, "通知网络状态改变:" + UploadOfflineDataService.isContected);
        }
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

    @Override
    public long scheduledExecutionTime() {
        return super.scheduledExecutionTime();
    }
}
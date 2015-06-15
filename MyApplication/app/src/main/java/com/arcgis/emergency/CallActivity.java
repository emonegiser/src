package com.arcgis.emergency;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import cn.showclear.sc_sip.SipContext;
import cn.showclear.sc_sip.SipInviteEventArgs;
import cn.showclear.sc_sip.SipMediaPluginEventArgs;
import cn.showclear.sc_sip.SipSession;

/**
 *
 * Created by jiangwj on 2015/1/27.
 */
public class CallActivity extends Activity {
    private static final String TAG = CallActivity.class.getCanonicalName();

    public static final String ACTION_VIDEO = "video_call";
    public static final String ACTION_VOICE = "voice_call";

    public static final String KEY_ID  = "id";
    public static final String KEY_TEL = "tel";

    private BroadcastReceiver mBroadCastRecv;

    private SipContext sipContext;
    private SipSession sipSession;

    private Fragment currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        sipContext = AppGlobal.getInstance().getSipContext();
        //
        initReceiver();
        //
        initSession();
        //
        initView();
    }

    @Override
    protected void onDestroy() {
        //
        destroyReceiver();
        //
        destroySession();
        //
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        if (currentView instanceof ICallView) {
            ((ICallView) currentView).updateView(getIntent());
        }
        //
        if (sipSession != null) {
            switch(sipSession.getState()){
                case TERMINATED:
                    doTerminated();
                    break;
                default:
                    loadView();
                    break;
            }
        }
    }

    private boolean isStateSaved = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //
        isStateSaved = true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //
        isStateSaved = false;
    }

    private void initSession() {
        long id = getIntent().getLongExtra(KEY_ID, -1);
        if (id == -1) {
            Log.e(TAG, "Invalid audio/video session");
            return;
        }
        //
        sipSession = sipContext.getSession(id);
        if (sipSession == null) {
            Log.e(TAG, String.format("Cannot find audio/video session with id=%s", id));
            return;
        }
        sipSession.setContext(this);

        if (sipSession.getState() == SipSession.InviteState.INCALL) {
            try {
                mTimerInCall.schedule(mTimerTaskInCall, 0, 1000);
            } catch (Exception ex) {
                Log.e(TAG, "" + ex.getMessage());
            }
        }
    }

    private void destroySession() {
        mTimerInCall.cancel();
        if(sipSession != null){
            sipSession.setContext(null);
        }
    }

    private void initReceiver() {
        mBroadCastRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(SipInviteEventArgs.ACTION_INVITE_EVENT.equals(intent.getAction())){
                    handleSipEvent(intent);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SipInviteEventArgs.ACTION_INVITE_EVENT);
        intentFilter.addAction(SipMediaPluginEventArgs.ACTION_MEDIA_PLUGIN_EVENT);
        registerReceiver(mBroadCastRecv, intentFilter);
    }

    private void destroyReceiver() {
        if(mBroadCastRecv != null){
            unregisterReceiver(mBroadCastRecv);
            mBroadCastRecv = null;
        }
    }

    private void initView() {
        String action = getIntent().getAction();
        Fragment fragment = loadCurrentView(action);
        //
        if (fragment != null) {
//            if (!isStateSaved) {
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(android.R.id.content, fragment, action)
//                        .commit();
//            }
            //
            currentView = fragment;
        }
        //
        if (currentView instanceof ICallView) {
            ICallView cv = (ICallView) currentView;
            cv.setSession(sipSession);
            cv.updateView();
        }
    }

    private Fragment loadCurrentView(String action) {
        Fragment fragment = getFragmentManager().findFragmentByTag(action);
        if (fragment != null) return fragment;
        if (ACTION_VIDEO.equals(action)) {
            fragment = new VideoCallFragment();
            getIntent().setAction(ACTION_VIDEO);
        } else if (ACTION_VOICE.equals(action)) {
            fragment = new VoiceCallFragment();
        }
        return fragment;
    }

    private void loadView() {
        loadView(false);
    }

    private void loadView(boolean bPickedCall) {
        String action = getIntent().getAction();
        Fragment fragment = null;
        boolean isVideoCall = sipSession.isVideoCall();
        //
        switch(sipSession.getState()){
            case INCOMING:
            {
                fragment = loadCurrentView(action);
                break;
            }
            case INPROGRESS: // 呼出时
            case REMOTE_RINGING:
            {
                fragment = loadCurrentView(action);
                break;
            }

            case INCALL: // 呼入
            case EARLY_MEDIA:
            {
                if (bPickedCall) {
                    if (isVideoCall) {
                        fragment = loadCurrentView(action = ACTION_VIDEO);
                    } else {
                        fragment = loadCurrentView(action = ACTION_VOICE);
                    }
                } else {
                    if (TextUtils.isEmpty(action)) action = ACTION_VOICE;
                    fragment = loadCurrentView(action);
                }
                break;
            }

            case NONE:
            case TERMINATING:
            case TERMINATED:
            default:
                fragment = loadCurrentView(action);
                break;
        }
        //
        if (fragment instanceof ICallView) {
            ICallView cv = (ICallView) fragment;
            cv.setSession(sipSession);
        }
        //
        if (fragment != null) {
            if (!isStateSaved) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, fragment, action)
                        .commit();
            }
            currentView = fragment;
        }
        //
        if (fragment instanceof ICallView) {
            ICallView cv = (ICallView) fragment;
            cv.updateView();
        }
    }



    private void handleSipEvent(Intent intent){
        if(sipSession == null){
            Log.e(TAG, "Invalid session object");
            return;
        }
        SipSession.InviteState state;
        SipInviteEventArgs args = intent.getParcelableExtra(SipInviteEventArgs.EXTRA_EMBEDDED);
        if(args == null) {
            Log.e(TAG, "Invalid event args");
            return;
        }
        if(args.getSessionId() != sipSession.getId()){
            return;
        }

        state = sipSession.getState();
        switch (state) {
            case NONE:
            default:
                break;

            case INCOMING:
            case INPROGRESS:
            case REMOTE_RINGING:
                //
                loadView();
                break;

            case EARLY_MEDIA:
            case INCALL:
                processInCall(intent, args);
                break;

            case TERMINATING:
            case TERMINATED:
                loadView();
                //
                doTerminated();
                break;
        }
    }

    // 结束呼叫
    protected void doTerminated() {
        mTimerInCall.cancel();
        //
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    //
    private void processInCall(Intent intent, SipInviteEventArgs args) {
        Log.i(TAG, "processInCall " + args.getEventType());
        //
        loadView(true);
        //
        if (sipSession != null) {
            try {
                mTimerInCall.schedule(mTimerTaskInCall, 0, 1000);
            } catch (Exception ex) {
                Log.e(TAG, "" + ex.getMessage());
            }
        }
    }

    //////////////////////////////////////////////////////////////////////
    // time duration update

    /** 更新当前呼叫持续时长 */
    private Timer mTimerInCall = new Timer("Call timer");

    private final TimerTask mTimerTaskInCall = new TimerTask(){
        @Override
        public void run() {
            if (currentView instanceof ICallView) {
                ((ICallView)currentView).updateTimeView();
            }
        }
    };

    //////////////////////////////////////////////////////////////////////

}

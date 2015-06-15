package com.arcgis.emergency;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.arcgis.R;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.showclear.sc_sip.SipContext;
import cn.showclear.sc_sip.SipInviteEventArgs;
import cn.showclear.sc_sip.SipMediaPluginEventArgs;
import cn.showclear.sc_sip.SipSession;
import cn.showclear.sc_sip.contacts.GroupInfo;
import cn.showclear.sc_sip.poc.PoCMessage;
import cn.showclear.sc_sip.poc.SipPoCMessageArgs;
import cn.showclear.sc_sip.poc.SipPoCMessageOps;
import cn.showclear.sc_sip.poc.SipPoCMessageTypes;


public class PoCActivity extends Activity {
    private static final String TAG = PoCActivity.class.getCanonicalName();

    public static String EXTRA_ID  = "id";
    public static String EXTRA_TEL = "tel";

    private BroadcastReceiver mBroadCastRecv;

    private SipContext sipContext;

    private TextView titleLabel;
    private TextView timeLabel;
    private Button closeButton;
    private Button pttButton;
    private ListView listView;

    private SipSession sipSession;

    private boolean silence = false;

    private PoCMemberAdapter memberListHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po_c);
        //
        titleLabel = (TextView) findViewById(R.id.title);
        timeLabel = (TextView) findViewById(R.id.time);
        closeButton = (Button) findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseButtonClicked();
            }
        });
        pttButton = (Button) findViewById(R.id.ptt);
        pttButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE: break;
                    case MotionEvent.ACTION_DOWN: onPttDown(); break;
                    case MotionEvent.ACTION_UP  : onPttUp(); break;
                }
                return false;
            }
        });
        listView = (ListView) findViewById(R.id.listView);
        //
        sipContext = AppGlobal.getInstance().getSipContext();
        //
        initReceiver();
        initSession();
        //
        timeLabel.setText("00:00");
        //
        memberListHelper = new PoCMemberAdapter(this);
        listView.setAdapter(memberListHelper);
        //
        memberListHelper.addMember(sipContext.getLoginUser());
        if (sipSession.isPoCGroup()) {
            String group = sipSession.getPoCGroupTel();
            titleLabel.setText(group);
            Collection<String> members = sipSession.getPoCGroupMembers();
            for (String memTel : members) {
                memberListHelper.checkAndAddMember(memTel);
            }
        } else {
            memberListHelper.addMember(sipSession.getRemotePartyDisplayName());
        }
        //
        if (sipSession.getState() == SipSession.InviteState.INCALL) {
            try {
                mTimerInCall.schedule(mTimerTaskInCall, 0, 1000);
            } catch (Exception ex) {
                Log.e(TAG, "" + ex.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        destroyReceiver();
        destroySession();
        //
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //
    }

    private void initSession() {
        //
        long id = getIntent().getLongExtra(EXTRA_ID, -1);
        if (id == -1) {
            finish();
            return;
        }
        sipSession = sipContext.getSession(id);
        if (sipSession == null) {
            finish();
            return;
        }
        //
        sipSession.setContext(this);
        // 自动接起呼入的对讲
        if (!sipSession.isOutgoing()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sipSession.acceptCall();
                }
            }, 500);
        }
        //
    }

    private void destroySession() {
        //
        mTimerInCall.cancel();
        //
        if (sipSession != null) {
            sipSession.setContext(null);
        }
    }

    //
    private void initReceiver() {
        Log.i(TAG, "initReceiver()");
        mBroadCastRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if(SipInviteEventArgs.ACTION_INVITE_EVENT.equals(action)){
                    handleSipEvent(intent);
                }
                else if(SipPoCMessageArgs.ACTION_POC_EVENT.equals(action)) {
                    handlePoCEvent(intent);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SipInviteEventArgs.ACTION_INVITE_EVENT);
        intentFilter.addAction(SipMediaPluginEventArgs.ACTION_MEDIA_PLUGIN_EVENT);
        intentFilter.addAction(SipPoCMessageArgs.ACTION_POC_EVENT);
        registerReceiver(mBroadCastRecv, intentFilter);
    }

    private void destroyReceiver() {
        Log.i(TAG, "destroyReceiver()");
        if(mBroadCastRecv != null){
            unregisterReceiver(mBroadCastRecv);
            mBroadCastRecv = null;
        }
    }

    private void onCloseButtonClicked() {
        // 挂断呼叫
        if (sipSession != null) {
            sipSession.hangUpCall();
        }
        finish();
    }

    private void onPttDown() {
        // 按下请求话权
        if (sipSession != null) {
            sipSession.requestPTT();
        }
    }

    private void onPttUp() {
        // 放开，取消话权请求
        if (sipSession != null) {
            sipSession.releasePTT();
        }
        //
        memberListHelper.updateCurrentSpeaker("");
    }

    private void handleSipEvent(Intent intent) {
        if(sipSession == null){
            Log.e(TAG, "Invalid session object");
            return;
        }
        //
        SipSession.InviteState state;
        SipInviteEventArgs args = intent.getParcelableExtra(SipInviteEventArgs.EXTRA_EMBEDDED);
        if(args == null) {
            Log.e(TAG, "Invalid event args");
            return;
        }
        if(args.getSessionId() != sipSession.getId()){
            return;
        }
        //
        state = sipSession.getState();
        switch (state) {
            case NONE:
            default:
                break;

            case INCOMING:
            case INPROGRESS:
            case REMOTE_RINGING:
//                loadView();
                break;

            case EARLY_MEDIA:
            case INCALL:
                processInCall(intent, args);
                break;

            case TERMINATING:
            case TERMINATED:
                //
                mTimerInCall.cancel();
                // 通话结束后立即退出
                runOnUiThread(new Runnable() {
                    public void run() {
                        finish();
                    }
                });
                break;
        }
    }

    private void handlePoCEvent(Intent intent) {
        if(sipSession == null){
            Log.e(TAG, "Invalid session object");
            return;
        }
        //
        //
        final SipPoCMessageArgs args = intent.getParcelableExtra(SipPoCMessageArgs.EXTRA_EMBEDDED);
        if(args == null) {
            Log.e(TAG, "Invalid event args");
            return;
        }
        if(args.getSessionId() != sipSession.getId()){
            return;
        }

        final SipPoCMessageOps op = args.getOp();
        final SipPoCMessageTypes type = args.getType();
        final PoCMessage data = args.getMessage();
        //
        if (SipPoCMessageOps.RESPONSE == op) {
            //  处理按下返回成功后，调整本机号码显示状态
            if (SipPoCMessageTypes.PTT == type) {
                String result = data.optString(PoCMessage.FIELD_RESULT);
                if ("ok".equals(result) && pttButton.isPressed()) {
                    silence = false;
                    memberListHelper.updateCurrentSpeaker(sipContext.getLoginUser());
                } else {
                    silence = true;
                }
            }
            return;
        } else if (SipPoCMessageOps.NOTIFY != op) return;
        //
        if (SipPoCMessageTypes.PTT == type) { // 通知某个成员获得话权
            String speaker = data.optString(PoCMessage.FIELD_SPEAKER);
            // update current speaker
            memberListHelper.updateCurrentSpeaker(speaker);
        } else if (SipPoCMessageTypes.Group == type) { // 通知组建立以及组中哪个成员拥有话权
            String group = data.optString(PoCMessage.FIELD_CURRENT);
            String speaker = data.optString(PoCMessage.FIELD_SPEAKER);
            //
            if (!TextUtils.isEmpty(group)) {
                GroupInfo info = sipContext.getContactsManager().getGroupManager().getGroupByTel(group);
                if (info == null) {
                    titleLabel.setText(group);
                } else {
                    titleLabel.setText(info.getName());
                }
            }
            // update current speaker
            memberListHelper.updateCurrentSpeaker(speaker);
            // current tel join to members
            memberListHelper.checkAndAddMember(sipContext.getLoginUser());
        } else if (SipPoCMessageTypes.GroupState == type) { // 通知组状态
            if (data.has(PoCMessage.FIELD_GROUP)) {
                String group = data.optString(PoCMessage.FIELD_GROUP);
                if (!TextUtils.isEmpty(group)) {
                    GroupInfo info = sipContext.getContactsManager().getGroupManager().getGroupByTel(group);
                    if (info == null) {
                        titleLabel.setText(group);
                    } else {
                        titleLabel.setText(info.getName());
                    }
                    memberListHelper.removeMember(group);
                }
            }
            //
            if (data.has(PoCMessage.FIELD_MEMBERS)) { // 对讲组成员列表通知
                memberListHelper.clearMembers();
                JSONArray arr = data.optJSONArray(PoCMessage.FIELD_MEMBERS);
                if (arr != null && arr.length() > 0) {
                    for (int i = 0; i < arr.length(); i++) {
                        memberListHelper.checkAndAddMember(arr.optString(i));
                    }
                }
            } else if (data.has(PoCMessage.FIELD_JOIN)) { // 通知成员加入对讲组
                JSONArray arr = data.optJSONArray(PoCMessage.FIELD_JOIN);
                if (arr != null && arr.length() > 0) {
                    for (int i = 0; i < arr.length(); i++) {
                        memberListHelper.checkAndAddMember(arr.optString(i));
                    }
                }
            } else if (data.has(PoCMessage.FIELD_LEAVE)) { // 通知成员离开对讲组
                JSONArray arr = data.optJSONArray(PoCMessage.FIELD_LEAVE);
                if (arr != null && arr.length() > 0) {
                    for (int i = 0; i < arr.length(); i++) {
                        memberListHelper.removeMember(arr.optString(i));
                    }
                }
            }
        }
    }

    // 语音对讲
    private void processInCall(Intent intent, SipInviteEventArgs args) {
        sipSession.setMicrophoneMute(true);
        sipSession.setSpeakerphoneOn(true);
        if (sipSession != null) {
            try {
                // 开始计时
                mTimerInCall.schedule(mTimerTaskInCall, 0, 1000);
            } catch (Exception ex) {
                Log.e(TAG, "" + ex.getMessage());
            }
        }
    }

    static final SimpleDateFormat sDurationTimerFormat = new SimpleDateFormat("mm:ss");

    private void updateTimeView() {
        final Date duration = new Date(new Date().getTime() - sipSession.getStartTime());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    timeLabel.setText(sDurationTimerFormat.format(duration));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    //////////////////////////////////////////////////////////////////////
    // time duration update

    /** 更新当前呼叫持续时长 */
    private Timer mTimerInCall = new Timer("PoC timer");

    private final TimerTask mTimerTaskInCall = new TimerTask(){
        @Override
        public void run() {
            updateTimeView();
        }
    };

    //////////////////////////////////////////////////////////////////////

}

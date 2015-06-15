package com.arcgis.emergency;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.arcgis.R;

import cn.showclear.sc_sip.SipContext;
import cn.showclear.sc_sip.SipInviteEventArgs;
import cn.showclear.sc_sip.SipMediaType;
import cn.showclear.sc_sip.SipRegistrationEventArgs;
import cn.showclear.sc_sip.SipSession;
import cn.showclear.sc_sip.msg.MessageData;
import cn.showclear.sc_sip.msg.MessageEventArgs;

public class SipCallBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = SipCallBroadcastReceiver.class.getCanonicalName();

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        final String action = intent.getAction();

        // Registration Events
        if(SipRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)) {
            onRegistrationEvent(intent);
        }
        // Invite Events
        else if(SipInviteEventArgs.ACTION_INVITE_EVENT.equals(action)) {
            onInviteEvent(intent);
        }
        // Message Events
        else if (MessageEventArgs.ACTION_RECEIVED.equals(action)) {
            onMessageEvent(intent);
        }
    }

    private void onMessageEvent(Intent intent) {
        MessageEventArgs args = intent.getParcelableExtra(MessageEventArgs.EXTRA_EMBEDDED);
        if(args == null){
            Log.e(TAG, "Invalid event args");
            return;
        }
        final SipContext sipContext = AppGlobal.getInstance().getSipContext();
        if (sipContext == null || !sipContext.isRegistered()) {
            return;
        }
        // 当接收到消息时
        MessageData[] datas = args.getDatas();
        //
        String title = "";
        String content = "";
        if (datas.length == 1) {
            title = "收到来自 " + datas[0].getSender() + "的消息";
            content = datas[0].getContent();
        } else {
            title = "收到 " + datas.length + " 条消息";
        }
        //
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MessagesActivity.class), 0))
                .setWhen(System.currentTimeMillis());
        Notification notification;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //低于4.1版本
            notification = builder.getNotification();
        } else {
            //4.1及以上版本才有
            notification = builder.build();
        }
        notificationManager.notify(123, notification);
    }

    private void onRegistrationEvent(Intent intent) {
        SipRegistrationEventArgs args = intent.getParcelableExtra(SipRegistrationEventArgs.EXTRA_EMBEDDED);
        if(args == null){
            Log.e(TAG, "Invalid event args");
            return;
        }
        switch(args.getEventType()){
            // 注册成功
            case REGISTRATION_OK:
            case REGISTRATION_NOK:
            case REGISTRATION_INPROGRESS:
            // 注销成功
            case UNREGISTRATION_OK:
            case UNREGISTRATION_INPROGRESS:
            case UNREGISTRATION_NOK:
            default:
                break;
        }
    }

    private void onInviteEvent(Intent intent) {
        SipInviteEventArgs args = intent.getParcelableExtra(SipInviteEventArgs.EXTRA_EMBEDDED);
        if (args == null) {
            Log.e(TAG, "Invalid event args");
            return;
        }

        final SipContext sipContext = AppGlobal.getInstance().getSipContext();
        if (sipContext == null || !sipContext.isRegistered()) {
            return;
        }

        final SipMediaType mediaType = args.getMediaType();

        switch (args.getEventType()) {
            // 挂机
            case TERMWAIT:
            case TERMINATED:
                Log.i(TAG, "onInviteEvent : TERMINATED");
                if (SipMediaType.isAudioVideoType(mediaType)) {
                    Log.i(TAG, "----------------------- stop ring-back-tone");
                    sipContext.stopRingBackTone();
                    sipContext.stopRingTone();
                }
                //
                break;

            // 收到呼入
            case INCOMING:
                if (SipMediaType.isAudioVideoType(mediaType)) {
                    final SipSession avSession = SipSession.getSession(args.getSessionId());
                    if (avSession != null) {
                        onReceiveCall(avSession, mediaType, args);
                        //  响铃
                        sipContext.startRingTone();
                        sipContext.setSpeakerphoneOn(true);
                    } else {
                        Log.e(TAG, String.format("Failed to find session with id=%d", args.getSessionId()));
                    }
                }
                break;

            // 正在处理呼出
            case INPROGRESS:
                if (SipMediaType.isAudioVideoType(mediaType)) {
                    final SipSession avSession = SipSession.getSession(args.getSessionId());
                    if (avSession != null) {
                        onOutgoingCall(avSession, mediaType, args);
                    } else {
                        Log.e(TAG, String.format("Failed to find session with id=%d", args.getSessionId()));
                    }
                }
                break;

            // 对方振铃中
            case RINGING:
                if (SipMediaType.isAudioVideoType(mediaType)) {
                    final SipSession avSession = SipSession.getSession(args.getSessionId());
                    // 回铃音
                    Log.i(TAG, "----------------------- play ring-back-tone");
                    // 音频通话时默认不启用外放（因为通话还未建立，还没有建立音频通道，所以这里调用session.setSpeakerphoneOn无效）
                    if (!avSession.isVideoCall() && !avSession.isPoC()) {
                        sipContext.setSpeakerphoneOn(false);
                    } else {
                        sipContext.setSpeakerphoneOn(true);
                    }
                    sipContext.startRingBackTone();
                }
                break;

            // 接起呼叫、数据连接建立
            case CONNECTED:
            case EARLY_MEDIA:
                if (SipMediaType.isAudioVideoType(mediaType)) {
                    final SipSession avSession = SipSession.getSession(args.getSessionId());
                    Log.i(TAG, "----------------------- stop ring-back-tone");
                    sipContext.stopRingBackTone();
                    sipContext.stopRingTone();
                    //
                    if (!avSession.isVideoCall() && !avSession.isPoC()) {
                        sipContext.setSpeakerphoneOn(false);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void onOutgoingCall(SipSession avSession, SipMediaType mediaType, SipInviteEventArgs args) {
        showCallActivity(avSession, mediaType, args);
    }

    private void onReceiveCall(SipSession avSession, SipMediaType mediaType, SipInviteEventArgs args) {
        showCallActivity(avSession, mediaType, args);
    }

    // 显示呼叫界面
    private void showCallActivity(SipSession avSession, SipMediaType mediaType, SipInviteEventArgs args) {
        //
        long id = avSession.getId();
        String tel = avSession.getRemotePartyDisplayName();

        String message = "";
        //
        Intent intent;
        // 语音对讲
        if (avSession.isPoC()) {
            intent = new Intent(context, PoCActivity.class);
            intent.putExtra(PoCActivity.EXTRA_ID , id );
            intent.putExtra(PoCActivity.EXTRA_TEL, tel);
            //
            message = "正在与" + tel + "对讲";
        } else {
            String action = CallActivity.ACTION_VIDEO;
            if (SipMediaType.isAudioType(mediaType)) {
                action = CallActivity.ACTION_VOICE;
                message = "正在与" + tel + "通话";
            }
            if (SipMediaType.isVideoType(mediaType)) {
                message = "正在与" + tel + "视频通话";
            }
            //
            intent = new Intent(context, CallActivity.class);
            intent.setAction(action);
            intent.putExtra(CallActivity.KEY_ID, id);
            intent.putExtra(CallActivity.KEY_TEL, tel);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        //
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}

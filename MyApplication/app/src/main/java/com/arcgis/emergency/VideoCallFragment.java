package com.arcgis.emergency;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.arcgis.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.showclear.sc_sip.SipSession;

/**
 * 视频呼叫
 * Created by jiangwj on 2015/1/27.
 */
public class VideoCallFragment extends Fragment implements ICallView {
    private static final String TAG = VideoCallFragment.class.getCanonicalName();

    private SipSession mSession;

    private FrameLayout largeVideo;
    private FrameLayout liteVideo;

    private TextView timeLabel;
    private Button toggleCameraButton;
    private Button muteButton;
    private Button speakerButton;
    private Button acceptButton;
    private Button hungupButton;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        largeVideo = (FrameLayout) view.findViewById(R.id.large_video);
        liteVideo = (FrameLayout) view.findViewById(R.id.lite_video);
        timeLabel = (TextView) view.findViewById(R.id.time);
        toggleCameraButton = (Button) view.findViewById(R.id.toggle_camera);
        toggleCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleCameraClicked();
            }
        });
        muteButton = (Button) view.findViewById(R.id.mute);
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleMuteClicked();
            }
        });
        speakerButton = (Button) view.findViewById(R.id.speaker);
        speakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleSpeakerClicked();
            }
        });
        acceptButton = (Button) view.findViewById(R.id.accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAcceptButtonClicked();
            }
        });
        hungupButton = (Button) view.findViewById(R.id.hungup);
        hungupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHungupButtonClicked();
            }
        });
        //
        updateView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    private void onHungupButtonClicked() {
        if (mSession != null) {
            mSession.hangUpCall();
        }
    }

    private void onAcceptButtonClicked() {
        if (mSession != null) {
            mSession.acceptCall();
        }
    }

    private void onToggleCameraClicked() {
        if (mSession != null) {
            mSession.toggleCamera();
        }
    }

    private void onToggleMuteClicked() {
        if (mSession != null) {
            mSession.setMicrophoneMute( !mSession.isMicrophoneMute() );
            updateMuteButton();
        }
    }

    private void updateMuteButton() {
        if (mSession.isMicrophoneMute()) {
            muteButton.setText("开启话筒");
        } else {
            muteButton.setText("关闭话筒");
        }
    }

    private void onToggleSpeakerClicked() {
        if (mSession != null) {
            mSession.setSpeakerphoneOn( !mSession.isSpeakerOn() );
            updateSpeakerButton();
        }
    }

    private void updateSpeakerButton() {
        if (mSession.isSpeakerOn()) {
            speakerButton.setText("关闭喇叭");
        } else {
            speakerButton.setText("开启喇叭");
        }
    }

    @Override
    public void updateView(Intent intent) {
        updateView();
    }

    @Override
    public void updateView() {
        if (mSession == null) return;
        if (getActivity() == null) return;
        if (getView() == null) return;

        acceptButton.setVisibility(View.GONE);

        switch(mSession.getState()) {
            case INCOMING:
            {
                acceptButton.setVisibility(View.VISIBLE);
                break;
            }
        }

        // Video Consumer
        loadRemoteVideoPreview();

        // Video Producer
        startStopVideo(mSession.isSendingVideo());

        updateMuteButton();
        updateSpeakerButton();
    }

    static final SimpleDateFormat sDurationTimerFormat = new SimpleDateFormat("mm:ss");

    @Override
    public void updateTimeView() {
        final Date duration = new Date(new Date().getTime() - mSession.getStartTime());
        // 更新通话时长
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        timeLabel.setText(sDurationTimerFormat.format(duration));
                    } catch (Exception e) {
                        // Ignore.
                    }
                }
            });
        }
    }

    @Override
    public void setSession(SipSession session) {
        this.mSession = session;
    }




    private void loadRemoteVideoPreview() {
        largeVideo.removeAllViews();
        final View remotePreview = mSession.startVideoConsumerPreview();
        if(remotePreview != null){
            final ViewParent viewParent = remotePreview.getParent();
            if(viewParent != null && viewParent instanceof ViewGroup){
                ((ViewGroup)(viewParent)).removeView(remotePreview);
            }
            largeVideo.addView(remotePreview);
            //
            if(remotePreview instanceof SurfaceView){
                ((SurfaceView)remotePreview).setZOrderOnTop(false);
            }
        }
        //
        liteVideo.bringToFront();
    }

    private void startStopVideo(boolean bStart) {
        Log.d(TAG, "startStopVideo(" + bStart + ")");
        if(!mSession.isVideoCall()){
            return;
        }
        //
        Log.d(TAG, "setSendingVideo(" + bStart + ")");
        mSession.setSendingVideo(bStart);

        if(liteVideo != null){
            liteVideo.removeAllViews();
            if(bStart) {
                mSession.setRotation(0);
                //
                final View localPreview = mSession.startVideoProducerPreview();
                if(localPreview != null){
                    final ViewParent viewParent = localPreview.getParent();
                    if(viewParent != null && viewParent instanceof ViewGroup){
                        ((ViewGroup)(viewParent)).removeView(localPreview);
                    }
                    if(localPreview instanceof SurfaceView){
                        ((SurfaceView)localPreview).setZOrderOnTop(true);
                    }
                    liteVideo.addView(localPreview);
                    liteVideo.bringChildToFront(localPreview);
                    //
                    //resizeLocalVideo();
                    localPreview.bringToFront();
                }
            }
            liteVideo.setVisibility(bStart ? View.VISIBLE : View.GONE);
            liteVideo.bringToFront();
        }
    }


}
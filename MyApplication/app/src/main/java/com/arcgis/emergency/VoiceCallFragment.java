package com.arcgis.emergency;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arcgis.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.showclear.sc_sip.SipSession;

/**
 * 音频呼叫
 * Created by jiangwj on 2015/1/27.
 */
public class VoiceCallFragment extends Fragment implements ICallView {
    private static final String TAG = VoiceCallFragment.class.getCanonicalName();

    private SipSession mSession;

    private TextView telLabel;
    private TextView timeLabel;
    private TextView callInfoLabel;
    private Button acceptButton;
    private Button hungupButton;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        telLabel = (TextView) view.findViewById(R.id.remote_tel);
        timeLabel = (TextView) view.findViewById(R.id.time);
        callInfoLabel = (TextView) view.findViewById(R.id.info);
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
        return inflater.inflate(R.layout.fragment_voice, container, false);
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

        String tel = mSession.getRemotePartyDisplayName();
        telLabel.setText(tel);

        switch(mSession.getState()){
            case INCOMING:
            {
                if (callInfoLabel != null) {
                    callInfoLabel.setText("新呼入");
                }
                acceptButton.setVisibility(View.VISIBLE);
                break;
            }
            case INCALL:
            case EARLY_MEDIA:
            {
                if (callInfoLabel != null) {
                    callInfoLabel.setText("正在通话");
                }
                break;
            }
            case INPROGRESS:
            case REMOTE_RINGING:
            default:
            {
                if (callInfoLabel != null) {
                    callInfoLabel.setText("正在拨号");
                }
                break;
            }
            case NONE:
            case TERMINATING:
            case TERMINATED:
            {
                if (callInfoLabel != null) {
                    callInfoLabel.setText("通话结束");
                }
                break;
            }
        }
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
}

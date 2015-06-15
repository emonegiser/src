package com.arcgis.emergency;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.arcgis.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.showclear.sc_sip.SipContext;
import cn.showclear.sc_sip.msg.MessageEventArgs;
import cn.showclear.sc_sip.msg.MessageManager;
import cn.showclear.utils.PhoneUtils;

public class MessagesActivity extends Activity {
    private static final String TAG = MessagesActivity.class.getCanonicalName();

    public static final int REQUEST_CODE_PICK_IMAGE = 100;
    public static final int REQUEST_CODE_PICK_VIDEO = 200;

    private ListView listView;
    private EditText telEdit;
    private EditText contentEdit;
    private Button sendButton;
    private Button pictureButton;
    private Button videoButton;

    private SipContext sipContext;

    private SimpleCursorAdapter adapter;

    private Uri storageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        //
        listView = (ListView) findViewById(R.id.listView);
        telEdit = (EditText) findViewById(R.id.to_tel);
        contentEdit = (EditText) findViewById(R.id.content);
        sendButton = (Button) findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendClicked();
            }
        });
        pictureButton = (Button) findViewById(R.id.picture);
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPictureButtonClicked();
            }
        });
        videoButton = (Button) findViewById(R.id.video);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVideoButtonClicked();
            }
        });
        //
        sipContext = AppGlobal.getInstance().getSipContext();
        //
        initBroadcast();
    }

    @Override
    protected void onDestroy() {
        destroyBroadcast();
        //
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        refreshListView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        //
        String remoteTel = telEdit.getText().toString();
        // 拍摄视频或照片的结果
        Uri uri = data == null ? (storageUri == null ? null : storageUri) : data.getData();
        File file = null;
        if (uri != null) {
            if ("file".equals(uri.getScheme())) {
                file = new File(uri.getPath());
            } else {
                String[] proj = { MediaStore.Images.Media.DATA };
                CursorLoader loader = new CursorLoader(this, uri, proj, null, null, null);
                Cursor cursor = loader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                file = new File(cursor.getString(column_index));
            }
        }
        //
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            sipContext.getMessageManager().sendImage(remoteTel, file, "something...");
        } else if (requestCode == REQUEST_CODE_PICK_VIDEO) {
            sipContext.getMessageManager().sendVideo(remoteTel, file, "addition information");
        }
        //
        scrollToBottom();
    }

    private void onSendClicked() {
        String tel = telEdit.getText().toString();
        String content = contentEdit.getText().toString();
        //
        if (TextUtils.isEmpty(tel) || TextUtils.isEmpty(content)) {
            return;
        }
        //
        sipContext.getMessageManager().sendMessage(tel, content);
        //
        refreshListView();
    }

    private void onVideoButtonClicked() {
        String tel = telEdit.getText().toString();
        if (TextUtils.isEmpty(tel)) {
            return;
        }
        // 上传视频录像
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        Button takeVideo = new Button(this);
        takeVideo.setText("拍摄");
        takeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 录像时间不建议过长，否则文件过大不利于传输
                PhoneUtils.takeVideo(MessagesActivity.this, null, REQUEST_CODE_PICK_VIDEO,
                        0, 10, 10 * 1024 * 1024); // 限制：低画质，10秒，10MB
                ((AlertDialog)v.getTag()).dismiss();
            }
        });
        content.addView(takeVideo);
        Button pickVideo = new Button(this);
        pickVideo.setText("录像");
        pickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtils.pickVideo(MessagesActivity.this, REQUEST_CODE_PICK_VIDEO);
                ((AlertDialog)v.getTag()).dismiss();
            }
        });
        content.addView(pickVideo);
        //
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setView(content)
                .setCancelable(true)
                .setNegativeButton("取消", null)
                .show();
        //
        takeVideo.setTag(dlg);
        pickVideo.setTag(dlg);
    }

    private void onPictureButtonClicked() {
        String tel = telEdit.getText().toString();
        if (TextUtils.isEmpty(tel)) {
            return;
        }
        // 先显示 从文件还是拍摄 的弹出框
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        Button takePhoto = new Button(this);
        takePhoto.setText("拍照");
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtils.takePhoto(MessagesActivity.this, storageUri = getPictureOutputUri(), REQUEST_CODE_PICK_IMAGE);
                ((AlertDialog)v.getTag()).dismiss();
            }
        });
        content.addView(takePhoto);
        Button pickPhoto = new Button(this);
        pickPhoto.setText("相册");
        pickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtils.pickPhoto(MessagesActivity.this, REQUEST_CODE_PICK_IMAGE);
                ((AlertDialog) v.getTag()).dismiss();
            }
        });
        content.addView(pickPhoto);
        //
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setView(content)
                .setCancelable(true)
                .setNegativeButton("取消", null)
                .show();
        //
        takePhoto.setTag(dlg);
        pickPhoto.setTag(dlg);
        // 上传图片
    }

    private Uri getPictureOutputUri() {
        File dir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "scooper"
        );
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return null;
            }
        }
        //
        String timestamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        return Uri.fromFile(new File(dir, "IMG_" + timestamp + ".jpg"));
    }

    private void refreshListView() {
        Cursor cursor = sipContext.getMessageManager().query(null, null, null, "timestamp ASC");
        //
        if (adapter == null) {
            adapter = new SimpleCursorAdapter(this,
                    R.layout.item_message_content,
                    cursor,
                    new String[] { "remote_tel", "message", "timestamp" },
                    new int[] { R.id.tel, R.id.content, R.id.time },
                    SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                private int typeIndex = -1;
                private int telIndex = -1;
                private int dirIndex = -1;
                private int msgIndex = -1;
                private int timeIndex = -1;
                private int localPathIndex = -1;
                private int sendedIndex = -1;
                final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    if (typeIndex < 0) {
                        typeIndex = cursor.getColumnIndex("type");
                        telIndex = cursor.getColumnIndex("remote_tel");
                        dirIndex = cursor.getColumnIndex("direct");
                        msgIndex = cursor.getColumnIndex("message");
                        timeIndex = cursor.getColumnIndex("timestamp");
                        localPathIndex = cursor.getColumnIndex("local_path");
                        sendedIndex = cursor.getColumnIndex("send");
                    }
                    if (telIndex == columnIndex) {
                        String tel = cursor.getString(columnIndex);
                        int direction = cursor.getInt(dirIndex);
                        String text;
                        if (direction == 0) {
                            text = "来自" + tel + "：";
                        } else {
                            text = "发往" + tel + "：";
                        }
                        ((TextView) view).setText(text);
                        return true;
                    } else if (timeIndex == columnIndex) {
                        long time = cursor.getLong(columnIndex);
                        ((TextView) view).setText(sdf.format(new Date(time)));
                        return true;
                    } else if (msgIndex == columnIndex) {
                        int type = cursor.getInt(typeIndex);
                        int direct = cursor.getInt(dirIndex);
                        int sended = cursor.getInt(sendedIndex);
                        String localPath = cursor.getString(localPathIndex);
                        //
                        StringBuilder textBuf = new StringBuilder();
                        if (type == MessageManager.MEDIA_TYPE_PICTURE) {
                            textBuf.append("[图片]");
                            return true;
                        } else if (type == MessageManager.MEDIA_TYPE_VIDEO) {
                            textBuf.append("[视频]");
                            return true;
                        }
                        //
                        if (textBuf.length() > 0) {
                            if (direct == MessageManager.MSG_DIR_OUT) { // 发送的消息
                                if (sended == 0) {
                                    textBuf.append(" 发送中");
                                } else {
                                    textBuf.append(" 已发送");
                                }
                            } else { // 接收的消息
                                if (TextUtils.isEmpty(localPath)) {
                                    textBuf.append(" 接收中");
                                } else {
                                    textBuf.append(" 已接收");
                                }
                            }
                            //
                            ((TextView) view).setText(textBuf.toString());
                        }
                    }
                    return false;
                }
            });
            listView.setAdapter(adapter);
        } else {
            adapter.swapCursor(cursor);
        }
    }

    private void scrollToBottom() {
        listView.setSelection(listView.getBottom());
    }

    private BroadcastReceiver broadcastReceiver;

    private void initBroadcast() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if (MessageEventArgs.ACTION_RECEIVED.equals(action)
                            || MessageEventArgs.ACTION_SENDED.equals(action)
                            || MessageEventArgs.ACTION_DOWNLOADED.equals(action)) {
                        refreshListView();
                        //
                        scrollToBottom();
                    }
                }
            };
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MessageEventArgs.ACTION_RECEIVED);
        intentFilter.addAction(MessageEventArgs.ACTION_SENDED);
        intentFilter.addAction(MessageEventArgs.ACTION_DOWNLOADED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void destroyBroadcast() {
        unregisterReceiver(broadcastReceiver);
    }
}

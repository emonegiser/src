package com.arcgis.photo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mars on 2015/2/9.
 */
public class PhotoActivity extends Activity {
    private File file;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startPhoto();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startPhoto(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // sd卡路径
            String saveDir = Environment.getExternalStorageDirectory() + "/image";
            System.out.println("正常+++++++++++++++++++++++++");
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(saveDir, formatter.format(date) + ".jpg");
            file.delete();
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file));
            startActivityForResult(intent, 1);
        } else {
        }
    }

}

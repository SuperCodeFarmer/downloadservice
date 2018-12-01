package com.example.downloadactivity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by yly on 2018/12/1.
 */

public class DownloadService extends Service {

    private MyBinder myBinder=new MyBinder();
    class MyBinder extends Binder{
        public void startDownload(String downloadUrl){

        }
        public void pauseDownload(){

        }
        public void cancelDownload(){

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
}

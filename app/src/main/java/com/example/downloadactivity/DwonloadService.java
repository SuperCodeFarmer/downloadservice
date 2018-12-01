package com.example.downloadactivity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by yly on 2018/12/1.
 */

public class DwonloadService extends Service {

    private MyBinder myBinder=new MyBinder();
    class MyBinder extends Binder{
        private void startDownload(){

        }
        private void pauseDownload(){

        }
        private void cancelDownload(){

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
}

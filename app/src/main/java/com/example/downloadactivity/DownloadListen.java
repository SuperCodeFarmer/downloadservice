package com.example.downloadactivity;

/**
 * Created by yly on 2018/12/1.
 */

public interface DownloadListen {
    void onProgress(int progresss);
    void onSuccess();
    void onPaused();
    void onFailed();
    void onCanceled();
}

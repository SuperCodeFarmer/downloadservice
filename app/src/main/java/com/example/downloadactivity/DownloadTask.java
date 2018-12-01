package com.example.downloadactivity;

import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.DownloadListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.PublicKey;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yly on 2018/12/1.
 */

public class DownloadTask extends AsyncTask<String,Integer,Integer> {

    public static final int TYPR_SUCCESS=0;
    public static final int TYPR_PAUSED=2;
    public static final int TYPR_FAILED=1;
    public static final int TYPR_CANCELED=3;

    private boolean isCanceled=false;
    private boolean isPaused=false;

    private DownloadListen listener;
    public DownloadTask (DownloadListen listener){
        this.listener=listener;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        long contentLength=0;
        long downloadedLength=0;
        File file=null;
        Response response=null;
        RandomAccessFile savedFile=null;
        InputStream is=null;
        String downloadUrl=strings[0];
        try{
            String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory= Environment.getExternalStoragePublicDirectory(Environment
                    .DIRECTORY_DOWNLOADS).getPath();
            file=new File(fileName+directory);
            if (file.exists()){
                downloadedLength=file.length();
            }
            //得到文件的总长度
            contentLength=getContentLength(downloadUrl);
            if (contentLength==0){
                return TYPR_FAILED;
            }else if (contentLength==downloadedLength){
                return TYPR_SUCCESS;
            }
            //开始正式下载
            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder()
                    .addHeader("RANGE","bytes="+downloadedLength+"-")
                    .url(downloadUrl)
                    .build();
            response=client.newCall(request).execute();
            if (response!=null){
                is=response.body().byteStream();
                savedFile=new RandomAccessFile(file,"rw");
                savedFile.seek(downloadedLength);
                byte[] b=new byte[1024];
                int total=0;
                int len;
                while((len=is.read(b))!=-1){
                    if (isCanceled){
                        return TYPR_CANCELED;
                    }else if (isPaused){
                        return TYPR_PAUSED;
                    }else{
                        total+=len;
                        savedFile.write(b,0,len);
                        int progress=(int)((total+downloadedLength)*100/contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPR_SUCCESS;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (is!=null){
                    is.close();
                }
                if (savedFile!=null){
                    savedFile.close();
                }
                if (isCanceled&&file!=null){
                    file.delete();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return TYPR_FAILED;
    }



    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case TYPR_SUCCESS:
                listener.onSuccess();
                break;
            case TYPR_FAILED:
                listener.onFailed();
                break;
            case TYPR_CANCELED:
                listener.onCanceled();
                break;
            case TYPR_PAUSED:
                listener.onPaused();
                break;
            default:
                break;
        }
    }

    public void pauseDownload(){
        isPaused=true;
    }
    public void cancelDownload(){
        isCanceled=true;
    }

    private long getContentLength(String downloadUrl) throws IOException{
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response=client.newCall(request).execute();
        if (response!=null&&response.isSuccessful()){
            long contentLength=response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }
}

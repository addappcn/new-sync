package com.newsync;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by qgswsg on 2018/3/13.
 */

public class CheckUpdate {

    public class Version {
        private int versionCode;
        private String versionName;
        private String updateContent;
        private String updateURL;

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getUpdateContent() {
            return updateContent;
        }

        public void setUpdateContent(String updateContent) {
            this.updateContent = updateContent;
        }

        public String getUpdateURL() {
            return updateURL;
        }

        public void setUpdateURL(String updateURL) {
            this.updateURL = updateURL;
        }
    }

    public void check(Handler handler) {
        getLatestVersion(version -> {
            if (BuildConfig.VERSION_CODE >= version.getVersionCode()) {
                handler.sendEmptyMessage(0);
            } else {
                Message message = Message.obtain();
                message.obj = version;
                message.what = 1;
                handler.sendMessage(message);
            }
        });
    }

    public void startUpdate(Context context, String updateURL) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(updateURL);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationInExternalPublicDir("Download/" + context.getPackageName(), "install.apk");
        BaseApplication app = (BaseApplication) context.getApplicationContext();
        app.downloadId = downloadManager.enqueue(request);
    }

    public void getLatestVersion(CallBack<Version> callBack) {
        Executors.newCachedThreadPool().execute(() -> {
            try {
                URL url = new URL("https://raw.githubusercontent.com/qgswsg/new-sync/master/version");
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = httpsURLConnection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buff)) != -1) {
                    byteArrayOutputStream.write(buff, 0, len);
                }
                String result = byteArrayOutputStream.toString();
                Gson gson = new Gson();
                Version version = gson.fromJson(result, Version.class);
                callBack.run(version);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

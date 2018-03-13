package com.newsync;

import android.app.DownloadManager;
import android.content.Context;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by qgswsg on 2018/3/13.
 */

public class CheckUpdate {

    class Version{
        private int versionCode;
        private String versionName;
        private String updateContent;

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
    }

    public boolean check(){
        Version latestVersion = getLatestVersion();
        if (latestVersion == null){
            return false;
        }
        System.out.println(latestVersion.getVersionCode() + " " + latestVersion.getVersionName() + " " + latestVersion.getUpdateContent());
        return BuildConfig.VERSION_CODE == latestVersion.getVersionCode();
    }

    public void startUpdate(Context context){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    private Version getLatestVersion() {
        Version version = null;
        try {
            URL url = new URL("https://raw.githubusercontent.com/qgswsg/new-sync/master/version");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            InputStream inputStream = httpsURLConnection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buff)) != -1){
                byteArrayOutputStream.write(buff,0,len);
            }
            String result = byteArrayOutputStream.toString();
            System.out.println(result);
            Gson gson = new Gson();
            version = gson.fromJson(result, Version.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }
}

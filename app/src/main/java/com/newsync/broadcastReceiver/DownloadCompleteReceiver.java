package com.newsync.broadcastReceiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.newsync.BaseApplication;

import java.io.File;
import java.io.FileNotFoundException;

public class DownloadCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        BaseApplication app = (BaseApplication) context.getApplicationContext();
        if (ID == app.downloadId) {
            installApk(context);
        }
    }

    private void installApk(Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), context.getPackageName() + "/install.apk");
        System.out.println(file.getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        }
        intent.setDataAndType(data,
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

//        File file= new File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                , "install.apk");
//        System.out.println(file.getAbsolutePath());
//        //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
//        Uri apkUri =
//                FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
//
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        // 由于没有在Activity环境下启动Activity,设置下面的标签
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        //添加这一句表示对目标应用临时授权该Uri所代表的文件
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//        context.startActivity(intent);
    }
}

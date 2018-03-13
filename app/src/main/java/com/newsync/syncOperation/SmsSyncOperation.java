package com.newsync.syncOperation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.Telephony;

import com.newsync.CallBack;
import com.newsync.activity.MainActivity;
import com.newsync.data.DataModelBase;
import com.newsync.data.Sms;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by qgswsg on 2018/3/8.
 */

public class SmsSyncOperation implements SyncOperation {

    private Context context;
    private List<Integer> cloudHashcodeList = new ArrayList<>();
    private List<Integer> localHashcodeList = new ArrayList<>();

    public SmsSyncOperation(Context context) {
        this.context = context;
    }

    @Override
    public void computeLocalMore(CallBack<? super DataModelBase> singleCallBack) {
        cloudHashcodeList.clear();
        Cursor query = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, "type in (1,2)", null, "date DESC");
        try {
            if (query.moveToFirst()) {
                do {
                    Sms sms = new Sms();
                    sms.initialize(query);
                    if (cloudHashcodeList.isEmpty()) {
                        List<Sms> all = DataSupport.findAll(Sms.class);
                        for (Sms s : all) {
                            cloudHashcodeList.add(s.getHashCode());
                        }
                    }
                    if (!cloudHashcodeList.contains(sms.getHashCode())) {
                        singleCallBack.run(sms);
                    }
                } while (query.moveToNext());
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        query.close();
    }

    @Override
    public void computeCloudMore(CallBack<? super DataModelBase> singleCallBack) {
        localHashcodeList.clear();
        Cursor bySQL = DataSupport.findBySQL("SELECT * FROM Sms WHERE type IN (1,2) ORDER BY date DESC");
        try {
            if (bySQL.moveToFirst()) {
                do {
                    Sms sms = new Sms();
                    sms.initializeFromCloud(bySQL);
                    if (localHashcodeList.isEmpty()) {
                        Cursor query = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);
                        if (query.moveToFirst()) {
                            do {
                                Sms s = new Sms();
                                s.initialize(query);
                                localHashcodeList.add(s.getHashCode());
                            } while (query.moveToNext());
                        }
                        query.close();
                    }
                    if (!localHashcodeList.contains(sms.getHashCode())) {
                        singleCallBack.run(sms);
                    }
                } while (bySQL.moveToNext());
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        bySQL.close();
    }

    @Override
    public void download(List<Integer> idList, Handler handler) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String packageName = context.getPackageName();
            //获取手机当前设置的默认短信应用的包名
            String defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(context);
            if (!defaultSmsPackage.equals(packageName)) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
                ((Activity) context).startActivityForResult(intent, 58);
            }
            ((MainActivity) context).writeSms = () -> {
                Executors.newCachedThreadPool().execute(() -> {
                    for (int id : idList) {
                        Sms sms = DataSupport.find(Sms.class, (long) id);
                        context.getContentResolver().insert(Telephony.Sms.CONTENT_URI, sms.getContentValues());
                    }
                    handler.sendEmptyMessage(101);
                });
            };
        }
    }

    @Override
    public void delete(List<Integer> idList, Handler handler) {
        new AlertDialog.Builder(context)
                .setTitle("删除警告")
                .setMessage("删除后将不可撤回，是否继续")
                .setNegativeButton("继续", (dialogInterface, i) -> {
                    Executors.newCachedThreadPool().execute(() -> {
                        for (int id : idList) {
                            Sms sms = DataSupport.find(Sms.class, (long) id);
                            sms.delete();
                        }
                        handler.sendEmptyMessage(0);
                    });
                })
                .setPositiveButton("取消", (dialogInterface, i) -> {
                    handler.sendEmptyMessage(0);
                }).show();
    }

//    @Override
//    public void clearCache() {
//        cloudHashcodeList.clear();
//        localHashcodeList.clear();
//    }
}

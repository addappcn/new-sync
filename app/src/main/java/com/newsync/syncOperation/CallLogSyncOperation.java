package com.newsync.syncOperation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;

import com.newsync.CallBack;
import com.newsync.data.CallLogs;
import com.newsync.data.DataModelBase;
import com.newsync.data.Sms;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by qgswsg on 2018/3/8.
 */

public class CallLogSyncOperation implements SyncOperation {

    private Context context;
    private List<Integer> cloudHashcodeList = new ArrayList<>();
    private List<Integer> localHashcodeList = new ArrayList<>();

    public CallLogSyncOperation(Context context) {
        this.context = context;
    }

    @Override
    public void computeLocalMore(CallBack<? super DataModelBase> singleCallBack) {
        cloudHashcodeList.clear();
        Cursor query = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, "date DESC");
        try {
            if (query.moveToFirst()) {
                do {
                    CallLogs callLogs = new CallLogs();
                    callLogs.initialize(query);
                    if (cloudHashcodeList.isEmpty()) {
                        List<CallLogs> all = DataSupport.findAll(CallLogs.class);
                        for (CallLogs s : all) {
                            cloudHashcodeList.add(s.getHashCode());
                        }
                    }
                    if (!cloudHashcodeList.contains(callLogs.getHashCode())) {
                        singleCallBack.run(callLogs);
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
        Cursor bySQL = DataSupport.findBySQL("SELECT * FROM CallLogs ORDER BY date DESC");
        try {
            if (bySQL.moveToFirst()) {
                do {
                    CallLogs callLogs = new CallLogs();
                    callLogs.initializeFromCloud(bySQL);
                    if (localHashcodeList.isEmpty()) {
                        Cursor query = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
                        if (query.moveToFirst()) {
                            do {
                                CallLogs c = new CallLogs();
                                c.initialize(query);
                                localHashcodeList.add(c.getHashCode());
                            } while (query.moveToNext());
                        }
                    }
                    if (!localHashcodeList.contains(callLogs.getHashCode())) {
                        singleCallBack.run(callLogs);
                    }
                } while (bySQL.moveToNext());
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
        bySQL.close();
    }

    @Override
    public void download(List<Integer> idList, Handler handler) {
        Executors.newCachedThreadPool().execute(() -> {
            for (int id : idList) {
                CallLogs ts = DataSupport.find(CallLogs.class, (long) id);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, ts.getContentValues());
            }
            handler.sendEmptyMessage(0);
        });
    }

    @Override
    public void delete(List<Integer> idList, Handler handler) {
        new AlertDialog.Builder(context)
                .setTitle("删除警告")
                .setMessage("删除后将不可撤回，是否继续")
                .setNegativeButton("继续", (dialogInterface, i) -> {
                    Executors.newCachedThreadPool().execute(() -> {
                        for (int id : idList) {
                            CallLogs ts = DataSupport.find(CallLogs.class, (long) id);
                            ts.delete();
                        }
                        handler.sendEmptyMessage(0);
                    });
                })
                .setPositiveButton("取消", (dialogInterface, i) -> {
                    handler.sendEmptyMessage(1);
                })
                .show();
    }

//    @Override
//    public void clearCache() {
//        cloudHashcodeList.clear();
//        localHashcodeList.clear();
//    }
}

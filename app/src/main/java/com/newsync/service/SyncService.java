package com.newsync.service;

import android.Manifest;
import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.newsync.BaseApplication;
import com.newsync.Config;
import com.newsync.activity.TranslucentActivity;
import com.newsync.content_observer.mContentObserver;


public class SyncService extends Service {

    // Storage for an instance of the sync adapter
    private static SyncAdapter sSyncAdapter = null;
    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();

    public SyncService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }

    class SyncAdapter extends AbstractThreadedSyncAdapter {

        private BaseApplication app;
        private boolean wake_up = false;

        public SyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
//            Intent intent = new Intent(getApplicationContext(), EmptyActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getApplication().startActivity(intent);
            Log.i("Tag", "被系统唤醒");
            wake_up = true;
            app = (BaseApplication) getApplicationContext();
            mContentObserver contentObserver = new mContentObserver(getApplicationContext());
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                getContext().getContentResolver().registerContentObserver(Telephony.Sms.CONTENT_URI, true, contentObserver);
            }
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, contentObserver);
            }
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contentObserver);
            }
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            if (app.syncing) return;
            if (!Config.getInstance(getContext()).getSyncStatus() || wake_up || extras.getString("key") != null || Config.getInstance(getContext()).canSync()) {
                //如果用户上次没有同步完成
                //如果程序是刚被唤醒
                //如果监听数据发生变化
                //如果同步时机合适(下午四点之后，而恰好今天又没有进行过同步)
                Log.i("Tag", "开始同步");
                Config.getInstance(getContext()).setSyncStatus(false);
                if (Config.getInstance(getContext()).isSyncByOnlyWifi() && !app.isWifi()) {
                    //如果用户设置只在wifi环境同步，而当前环境不是wifi时不进行同步
                    if (app.mainFragementHandler != null){
                        app.mainFragementHandler.sendEmptyMessage(5005);
                    }
                    return;
                }
                try {
                    app.syncSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(android.os.Process.myPid());
                app.syncing = true;
                Intent intent = new Intent(getApplicationContext(), TranslucentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
            wake_up = false;
        }
    }
}

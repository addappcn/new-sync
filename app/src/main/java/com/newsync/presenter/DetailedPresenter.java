package com.newsync.presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;

import com.newsync.CallBack;
import com.newsync.R;
import com.newsync.data.CallLogs;
import com.newsync.data.Contacts;
import com.newsync.data.ListItem;
import com.newsync.data.Sms;
import com.newsync.syncOperation.CallLogSyncOperation;
import com.newsync.syncOperation.ContactsSyncOperation;
import com.newsync.syncOperation.SmsSyncOperation;
import com.newsync.syncOperation.SyncOperation;
import com.newsync.view.BaseView;
import com.newsync.view.ItemFragmentView;

import org.litepal.crud.DataSupport;


/**
 * Created by qgsws on 2018/2/26.
 */

public class DetailedPresenter implements BasePresenter {

    private int position;
    private Context context;
    public int[] drawableIds;
    private String type;
    private SyncOperation syncOperation;
    private String[] titles = new String[]{"短信", "联系人", "通话记录"};

    public SyncOperation getSyncOperation() {
        return syncOperation;
    }


    public DetailedPresenter(Context context, int position) {
        this.context = context;
        this.position = position;
    }

    public String getPhoneCount(Context context, int position) {
        String result = "没有权限";
        switch (position) {
            case 0:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Cursor smsCursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, "type in (1,2)", null, null);
                    if (smsCursor != null) {
                        result = "本机：" + smsCursor.getCount();
                    }
                }
                break;
            case 1:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    Cursor contactsCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                    if (contactsCursor != null) {
                        result = "本机：" + contactsCursor.getCount();
                    }
                }
                break;
            case 2:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    Cursor callLogCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
                    if (callLogCursor != null) {
                        result = "本机：" + callLogCursor.getCount();
                    }
                }
                break;
        }
        return result;
    }

    public String getCloudCount(int position) {
        String result = "云端：";
        switch (position) {
            case 0:
                result += DataSupport.count(Sms.class);
                break;
            case 1:
                result += DataSupport.count(Contacts.class);
                break;
            case 2:
                result += DataSupport.count(CallLogs.class);
                break;
        }
        return result;
    }

    @Override
    public void setView(BaseView baseView) {

    }

    public void initSync() {
        SyncOperation syncOperation = null;
        switch (position) {
            case 0:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                    syncOperation = new SmsSyncOperation(context);
                }
                break;
            case 1:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    syncOperation = new ContactsSyncOperation(context);
                }
                break;
            case 2:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    syncOperation = new CallLogSyncOperation(context);
                }
                break;
        }
        this.syncOperation = syncOperation;
    }

    public String getTitle() {
        return titles[position];
    }

    public void initIcon() {
        switch (position) {
            case 0:
                drawableIds = new int[2];
                drawableIds[0] = R.drawable.ic_inbox;
                drawableIds[1] = R.drawable.ic_outbox;
                break;
            case 2:
                drawableIds = new int[3];
                drawableIds[0] = R.drawable.ic_call_received;
                drawableIds[1] = R.drawable.ic_call_made;
                drawableIds[2] = R.drawable.ic_call_missed;
                break;
        }
    }

    public int getIconId(int type) {
        if (drawableIds == null) return 0;
        if (drawableIds.length <= type - 1) return 0;
        if (type == 0) return 0;
        return drawableIds[type - 1];
    }

    public void initMessage() {
        switch (position) {
            case 0:
                type = "类型：短信\n通信对象：{name}\n\n内容：{context}\n\n时间：{date}";
                break;
            case 1:
                type = "类型：联系人\n联系姓名：{name}\n电话号码：{context}";
                break;
            case 2:
                type = "类型：通话记录\n通话对象：{name}\n地区：{context}\n时间：{date}";
                break;
        }
    }

    public String getMessage(ListItem listItem) {
        String message = type.replace("{name}", listItem.getTitle() + "")
                .replace("{context}", listItem.getContext() + "")
                .replace("{date}", listItem.getDate() + "");
        return message;
    }

    public void init() {
        initSync();
        initIcon();
        initMessage();
    }

    public String[] getButtonText(int tag) {
        switch (tag) {
            case 0:
                return new String[]{"", ""};
            case 1:
                return new String[]{"下载", "删除"};
        }
        throw new RuntimeException();
    }


}

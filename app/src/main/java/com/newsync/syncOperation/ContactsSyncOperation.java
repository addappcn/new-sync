package com.newsync.syncOperation;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import com.newsync.CallBack;
import com.newsync.data.*;
import com.newsync.data.Contacts;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * Created by qgswsg on 2018/3/8.
 */

public class ContactsSyncOperation implements SyncOperation {

    private Context context;
    private List<String> cloudLookupList = new ArrayList<>();
    private List<String> localLookupList = new ArrayList<>();

    public ContactsSyncOperation(Context context) {
        this.context = context;
    }

    @Override
    public void computeLocalMore(CallBack<? super DataModelBase> singleCallBack) {
        cloudLookupList.clear();
        Cursor query = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts.NAME_RAW_CONTACT_ID, ContactsContract.Contacts.LOOKUP_KEY}, null, null, null);
        try {
            if (query.moveToFirst()) {
                do {
                    Contacts contacts = new Contacts();
                    contacts.initialize(query);
                    if (cloudLookupList.isEmpty()) {
                        List<Contacts> all = DataSupport.findAll(Contacts.class);
                        for (Contacts t : all) {
                            cloudLookupList.add(t.getLookup());
                        }
                    }
                    if (!cloudLookupList.contains(contacts.getLookup())) {
                        singleCallBack.run(contacts);
                    }
                } while (query.moveToNext());
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        query.close();
    }

    public void uploadLocalMore() {
        localLookupList.clear();
        computeLocalMore(contacts -> {
            Cursor dataCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                    null, ContactsContract.Data.RAW_CONTACT_ID + " = ?",
                    new String[]{((Contacts) contacts).getName_raw_contact_id() + ""}, null);
            if (dataCursor.moveToFirst()) {
                do {
                    Data data = new Data();
                    data.initialize(dataCursor);
                    data.save();
                } while (dataCursor.moveToNext());
            }
            Cursor rawContactsCursor = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                    null, ContactsContract.RawContacts.CONTACT_ID + " = ?",
                    new String[]{((Contacts) contacts).getName_raw_contact_id() + ""}, null);
            if (rawContactsCursor.moveToFirst()) {
                Raw_Contacts raw_contacts = new Raw_Contacts();
                raw_contacts.initialize(rawContactsCursor);
                raw_contacts.save();
            }
            contacts.save();
        });
    }

    @Override
    public void computeCloudMore(CallBack<? super DataModelBase> singleCallBack) {
        Cursor bySQL = DataSupport.findBySQL("SELECT * FROM Contacts");
        try {
            if (bySQL.moveToFirst()) {
                do {
                    Contacts contacts = new Contacts();
                    contacts.initializeFromCloud(bySQL);
                    if (localLookupList.isEmpty()) {
                        Cursor query = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts.LOOKUP_KEY}, null, null, null);
                        int lookupColumnIndex = query.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
                        if (query.moveToFirst()) {
                            do {
                                localLookupList.add(query.getString(lookupColumnIndex));
                            } while (query.moveToNext());
                        }
                        query.close();
                    }
                    if (!localLookupList.contains(contacts.getLookup())) {
                        singleCallBack.run(contacts);
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
        Executors.newCachedThreadPool().execute(() -> {
            for (int id : idList) {
                List<Raw_Contacts> raw_contactsList = DataSupport.where("raw_contacts_id = ?", id + "").find(Raw_Contacts.class);
                if (raw_contactsList.isEmpty()) return;
                Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, raw_contactsList.get(0).getContentValues());
                long rawContactId = ContentUris.parseId(rawContactUri);
                List<Data> datas = DataSupport.where(ContactsContract.Data.RAW_CONTACT_ID + " = ?", id + "").find(Data.class);
                for (Data data : datas) {
                    data.setRaw_contact_id((int) rawContactId);
                    context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, data.getContentValues());
                }
                List<Contacts> ts = DataSupport.where("name_raw_contact_id = ?", id + "").find(Contacts.class);
                Cursor query = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts.NAME_RAW_CONTACT_ID,
                                ContactsContract.Contacts.LOOKUP_KEY},
                        ContactsContract.Contacts.NAME_RAW_CONTACT_ID + " = ?", new String[]{rawContactId + ""}, null);
                int columnIndex = query.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
                if (query.moveToFirst()) {
                    ts.get(0).setLookup(query.getString(columnIndex));
                    ts.get(0).save();
                }
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
                            List<Raw_Contacts> raw_contactsList = DataSupport.where("raw_contacts_id = ?", id + "").find(Raw_Contacts.class);
                            for (Raw_Contacts raw_contacts : raw_contactsList) {
                                raw_contacts.delete();
                            }
                            List<Data> datas = DataSupport.where(ContactsContract.Data.RAW_CONTACT_ID + " = ?", id + "").find(Data.class);
                            for (Data data : datas) {
                                data.delete();
                            }
                            List<Contacts> ts = DataSupport.where("name_raw_contact_id = ?", id + "").find(Contacts.class);
                            for (Contacts contacts : ts) {
                                contacts.delete();
                            }
                        }
                        handler.sendEmptyMessage(0);
                    });
                })
                .setPositiveButton("取消", null)
                .show();
    }
}

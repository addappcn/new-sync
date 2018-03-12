package com.newsync;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.service.autofill.Dataset;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.newsync.data.CallLogs;
import com.newsync.data.Contacts;
import com.newsync.data.Data;
import com.newsync.data.DataModelBase;
import com.newsync.data.ListItem;
import com.newsync.data.Raw_Contacts;
import com.newsync.data.Sms;
import com.onedrive.sdk.extensions.IOneDriveClient;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Created by qgswsg on 2018/2/13.
 */

class Sync<T extends DataModelBase> {
    private Class<T> clazz;
    private Context context;
    private Uri uri;
    public String defaultSmsPackage;
    private List<Integer> localHashCodeList = new ArrayList<>();
    private List<Integer> cloudHashCodeList = new ArrayList<>();
    private List<String> localLookupList = new ArrayList<>();
    private List<String> cloudLookupList = new ArrayList<>();

    public Sync(Context context, Uri uri, Class<T> clazz) {
        this.context = context;
        this.clazz = clazz;
        this.uri = uri;
    }

    /**
     * 检查本地未上传的记录，如果存在记录未上传
     */
    public void loadLocalMore() {
        loadLocalMore(save -> save.save());
    }

    public synchronized void loadLocalMore(CallBack<T> saveCallBack) {
        String selection = null;
        String sortOrder = "date DESC";
        if (clazz == Sms.class) {
            selection = "type in (1,2)";
        } else if (clazz == Contacts.class || clazz == Raw_Contacts.class || clazz == Data.class) {
            sortOrder = "_id DESC";
        }
        Cursor query = context.getContentResolver().query(uri, null, selection, null, sortOrder);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    do {
                        T lData = clazz.newInstance();
                        lData.initialize(query);
                        if (clazz == Contacts.class) {
                            if (cloudLookupList.isEmpty()) {
                                List<Contacts> ts = DataSupport/*.where("lookup = ?", ((Contacts) lData).getLookup() + "")*/.findAll(Contacts.class);
                                for (Contacts contacts : ts) {
                                    cloudLookupList.add(contacts.getLookup());
                                }
                                if (!cloudLookupList.contains(((Contacts) lData).getLookup())) {
                                    saveCallBack.run(lData);
                                }
                            } else {
                                if (!cloudLookupList.contains(((Contacts) lData).getLookup())) {
                                    saveCallBack.run(lData);
                                }
                            }
                        } else {
                            if (cloudHashCodeList.isEmpty()) {
                                List<T> ts = DataSupport/*.where("hashcode = ?", lData.getHashCode() + "")*/.findAll(clazz);
                                for (T t : ts) {
                                    cloudHashCodeList.add(t.getHashCode());
                                }
                                if (!cloudHashCodeList.contains(lData.getHashCode())) {
                                    saveCallBack.run(lData);
                                }
                            } else {
                                if (!cloudHashCodeList.contains(lData.getHashCode())) {
                                    saveCallBack.run(lData);
                                }
                            }
                        }
                    } while (query.moveToNext());
                }
                query.close();
            } catch (IllegalAccessException e) {
                query.close();
                return;
            } catch (InstantiationException e) {
                query.close();
                return;
            } catch (NullPointerException e) {
                query.close();
                return;
            }
        }
    }

    public synchronized void loadCloudMore(CallBack<T> saveCallBack) {
        if (clazz == Contacts.class) {
            readData("*", bySQL -> {
                if (bySQL != null) {
                    try {
                        if (bySQL.moveToFirst()) {
                            do {
                                Contacts cData = new Contacts();
                                cData.initializeFromCloud(bySQL);
                                if (localLookupList.isEmpty()) {
                                    Cursor query = context.getContentResolver().query(uri,
                                            new String[]{ContactsContract.Contacts.LOOKUP_KEY}, null, null
                                            /*ContactsContract.Contacts.LOOKUP_KEY + " = ?",
                                            new String[]{((Contacts) cData).getLookup()}*/, null);
                                    if (query != null) {
                                        int lookupColumnIndex = query.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
                                        if (query.moveToFirst()) {
                                            do {
                                                localLookupList.add(query.getString(lookupColumnIndex));
                                            } while (query.moveToNext());
                                            if (!localLookupList.contains(cData.getLookup())) {
                                                saveCallBack.run((T) cData);
                                            }
                                        }
                                    } else {
                                        saveCallBack.run((T) cData);
                                    }
                                    query.close();
                                } else {
                                    if (!localLookupList.contains(cData.getLookup())) {
                                        saveCallBack.run((T) cData);
                                    }
                                }
                            } while (bySQL.moveToNext());
                        }
                        bySQL.close();
                    } catch (NullPointerException e) {
                        bySQL.close();
                        return;
                    }
                }
            });
        } else {
            readData("*", bySQL -> {
                if (bySQL != null) {
                    try {
                        if (bySQL.moveToFirst()) {
                            do {
                                T cData = clazz.newInstance();
                                cData.initializeFromCloud(bySQL);
                                if (localHashCodeList.isEmpty()) {
                                    Cursor query = context.getContentResolver().query(uri, null, null, null, null);
                                    if (query != null) {
                                        if (query.moveToFirst()) {
                                            do {
                                                T t = clazz.newInstance();
                                                t.initialize(query);
                                                localHashCodeList.add(t.getHashCode());

                                            } while (query.moveToNext());
                                        }
                                        if (!localHashCodeList.contains(cData.getHashCode())) {
                                            saveCallBack.run(cData);
                                        }
                                    } else {
                                        saveCallBack.run(cData);
                                    }
                                    query.close();
                                } else {
                                    if (!localHashCodeList.contains(cData.getHashCode())) {
                                        saveCallBack.run(cData);
                                    }
                                }
                            } while (bySQL.moveToNext());
                        }
                        bySQL.close();
                    } catch (InstantiationException e) {
                        bySQL.close();
                        return;
                    } catch (IllegalAccessException e) {
                        bySQL.close();
                        return;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        bySQL.close();
                        return;
                    }
                }
            });
        }
    }


    private void readData(String projection, CallBack<Cursor> callBack) {
        String order = " ORDER BY date DESC";
        String selection = "";
        if (clazz == Sms.class) {
            selection = "Where type in (1,2) ";
        } else if (clazz == Contacts.class) {
            order = " ORDER BY contacts_id DESC";
        }
        Cursor bySQL = DataSupport.findBySQL("select " + projection + " from " + clazz.getSimpleName() + " " + selection + order);
        callBack.run(bySQL);
        bySQL.close();
    }

    public boolean download(int id) {
        if (clazz == Sms.class) {
            List<Sms> ts = DataSupport.where("sms_id = ?", id + "").order("sms_id DESC").find(Sms.class);
            if (ts.isEmpty()) return false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                String packageName = context.getPackageName();
                //获取手机当前设置的默认短信应用的包名
                defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(context);
                if (!defaultSmsPackage.equals(packageName)) {
                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
                    context.startActivity(intent);
                }
            }
            context.getContentResolver().insert(uri, ts.get(0).getContentValues());
            //短信要系统默认短信应用才能写
        } else if (clazz == Contacts.class) {
//            List<Contacts> ts = DataSupport.where("contacts_id = ?", id + "").find(Contacts.class);
//            if (ts.isEmpty()) return false;
            List<Raw_Contacts> raw_contactsList = DataSupport.where("raw_contacts_id = ?", id + "").find(Raw_Contacts.class);
            Log.i("Tag", raw_contactsList.get(0).getDisplay_name());
            if (raw_contactsList.isEmpty()) return false;
            Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, raw_contactsList.get(0).getContentValues());
            long rawContactId = ContentUris.parseId(rawContactUri);
            Log.i("rawContactUri:", rawContactUri.toString() + " rawContactId:" + rawContactId);
            List<Data> datas = DataSupport.where(ContactsContract.Data.RAW_CONTACT_ID + " = ?", id + "").find(Data.class);
            for (Data data : datas) {
                data.setRaw_contact_id((int) rawContactId);
                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, data.getContentValues());
            }
            List<Contacts> ts = DataSupport.where("name_raw_contact_id = ?", id + "").find(Contacts.class);
            Cursor query = context.getContentResolver().query(uri, new String[]{ContactsContract.Contacts.NAME_RAW_CONTACT_ID,
                    ContactsContract.Contacts.LOOKUP_KEY},
                    ContactsContract.Contacts.NAME_RAW_CONTACT_ID + " = ?", new String[]{rawContactId + ""}, null);
            int columnIndex = query.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
            if (query.moveToFirst()){
                ts.get(0).setLookup(query.getString(columnIndex));
                ts.get(0).save();
            }
        } else if (clazz == CallLogs.class) {
            List<CallLogs> ts = DataSupport.where("calllogs_id = ?", id + "").find(CallLogs.class);
            if (ts.isEmpty()) return false;
            context.getContentResolver().insert(uri, ts.get(0).getContentValues());
        }
        return false;
    }

    public void delete(int id) {
    }
}

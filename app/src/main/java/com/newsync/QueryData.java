package com.newsync;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.newsync.data.Data;
import com.newsync.data.NameTable;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qgswsg on 2018/3/5.
 */

public class QueryData {

    private List<NameTable> localNameTables = new ArrayList<>();
    //    private List<NameTable> cloudNmaeTables = new ArrayList<>();
    private List<Data> cloudNmaeTables = new ArrayList<>();
    //    private Cursor contact_idQueryRaw_contact_id;
//    private Cursor numberQueryData1;
//    private Cursor raw_contact_idQueryData1;
//    private Map<String, String> map = new HashMap<>();

    public static class SingletonHolder {
        public static QueryData queryData = new QueryData();
    }

    private QueryData() {

    }

    public static QueryData getInstance() {
        return SingletonHolder.queryData;
    }

    private String findCloudData1ByMimetype(int raw_contact_id, String mimetype) {
        if (!cloudNmaeTables.isEmpty()) {
            for (Data data : cloudNmaeTables) {
                if (raw_contact_id == data.getRaw_contact_id() && data.getMimetype_id().equals(mimetype)) {
                    return data.getData1();
                }
            }
        }
        return "未知";
    }

    private String findLocalData1ByMimetype(int raw_contact_id, String mimetype) {
        if (!localNameTables.isEmpty()) {
            for (NameTable nameTable : localNameTables) {
                if (raw_contact_id == nameTable.id && nameTable.mimetype.equals(mimetype)) {
                    return nameTable.data1;
                }
            }
        }
        return "未知";
    }

    public String findCloudNameByNumber(String number) {
        if (number == null || number.isEmpty()) return "未知";
        if (!cloudNmaeTables.isEmpty()) {
            for (Data data : cloudNmaeTables) {
                if (data.getData1().equals(number)) {
                    String cloudNameById = findCloudNameById(data.getRaw_contact_id());
                    if (cloudNameById.equals("未知")) {
                        return number;
                    } else {
                        return cloudNameById;
                    }
                }
            }
        }
        return number;
    }

    public String findLocalNameByNumber(String number) {
        if (number == null || number.isEmpty()) return "未知";
        if (!localNameTables.isEmpty()) {
            for (NameTable nameTable : localNameTables) {
                if (nameTable.data1.equals(number)) {
                    String localNameById = findLocalNameById(nameTable.id);
                    if (localNameById.equals("未知")) {
                        return number;
                    } else {
                        return localNameById;
                    }
                }
            }
        }
        return number;
    }

    public String findCloudNumberById(int raw_contact_id) {
        return findCloudData1ByMimetype(raw_contact_id, "vnd.android.cursor.item/phone_v2");
    }

    public String findCloudNameById(int raw_contact_id) {
        return findCloudData1ByMimetype(raw_contact_id, "vnd.android.cursor.item/name");
    }

    public String findLocalNumberById(int raw_contact_id) {
        return findLocalData1ByMimetype(raw_contact_id, "vnd.android.cursor.item/phone_v2");
    }

    public String findLocalNameById(int raw_contact_id) {
        return findLocalData1ByMimetype(raw_contact_id, "vnd.android.cursor.item/name");
    }

    public void loadNameTable(Context context) {
        localNameTables.clear();
        cloudNmaeTables.clear();
        Cursor query = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.MIMETYPE, ContactsContract.Data.RAW_CONTACT_ID, ContactsContract.Data.DATA1},
                ContactsContract.Data.MIMETYPE + " in (?,?)", new String[]{"vnd.android.cursor.item/phone_v2", "vnd.android.cursor.item/name"}, null);
        if (query != null) {
            int mimetypeColumIndex = query.getColumnIndex(ContactsContract.Data.MIMETYPE);
            int raw_contact_idColumIndex = query.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);
            int data1ColumIndex = query.getColumnIndex(ContactsContract.Data.DATA1);
            if (query.moveToFirst()) {
                do {
                    String mimetype = query.getString(mimetypeColumIndex);
                    int raw_contact_id = query.getInt(raw_contact_idColumIndex);
                    String data1 = query.getString(data1ColumIndex);
                    localNameTables.add(new NameTable(raw_contact_id, mimetype, data1));
                } while (query.moveToNext());
            }
        }
        cloudNmaeTables = DataSupport.where("mimetype_id in (?,?)", "vnd.android.cursor.item/name", "vnd.android.cursor.item/phone_v2").find(Data.class);
    }
}

package com.newsync.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by qgswsg on 2018/2/13.
 */

public class Data extends DataModelBase {

    private int data_id;
    private String mimetype_id;
    private int raw_contact_id;
    private int is_primary;
    private int is_super_primary;
    private int data_version;
    private String data1;
    private String data2;
    private String data3;
    private String data4;
    private String data5;
    private String data6;
    private String data7;
    private String data8;
    private String data9;
    private String data10;
    private String data11;
    private String data12;
    private String data13;
    private String data14;
    private String data_sync1;
    private String data_sync2;
    private String data_sync3;
    private String data_sync4;

    @Override
    public void initialize(Cursor query) {
        setData_id(query.getInt(query.getColumnIndex("_id")));
        setMimetype_id(query.getString(query.getColumnIndex("mimetype")));
        setRaw_contact_id(query.getInt(query.getColumnIndex("raw_contact_id")));
        setIs_primary(query.getInt(query.getColumnIndex("is_primary")));
        setIs_super_primary(query.getInt(query.getColumnIndex("is_super_primary")));
        setData_version(query.getInt(query.getColumnIndex("data_version")));
        setData1(query.getString(query.getColumnIndex("data1")));
        setData2(query.getString(query.getColumnIndex("data2")));
        setData3(query.getString(query.getColumnIndex("data3")));
        setData4(query.getString(query.getColumnIndex("data4")));
        setData5(query.getString(query.getColumnIndex("data5")));
        setData6(query.getString(query.getColumnIndex("data6")));
        setData7(query.getString(query.getColumnIndex("data7")));
        setData8(query.getString(query.getColumnIndex("data8")));
        setData9(query.getString(query.getColumnIndex("data9")));
        setData10(query.getString(query.getColumnIndex("data10")));
        setData11(query.getString(query.getColumnIndex("data11")));
        setData12(query.getString(query.getColumnIndex("data12")));
        setData13(query.getString(query.getColumnIndex("data13")));
        setData14(query.getString(query.getColumnIndex("data14")));
        setData_sync1(query.getString(query.getColumnIndex("data_sync1")));
        setData_sync2(query.getString(query.getColumnIndex("data_sync2")));
        setData_sync3(query.getString(query.getColumnIndex("data_sync3")));
        setData_sync4(query.getString(query.getColumnIndex("data_sync4")));
        setHashCode();
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("mimetype",getMimetype_id());
        contentValues.put("raw_contact_id",getRaw_contact_id());
        contentValues.put("is_primary",getIs_primary());
        contentValues.put("is_super_primary",getIs_super_primary());
        contentValues.put("data_version",getData_version());
        contentValues.put("data1",getData1());
        contentValues.put("data2",getData2());
        contentValues.put("data3",getData3());
        contentValues.put("data4",getData4());
        contentValues.put("data5",getData5());
        contentValues.put("data6",getData6());
        contentValues.put("data7",getData7());
        contentValues.put("data8",getData8());
        contentValues.put("data9",getData9());
        contentValues.put("data10",getData10());
        contentValues.put("data11",getData11());
        contentValues.put("data12",getData12());
        contentValues.put("data13",getData13());
        contentValues.put("data14",getData14());
        contentValues.put("data_sync1",getData_sync1());
        contentValues.put("data_sync2",getData_sync2());
        contentValues.put("data_sync3",getData_sync3());
        contentValues.put("data_sync4",getData_sync4());
        return contentValues;
    }

    public int getData_id() {
        return data_id;
    }

    public void setData_id(int data_id) {
        this.data_id = data_id;
    }
    public String getMimetype_id() {
        return mimetype_id;
    }

    public void setMimetype_id(String mimetype_id) {
        this.mimetype_id = mimetype_id;
    }

    public int getRaw_contact_id() {
        return raw_contact_id;
    }

    public void setRaw_contact_id(int raw_contact_id) {
        this.raw_contact_id = raw_contact_id;
    }

    public int getIs_primary() {
        return is_primary;
    }

    public void setIs_primary(int is_primary) {
        this.is_primary = is_primary;
    }

    public int getIs_super_primary() {
        return is_super_primary;
    }

    public void setIs_super_primary(int is_super_primary) {
        this.is_super_primary = is_super_primary;
    }

    public int getData_version() {
        return data_version;
    }

    public void setData_version(int data_version) {
        this.data_version = data_version;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public String getData4() {
        return data4;
    }

    public void setData4(String data4) {
        this.data4 = data4;
    }

    public String getData5() {
        return data5;
    }

    public void setData5(String data5) {
        this.data5 = data5;
    }

    public String getData6() {
        return data6;
    }

    public void setData6(String data6) {
        this.data6 = data6;
    }

    public String getData7() {
        return data7;
    }

    public void setData7(String data7) {
        this.data7 = data7;
    }

    public String getData8() {
        return data8;
    }

    public void setData8(String data8) {
        this.data8 = data8;
    }

    public String getData9() {
        return data9;
    }

    public void setData9(String data9) {
        this.data9 = data9;
    }

    public String getData10() {
        return data10;
    }

    public void setData10(String data10) {
        this.data10 = data10;
    }

    public String getData11() {
        return data11;
    }

    public void setData11(String data11) {
        this.data11 = data11;
    }

    public String getData12() {
        return data12;
    }

    public void setData12(String data12) {
        this.data12 = data12;
    }

    public String getData13() {
        return data13;
    }

    public void setData13(String data13) {
        this.data13 = data13;
    }

    public String getData14() {
        return data14;
    }

    public void setData14(String data14) {
        this.data14 = data14;
    }

    public String getData_sync1() {
        return data_sync1;
    }

    public void setData_sync1(String data_sync1) {
        this.data_sync1 = data_sync1;
    }

    public String getData_sync2() {
        return data_sync2;
    }

    public void setData_sync2(String data_sync2) {
        this.data_sync2 = data_sync2;
    }

    public String getData_sync3() {
        return data_sync3;
    }

    public void setData_sync3(String data_sync3) {
        this.data_sync3 = data_sync3;
    }

    public String getData_sync4() {
        return data_sync4;
    }

    public void setData_sync4(String data_sync4) {
        this.data_sync4 = data_sync4;
    }

    @Override
    public int hashCode() {
        return (/*data_id + package_id +*/ mimetype_id + raw_contact_id +
                /*is_read_only +*/ is_primary + is_super_primary + data_version +
                data1 + data2 + data3 + data4 + data5 + data6 + data7 + data8 +
                data9 + data10 + data11 + data12 + data13 + data14 /*+ data15*/ +
                data_sync1 + data_sync2 + data_sync3 + data_sync4).hashCode();
    }

    @Override
    public boolean equals(DataModelBase dataModelBase) {
        return getHashCode() == dataModelBase.hashCode();
    }

    @Override
    public int get_id() {
        return getData_id();
    }

    @Override
    public  void set_id(int id) {
        setData_id(id);
    }

    @Override
    public  String getWhere() {
        return "data_id = ?";
    }

    @Override
    public  String getWhereIn(ArrayList<String> strs) {
        return "data_id = -1";
    }

}

package com.newsync.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by qgswsg on 2018/2/12.
 */

public class DataModelBase extends DataSupport {


    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode() {
        this.hashCode = hashCode();
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }


    public boolean equals(DataModelBase dataModelBase) {
        throw new NullPointerException();
//        return hashCode == dataModelBase.getHashCode();
    }

    protected int hashCode;


    public void initialize(Cursor query) {

    }

    public void initializeFromCloud(Cursor cursor) {

    }

    public int get_id() {
        throw new NullPointerException();
    }

    public int update() {
        return update(get_id());
    }

    public String getWhere() {
        throw new NullPointerException();
    }

    public void set_id(int id) {

    }

    public String getWhereIn(ArrayList<String> strs) {
        throw new NullPointerException();
    }

    public ListItem getLocalListItem(Context context) {
        throw new RuntimeException();
    }

    public ListItem getCloudListItem() {
        throw new RuntimeException();
    }

    public ContentValues getContentValues() {
        throw new RuntimeException();
    }

}

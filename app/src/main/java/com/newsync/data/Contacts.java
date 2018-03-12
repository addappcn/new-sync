package com.newsync.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Map;

import com.newsync.QueryData;
import com.newsync.Util;

/**
 * Created by qgswsg on 2018/2/11.
 */

public class Contacts extends DataModelBase {

    private int contacts_id;
    private int name_raw_contact_id;
//    private int photo_id;
//    private int photo_file_id;
//    private int custom_ringtone;
//    private int send_to_voicemail;
//    private int times_contacted;
//    private long last_time_contacted;
//    private int starred;
//    private int pinned;
//    private int has_phone_number;
    private String lookup;
//    private long contact_last_updated_timestamp;

    public int getContacts_id() {
        return contacts_id;
    }

    public void setContacts_id(int contacts_id) {
        this.contacts_id = contacts_id;
    }

    public int getName_raw_contact_id() {
        return name_raw_contact_id;
    }

    public void setName_raw_contact_id(int name_raw_contact_id) {
        this.name_raw_contact_id = name_raw_contact_id;
    }

//    public int getPhoto_id() {
//        return photo_id;
//    }
//
//    public void setPhoto_id(int photo_id) {
//        this.photo_id = photo_id;
//    }
//
//    public int getPhoto_file_id() {
//        return photo_file_id;
//    }
//
//    public void setPhoto_file_id(int photo_file_id) {
//        this.photo_file_id = photo_file_id;
//    }
//
//    public int getCustom_ringtone() {
//        return custom_ringtone;
//    }
//
//    public void setCustom_ringtone(int custom_ringtone) {
//        this.custom_ringtone = custom_ringtone;
//    }
//
//    public int getSend_to_voicemail() {
//        return send_to_voicemail;
//    }
//
//    public void setSend_to_voicemail(int send_to_voicemail) {
//        this.send_to_voicemail = send_to_voicemail;
//    }
//
//    public int getTimes_contacted() {
//        return times_contacted;
//    }
//
//    public void setTimes_contacted(int times_contacted) {
//        this.times_contacted = times_contacted;
//    }
//
//    public long getLast_time_contacted() {
//        return last_time_contacted;
//    }
//
//    public void setLast_time_contacted(long last_time_contacted) {
//        this.last_time_contacted = last_time_contacted;
//    }
//
//    public int getStarred() {
//        return starred;
//    }
//
//    public void setStarred(int starred) {
//        this.starred = starred;
//    }
//
//    public int getPinned() {
//        return pinned;
//    }
//
//    public void setPinned(int pinned) {
//        this.pinned = pinned;
//    }
//
//    public int getHas_phone_number() {
//        return has_phone_number;
//    }
//
//    public void setHas_phone_number(int has_phone_number) {
//        this.has_phone_number = has_phone_number;
//    }


    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

//    public long getContact_last_updated_timestamp() {
//        return contact_last_updated_timestamp;
//    }
//
//    public void setContact_last_updated_timestamp(long contact_last_updated_timestamp) {
//        this.contact_last_updated_timestamp = contact_last_updated_timestamp;
//    }

    @Override
    public boolean equals(DataModelBase dataModelBase) {
        return getLookup().equals(((Contacts)dataModelBase).getLookup());
    }

//    @Override
//    public int hashCode() {
//        return (photo_id +
//                photo_file_id +
//                custom_ringtone +
//                send_to_voicemail +
//                times_contacted +
//                starred +
//                pinned +
//                has_phone_number +
//                lookup +
//                contact_last_updated_timestamp).hashCode();
//    }

    public void initializeFromCloud(Cursor cursor) {
//        setContacts_id(cursor.getInt(cursor.getColumnIndex("contacts_id")));
        setName_raw_contact_id(cursor.getInt(cursor.getColumnIndex("name_raw_contact_id")));
        setLookup(cursor.getString(cursor.getColumnIndex("lookup")));
    }

    public void initialize(Cursor cursor) {
//        setContacts_id(cursor.getInt(cursor.getColumnIndex("_id")));
        setName_raw_contact_id(cursor.getInt(cursor.getColumnIndex("name_raw_contact_id")));
        setLookup(cursor.getString(cursor.getColumnIndex("lookup")));
//        setHashCode();
    }

//    public ContentValues getContentValues() {
//        ContentValues contentValues = new ContentValues();
////        contentValues.put("photo_id", getPhoto_id());
////        contentValues.put("photo_file_id", getPhoto_file_id());
////        contentValues.put("custom_ringtone", getCustom_ringtone());
////        contentValues.put("send_to_voicemail", getSend_to_voicemail());
////        contentValues.put("times_contacted",getTimes_contacted());
////        contentValues.put("last_time_contacted",getLast_time_contacted());
////        contentValues.put("starred",getStarred());
////        contentValues.put("pinned",getPinned());
////        contentValues.put("has_phone_number",getHas_phone_number());
////        contentValues.put("lookup",getLookup());
////        contentValues.put("contact_last_updated_timestamp",getContact_last_updated_timestamp());
//        return contentValues;
//    }

    @Override
    public int get_id() {
        return getContacts_id();
    }

    @Override
    public synchronized void set_id(int id) {
        setContacts_id(id);
    }

    @Override
    public synchronized String getWhere() {
        return "contacts_id != -1 and contacts_id = ?";
    }

    @Override
    public synchronized String getWhereIn(ArrayList<String> strs) {
        return strs.toString().
                replace("[", "contacts_id not in (")
                .replace("]", ")");
    }

    @Override
    public ListItem getCloudListItem() {
        return new ListItem(getName_raw_contact_id(),
                QueryData.getInstance().findCloudNameById(getName_raw_contact_id()),
                QueryData.getInstance().findCloudNumberById(getName_raw_contact_id()), 0, "");
    }

    public ListItem getLocalListItem(Context context) {
        return new ListItem(getName_raw_contact_id(),
                QueryData.getInstance().findLocalNameById(getName_raw_contact_id()),
                QueryData.getInstance().findLocalNumberById(getName_raw_contact_id()), 0, "");
    }

}

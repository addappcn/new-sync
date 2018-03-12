package com.newsync.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by qgswsg on 2018/2/13.
 */

public class Raw_Contacts extends DataModelBase {

    private int raw_contacts_id;
    private String sourceid;
    private int version;
    private int dirty;
    private int deleted;
    private int contact_id;
    private int aggregation_mode;
    private int aggregation_needed;
    private String custom_ringtone;
    private int send_to_voicemail;
    private int times_contacted;
    private int last_time_contacted;
    private int starred;
    private int pinned;
    private String display_name;
    private String display_name_alt;
    private int display_name_source;
    private String phonetic_name;
    private String phonetic_name_style;
    private String sort_key;
    private String phonebook_label;
    private int phonebook_bucket;
    private String sort_key_alt;
    private String phonebook_label_alt;
    private int phonebook_bucket_alt;
    private String sync1;
    private String sync2;
    private String sync3;
    private String sync4;

    @Override
    public void initialize(Cursor query) {
        setRaw_contacts_id(query.getInt(query.getColumnIndex("_id")));
        setSourceid(query.getString(query.getColumnIndex("sourceid")));
        setVersion(query.getInt(query.getColumnIndex("version")));
        setDirty(query.getInt(query.getColumnIndex("dirty")));
        setDeleted(query.getInt(query.getColumnIndex("deleted")));
        setContact_id(query.getInt(query.getColumnIndex("contact_id")));
        setAggregation_mode(query.getInt(query.getColumnIndex("aggregation_mode")));
        setCustom_ringtone(query.getString(query.getColumnIndex("custom_ringtone")));
        setSend_to_voicemail(query.getInt(query.getColumnIndex("send_to_voicemail")));
        setTimes_contacted(query.getInt(query.getColumnIndex("times_contacted")));
        setLast_time_contacted(query.getInt(query.getColumnIndex("last_time_contacted")));
        setStarred(query.getInt(query.getColumnIndex("starred")));
        setPinned(query.getInt(query.getColumnIndex("pinned")));
        setDisplay_name(query.getString(query.getColumnIndex("display_name")));
        setDisplay_name_alt(query.getString(query.getColumnIndex("display_name_alt")));
        setDisplay_name_source(query.getInt(query.getColumnIndex("display_name_source")));
        setPhonetic_name(query.getString(query.getColumnIndex("phonetic_name")));
        setPhonetic_name_style(query.getString(query.getColumnIndex("phonetic_name_style")));
        setSort_key(query.getString(query.getColumnIndex("sort_key")));
        setPhonebook_label(query.getString(query.getColumnIndex("phonebook_label")));
        setPhonebook_bucket(query.getInt(query.getColumnIndex("phonebook_bucket")));
        setSort_key_alt(query.getString(query.getColumnIndex("sort_key_alt")));
        setPhonebook_label_alt(query.getString(query.getColumnIndex("phonebook_label_alt")));
        setPhonebook_bucket_alt(query.getInt(query.getColumnIndex("phonebook_bucket_alt")));
        setSync1(query.getString(query.getColumnIndex("sync1")));
        setSync2(query.getString(query.getColumnIndex("sync2")));
        setSync3(query.getString(query.getColumnIndex("sync3")));
        setSync4(query.getString(query.getColumnIndex("sync4")));
//        setHashCode();
    }

    public ContentValues getContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("sourceid",getSourceid());
        contentValues.put("version",getVersion());
        contentValues.put("dirty",getDirty());
        contentValues.put("deleted",getDeleted());
        contentValues.put("contact_id",getContact_id());
        contentValues.put("aggregation_mode",getAggregation_mode());
        contentValues.put("custom_ringtone",getCustom_ringtone());
        contentValues.put("send_to_voicemail",getSend_to_voicemail());
        contentValues.put("times_contacted",getTimes_contacted());
        contentValues.put("last_time_contacted",getLast_time_contacted());
        contentValues.put("starred",getStarred());
        contentValues.put("pinned",getPinned());
        contentValues.put("display_name",getDisplay_name());
        contentValues.put("display_name_alt",getDisplay_name_alt());
        contentValues.put("display_name_source",getDisplay_name_source());
        contentValues.put("phonetic_name",getPhonetic_name());
        contentValues.put("phonetic_name_style",getPhonetic_name_style());
        contentValues.put("sort_key",getSort_key());
        contentValues.put("phonebook_label",getPhonebook_label());
        contentValues.put("phonebook_bucket",getPhonebook_bucket());
        contentValues.put("sort_key_alt",getSort_key_alt());
        contentValues.put("phonebook_label_alt",getPhonebook_label_alt());
        contentValues.put("phonebook_bucket_alt",getPhonebook_bucket_alt());
        contentValues.put("sync1",getSync1());
        contentValues.put("sync2",getSync2());
        contentValues.put("sync3",getSync3());
        contentValues.put("sync4",getSync4());
        return contentValues;
    }

    @Override
    public boolean equals(DataModelBase dataModelBase) {
        return getHashCode() == dataModelBase.hashCode();
    }

    @Override
    public int hashCode() {
        return (sourceid +
                version +
                dirty +
                deleted +
                contact_id +
                aggregation_mode +
                aggregation_needed +
                custom_ringtone +
                send_to_voicemail +
                times_contacted +
                last_time_contacted +
                starred +
                pinned +
                display_name +
                display_name_alt +
                display_name_source +
                phonetic_name +
                phonetic_name_style +
                sort_key +
                phonebook_label +
                phonebook_bucket +
                sort_key_alt +
                phonebook_label_alt +
                phonebook_bucket_alt +
                sync1 +
                sync2 +
                sync3 +
                sync4).hashCode();
    }

    public int getRaw_contacts_id() {
        return raw_contacts_id;
    }

    public void setRaw_contacts_id(int raw_contacts_id) {
        this.raw_contacts_id = raw_contacts_id;
    }

    public String getSourceid() {
        return sourceid;
    }

    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getDirty() {
        return dirty;
    }

    public void setDirty(int dirty) {
        this.dirty = dirty;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public int getAggregation_mode() {
        return aggregation_mode;
    }

    public void setAggregation_mode(int aggregation_mode) {
        this.aggregation_mode = aggregation_mode;
    }

    public int getAggregation_needed() {
        return aggregation_needed;
    }

    public void setAggregation_needed(int aggregation_needed) {
        this.aggregation_needed = aggregation_needed;
    }

    public String getCustom_ringtone() {
        return custom_ringtone;
    }

    public void setCustom_ringtone(String custom_ringtone) {
        this.custom_ringtone = custom_ringtone;
    }

    public int getSend_to_voicemail() {
        return send_to_voicemail;
    }

    public void setSend_to_voicemail(int send_to_voicemail) {
        this.send_to_voicemail = send_to_voicemail;
    }

    public int getTimes_contacted() {
        return times_contacted;
    }

    public void setTimes_contacted(int times_contacted) {
        this.times_contacted = times_contacted;
    }

    public int getLast_time_contacted() {
        return last_time_contacted;
    }

    public void setLast_time_contacted(int last_time_contacted) {
        this.last_time_contacted = last_time_contacted;
    }

    public int getStarred() {
        return starred;
    }

    public void setStarred(int starred) {
        this.starred = starred;
    }

    public int getPinned() {
        return pinned;
    }

    public void setPinned(int pinned) {
        this.pinned = pinned;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDisplay_name_alt() {
        return display_name_alt;
    }

    public void setDisplay_name_alt(String display_name_alt) {
        this.display_name_alt = display_name_alt;
    }

    public int getDisplay_name_source() {
        return display_name_source;
    }

    public void setDisplay_name_source(int display_name_source) {
        this.display_name_source = display_name_source;
    }

    public String getPhonetic_name() {
        return phonetic_name;
    }

    public void setPhonetic_name(String phonetic_name) {
        this.phonetic_name = phonetic_name;
    }

    public String getPhonetic_name_style() {
        return phonetic_name_style;
    }

    public void setPhonetic_name_style(String phonetic_name_style) {
        this.phonetic_name_style = phonetic_name_style;
    }

    public String getSort_key() {
        return sort_key;
    }

    public void setSort_key(String sort_key) {
        this.sort_key = sort_key;
    }

    public String getPhonebook_label() {
        return phonebook_label;
    }

    public void setPhonebook_label(String phonebook_label) {
        this.phonebook_label = phonebook_label;
    }

    public int getPhonebook_bucket() {
        return phonebook_bucket;
    }

    public void setPhonebook_bucket(int phonebook_bucket) {
        this.phonebook_bucket = phonebook_bucket;
    }

    public String getSort_key_alt() {
        return sort_key_alt;
    }

    public void setSort_key_alt(String sort_key_alt) {
        this.sort_key_alt = sort_key_alt;
    }

    public String getPhonebook_label_alt() {
        return phonebook_label_alt;
    }

    public void setPhonebook_label_alt(String phonebook_label_alt) {
        this.phonebook_label_alt = phonebook_label_alt;
    }

    public int getPhonebook_bucket_alt() {
        return phonebook_bucket_alt;
    }

    public void setPhonebook_bucket_alt(int phonebook_bucket_alt) {
        this.phonebook_bucket_alt = phonebook_bucket_alt;
    }

    public String getSync1() {
        return sync1;
    }

    public void setSync1(String sync1) {
        this.sync1 = sync1;
    }

    public String getSync2() {
        return sync2;
    }

    public void setSync2(String sync2) {
        this.sync2 = sync2;
    }

    public String getSync3() {
        return sync3;
    }

    public void setSync3(String sync3) {
        this.sync3 = sync3;
    }

    public String getSync4() {
        return sync4;
    }

    public void setSync4(String sync4) {
        this.sync4 = sync4;
    }

    @Override
    public int get_id() {
        return getRaw_contacts_id();
    }

    @Override
    public  void set_id(int id){
        setRaw_contacts_id(id);
    }

    @Override
    public  String getWhere(){
        return "raw_contacts_id = ?";
    }

    @Override
    public  String getWhereIn(ArrayList<String> strs){
        return "raw_contacts_id = -1";
    }
}

package com.newsync.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.newsync.QueryData;
import com.newsync.Util;

import java.util.ArrayList;


/**
 * Created by qgswsg on 2018/2/11.
 */

public class CallLogs extends DataModelBase {

    private int id;
    private String number;
    private int presentation;
    private long date;
    private int duration;
    private int data_usage;
    /**
     * 通话记录类型
     * 1：已接 2：外拨 3：未接
     */
    private int type;
    private int features;
    private String subscription_component_name;
    private String subscription_id;
    private int New;
    private String name;
    private int numbertype;
    private String numberlabel;
    private String countryiso;
    private String voicemail_uri;
    private int is_read;
    private String geocoded_location;
    private String lookup_uri;
    private String matched_number;
    private String normalized_number;
    private int photo_id;
    private String formatted_number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getPresentation() {
        return presentation;
    }

    public void setPresentation(int presentation) {
        this.presentation = presentation;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getData_usage() {
        return data_usage;
    }

    public void setData_usage(int data_usage) {
        this.data_usage = data_usage;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFeatures() {
        return features;
    }

    public void setFeatures(int features) {
        this.features = features;
    }

    public String getSubscription_component_name() {
        return subscription_component_name;
    }

    public void setSubscription_component_name(String subscription_component_name) {
        this.subscription_component_name = subscription_component_name;
    }

    public String getSubscription_id() {
        return subscription_id;
    }

    public void setSubscription_id(String subscription_id) {
        this.subscription_id = subscription_id;
    }

    public int getNew() {
        return New;
    }

    public void setNew(int aNew) {
        New = aNew;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumbertype() {
        return numbertype;
    }

    public void setNumbertype(int numbertype) {
        this.numbertype = numbertype;
    }

    public String getNumberlabel() {
        return numberlabel;
    }

    public void setNumberlabel(String numberlabel) {
        this.numberlabel = numberlabel;
    }

    public String getCountryiso() {
        return countryiso;
    }

    public void setCountryiso(String countryiso) {
        this.countryiso = countryiso;
    }

    public String getVoicemail_uri() {
        return voicemail_uri;
    }

    public void setVoicemail_uri(String voicemail_uri) {
        this.voicemail_uri = voicemail_uri;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public String getGeocoded_location() {
        return geocoded_location;
    }

    public void setGeocoded_location(String geocoded_location) {
        this.geocoded_location = geocoded_location;
    }

    public String getLookup_uri() {
        return lookup_uri;
    }

    public void setLookup_uri(String lookup_uri) {
        this.lookup_uri = lookup_uri;
    }

    public String getMatched_number() {
        return matched_number;
    }

    public void setMatched_number(String matched_number) {
        this.matched_number = matched_number;
    }

    public String getNormalized_number() {
        return normalized_number;
    }

    public void setNormalized_number(String normalized_number) {
        this.normalized_number = normalized_number;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }

    public String getFormatted_number() {
        return formatted_number;
    }

    public void setFormatted_number(String formatted_number) {
        this.formatted_number = formatted_number;
    }

    @Override
    public boolean equals(DataModelBase dataModelBase) {
        return getHashCode() == dataModelBase.hashCode();
    }

    public void initializeFromCloud(Cursor cursor) {
        setId(cursor.getInt(cursor.getColumnIndex("id")));
        setNumber(cursor.getString(cursor.getColumnIndex("number")));
        setDate(cursor.getLong(cursor.getColumnIndex("date")));
        setType(cursor.getInt(cursor.getColumnIndex("type")));
        setGeocoded_location(cursor.getString(cursor.getColumnIndex("geocoded_location")));
        hashCode = cursor.getInt(cursor.getColumnIndex("hashcode"));
    }

    public void initialize(Cursor cursor) {
        setNumber(cursor.getString(cursor.getColumnIndex("number")));
        setPresentation(cursor.getInt(cursor.getColumnIndex("presentation")));
        setDate(cursor.getLong(cursor.getColumnIndex("date")));
        setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
        setData_usage(cursor.getInt(cursor.getColumnIndex("data_usage")));
        setType(cursor.getInt(cursor.getColumnIndex("type")));
        setFeatures(cursor.getInt(cursor.getColumnIndex("features")));
        setSubscription_component_name(cursor.getString(cursor.getColumnIndex("subscription_component_name")));
        setSubscription_id(cursor.getString(cursor.getColumnIndex("subscription_id")));
        setNew(cursor.getInt(cursor.getColumnIndex("new")));
        setName(cursor.getString(cursor.getColumnIndex("name")));
        setNumbertype(cursor.getInt(cursor.getColumnIndex("numbertype")));
        setNumberlabel(cursor.getString(cursor.getColumnIndex("numberlabel")));
        setCountryiso(cursor.getString(cursor.getColumnIndex("countryiso")));
        setVoicemail_uri(cursor.getString(cursor.getColumnIndex("voicemail_uri")));
        setIs_read(cursor.getInt(cursor.getColumnIndex("is_read")));
        setGeocoded_location(cursor.getString(cursor.getColumnIndex("geocoded_location")));
        setLookup_uri(cursor.getString(cursor.getColumnIndex("lookup_uri")));
        setMatched_number(cursor.getString(cursor.getColumnIndex("matched_number")));
        setNormalized_number(cursor.getString(cursor.getColumnIndex("normalized_number")));
        setPhoto_id(cursor.getInt(cursor.getColumnIndex("photo_id")));
        setFormatted_number(cursor.getString(cursor.getColumnIndex("formatted_number")));
        setHashCode();
    }



    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", getNumber());
        contentValues.put("presentation", getPresentation());
        contentValues.put("date", getDate());
        contentValues.put("duration", getDuration());
        contentValues.put("data_usage", getData_usage());
        contentValues.put("type", getType());
        contentValues.put("features", getFeatures());
        contentValues.put("subscription_component_name", getSubscription_component_name());
        contentValues.put("subscription_id", getSubscription_id());
        contentValues.put("new", getNew());
        contentValues.put("name", getName());
        contentValues.put("numbertype", getNumbertype());
        contentValues.put("numberlabel", getNumberlabel());
        contentValues.put("countryiso", getCountryiso());
        contentValues.put("voicemail_uri", getVoicemail_uri());
        contentValues.put("is_read", getIs_read());
        contentValues.put("geocoded_location", getGeocoded_location());
        contentValues.put("lookup_uri", getLookup_uri());
        contentValues.put("matched_number", getMatched_number());
        contentValues.put("normalized_number", getNormalized_number());
        contentValues.put("photo_id", getPhoto_id());
        contentValues.put("formatted_number", getFormatted_number());
        return contentValues;
    }


    @Override
    public int hashCode() {
        return (number + presentation + date + duration +
                data_usage + type + features + subscription_component_name +
                subscription_id +  New + name +
                numbertype + numberlabel + countryiso + voicemail_uri +
                is_read + geocoded_location + lookup_uri + matched_number +
                normalized_number + photo_id + formatted_number).hashCode();
    }

    public String getWhere() {
        return "id != -1 and id = ?";
    }

    public String getWhereIn(ArrayList<String> strs) {
        return strs.toString().
                replace("[", "id not in (")
                .replace("]", ")");
    }


    @Override
    public ListItem getCloudListItem() {
        return new ListItem(getId(), QueryData.getInstance().findCloudNameByNumber(getNumber()), getGeocoded_location(), getType(), Util.timeStampToTime(getDate()));
    }

    public ListItem getLocalListItem(Context context) {
        return new ListItem(getId(), QueryData.getInstance().findLocalNameByNumber(getNumber()), getGeocoded_location(), getType(), Util.timeStampToTime(getDate()));
    }
}

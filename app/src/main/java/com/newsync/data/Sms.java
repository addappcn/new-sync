package com.newsync.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import com.newsync.QueryData;
import com.newsync.Util;

/**
 * Created by qgswsg on 2018/2/11.
 */

public class Sms extends DataModelBase {

    /**
     * primary key     integer                  与words表内的source_id关联
     */
    private int sms_id;
    /**
     * 会话id，一个联系人的会话一个id，与threads表内的_id关联      integer
     */
    private int thread_id;

    /**
     * 对方号码          text
     */
    private String address;
    /**
     * 联系人id           integer
     */
    private int person;
    /**
     * 发件日期           integer
     */
    private long date;
    /**
     * 通信协议，判断是短信还是彩信    integer  0：SMS_RPOTO, 1：MMS_PROTO
     */
    private int protocol;
    /**
     * 是否阅读           integer   default 0 0：未读， 1：已读
     */
    private int read;
    /**
     * 状态           integer   default-1。 -1：接收，0：complete,64： pending, 128failed
     */
    private int status;
    /**
     * 短信类型           integer 1：inbox  2：sent 3：draft56  4：outbox  5：failed  6：queued
     */
    private int type;
    /**
     * 发短信为空，收到的为0
     */
    private int reply_path_present;
    /**
     * 内容
     */
    private String body;

    /**
     * 服务中心号码
     */
    private String service_center;
    /**
     * 主题 reply_path_present
     */
    private String subject;
    /**
     * 是否锁掉了。
     * 0为未锁，1已锁
     */
    private int locked;
    /**
     * 发送/检索过程的错误代码。
     */
    private int error_code;
    /**
     * 看到为1 否则为0
     */
    private int seen;
    private String creator;
    private long date_sent;


    public void initializeFromCloud(Cursor cursor) {
        setSms_id(cursor.getInt(cursor.getColumnIndex("id")));
        setThread_id(cursor.getInt(cursor.getColumnIndex("thread_id")));
        setAddress(cursor.getString(cursor.getColumnIndex("address")));
        setDate(cursor.getLong(cursor.getColumnIndex("date")));
        setRead(cursor.getInt(cursor.getColumnIndex("read")));
        setType(cursor.getInt(cursor.getColumnIndex("type")));
        setBody(cursor.getString(cursor.getColumnIndex("body")));
        hashCode = cursor.getInt(cursor.getColumnIndex("hashcode"));
    }

    public void initialize(Cursor cursor) {
//        setSms_id(cursor.getInt(cursor.getColumnIndex("_id")));
        setThread_id(cursor.getInt(cursor.getColumnIndex("thread_id")));
        setAddress(cursor.getString(cursor.getColumnIndex("address")));
        setPerson(cursor.getInt(cursor.getColumnIndex("person")));
        setDate(cursor.getLong(cursor.getColumnIndex("date")));
        setDate_sent(cursor.getLong(cursor.getColumnIndex("date_sent")));
        setProtocol(cursor.getInt(cursor.getColumnIndex("protocol")));
        setRead(cursor.getInt(cursor.getColumnIndex("read")));
        setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        setType(cursor.getInt(cursor.getColumnIndex("type")));
        setReply_path_present(cursor.getInt(cursor.getColumnIndex("reply_path_present")));
        setSubject(cursor.getString(cursor.getColumnIndex("subject")));
        setBody(cursor.getString(cursor.getColumnIndex("body")));
        setService_center(cursor.getString(cursor.getColumnIndex("service_center")));
        setLocked(cursor.getInt(cursor.getColumnIndex("locked")));
        setError_code(cursor.getInt(cursor.getColumnIndex("error_code")));
        setCreator(cursor.getString(cursor.getColumnIndex("creator")));
        setSeen(cursor.getInt(cursor.getColumnIndex("seen")));
        setHashCode();
    }

    public ContentValues getContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("address",getAddress());
        contentValues.put("person",getPerson());
        contentValues.put("date",getDate());
        contentValues.put("date_sent",getDate_sent());
        contentValues.put("protocol",getProtocol());
        contentValues.put("read",getRead());
        contentValues.put("status",getStatus());
        contentValues.put("type",getType());
        contentValues.put("reply_path_present",getReply_path_present());
        contentValues.put("subject",getSubject());
        contentValues.put("body",getBody());
        contentValues.put("service_center",getService_center());
        contentValues.put("locked",getLocked());
        contentValues.put("error_code",getError_code());
        contentValues.put("creator",getCreator());
        contentValues.put("seen",getSeen());
        return contentValues;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public long getDate_sent() {
        return date_sent;
    }

    public void setDate_sent(long date_sent) {
        this.date_sent = date_sent;
    }

    @Override
    public boolean equals(DataModelBase dataModelBase) {
        return getHashCode() == dataModelBase.hashCode();
    }

    @Override
    public int hashCode() {
        return (address + person +
                date + date_sent + protocol +
                status + type + reply_path_present + subject +
                body + service_center + locked +
                error_code + creator + seen).hashCode();
    }

    public int getSms_id() {
        return sms_id;
    }

    public void setSms_id(int sms_id) {
        this.sms_id = sms_id;
    }

    public int getThread_id() {
        return thread_id;
    }

    public void setThread_id(int thread_id) {
        this.thread_id = thread_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getReply_path_present() {
        return reply_path_present;
    }

    public void setReply_path_present(int reply_path_present) {
        this.reply_path_present = reply_path_present;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getService_center() {
        return service_center;
    }

    public void setService_center(String service_center) {
        this.service_center = service_center;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public int get_id() {
        return getSms_id();
    }

    public void set_id(int id) {
        setSms_id(id);
    }

    public String getWhere() {
        return "sms_id != -1 and sms_id = ?";
    }

    public String getWhereIn(ArrayList<String> strs) {
        return strs.toString().
                replace("[", "sms_id not in (")
                .replace("]", ")");
    }

    public ListItem getLocalListItem(Context context) {
        return new ListItem(get_id(), QueryData.getInstance().findLocalNameByNumber(getAddress()), getBody(), getType(), Util.timeStampToTime(getDate()));
    }

    public ListItem getCloudListItem() {
        return new ListItem(get_id(), QueryData.getInstance().findCloudNameByNumber(getAddress()), getBody(), getType(), Util.timeStampToTime(getDate()));
    }
}

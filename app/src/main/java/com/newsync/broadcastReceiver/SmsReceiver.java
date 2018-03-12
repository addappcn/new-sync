package com.newsync.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage msg = null;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (Object p : pdusObj) {
                msg= SmsMessage.createFromPdu((byte[]) p);
                String msgTxt =msg.getMessageBody();//得到消息的内容
                String senderNumber = msg.getOriginatingAddress();
                ContentValues contentValues = new ContentValues();
                contentValues.put("address",senderNumber);
                contentValues.put("date",System.currentTimeMillis());
                contentValues.put("read",0);
                contentValues.put("type",1);
                contentValues.put("body",msgTxt);
                context.getContentResolver().insert(Telephony.Sms.CONTENT_URI, contentValues);
            }
        }
    }
}

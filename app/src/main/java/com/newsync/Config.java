package com.newsync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.onedrive.sdk.extensions.Share;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;
import org.litepal.util.cipher.CipherUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by qgswsg on 2018/2/10.
 */
public class Config {

    private SharedPreferences sharedPreferences;
    private SimpleDateFormat simpleDateFormat;
    private Context context;
    private long smsSyncTime;
    private long contactsSyncTime;
    private long callLogSyncTime;
    private String syncMethod;

    private long get4PointTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }


    public boolean canSync() {
        long a4pointTimestamp = get4PointTimestamp();
        long nowTimestamp = System.currentTimeMillis();
        if (nowTimestamp > a4pointTimestamp) {
            if (smsSyncTime < a4pointTimestamp || callLogSyncTime < a4pointTimestamp || contactsSyncTime < a4pointTimestamp) {
                Log.i("Tag", "条件1满足了");
                return true;
            } else {
                Log.i("Tag", "条件2满足了");
                return false;
            }
        } else {
            Log.i("Tag", "条件3满足了");
            return false;
        }
    }


    public String getSmsSyncTime() {
        if (smsSyncTime == 0) {
            return context.getString(R.string.noSyncTime);
        } else {
            return simpleDateFormat.format(new Date(smsSyncTime));
        }
    }

    public void setSmsSyncTime(long smsSyncTime) {
        this.smsSyncTime = smsSyncTime;
        saveLongConfig(context.getString(R.string.smsSyncTime), smsSyncTime);
    }

    public String getContactsSyncTime() {
        if (smsSyncTime == 0) {
            return context.getString(R.string.noSyncTime);
        } else {
            return simpleDateFormat.format(new Date(contactsSyncTime));
        }
    }

    public void setContactsSyncTime(long contactsSyncTime) {
        this.contactsSyncTime = contactsSyncTime;
        saveLongConfig(context.getString(R.string.contactsSyncTime), contactsSyncTime);
    }

    public String getCallLogSyncTime() {
        if (smsSyncTime == 0) {
            return context.getString(R.string.noSyncTime);
        } else {
            return simpleDateFormat.format(new Date(callLogSyncTime));
        }
    }

    public void setCallLogSyncTime(long callLogSyncTime) {
        this.callLogSyncTime = callLogSyncTime;
        saveLongConfig(context.getString(R.string.callLogSyncTime), callLogSyncTime);
    }

    public String getSyncMethod() {
        return syncMethod;
    }

    private Config(Context context) {
        this.context = context;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd a hh:mm");
        sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_preferences", MODE_PRIVATE);
        syncMethod = sharedPreferences.getString(context.getString(R.string.syncMethod), context.getResources().getStringArray(R.array.sync_method)[0]);
        smsSyncTime = sharedPreferences.getLong(context.getString(R.string.smsSyncTime), 0);
        contactsSyncTime = sharedPreferences.getLong(context.getString(R.string.contactsSyncTime), 0);
        callLogSyncTime = sharedPreferences.getLong(context.getString(R.string.callLogSyncTime), 0);
    }

    public static Config config = null;

    public static Config getInstance(Context context) {
        if (config == null) {
            config = new Config(context);
        }
        return config;
    }

    private void saveLongConfig(String key, long value) {
        sharedPreferences
                .edit()
                .putLong(key, value)
                .commit();
    }

    public boolean isSyncByOnlyWifi() {
        return sharedPreferences.getBoolean("syncByOnlyWifi", true);
    }

    public void setSyncStatus(boolean status) {
        sharedPreferences.edit()
                .putBoolean("syncStatus", status)
                .commit();
    }

    public boolean getSyncStatus() {
        return sharedPreferences.getBoolean("syncStatus", false);
    }

    public void setPassword(String value) {
        try {
            String ciphertext = Util.encrypt(BuildConfig.RELEASE_PARAMETER, value);
            AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
            Account account = null;
            Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.account_type));
            if (accounts.length > 0) {
                account = accounts[0];
                accountManager.setPassword(account,ciphertext);
            } else {
                account = new Account(context.getString(R.string.app_name), context.getString(R.string.account_type));
                accountManager.addAccountExplicitly(account, ciphertext,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPassword() {
        try {
            String password = BuildConfig.RELEASE_PARAMETER;
            AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
            Account account = null;
            Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.account_type));
            if (accounts.length > 0) {
                account = accounts[0];
                password = accountManager.getPassword(account);
            }
            if (password.equals(BuildConfig.RELEASE_PARAMETER)) {
                return BuildConfig.RELEASE_PARAMETER;
            } else {
                return Util.decrypt(BuildConfig.RELEASE_PARAMETER, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BuildConfig.RELEASE_PARAMETER;
    }

}

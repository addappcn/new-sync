package com.newsync.content_observer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;


import com.newsync.BaseApplication;
import com.newsync.R;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by qgswsg on 2018/2/7.
 */

public class mContentObserver extends ContentObserver {

    private Context context;
    public mContentObserver(Context context) {
        super(null);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);
        if (((BaseApplication)context.getApplicationContext()).syncing) return;
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.account_type));
        Account account = null;
        if (accounts.length > 0){
            account = accounts[0];
        }else {
            account = new Account(context.getString(R.string.app_name),context.getString(R.string.account_type));
            accountManager.addAccountExplicitly(account,null,null);
        }
        Bundle bundle = new Bundle();
        bundle.putString("key",uri.toString());
        ContentResolver.requestSync(account,context.getString(R.string.authority),bundle);
    }
}

package com.newsync;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import com.newsync.activity.MainActivity;
import com.onedrive.sdk.authentication.MSAAuthenticator;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.core.DefaultClientConfig;
import com.onedrive.sdk.core.IClientConfig;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.logger.LoggerLevel;

import net.sqlcipher.database.SQLiteDatabase;

import org.litepal.LitePal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by qgswsg on 2018/2/7.
 */


public class BaseApplication extends Application {


    //a88aba5d-ac27-4ab9-a0c2-12c1175a007c
    public static String CLIENT_ID = "";
    public static final String CLIENT_ID3 = "4ab9";
    public final static String SCOPES[] = {"onedrive.readwrite", "onedrive.appfolder", "wl.offline_access"};
    public Handler mainFragementHandler;
    public Handler cloudItemHandler;
    public Handler localItemHandler;
    public boolean syncing = false;
    public Semaphore syncSemaphore = new Semaphore(1);
    public Map<String,String> permissionMap;

    private final AtomicReference<IOneDriveClient> mClient = new AtomicReference<>();
    private ConnectivityManager mConnectivityManager;

    public void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Iterator<Map.Entry<String, String>> iterator = permissionMap.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String, String> next = iterator.next();
                if (checkSelfPermission(next.getValue()) == PackageManager.PERMISSION_GRANTED){
                    iterator.remove();
                }

            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BaseApplication.CLIENT_ID =
                getString(R.string.CLIENT_ID1) + "-" +
                        BuildConfig.CLIENT_ID2 + "-" +
                        BaseApplication.CLIENT_ID3 + "-" +
                        BuildConfig.CLIENT_ID4 + "-" +
                        getString(R.string.client_idl);
        SQLiteDatabase.loadLibs(this);
        permissionMap = new HashMap<>();
        permissionMap.put("短信权限",Manifest.permission.READ_SMS);
        permissionMap.put("联系人权限",Manifest.permission.READ_CONTACTS);
        permissionMap.put("通话记录权限",Manifest.permission.READ_CALL_LOG);
        checkPermission();
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    public synchronized boolean isWifi() {
        final NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info != null || info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * Create the client configuration
     *
     * @return the newly created configuration
     */
    public IClientConfig createConfig() {
        final MSAAuthenticator msaAuthenticator = new MSAAuthenticator() {
            @Override
            public String getClientId() {
                return BaseApplication.CLIENT_ID;
            }

            @Override
            public String[] getScopes() {
                return BaseApplication.SCOPES;
            }
        };
        final IClientConfig config = DefaultClientConfig.createWithAuthenticator(msaAuthenticator);
        config.getLogger().setLoggingLevel(LoggerLevel.Debug);
        return config;
    }

    /**
     * Clears out the auth token from the application store
     */
    public void signOut() {
        if (mClient.get() == null) {
            return;
        }
        mClient.get().getAuthenticator().logout(new ICallback<Void>() {
            @Override
            public void success(final Void result) {
                mClient.set(null);
                final Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void failure(final ClientException ex) {
                Toast.makeText(getBaseContext(), "退出登录时遇到问题", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Get an instance of the service
     *
     * @return The Service
     */
    public synchronized IOneDriveClient getOneDriveClient() {
        if (mClient.get() == null) {
            throw new UnsupportedOperationException("Unable to generate a new service object");
        }
        return mClient.get();
    }

    /**
     * Used to setup the Services
     *
     * @param activity       the current activity
     * @param serviceCreated the callback
     */
    public synchronized void createOneDriveClient(final Activity activity, final ICallback<Void> serviceCreated) {
        final ICallback<IOneDriveClient> callback = new ICallback<IOneDriveClient>() {
            @Override
            public void success(final IOneDriveClient result) {
                mClient.set(result);
                serviceCreated.success(null);
            }

            @Override
            public void failure(final ClientException error) {
                serviceCreated.failure(error);
            }
        };
        new MyOneDriveClient
                .Builder()
                .fromConfig(createConfig())
                .loginAndBuildClient(activity, callback);
    }


}

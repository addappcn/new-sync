package com.newsync.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.newsync.BaseApplication;
import com.newsync.Config;
import com.newsync.R;
import com.newsync.syncOperation.CallLogSyncOperation;
import com.newsync.syncOperation.ContactsSyncOperation;
import com.newsync.syncOperation.SmsSyncOperation;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.concurrency.IProgressCallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.Folder;
import com.onedrive.sdk.extensions.IOneDriveClient;
import com.onedrive.sdk.extensions.Item;
import com.onedrive.sdk.options.Option;
import com.onedrive.sdk.options.QueryOption;

import net.sqlcipher.database.SQLiteDatabase;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TranslucentActivity extends Activity {

    private BaseApplication app;
    private AtomicInteger atomicInteger;

    private void createFolderInRoot(String folderName, ICallback<Item> callback) {
        final Item newItem = new Item();
        newItem.name = folderName;
        newItem.folder = new Folder();
        app.getOneDriveClient()
                .getDrive()
                .getItems("root")
                .getChildren()
                .buildRequest()
                .create(newItem, callback);
    }

    private void startUpload(String itemId, File file) {
        try {
            //getDatabasePath("手机数据") + ".db"
            FileInputStream fileInputStream = new FileInputStream(file);
            int length = fileInputStream.available();
            byte[] buff = new byte[length];
            fileInputStream.read(buff, 0, length);
            Option option = new QueryOption("@name.conflictBehavior", "fail");
            IOneDriveClient oneDriveClient = app.getOneDriveClient();
            oneDriveClient
                    .getDrive()
                    .getItems(itemId)
                    .getChildren()
                    .byId(file.getName().equals("手机数据.db") ? "手机数据.db" :"配置信息.xml")
                    .getContent()
                    .buildRequest(Collections.singletonList(option))
                    .put(buff,
                            new IProgressCallback<Item>() {

                                @Override
                                public void success(Item item) {
                                    Log.i("Tag", "上传成功" + item.name);
                                    Log.i("Tag", "同步完成");
                                    if (atomicInteger.addAndGet(1) == 2) {
                                        Config.getInstance(getApplicationContext()).setSyncStatus(true);
                                        if (app.cloudItemHandler != null) {
                                            app.cloudItemHandler.sendEmptyMessage(0);
                                        }
                                        if (app.localItemHandler != null) {
                                            app.localItemHandler.sendEmptyMessage(0);
                                        }
                                        app.syncSemaphore.release();
                                        app.syncing = false;
                                    }
                                }

                                @Override
                                public void failure(ClientException ex) {
                                    Log.i("Tag", "上传失败" + ex.getMessage());
                                    if (atomicInteger.addAndGet(1) == 2) {
                                        Config.getInstance(getApplicationContext()).setSyncStatus(true);
                                        if (app.cloudItemHandler != null) {
                                            app.cloudItemHandler.sendEmptyMessage(0);
                                        }
                                        if (app.localItemHandler != null) {
                                            app.localItemHandler.sendEmptyMessage(0);
                                        }
                                        app.syncSemaphore.release();
                                        app.syncing = false;
                                    }
                                }

                                @Override
                                public void progress(long current, long max) {

                                }
                            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在上传前先缓存到本地数据库
     */
    private synchronized void cache() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (app.mainFragementHandler != null) {
                app.mainFragementHandler.sendMessage(Message.obtain(app.mainFragementHandler, 1001, "正在同步..."));
                app.mainFragementHandler.sendEmptyMessage(1002);
            }
//            if (app.smsSync == null) {
//
//                app.smsSync = new Sync<Sms>(getApplicationContext(), Telephony.Sms.CONTENT_URI, Sms.class);
//            }
            new SmsSyncOperation(app).computeLocalMore(sms -> sms.save());
//            app.smsSync.loadLocalMore();
            Config.getInstance(getApplicationContext()).setSmsSyncTime(System.currentTimeMillis());
            if (app.mainFragementHandler != null) {
                app.mainFragementHandler.sendMessage(Message.obtain(app.mainFragementHandler, 1001,
                        Config.getInstance(getApplicationContext()).getSmsSyncTime()));
                app.mainFragementHandler.sendEmptyMessage(1003);
            }
        } else {
            if (app.mainFragementHandler != null) {
                app.mainFragementHandler.sendMessage(Message.obtain(app.mainFragementHandler, 1001, "没有读取短信的权限"));
            }
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (app.mainFragementHandler != null) {
                app.mainFragementHandler.sendMessage(Message.obtain(app.mainFragementHandler, 2001, "正在同步..."));
                app.mainFragementHandler.sendEmptyMessage(2002);
            }
            new ContactsSyncOperation(app).uploadLocalMore();
            Config.getInstance(getApplicationContext()).setContactsSyncTime(System.currentTimeMillis());
            if (app.mainFragementHandler != null) {
                app.mainFragementHandler.sendMessage(Message.obtain(app.mainFragementHandler, 2001,
                        Config.getInstance(getApplicationContext()).getContactsSyncTime()));
                app.mainFragementHandler.sendEmptyMessage(2003);
            }
        } else {
            if (app.mainFragementHandler != null) {
                app.mainFragementHandler.sendMessage(Message.obtain(app.mainFragementHandler, 2001, "没有读取联系人的权限"));
            }
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            if (app.mainFragementHandler != null) {
                app.mainFragementHandler.sendMessage(Message.obtain(app.mainFragementHandler, 3001, "正在同步..."));
                app.mainFragementHandler.sendEmptyMessage(3002);
            }
            new CallLogSyncOperation(app).computeLocalMore(callLog -> callLog.save());
//            if (app.callLogSync == null) {
//                app.callLogSync = new Sync<CallLogs>(getApplicationContext(), CallLog.Calls.CONTENT_URI, CallLogs.class);
//            }
//            app.callLogSync.loadLocalMore();
            Config.getInstance(getApplicationContext()).setCallLogSyncTime(System.currentTimeMillis());
            if (app.mainFragementHandler != null) {
                app.mainFragementHandler.sendMessage(Message.obtain(app.mainFragementHandler, 3001,
                        Config.getInstance(getApplicationContext()).getCallLogSyncTime()));
                app.mainFragementHandler.sendEmptyMessage(3003);
            }
        } else {
            if (app.mainFragementHandler != null) {
                app.mainFragementHandler.sendMessage(Message.obtain(app.mainFragementHandler, 3001, "没有读取通话记录的权限"));
            }
        }
    }

    private void getOnedriveItemWithPath(String path, ICallback<Item> itemICallback) {
        app.getOneDriveClient()
                .getDrive()
                .getRoot()
                .getItemWithPath(path)
                .buildRequest()
                .expand("children")
                .get(itemICallback);
    }

    private void deleteItemWithPath(String path) {
        app.getOneDriveClient()
                .getDrive()
                .getRoot()
                .getItemWithPath(path)
                .buildRequest()
                .delete(new ICallback<Void>() {
                    @Override
                    public void success(Void aVoid) {

                    }

                    @Override
                    public void failure(ClientException ex) {

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translucent);
        app = (BaseApplication) getApplicationContext();
        atomicInteger = new AtomicInteger(0);
        String password = Config.getInstance(this).getPassword();
        LitePal.aesKey(password);
        LitePal.initialize(app);
        app.createOneDriveClient(TranslucentActivity.this, new ICallback<Void>() {
            @Override
            public void success(Void aVoid) {
                getOnedriveItemWithPath("手机数据同步目录", new ICallback<Item>() {
                    @Override
                    public void success(Item item) {
                        getOnedriveItemWithPath("手机数据同步目录/手机数据.db", new ICallback<Item>() {
                            @Override
                            public void success(Item item) {
                                deleteItemWithPath("手机数据同步目录/手机数据.db");
                                getOnedriveItemWithPath("手机数据同步目录/手机数据.db", this);
                            }

                            @Override
                            public void failure(ClientException ex) {
                                Executors.newCachedThreadPool().execute(() -> {
                                    cache();
                                    startUpload(item.id, getDatabasePath("手机数据.db"));
                                });
                            }
                        });
                        getOnedriveItemWithPath("手机数据同步目录/" + getPackageName() + "_preferences.xml", new ICallback<Item>() {
                            @Override
                            public void success(Item item) {
                                deleteItemWithPath("手机数据同步目录/" + getPackageName() + "_preferences.xml");
                                getOnedriveItemWithPath("手机数据同步目录/" + getPackageName() + "_preferences.xml", this);
                            }

                            @Override
                            public void failure(ClientException ex) {
                                Executors.newCachedThreadPool().execute(() -> {
                                    File file = new File("/data/data/" + getPackageName() + "/shared_prefs/" + getPackageName() + "_preferences.xml");
                                    startUpload(item.id, file);
                                });
                            }
                        });
//                        getOnedriveItemWithPath("手机数据同步目录/password.xml", new ICallback<Item>() {
//                            @Override
//                            public void success(Item item) {
//                                deleteItemWithPath("手机数据同步目录/password.xml");
//                                getOnedriveItemWithPath("手机数据同步目录/password.xml", this);
//                            }
//
//                            @Override
//                            public void failure(ClientException ex) {
//                                Executors.newCachedThreadPool().execute(() -> {
//                                    File file = new File("/data/data/" + getPackageName() + "/shared_prefs/password.xml");
//                                    startUpload(item.id, file);
//                                });
//                            }
//                        });
                    }

                    @Override
                    public void failure(ClientException ex) {
                        createFolderInRoot("手机数据同步目录", this);
                    }
                });

//                app.getOneDriveClient()
//                        .getDrive()
//                        .getRoot()
//                        .getItemWithPath("手机数据同步目录/手机数据.db")
//                        .buildRequest()
//                        .expand("children")
//                        .get(new ICallback<Item>() {
//
//                            @Override
//                            public void success(Item item) {
//                                app.getOneDriveClient()
//                                        .getDrive()
//                                        .getItems(item.id)
//                                        .buildRequest()
//                                        .delete(new ICallback<Void>() {
//                                            @Override
//                                            public void success(Void aVoid) {
//
//                                            }
//
//                                            @Override
//                                            public void failure(ClientException ex) {
//
//                                            }
//                                        });
//                                app.getOneDriveClient()
//                                        .getDrive()
//                                        .getRoot()
//                                        .getItemWithPath("手机数据同步目录/手机数据.db")
//                                        .buildRequest()
//                                        .expand("children")
//                                        .get(this);
//                            }
//
//                            @Override
//                            public void failure(ClientException ex) {
//                                app.getOneDriveClient()
//                                        .getDrive()
//                                        .getRoot()
//                                        .getItemWithPath("手机数据同步目录")
//                                        .buildRequest()
//                                        .expand("children")
//                                        .get(new ICallback<Item>() {
//                                            @Override
//                                            public void success(Item item) {
//                                                Executors.newCachedThreadPool().execute(() -> {
//                                                    startUpload(item.id, getDatabasePath("手机数据.db"));
//                                                });
//                                            }
//
//                                            @Override
//                                            public void failure(ClientException ex) {
//                                                createFolderInRoot("手机数据同步目录", this);
//                                            }
//                                        });
//                            }
//                        });
            }

            @Override
            public void failure(ClientException ex) {
                Log.i("Tag", "登录遇到问题");
            }
        });
        finish();
    }


}

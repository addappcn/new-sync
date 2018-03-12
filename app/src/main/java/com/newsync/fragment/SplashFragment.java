package com.newsync.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newsync.BaseApplication;
import com.newsync.BuildConfig;
import com.newsync.Config;
import com.newsync.R;
import com.onedrive.sdk.concurrency.ICallback;
import com.onedrive.sdk.core.ClientException;
import com.onedrive.sdk.extensions.Item;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;


public class SplashFragment extends Fragment {

    private BaseApplication app;
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public SplashFragment() {
        // Required empty public constructor
    }

    public static SplashFragment newInstance() {

        Bundle args = new Bundle();

        SplashFragment fragment = new SplashFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        app = (BaseApplication) getActivity().getApplication();
        View inflate = inflater.inflate(R.layout.fragment_splash, container, false);
        TextView everything = inflate.findViewById(R.id.everything);
        ImageView syncImage = inflate.findViewById(R.id.syncImage);
        syncImage.setVisibility(View.VISIBLE);
        everything.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.gradually);
        everything.startAnimation(animation);
        syncImage.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    app.getOneDriveClient();
                    navigateToMain();
                } catch (UnsupportedOperationException ex) {
                    app.createOneDriveClient(getActivity(), new ICallback<Void>() {
                        @Override
                        public void success(Void aVoid) {
                            init();
                        }

                        @Override
                        public void failure(ClientException ex) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("错误")
                                    .setMessage("登录时遇到问题，请重试")
                                    .setPositiveButton("重试", (dialogInterface, i) -> {
                                        app.createOneDriveClient(getActivity(), this);
                                    })
                                    .setOnCancelListener(dialogInterface -> {
                                        getActivity().finish();
                                    })
                                    .show();
                        }
                    });
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return inflate;
    }


    private void navigateToMain() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, MainFragment.newInstance())
                .commit();
    }

    private void navigateTo(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, fragment)
                .commit();
    }

    private void init() {
        //下载三个操作都是SDK自己开的线程，不好如何使用信号量，只好用原子操作记录操作完成的任务数了，如果三个任务都完成了，就跳转到下一个页面；
        getOnedriveItemWithPath("手机数据同步目录/手机数据.db", new ICallback<Item>() {

            @Override
            public void success(Item item) {
                download(item, getActivity().getDatabasePath("手机数据.db"), () -> {
                    if (atomicInteger.addAndGet(1) == 2) {
                        //所有任务都执行完成了
                        openData();
                    }
                });
            }

            @Override
            public void failure(ClientException ex) {
                if (atomicInteger.addAndGet(1) == 2) {
                    //所有任务都执行完成了
                    openData();
                }
            }
        });
        getOnedriveItemWithPath("手机数据同步目录/配置信息.xml", new ICallback<Item>() {

            @Override
            public void success(Item item) {
                download(item, new File("/data/data/" + getActivity().getPackageName() + "/shared_prefs/" + getActivity().getPackageName() + "_preferences.xml"), () -> {
                    if (atomicInteger.addAndGet(1) == 2) {
                        //所有任务都执行完成了
                        openData();
                    }
                });
            }

            @Override
            public void failure(ClientException ex) {
                if (atomicInteger.addAndGet(1) == 2) {
                    //所有任务都执行完成了
                    openData();
                }
            }
        });
//        getOnedriveItemWithPath("手机数据同步目录/password.xml", new ICallback<Item>() {
//
//            @Override
//            public void success(Item item) {
//                download(item, new File("/data/data/" + getActivity().getPackageName() + "/shared_prefs/password.xml"), () -> {
//                    atomicInteger.set(atomicInteger.get() + 1);
//                    if (atomicInteger.get() == 3) {
//                        //所有任务都执行完成了
//                        Log.i("Tag", atomicInteger.get() + item.name);
//                        openData();
//                    }
//                });
//            }
//
//            @Override
//            public void failure(ClientException ex) {
//                atomicInteger.set(atomicInteger.get() + 1);
//                Log.i("Tag", atomicInteger.get() + ex.getMessage());
//                if (atomicInteger.get() == 3) {
//                    //所有任务都执行完成了
//                    openData();
//                }
//            }
//        });
    }

    private void openData() {
        String password = Config.getInstance(getActivity()).getPassword();
        LitePal.aesKey(password);
        LitePal.initialize(getActivity().getApplicationContext());
//        String password = Config.getInstance(getActivity()).getPassword();
        if (password.equals(BuildConfig.RELEASE_PARAMETER)) {
            try {
                //验证数据库密码是否为默认密码，如果是默认密码要求用户设置新密码
                LitePal.getDatabase();
                navigateTo(PasswordFragment.newInstance("new"));
            } catch (SQLiteException sqlEx) {
                //如果密码不是默认密码，会报这个异常，要求用户输入密码进行验证
                navigateTo(VerificationFragment.newInstance());
            }
        } else {
            try {
                LitePal.aesKey(password);
                LitePal.getDatabase();
                navigateToMain();
            } catch (SQLiteException sqlEx) {
                //如果密码不是默认密码，会报这个异常，要求用户输入密码进行验证
                navigateTo(VerificationFragment.newInstance());
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

    private void download(Item item,File databasePath, Runnable runnable) {
        Executors.newCachedThreadPool()
                .execute(() -> {
                    String downloadUrl = item.getRawObject().get("@content.downloadUrl").getAsString();
                    try {
                        URL url = new URL(downloadUrl);
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                        if (httpsURLConnection.getResponseCode() == 200) {
                            InputStream inputStream = httpsURLConnection.getInputStream();
//                            File databasePath = new File(LitePal.getDatabase().getPath());
                            if (!databasePath.exists()) {
                                String path = databasePath.toString();
                                File dir = new File(path.substring(0, path.lastIndexOf("/") + 1));
                                dir.mkdirs();
                                FileOutputStream fileOutputStream = new FileOutputStream(databasePath);
                                byte[] buff = new byte[1024];
                                int read = -1;
                                while ((read = inputStream.read(buff)) != -1) {
                                    fileOutputStream.write(buff, 0, read);
                                }
                                inputStream.close();
                                fileOutputStream.close();
                            }
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(() -> runnable.run());
                });
    }
}

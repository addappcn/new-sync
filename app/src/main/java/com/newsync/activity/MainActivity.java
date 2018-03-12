package com.newsync.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import android.widget.Toolbar;

import com.newsync.BaseApplication;
import com.newsync.R;
import com.newsync.fragment.SplashFragment;

import org.litepal.LitePal;


public class MainActivity extends Activity {

    private BaseApplication app;
    public boolean intercept = false;
    public Runnable backPressdRun;
    public Runnable writeSms;
    public Runnable noPermissionWriteSms;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 58) {
            if (resultCode == -1) {
                //申请默认短信应用时，用户点击了是
                if (writeSms != null) {
                    writeSms.run();
                }
            } else {
                //用户点击了否
                if (noPermissionWriteSms != null) {
                    noPermissionWriteSms.run();
                }
                Toast.makeText(app, "下载未进行，没有写短信的权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (BaseApplication) getApplicationContext();
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction()
                .add(R.id.fragment, SplashFragment.newInstance())
                .commit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (intercept) {
            if (backPressdRun != null) {
                backPressdRun.run();
            }
        } else {
            super.onBackPressed();
        }
    }
}

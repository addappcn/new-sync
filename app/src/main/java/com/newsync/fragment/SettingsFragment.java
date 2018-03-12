package com.newsync.fragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.newsync.BaseApplication;
import com.newsync.R;


/**
 * Created by qgswsg on 2018/2/9.
 */

public class SettingsFragment extends PreferenceFragment {

    private static SettingsFragment fragment;
    private ActionBar actionBar;

    public static SettingsFragment newInstance() {
        if (fragment == null) {
            Bundle args = new Bundle();
            fragment = new SettingsFragment();
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.settings_pref);
        actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("设置");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Preference.OnPreferenceClickListener onPreferenceClickListener = preference -> {
            switch (preference.getKey()) {
                case "EncryptionPassword":
                    navigateTo(PasswordFragment.newInstance("change"));
                    break;
//                case "checkUpload":
//
//                    break;
                case "about":
                    new AlertDialog.Builder(getActivity())
                            .setTitle("关于新同步")
                            .setMessage("        本着尊重用户隐私的出发点，" +
                                    "新同步软件在实现数据同步的基础上，" +
                                    "对用户数据进行了严格加密。" +
                                    "并且将加密后的数据库上传到用户自己的OneDrive网盘，" +
                                    "用户在使用新同步时完全不用担心自己的隐私数据会被人查看或被利用。")
                            .setPositiveButton("确认",null)
                            .show();
                    break;
            }
            return false;
        };
//        findPreference(getString(R.string.checkUpload)).setOnPreferenceClickListener(onPreferenceClickListener);
        findPreference(getString(R.string.about)).setOnPreferenceClickListener(onPreferenceClickListener);
        findPreference(getString(R.string.encryptionPassword)).setOnPreferenceClickListener(onPreferenceClickListener);
    }

    private void navigateTo(Fragment toFragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.hide(this);
        if (!toFragment.isAdded()) {
            fragmentTransaction.add(R.id.fragment, toFragment);
        } else {
            fragmentTransaction.show(toFragment);
        }
        fragmentTransaction
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
        menu.findItem(R.id.outLogin).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getFragmentManager().popBackStack();
                break;
            case R.id.outLogin:
                BaseApplication app = (BaseApplication) getActivity().getApplicationContext();
                getActivity().finish();
                app.signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }
}

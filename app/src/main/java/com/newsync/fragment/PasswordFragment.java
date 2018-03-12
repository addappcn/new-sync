package com.newsync.fragment;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.newsync.BaseApplication;
import com.newsync.BuildConfig;
import com.newsync.Config;
import com.newsync.R;

import net.sqlcipher.database.SQLiteException;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

/**
 * Created by qgswsg on 2018/3/9.
 */

public class PasswordFragment extends Fragment {

    private EditText newPasswordEditText;
    private EditText oldPasswordEditText;
    private BaseApplication app;
    private String parameter;
    private Runnable runnable;
    private ActionBar actionBar;
    private String errorMessage;
    private Toast toast;

    public static PasswordFragment newInstance(String parameter) {

        Bundle args = new Bundle();
        args.putString("parameter", parameter);
        PasswordFragment fragment = new PasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.parameter = getArguments().getString("parameter");
        }
        setHasOptionsMenu(true);
        actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        app = (BaseApplication) getActivity().getApplicationContext();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.password_menu, menu);
        menu.findItem(R.id.complete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void navigateToMain() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, MainFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                break;
            case R.id.complete:
                if (app.syncing) {
                    toast.setText("正在同步，请稍后再试");
                    toast.show();
                    return true;
                }
                String oldPassword = oldPasswordEditText.getText().toString();
                if (oldPassword.isEmpty()) {
                    toast.setText("原密码不能为空！");
                    toast.show();
                    return true;
                }
                String newPassword = newPasswordEditText.getText().toString();
                if (newPassword.isEmpty()) {
                    toast.setText("新密码不能为空！");
                    toast.show();
                    return true;
                }
                try {
                    LitePal.useDefault();
                    LitePal.aesKey(oldPassword);
                    errorMessage = "原密码不正确！";
                    LitePal.getDatabase();
                    errorMessage = "设置失败，密码中包含特殊符号";
                    Connector.rekey(newPassword);
                    Config.getInstance(getActivity()).setPassword(newPassword);
                    InputMethodManager imm = (InputMethodManager) newPasswordEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(newPasswordEditText.getApplicationWindowToken(), 0);
                    }
                    runnable.run();
                } catch (SQLiteException ex) {
                    toast.setText(errorMessage);
                    toast.show();
                    LitePal.aesKey(Config.getInstance(getActivity()).getPassword());
                }
                LitePal.initialize(getActivity().getApplicationContext());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, null);
        oldPasswordEditText = view.findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        toast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        switch (parameter) {
            case "new"://新建密码
                runnable = () -> {
                    navigateToMain();
                    Toast.makeText(app, "密码创建成功！", Toast.LENGTH_SHORT).show();
                };
                actionBar.setTitle("新建密码");
                newPasswordEditText.setHint("请设置密码");
                oldPasswordEditText.setVisibility(View.GONE);
                oldPasswordEditText.setText(BuildConfig.RELEASE_PARAMETER);
                break;
            case "change"://修改密码
                runnable = () -> {
                    getFragmentManager().popBackStack();
                    Toast.makeText(app, "密码修改成功！", Toast.LENGTH_SHORT).show();
                };
                actionBar.setTitle("修改密码");
                oldPasswordEditText.setVisibility(View.VISIBLE);
                break;
        }
        return view;
    }
}

package com.newsync.fragment;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.newsync.Config;
import com.newsync.R;

import net.sqlcipher.database.SQLiteException;

import org.litepal.LitePal;

/**
 * Created by qgswsg on 2018/3/10.
 */

public class VerificationFragment extends Fragment {

    private ActionBar actionBar;

    public static VerificationFragment newInstance() {

        Bundle args = new Bundle();

        VerificationFragment fragment = new VerificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){

        }
        setHasOptionsMenu(true);
        actionBar = getActivity().getActionBar();
        actionBar.setTitle("验证");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verification,null);
        EditText verificationPasswordEditText = view.findViewById(R.id.verificationPasswordEditText);
        Button verificationButton = view.findViewById(R.id.verificationButton);
        verificationButton.setOnClickListener(view1 -> {
            String password = verificationPasswordEditText.getText().toString();
            if (password.isEmpty()){
                Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            try{
                LitePal.aesKey(password);
                LitePal.getDatabase();
                Config.getInstance(getActivity()).setPassword(password);
                navigateToMain();
                InputMethodManager imm = (InputMethodManager) verificationPasswordEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(verificationPasswordEditText.getApplicationWindowToken(), 0);
                }
            }catch (SQLiteException ex){
                Toast.makeText(getActivity(), "密码不正确", Toast.LENGTH_SHORT).show();
                verificationPasswordEditText.setText("");
            }
        });
        return view;
    }

    private void navigateToMain() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, MainFragment.newInstance())
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        actionBar.setTitle("新同步");
        actionBar.setDisplayHomeAsUpEnabled(false);
    }
}

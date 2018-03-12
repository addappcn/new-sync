package com.newsync.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.newsync.BaseApplication;
import com.newsync.BuildConfig;
import com.newsync.Config;
import com.newsync.R;
import com.newsync.Util;
import com.newsync.data.MainListItem;

import net.sqlcipher.database.SQLiteDatabase;

import org.litepal.LitePal;
import org.litepal.util.cipher.CipherUtil;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


public class MainFragment extends Fragment {


    private static MainFragment fragment;
    private BaseApplication app;
    private RecyclerViewAdapter adapter;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        if (fragment == null) fragment = new MainFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        app = (BaseApplication) getActivity().getApplicationContext();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            app.checkPermission();
            if (!app.permissionMap.values().isEmpty()) {
                getActivity().requestPermissions(app.permissionMap.values().toArray(new String[]{}), 58);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(app, permissions[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            navigateTo(SettingsFragment.newInstance());
        }
        return super.onOptionsItemSelected(item);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_main, container, false);
        ActionBar toolbar = getActivity().getActionBar();
        toolbar.setTitle(getString(R.string.app_name));
        RecyclerView recyclerView = inflate.findViewById(R.id.myRecyclerView);
        recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.in_from_bottom));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        SlideInLeftAnimator animator = new SlideInLeftAnimator();
        animator.setAddDuration(300);
        recyclerView.setItemAnimator(animator);
        ArrayList<MainListItem> items = new ArrayList<>();
        String smsSyncHint = getString(R.string.hint_information)
                .replace(getString(R.string.sync_obj), "短信")
                .replace(getString(R.string.sync_time), Config.getInstance(getActivity()).getSmsSyncTime());
        String contactsSyncHint = getString(R.string.hint_information)
                .replace(getString(R.string.sync_obj), "通讯录")
                .replace(getString(R.string.sync_time), Config.getInstance(getActivity()).getContactsSyncTime());
        String callLogSyncHint = getString(R.string.hint_information)
                .replace(getString(R.string.sync_obj), "通话记录")
                .replace(getString(R.string.sync_time), Config.getInstance(getActivity()).getCallLogSyncTime());
        items.add(new MainListItem(smsSyncHint, "正在计算...", false));
        items.add(new MainListItem(contactsSyncHint, "正在计算...", false));
        items.add(new MainListItem(callLogSyncHint, "正在计算...", false));
        Executors.newCachedThreadPool()
                .execute(() -> {
                    for (MainListItem item : items) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(() -> {
                            adapter.addItem(item);
                        });
                    }

                });
        app.mainFragementHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1001:
                        //1000 是短信
                        adapter.setSmsItem(getString(R.string.hint_information).replace(getString(R.string.sync_obj), "短信")
                                .replace(getString(R.string.sync_time), msg.obj.toString()));
                        break;
                    case 1002:
                        adapter.showProgressBar(0);
                        //2 是圆形进度
                        break;
                    case 1003:
                        adapter.computationalCompletion(0);
                        break;
                    case 2001:
                        //2000是通讯录
                        adapter.setConactsItem(getString(R.string.hint_information).replace(getString(R.string.sync_obj), "联系人")
                                .replace(getString(R.string.sync_time), msg.obj.toString()));
                        break;
                    case 2002:
                        adapter.showProgressBar(1);
                        break;
                    case 2003:
                        adapter.computationalCompletion(1);
                        break;
                    case 3001:
                        //3000是通话记录
                        adapter.setCallLogsItems(getString(R.string.hint_information).replace(getString(R.string.sync_obj), "通话记录")
                                .replace(getString(R.string.sync_time), msg.obj.toString()));
                        break;
                    case 3002:
                        adapter.showProgressBar(2);
                        break;
                    case 3003:
                        adapter.computationalCompletion(2);
                        break;
                    case 5005:
                        new AlertDialog.Builder(getActivity())
                                .setTitle("提示")
                                .setMessage("当前并不在wifi环境，同步无法进行。如需在当前环境同步，请更换设置")
                                .setPositiveButton("知道了",null)
                                .show();
                        break;

                }
            }
        };
        setSync(5 * 60);
        return inflate;
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewViewHolder> {

        private ArrayList<MainListItem> items;

        public RecyclerViewAdapter() {
            items = new ArrayList<>();
        }

        public void computationalCompletion(int i) {
            MainListItem item = items.get(i);
            item.progressBarShowState = false;
            item.look = "查看";
            items.set(i, item);
            notifyItemChanged(i);
        }

        public void showProgressBar(int i) {
            MainListItem item = items.get(i);
            item.progressBarShowState = true;
            items.set(i, item);
            notifyItemChanged(i);
        }

        public void addItem(MainListItem item) {
            items.add(item);
            notifyItemInserted(items.size());
        }

        public void setCallLogsItems(String text) {
            MainListItem item = items.get(2);
            item.title = text;
            items.set(2, item);
            notifyItemChanged(2);
        }

        public void setConactsItem(String text) {
            MainListItem item = items.get(1);
            item.title = text;
            items.set(1, item);
            notifyItemChanged(1);
        }

        public void setSmsItem(String text) {
            MainListItem item = items.get(0);
            item.title = text;
            items.set(0, item);
            notifyItemChanged(0);
        }

        @Override
        public RecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerViewViewHolder recyclerViewViewHolder = new RecyclerViewViewHolder(View.inflate(getActivity(), R.layout.recycler_view_layout, null));
            return recyclerViewViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewViewHolder holder, int position) {
            holder.textView.setText(items.get(position).title);
//            holder.look.setText(items.get(position).look);
            holder.look.setVisibility(items.get(position).progressBarShowState ? View.INVISIBLE : View.VISIBLE);
            holder.progressBar.setVisibility(items.get(position).progressBarShowState ? View.VISIBLE : View.INVISIBLE);
            if (!items.get(position).progressBarShowState) {
                holder.itemView.setOnClickListener(view -> {
                    navigateTo(DetailedFragment.newInstance(position));
                });
            } else {
                holder.itemView.setOnClickListener(null);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    class RecyclerViewViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView look;
        public ProgressBar progressBar;

        public RecyclerViewViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.nameTextView);
            look = itemView.findViewById(R.id.look);
            progressBar = itemView.findViewById(R.id.syncProgressBar);
        }
    }

    private void setSync(long pollFrequency) {
        AccountManager accountManager = (AccountManager) getActivity().getSystemService(Context.ACCOUNT_SERVICE);
        Account account = null;
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
        if (accounts.length > 0) {
            account = accounts[0];
            ContentResolver.setIsSyncable(account, getString(R.string.authority), 1);
            ContentResolver.setSyncAutomatically(account, getString(R.string.authority), true);
            ContentResolver.addPeriodicSync(account, getString(R.string.authority), new Bundle(), pollFrequency);    // 间隔时间单位为秒
        }
    }


}

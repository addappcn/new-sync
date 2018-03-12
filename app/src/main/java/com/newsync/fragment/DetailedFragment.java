package com.newsync.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.newsync.BaseApplication;
import com.newsync.CallBack;
import com.newsync.QueryData;
import com.newsync.R;
import com.newsync.presenter.DetailedPresenter;
import com.newsync.view.BaseView;

import java.util.ArrayList;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by qgswsg on 2018/2/26.
 */

public class DetailedFragment extends Fragment implements BaseView {

    private ActionBar actionBar;
    private int position;
    public DetailedPresenter detailedPresenter;
    private ArrayList<Fragment> cacheFragment = new ArrayList<>();
    private Fragment oldFragment;
    private Menu menu;
    public CallBack<Integer> callBack;
    private TabLayout tabLayout;
    private TextView phoneTextView;
    private TextView cloudTextView;
    private BaseApplication app;

    public static DetailedFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        DetailedFragment fragment = new DetailedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.select_menu, menu);
        this.menu = menu;
        setAlways();
        syncMenuShow();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setAlways() {
        menu.findItem(R.id.select_all).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.select_invert).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.download).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.sync).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    public void hideMenu() {
        if (tabLayout.getSelectedTabPosition() == 0) {
            menu.findItem(R.id.sync).setVisible(true);
        } else {
            menu.findItem(R.id.sync).setVisible(false);
        }
        menu.findItem(R.id.select_all).setVisible(false);
        menu.findItem(R.id.select_invert).setVisible(false);
        menu.findItem(R.id.delete).setVisible(false);
        menu.findItem(R.id.download).setVisible(false);
    }


    public void syncMenuShow() {
        menu.findItem(R.id.select_all).setVisible(false);
        menu.findItem(R.id.select_invert).setVisible(false);
        menu.findItem(R.id.delete).setVisible(false);
        menu.findItem(R.id.download).setVisible(false);
        menu.findItem(R.id.sync).setVisible(true);
    }

    public void downloadMenuShow() {
        menu.findItem(R.id.select_all).setVisible(true);
        menu.findItem(R.id.select_invert).setVisible(true);
        menu.findItem(R.id.delete).setVisible(true);
        menu.findItem(R.id.download).setVisible(true);
        menu.findItem(R.id.sync).setVisible(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
        }
        app = (BaseApplication) getActivity().getApplicationContext();
        setHasOptionsMenu(true);
        actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        QueryData.getInstance().loadNameTable(getActivity());
        detailedPresenter = new DetailedPresenter(getActivity(), position);
//        detailedPresenter.setView(this);
        cacheFragment.add(ItemFragment.newInstance(0));
        cacheFragment.add(ItemFragment.newInstance(1));
        detailedPresenter.init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_detailed, null);
        if (position == 2) {
            inflate.findViewById(R.id.callLogHint).setVisibility(View.VISIBLE);
        }
        actionBar.setTitle(detailedPresenter.getTitle());
        phoneTextView = inflate.findViewById(R.id.phone);
        cloudTextView = inflate.findViewById(R.id.oneDrive);
        setCount();
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.listFrameLayout, cacheFragment.get(0))
                .commit();
        tabLayout = inflate.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                navigateTo(oldFragment, cacheFragment.get(tab.getPosition()));
                switch (tab.getPosition()) {
                    case 0:
                        if (!actionBar.getTitle().equals("选择")) {
                            syncMenuShow();
                        }
                        break;
                    case 1:
                        if (!actionBar.getTitle().equals("选择")) {
                            hideMenu();
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                oldFragment = cacheFragment.get(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return inflate;
    }


    public void setCount() {
        phoneTextView.setText(detailedPresenter.getPhoneCount(getActivity(), position));
        cloudTextView.setText(detailedPresenter.getCloudCount(position));
    }

    private void navigateTo(Fragment fromFragment, Fragment toFragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.hide(fromFragment);
        if (!toFragment.isAdded()) {
            fragmentTransaction.add(R.id.listFrameLayout, toFragment);
        } else {
            fragmentTransaction.show(toFragment);
        }
        fragmentTransaction
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                (getActivity()).onBackPressed();
                break;
            case R.id.sync:
                if (app.syncing) break;
                else Toast.makeText(getActivity(), "开始同步", Toast.LENGTH_SHORT).show();
                AccountManager accountManager = (AccountManager) getActivity().getSystemService(ACCOUNT_SERVICE);
                Account[] accounts = accountManager.getAccountsByType(getActivity().getString(R.string.account_type));
                Account account = null;
                if (accounts.length > 0) {
                    account = accounts[0];
                } else {
                    account = new Account(getActivity().getString(R.string.app_name), getActivity().getString(R.string.account_type));
                    accountManager.addAccountExplicitly(account, null, null);
                }
                Bundle bundle = new Bundle();
                bundle.putString("key", "手动发起");
                ContentResolver.requestSync(account, getActivity().getString(R.string.authority), bundle);
                break;
            default:
                if (callBack != null) {
                    callBack.run(item.getItemId());
                } else {
                    Toast.makeText(getActivity(), "尚未加载完成，无法进行选择。请稍后再试！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        app.cloudItemHandler = null;
        app.localItemHandler = null;
        actionBar.setTitle(getString(R.string.app_name));
        actionBar.setDisplayHomeAsUpEnabled(false);
    }
}

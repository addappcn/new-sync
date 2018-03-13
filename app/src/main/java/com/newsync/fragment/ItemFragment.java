package com.newsync.fragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newsync.BaseApplication;
import com.newsync.CallBack;
import com.newsync.R;
import com.newsync.activity.MainActivity;
import com.newsync.data.ListItem;
import com.newsync.presenter.DetailedPresenter;
import com.newsync.syncOperation.SyncOperation;
import com.newsync.view.ItemFragmentView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;


/**
 * Created by qgswsg on 2018/2/27.
 */

public class ItemFragment extends Fragment implements ItemFragmentView {

    private int tagItem;
    private BaseApplication app;
    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private MyRecyclerViewAdapter adapter;
    private View loadView;
    private DetailedPresenter detailedPresenter;
    private String[] buttonText;
    private CallBack<Integer> callBack;
    private Menu menu;
    private boolean loading = true;

    public static ItemFragment newInstance(int tagItem) {

        Bundle args = new Bundle();

        args.putInt("tagItem", tagItem);
        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tagItem = getArguments().getInt("tagItem");
        }
//        setHasOptionsMenu(true);
        app = (BaseApplication) getActivity().getApplicationContext();
    }

    private void getItems(int tagItem) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                SyncOperation syncOperation = detailedPresenter.getSyncOperation();
                switch (tagItem) {
                    case 0:
                        syncOperation.computeLocalMore(dataModelBase -> {
                            refreshList(dataModelBase.getLocalListItem(getParentFragment().getActivity()));
                        });
                        break;
                    case 1:
                        syncOperation.computeCloudMore(dataModelBase -> {
                            refreshList(dataModelBase.getCloudListItem());
                        });
                        break;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loading = false;
                if (adapter.getItemCount() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    loadView.setVisibility(View.GONE);
                } else if (adapter.getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    loadView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_list, null);
        recyclerView = inflate.findViewById(R.id.myRecyclerViewList);
        emptyView = inflate.findViewById(R.id.emptyView);
        loadView = inflate.findViewById(R.id.loadView);
        loadView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyRecyclerViewAdapter(new ArrayList<ListItem>());
        recyclerView.setAdapter(adapter);
        detailedPresenter = ((DetailedFragment) getParentFragment()).detailedPresenter;
        buttonText = detailedPresenter.getButtonText(tagItem);
        getItems(tagItem);
        switch (tagItem) {
            case 0:
                app.localItemHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        adapter.reload();
                        Toast.makeText(app, "同步完成", Toast.LENGTH_SHORT).show();
                    }
                };
                break;
            case 1:
                app.cloudItemHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        adapter.reload();
                        Toast.makeText(app, "同步完成", Toast.LENGTH_SHORT).show();
                    }
                };
                break;
        }
        return inflate;
    }

    @Override
    public void refreshList(List<ListItem> items) {
        getParentFragment().getActivity().runOnUiThread(() -> adapter.setItems(items));
    }

    @Override
    public void refreshList(ListItem listItem) {
        getParentFragment().getActivity().runOnUiThread(() -> adapter.addItme(listItem));
    }

    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {

        List<ListItem> items;

        public MyRecyclerViewAdapter(List<ListItem> items) {
            this.items = items;
        }

        public void setItems(List<ListItem> items) {
            this.items = items;
            notifyItemChanged(0, getItemCount());
        }

        public void setSelectStatue(int show) {
            for (int i = 0; i < items.size(); i++) {
                ListItem listItem = items.get(i);
                listItem.setVisibility(show);
                items.set(i, listItem);
                notifyItemChanged(i);
            }
        }

        public void clearSelect() {
            for (int i = 0; i < items.size(); i++) {
                ListItem listItem = items.get(i);
                listItem.setSelect(false);
                items.set(i, listItem);
                notifyItemChanged(i);
            }
        }

        public void select(int position) {
            ListItem listItem = items.get(position);
            listItem.setSelect(!listItem.isSelect());
            items.set(position, listItem);
            notifyItemChanged(position);
        }

        public void reload() {
//            detailedPresenter.getSyncOperation().clearCache();
            notifyItemRangeRemoved(0, getItemCount());
            items.clear();
            getItems(tagItem);
            ((DetailedFragment) getParentFragment()).setCount();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = View.inflate(getActivity(), R.layout.fragment_item, null);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.title.setText(items.get(position).getTitle());
            holder.context.setText(items.get(position).getContext());
            holder.date.setText(items.get(position).getDate());
            holder.select.setVisibility(items.get(position).getVisibility());
            holder.select.setImageDrawable(getDrawable(items.get(position).isSelect() ? R.drawable.ic_selected : R.drawable.ic_un_selected));
            int iconId = 0;
            if ((iconId = detailedPresenter.getIconId(items.get(position).getType())) != 0) {
                holder.title.setCompoundDrawables(null, null,
                        getDrawable(iconId), null);
            }
            holder.itemView.setOnLongClickListener(view -> {
                if (tagItem == 1) {
                    if (loading) {
                        Toast.makeText(app, "尚未加载完成，无法进行选择。请稍后再试！", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if (items.get(position).getVisibility() != View.VISIBLE) {
                        ActionBar actionBar = getParentFragment().getActivity().getActionBar();
                        Vibrator vibrator = (Vibrator) getParentFragment().getActivity().getSystemService(VIBRATOR_SERVICE);
                        vibrator.vibrate(25);
                        if (!((MainActivity) getParentFragment().getActivity()).intercept) {
                            ((MainActivity) getParentFragment().getActivity()).intercept = true;
                            ((MainActivity) getParentFragment().getActivity()).backPressdRun = () -> {
                                //从activity偷过来的返回键按下事件
                                ((DetailedFragment) getParentFragment()).hideMenu();
                                vibrator.vibrate(25);
                                actionBar.setTitle(detailedPresenter.getTitle());
                                ((MainActivity) getParentFragment().getActivity()).intercept = false;
                                actionBar.setBackgroundDrawable(getDrawable(R.drawable.action_bar_bg));
                                setSelectStatue(View.GONE);
                                clearSelect();
                            };
                        } else {
                            Toast.makeText(app, "请先处理另一个列表中的选中项！", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        //从上面一层fragment那里偷过来的菜单点击事件
                        ((DetailedFragment) getParentFragment()).callBack = id -> {
                            switch (id) {
                                case R.id.select_all:
                                    adapter.allSelect();
                                    break;
                                case R.id.select_invert:
                                    for (int i = 0; i < adapter.getItemCount(); i++) {
                                        adapter.select(i);
                                    }
                                    break;
                                case R.id.download:
                                    try {
                                        app.syncSemaphore.acquire();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    ProgressDialog progressDialog = new ProgressDialog(getParentFragment().getActivity());
                                    progressDialog.setMessage("正在下载...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    List<Integer> idList = new ArrayList<>();
                                    for (int i = items.size() - 1; i >= 0; i--) {
                                        if (items.get(i).isSelect()) {
                                            idList.add(items.get(i).getId());
                                        }
                                    }
                                    ((MainActivity) getParentFragment().getActivity()).noPermissionWriteSms = () -> {
                                        //用户拒绝了本应用成为默认短信应用
                                        progressDialog.cancel();
                                        app.syncSemaphore.release();
                                    };
                                    detailedPresenter.getSyncOperation().download(idList, new Handler() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            super.handleMessage(msg);
                                            switch (msg.what) {
                                                case 101:
                                                    new AlertDialog.Builder(getParentFragment().getActivity())
                                                            .setCancelable(false)
                                                            .setTitle("下载完成")
                                                            .setMessage("请打开短信以恢复默认短信应用！")
                                                            .setNegativeButton("好的", (dialogInterface, i) -> {
                                                                Intent intent = new Intent();
                                                                intent.setType("vnd.android-dir/mms-sms");
                                                                getParentFragment().getActivity().startActivity(intent);
                                                            })
                                                            .show();
                                                    break;
                                            }
                                            ((MainActivity) getParentFragment().getActivity()).backPressdRun.run();
                                            reload();
                                            app.syncSemaphore.release();
                                            progressDialog.cancel();
                                        }
                                    });
                                    break;
                                case R.id.delete:
                                    try {
                                        app.syncSemaphore.acquire();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    ProgressDialog progressDialogDelete = new ProgressDialog(getParentFragment().getActivity());
                                    progressDialogDelete.setMessage("正在删除...");
                                    progressDialogDelete.setCancelable(false);
                                    progressDialogDelete.show();
                                    List<Integer> ids = new ArrayList<>();
                                    for (ListItem listItem : items) {
                                        if (listItem.isSelect()) {
                                            ids.add(listItem.getId());
                                        }
                                    }
                                    detailedPresenter.getSyncOperation().delete(ids, new Handler() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            super.handleMessage(msg);
                                            app.syncSemaphore.release();
                                            switch (msg.what){
                                                case 0:
                                                    reload();
                                                    ((MainActivity) getParentFragment().getActivity()).backPressdRun.run();
                                                    break;
                                                case 1:
                                                    progressDialogDelete.cancel();
                                                    break;
                                            }
                                        }
                                    });
                                    break;
                            }
                        };
                        ((DetailedFragment) getParentFragment()).downloadMenuShow();
                        actionBar.setTitle("选择");
                        actionBar.setBackgroundDrawable(getDrawable(R.drawable.selected_action_bar_bg));
                        setSelectStatue(View.VISIBLE);
                        select(position);
                        return true;
                    }
                    return false;
                }
                return false;
            });
            holder.itemView.setOnClickListener(view -> {
                if (items.get(position).getVisibility() == View.VISIBLE) {
                    select(position);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentFragment().getActivity());
                    builder.setTitle("详情");
                    builder.setMessage(detailedPresenter.getMessage(items.get(position)));
                    builder.setPositiveButton(buttonText[0], (dialogInterface, i) -> {
                        ArrayList<Integer> ids = new ArrayList<>();
                        ids.add(items.get(position).getId());
                        detailedPresenter.getSyncOperation().download(ids, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                reload();
                            }
                        });
                    });
                    builder.setNegativeButton(buttonText[1], (dialogInterface, i) -> {
                        ArrayList<Integer> ids = new ArrayList<>();
                        ids.add(items.get(position).getId());
                        detailedPresenter.getSyncOperation().delete(ids, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                reload();
                            }
                        });
                    });
                    builder.show();
                }
            });
        }

        private Drawable getDrawable(int id) {
            Drawable drawable = getResources().getDrawable(id);// 找到资源图片
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置图片宽高
            return drawable;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void addItme(ListItem listItem) {
            items.add(listItem);
            notifyItemInserted(items.size());
        }

        public void allSelect() {
            for (int i = 0; i < items.size(); i++) {
                ListItem listItem = items.get(i);
                listItem.setSelect(true);
                items.set(i, listItem);
                notifyItemChanged(i);
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView context;
        TextView date;
        ImageView select;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            context = itemView.findViewById(R.id.context);
            date = itemView.findViewById(R.id.date);
            select = itemView.findViewById(R.id.select);
        }

    }

}

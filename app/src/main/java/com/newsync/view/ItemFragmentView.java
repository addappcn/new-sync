package com.newsync.view;

import com.newsync.data.ListItem;

import java.util.List;

/**
 * Created by qgswsg on 2018/3/4.
 */

public interface ItemFragmentView extends BaseView {

    void refreshList(List<ListItem> items);

    void refreshList(ListItem listItem);

}

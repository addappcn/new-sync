package com.newsync.presenter;


import com.newsync.view.BaseView;

/**
 * Created by qgswsg on 2018/2/26.
 */

public interface BasePresenter<T extends BaseView> {

    void setView(T t);
}

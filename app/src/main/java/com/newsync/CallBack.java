package com.newsync;

import com.newsync.data.DataModelBase;


/**
 * Created by qgswsg on 2018/3/5.
 */

public interface CallBack<T> {

    void run(T t);

}

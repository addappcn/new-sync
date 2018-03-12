package com.newsync.syncOperation;

import android.os.Handler;

import com.newsync.CallBack;
import com.newsync.data.DataModelBase;

import java.util.List;

/**
 * Created by qgswsg on 2018/3/8.
 */

public interface SyncOperation {

    /**
     * 计算出本地未上传项
     * @param singleCallBack 每计算出一个进行一次回调
     */
    void computeLocalMore(CallBack<? super DataModelBase> singleCallBack);

    /**
     * 计算出云端未下载项
     * @param singleCallBack 每计算出一个进行一次回调
     */
    void computeCloudMore(CallBack<? super DataModelBase> singleCallBack);

    /**
     * 下载云端项
     * @param idList 下载项id的集合
     * @param handler 下载完成的回调
     * @return
     */
    void download(List<Integer> idList,Handler handler);

    /**
     * 删除选中项
     * @param idList 选中的id
     * @param handler 删除完成的回调
     */
    void delete(List<Integer> idList,Handler handler);

//    void clearCache();

}

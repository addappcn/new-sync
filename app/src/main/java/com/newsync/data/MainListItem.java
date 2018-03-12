package com.newsync.data;

/**
 * Created by qgswsg on 2018/3/3.
 */

public class MainListItem {
    public MainListItem(String title,String look,boolean progressBarShowState){
        this.title = title;
        this.look = look;
        this.progressBarShowState = progressBarShowState;
    }
    public String title;
    public String look;
    public boolean progressBarShowState;

}

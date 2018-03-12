package com.newsync.data;


import android.view.View;

/**
 * Created by qgswsg on 2018/2/27.
 */

public class ListItem {


    public ListItem(int id,String title, String context, int type,String date){
        this.id = id;
        this.title = title;
        this.context = context;
        this.type = type;
        this.date = date;
        this.visibility = View.GONE;
        this.select = false;
    }

    private int id;
    private String title;
    private String context;
    private int type;
    private String date;
    private int visibility;
    private boolean select;

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

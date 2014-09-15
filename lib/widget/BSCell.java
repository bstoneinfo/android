package com.bstoneinfo.lib.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BSCell {

    public int position = -1;
    private final View rootView;

    public BSCell(View rootView) {
        this.rootView = rootView;
    }

    public BSCell(Context context, int layout) {
        this.rootView = LayoutInflater.from(context).inflate(layout, null);
    }

    public Context getContext() {
        return rootView.getContext();
    }

    public View getRootView() {
        return rootView;
    }

    abstract public void loadContent(Object data);

    public void updateContent(Object data) {
    }

    public void destory() {
    }

}

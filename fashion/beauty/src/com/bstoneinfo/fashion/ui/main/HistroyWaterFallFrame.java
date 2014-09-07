package com.bstoneinfo.fashion.ui.main;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bstoneinfo.fashion.app.MyObserverEvent;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.app.BSApplication;

import custom.R;

public class HistroyWaterFallFrame extends CategoryWaterFallFrame {

    public HistroyWaterFallFrame(Context context, String categoryName) {
        super(context, categoryName, MyObserverEvent.CATEGORY_HISTORY_FINISHED_ + categoryName, "HistroyMain");

        final View emptyTip = LayoutInflater.from(getContext()).inflate(R.layout.empty_tips, null);
        BSApplication.defaultNotificationCenter.addObserver(this, MyObserverEvent.CATEGORY_HISTORY_FINISHED_ + categoryName, new Observer() {
            @SuppressWarnings("unchecked")
            @Override
            public void update(Observable observable, Object data) {
                ArrayList<CategoryItemData> dataList = (ArrayList<CategoryItemData>) data;
                if (dataList != null && dataList.isEmpty() && getDataList().isEmpty()) {
                    getRootView().addView(emptyTip);
                } else {
                    getRootView().removeView(emptyTip);
                }
            }
        });

    }

    @Override
    protected void loadMore() {
        getDataSource().histroyMore();
    }

    @Override
    protected void recordFlurry(String event) {
        BSAnalyses.getInstance().event(event, "Histroy");
    }

}

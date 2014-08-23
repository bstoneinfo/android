package com.bstoneinfo.fashion.ui.main;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bstoneinfo.fashion.app.NotificationEvent;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.common.BSApplication;

import custom.R;

public class HistroyWaterFallViewController extends CategoryWaterFallViewController {

    public HistroyWaterFallViewController(Context context, String categoryName) {
        super(context, categoryName, NotificationEvent.CATEGORY_HISTORY_FINISHED_ + categoryName, "HistroyMain", "HistroyFooter");

        final View emptyTip = LayoutInflater.from(getContext()).inflate(R.layout.empty_tips, null);
        BSApplication.defaultNotificationCenter.addObserver(this, NotificationEvent.CATEGORY_HISTORY_FINISHED_ + categoryName, new Observer() {
            @SuppressWarnings("unchecked")
            @Override
            public void update(Observable observable, Object data) {
                ArrayList<CategoryItemData> dataList = (ArrayList<CategoryItemData>) data;
                if (dataList != null && dataList.isEmpty() && imageWaterFallViewController.getDataList().isEmpty()) {
                    imageWaterFallViewController.getRootView().addView(emptyTip);
                } else {
                    imageWaterFallViewController.getRootView().removeView(emptyTip);
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

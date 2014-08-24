package com.bstoneinfo.fashion.ui.main;

import android.content.Context;

import com.bstoneinfo.fashion.app.NotificationEvent;
import com.bstoneinfo.lib.ad.BSAnalyses;

public class ExploreWaterFallViewController extends CategoryWaterFallViewController {

    public ExploreWaterFallViewController(Context context, String categoryName) {
        super(context, categoryName, NotificationEvent.CATEGORY_EXPLORE_FINISHED_ + categoryName, "ExploreMain", "ExploreFooter");
    }

    @Override
    protected void loadMore() {
        getDataSource().exploreMore();
    }

    @Override
    protected void recordFlurry(String event) {
        BSAnalyses.getInstance().event(event, "Explore");
    }
}

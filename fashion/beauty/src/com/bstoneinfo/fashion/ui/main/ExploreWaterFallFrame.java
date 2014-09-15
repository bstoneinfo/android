package com.bstoneinfo.fashion.ui.main;

import android.content.Context;

import com.bstoneinfo.fashion.app.MyObserverEvent;
import com.bstoneinfo.lib.ad.BSAnalyses;

public class ExploreWaterFallFrame extends CategoryWaterFallFrame {

    public ExploreWaterFallFrame(Context context, String categoryName) {
        super(context, categoryName, MyObserverEvent.CATEGORY_EXPLORE_FINISHED_ + categoryName, "ExploreBanner");
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

package com.bstoneinfo.fashion.app;

import android.content.Context;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.bstoneinfo.lib.frame.BSAdPagerFrame;
import com.bstoneinfo.lib.frame.BSFrame;

import custom.R;

public class MyPagerFrame extends BSAdPagerFrame {

    public MyPagerFrame(Context context, BSFrame childFrames[], String titles[], int defaultSelected, String adUnit) {
        super(context, childFrames, titles, defaultSelected, adUnit);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        pagerFrame.setTabBackground(R.drawable.pager_sliding_tab_bg);
        pagerFrame.setIndicatorColor(getContext().getResources().getColor(R.color.pager_sliding_tab_indicator_color));
        pagerFrame.setTextColor(getContext().getResources().getColor(R.color.pager_sliding_tab_text_color));
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        pagerFrame.setOnPageChangeListener(onPageChangeListener);
    }

}

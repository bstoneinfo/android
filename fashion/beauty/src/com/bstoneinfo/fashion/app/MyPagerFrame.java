package com.bstoneinfo.fashion.app;

import android.content.Context;

import com.bstoneinfo.lib.frame.BSFrame;
import com.bstoneinfo.lib.frame.BSPagerFrame;

import custom.R;

public class MyPagerFrame extends BSPagerFrame {

    public MyPagerFrame(Context context, BSFrame childFrames[], String titles[], int defaultSelected) {
        super(context, childFrames, titles, defaultSelected);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        setTabBackground(R.drawable.pager_sliding_tab_bg);
        setIndicatorColor(getContext().getResources().getColor(R.color.pager_sliding_tab_indicator_color));
        setTextColor(getContext().getResources().getColor(R.color.pager_sliding_tab_text_color));
    }

}

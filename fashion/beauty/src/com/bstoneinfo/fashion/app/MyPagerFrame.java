package com.bstoneinfo.fashion.app;

import android.content.Context;
import android.graphics.Typeface;

import com.bstoneinfo.lib.app.BSActivity;
import com.bstoneinfo.lib.frame.BSFrame;
import com.bstoneinfo.lib.frame.BSPagerFrame;

import custom.R;

public class MyPagerFrame extends BSPagerFrame {

    public MyPagerFrame(Context context, BSFrame childFrames[], String titles[]) {
        super(context, childFrames, titles, 0);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        setAllCaps(false);
        setShouldExpand(true);
        setIndicatorHeight(BSActivity.dip2px(4));
        setTypeface(null, Typeface.NORMAL);
        setTabBackground(R.drawable.pager_sliding_tab_bg);
        setIndicatorColor(getContext().getResources().getColor(R.color.pager_sliding_tab_indicator_color));
        setTextColor(getContext().getResources().getColor(R.color.pager_sliding_tab_text_color));
    }

}

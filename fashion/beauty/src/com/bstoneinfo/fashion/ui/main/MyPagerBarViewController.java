package com.bstoneinfo.fashion.ui.main;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;

import com.bstoneinfo.lib.ui.BSActivity;
import com.bstoneinfo.lib.ui.BSPagerBarViewController;
import com.bstoneinfo.lib.ui.BSViewController;

import custom.R;

public class MyPagerBarViewController extends BSPagerBarViewController {

    public MyPagerBarViewController(Context context, ArrayList<BSViewController> childViewControllers, ArrayList<String> titles) {
        super(context, childViewControllers, titles);
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        setAllCaps(false);
        setShouldExpand(true);
        setIndicatorHeight(BSActivity.dip2px(4));
        setTypeface(null, Typeface.NORMAL);
        setTabBackground(R.drawable.pager_sliding_tab_bg);
        setIndicatorColor(getContext().getResources().getColor(R.color.pager_sliding_tab_indicator_color));
        setTextColor(getContext().getResources().getColor(R.color.pager_sliding_tab_text_color));

    }

}

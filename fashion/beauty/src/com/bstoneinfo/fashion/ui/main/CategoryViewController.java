package com.bstoneinfo.fashion.ui.main;

import java.util.ArrayList;

import android.content.Context;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.ui.BSPagerBarViewController;
import com.bstoneinfo.lib.ui.BSViewController;

import custom.R;

public class CategoryViewController extends BSViewController {

    protected final String categoryName;

    public CategoryViewController(Context context, String categoryName) {
        super(new LinearLayout(context));
        ((LinearLayout) getRootView()).setOrientation(LinearLayout.VERTICAL);
        this.categoryName = categoryName;
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        ArrayList<String> titles = new ArrayList<String>();
        titles.add(getContext().getString(R.string.tab_explore));
        titles.add(getContext().getString(R.string.tab_history));

        ArrayList<BSViewController> childViewControllers = new ArrayList<BSViewController>();
        childViewControllers.add(new ExploreWaterFallViewController(getContext(), categoryName));
        childViewControllers.add(new HistroyWaterFallViewController(getContext(), categoryName));

        BSPagerBarViewController pagerViewController = new MyPagerBarViewController(getContext(), childViewControllers, titles);
        addChildViewController(pagerViewController);
    }

}

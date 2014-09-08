package com.bstoneinfo.fashion.ui.main;

import android.content.Context;

import com.bstoneinfo.fashion.data.CategoryDataSource;
import com.bstoneinfo.fashion.data.CategoryManager;

public abstract class CategoryWaterFallFrame extends ImageWaterFallFrame {

    protected final String categoryName;

    public CategoryWaterFallFrame(Context context, String category, final String dataEventName, String mainAdBannerName) {
        super(context, dataEventName, mainAdBannerName);
        this.categoryName = category;
    }

    protected CategoryDataSource getDataSource() {
        return CategoryManager.getInstance().getDataSource(categoryName);
    }

}

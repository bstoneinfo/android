package com.bstoneinfo.fashion.ui.main;

import android.content.Context;

import com.bstoneinfo.fashion.app.MyPagerFrame;
import com.bstoneinfo.lib.frame.BSFrame;

import custom.R;

public class CategoryFrame extends MyPagerFrame {

    protected final String categoryName;

    public CategoryFrame(Context _context, String _categoryName) {
        super(_context, new BSFrame[] { new ExploreWaterFallFrame(_context, _categoryName), new HistroyWaterFallFrame(_context, _categoryName) }, new String[] {
                _context.getString(R.string.tab_explore), _context.getString(R.string.tab_history) });
        categoryName = _categoryName;
    }

}

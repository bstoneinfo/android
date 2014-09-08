package com.bstoneinfo.lib.frame;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.ad.BSAdBanner;

public class BSAdPagerFrame extends BSFrame {

    private final BSAdBanner adBanner;
    protected final BSPagerFrame pagerFrame;

    public BSAdPagerFrame(Context _context, BSFrame _childFrames[], String _titles[], int _defaultSelected, String adUnit) {
        super(new LinearLayout(_context));
        ((LinearLayout) getRootView()).setOrientation(LinearLayout.VERTICAL);
        adBanner = new BSAdBanner(_context, adUnit);
        adBanner.getRootView().setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pagerFrame = new BSPagerFrame(_context, _childFrames, _titles, _defaultSelected);
        pagerFrame.getRootView().setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        addChild(pagerFrame);
        addChild(adBanner);
    }

}

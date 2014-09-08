package com.bstoneinfo.lib.frame;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.app.BSActivity;
import com.bstoneinfo.lib.view.BSPagerSlidingTabView;

public class BSPagerFrame extends BSFrame {

    private final ArrayList<String> titles = new ArrayList<String>();
    private final BSPagerSlidingTabView pagerSlidingTabStrip;
    private final ViewPager viewPager;
    private BSPagerAdapter pagerAdapter;
    private OnPageChangeListener onPageChangeListener;
    private int currentSelectedPosition = -1;

    public BSPagerFrame(Context _context, BSFrame _childFrames[], String _titles[], int _defaultSelected) {
        super(new LinearLayout(_context));
        for (BSFrame frame : _childFrames) {
            getChildFrames().add(frame);
        }
        for (String title : _titles) {
            titles.add(title);
        }
        currentSelectedPosition = _defaultSelected;

        LinearLayout rootView = (LinearLayout) getRootView();
        rootView.setOrientation(LinearLayout.VERTICAL);
        pagerSlidingTabStrip = new BSPagerSlidingTabView(_context);
        rootView.addView(pagerSlidingTabStrip, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BSActivity.dip2px(45)));
        viewPager = new ViewPager(_context);
        viewPager.setOffscreenPageLimit(1);
        rootView.addView(viewPager, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        pagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (currentSelectedPosition >= 0) {
                    getChildFrames().get(currentSelectedPosition).hide();
                }
                currentSelectedPosition = position;
                getChildFrames().get(currentSelectedPosition).show();
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
        pagerAdapter = new BSPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        pagerSlidingTabStrip.setViewPager(viewPager);
        pagerSlidingTabStrip.setAllCaps(false);
        pagerSlidingTabStrip.setShouldExpand(true);
        pagerSlidingTabStrip.setIndicatorHeight(BSActivity.dip2px(4));
        pagerSlidingTabStrip.setTypeface(null, Typeface.NORMAL);
    }

    @Override
    protected ArrayList<BSFrame> getActiveChildFrames() {
        ArrayList<BSFrame> frames = new ArrayList<BSFrame>();
        if (currentSelectedPosition >= 0 && currentSelectedPosition < getChildFrames().size()) {
            frames.add(getChildFrames().get(currentSelectedPosition));
        }
        return frames;
    }

    public void setAllCaps(boolean textAllCaps) {
        pagerSlidingTabStrip.setAllCaps(textAllCaps);
    }

    public void setShouldExpand(boolean shouldExpand) {
        pagerSlidingTabStrip.setShouldExpand(shouldExpand);
    }

    public void setTextSize(int textSizePx) {
        pagerSlidingTabStrip.setTextSize(textSizePx);
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        pagerSlidingTabStrip.setIndicatorHeight(indicatorLineHeightPx);
    }

    public void setTypeface(Typeface typeface, int style) {
        pagerSlidingTabStrip.setTypeface(typeface, style);
    }

    public void setTabBackground(int resId) {
        pagerSlidingTabStrip.setTabBackground(resId);
    }

    public void setIndicatorColor(int indicatorColor) {
        pagerSlidingTabStrip.setIndicatorColor(indicatorColor);
    }

    public void setIndicatorColorResource(int resId) {
        pagerSlidingTabStrip.setIndicatorColorResource(resId);
    }

    public void setTextColor(int textColor) {
        pagerSlidingTabStrip.setTextColor(textColor);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        onPageChangeListener = listener;
    }

    private class BSPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return getChildFrames().size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            BSFrame frame = getChildFrames().get(position);
            View rootView = frame.getRootView();
            if (rootView.getParent() == null) {
                container.addView(rootView);
                frame.load();
            }
            return rootView;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}

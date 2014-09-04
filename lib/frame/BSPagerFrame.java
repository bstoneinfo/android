package com.bstoneinfo.lib.frame;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.view.BSPagerSlidingTabView;

public class BSPagerFrame extends BSFrame {

    private final ArrayList<String> titles;
    private final BSPagerSlidingTabView pagerSlidingTabStrip;
    private final ViewPager viewPager;
    private BSPagerAdapter pagerAdapter;
    private OnPageChangeListener onPageChangeListener;
    private int currentSelectedPosition = -1;

    public BSPagerFrame(Context _context, ArrayList<BSFrame> _childFrames, ArrayList<String> _titles) {
        super(new LinearLayout(_context));
        getChildFrames().addAll(_childFrames);
        titles = _titles;

        LinearLayout rootView = (LinearLayout) getRootView();
        viewPager = new ViewPager(_context);
        viewPager.setOffscreenPageLimit(1);
        pagerSlidingTabStrip = new BSPagerSlidingTabView(_context);

        pagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getChildFrames().get(position).show();
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
    }

    @Override
    protected void onLoad() {
        pagerAdapter = new BSPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        pagerSlidingTabStrip.setViewPager(viewPager);
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
            return getChildViewControllers().size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Log.d("BSPagerAdapter", "instantiateItem position=" + position);
            BSFrame viewController = getChildViewControllers().get(position);
            View rootView = viewController.getRootView();
            if (rootView.getParent() == null) {
                container.addView(rootView);
                viewController.viewDidLoad();
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

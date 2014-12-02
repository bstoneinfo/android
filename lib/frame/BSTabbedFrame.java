package com.bstoneinfo.lib.frame;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BSTabbedFrame extends BSFrame {

    public interface OnSelectListener {
        void onSelect(int currentSelectIndex, int lastSelectIndex);
    }

    private final String[] titles;
    private final int[] normalIconDrawableIDs, selectedIconDrawableIDs;
    private int currentSelected = -1;
    private int defaultSelected;
    final LinearLayout tabbarLayout;
    private OnSelectListener onSelectListener;
    public int backgroundColor = 0xFFF9F9F9;
    public int selectedTextColor = 0xFF0479FB;
    public int normalTextColor = 0xFF959595;
    public int textFontSizeDip = 12;

    public BSTabbedFrame(Context context, BSFrame childFrames[], String titles[], int normalIconDrawableIDs[], int selectedIconDrawableIDs[], int tabbarHeight, int defaultSelected) {
        super(new RelativeLayout(context));
        for (BSFrame frame : childFrames) {
            this.childFrames.add(frame);
        }
        this.titles = titles;
        this.normalIconDrawableIDs = normalIconDrawableIDs;
        this.selectedIconDrawableIDs = selectedIconDrawableIDs;
        this.defaultSelected = defaultSelected;
        tabbarLayout = new LinearLayout(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tabbarHeight);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        getRootView().addView(tabbarLayout, lp);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        tabbarLayout.setBackgroundColor(backgroundColor);
        int index = 0;
        for (BSFrame frame : childFrames) {
            RelativeLayout tabItemView = new RelativeLayout(getContext());
            tabbarLayout.addView(tabItemView, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(normalIconDrawableIDs[index]);
            TextView textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textFontSizeDip);
            textView.setTextColor(normalTextColor);
            textView.setText(titles[index]);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            tabItemView.addView(imageView, lp);
            lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.bottomMargin = BSActivity.dip2px(2);
            tabItemView.addView(textView, lp);
            tabItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int index = 0; index < tabbarLayout.getChildCount(); index++) {
                        if (tabbarLayout.getChildAt(index) == v) {
                            select(index);
                            return;
                        }
                    }
                }
            });

            //            frame.getRootView().setVisibility(View.GONE);
            //            getRootView().addView(frame.getRootView(), index);
            //            frame.parentFrame = this;
            //            frame.setMargin();
            frame.onLoad();
            index++;
        }
        select(defaultSelected);
    }

    private boolean select(int index) {
        if (!enabled) {
            return false;
        }
        if (index < 0 || index >= childFrames.size()) {
            index = -1;
        }
        if (currentSelected == index) {
            return false;
        }
        final BSFrame oldFrame = currentSelected < 0 ? null : childFrames.get(currentSelected);
        final BSFrame newFrame = index < 0 ? null : childFrames.get(index);
        if (currentSelected >= 0 && currentSelected < childFrames.size()) {
            ((ImageView) ((ViewGroup) tabbarLayout.getChildAt(currentSelected)).getChildAt(0)).setBackgroundResource(normalIconDrawableIDs[currentSelected]);
            ((TextView) ((ViewGroup) tabbarLayout.getChildAt(currentSelected)).getChildAt(1)).setTextColor(normalTextColor);
        }
        newFrame.show();
        if (oldFrame != null) {
            oldFrame.getRootView().setVisibility(View.GONE);
        }
        if (newFrame != null) {
            newFrame.getRootView().setVisibility(View.VISIBLE);
        }
        if (newFrame.hideBottomBar) {
            tabbarLayout.setVisibility(View.GONE);
        } else {
            tabbarLayout.setVisibility(View.VISIBLE);
        }
        if (onSelectListener != null) {
            onSelectListener.onSelect(index, currentSelected);
        }
        currentSelected = index;
        if (index >= 0 && index < childFrames.size()) {
            ((ImageView) ((ViewGroup) tabbarLayout.getChildAt(currentSelected)).getChildAt(0)).setBackgroundResource(selectedIconDrawableIDs[currentSelected]);
            ((TextView) ((ViewGroup) tabbarLayout.getChildAt(currentSelected)).getChildAt(1)).setTextColor(selectedTextColor);
        }
        return true;
    }

    public int getSelectedIndex() {
        return currentSelected;
    }

    public BSFrame getSelectedFrame() {
        if (currentSelected < 0) {
            return null;
        } else {
            return childFrames.get(currentSelected);
        }
    }

    @Override
    protected void onShow() {
        frameStatus = FrameStatus.SHOWING;
        BSFrame selectedViewController = getSelectedFrame();
        if (selectedViewController != null) {
            selectedViewController.onShow();
        }
    }

    @Override
    protected void onShown() {
        frameStatus = FrameStatus.SHOWN;
        BSFrame selectedViewController = getSelectedFrame();
        if (selectedViewController != null) {
            selectedViewController.onShown();
        }
    }

    @Override
    protected void onHide() {
        frameStatus = FrameStatus.HIDING;
        BSFrame selectedViewController = getSelectedFrame();
        if (selectedViewController != null) {
            selectedViewController.onHide();
        }
    }

    @Override
    protected void onHidden() {
        frameStatus = FrameStatus.HIDDEN;
        BSFrame selectedViewController = getSelectedFrame();
        if (selectedViewController != null) {
            selectedViewController.onHidden();
        }
    }

}

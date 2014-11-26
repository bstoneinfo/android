package com.bstoneinfo.lib.frame;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class BSTabbedFrame extends BSFrame {

    public interface OnSelectListener {
        void onSelect(int currentSelectIndex, int lastSelectIndex);
    }

    private final String[] titles;
    private final int[] drawableIDs;
    private int currentSelected = -1;
    final RadioGroup tabbarView;
    private final ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>();
    private OnSelectListener onSelectListener;

    public BSTabbedFrame(Context context, BSFrame childFrames[], String titles[], int drawableIDs[], int height, int defaultSelected) {
        super(new RelativeLayout(context));
        for (BSFrame frame : childFrames) {
            this.childFrames.add(frame);
        }
        this.titles = titles;
        this.drawableIDs = drawableIDs;
        this.currentSelected = defaultSelected;
        this.tabbarView = new RadioGroup(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        getRootView().addView(tabbarView, lp);
        tabbarView.setBackgroundColor(0xFFF8F8F8);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        int index = 0;
        for (BSFrame frame : childFrames) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(titles[index]);
            radioButton.setCompoundDrawables(null, getContext().getResources().getDrawable(drawableIDs[index]), null, null);
            radioButton.setBackgroundColor(0xFFFF0000);
            radioButtons.add(radioButton);
            tabbarView.addView(radioButton, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int index = radioButtons.size() - 1; index >= 0; index--) {
                            if (radioButtons.get(index) == buttonView) {
                                int lastSelectIndex = currentSelected;
                                int newSelectIndex = index;
                                final BSFrame oldFrame = currentSelected < 0 ? null : childFrames.get(currentSelected);
                                final BSFrame newFrame = index < 0 ? null : childFrames.get(index);
                                currentSelected = index;
                                newFrame.show();
                                if (oldFrame != null) {
                                    oldFrame.getRootView().setVisibility(View.GONE);
                                }
                                if (newFrame != null) {
                                    oldFrame.getRootView().setVisibility(View.VISIBLE);
                                }
                                if (newFrame.hideBottomBar) {
                                    tabbarView.setVisibility(View.GONE);
                                } else {
                                    tabbarView.setVisibility(View.VISIBLE);
                                }
                                if (onSelectListener != null) {
                                    onSelectListener.onSelect(newSelectIndex, lastSelectIndex);
                                }
                                return;
                            }
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
        //        select(currentSelected);
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
        if (currentSelected >= 0 && currentSelected < childFrames.size()) {
            radioButtons.get(currentSelected).setChecked(false);
        }
        if (index >= 0 && index < childFrames.size()) {
            radioButtons.get(index).setChecked(true);
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

package com.bstoneinfo.lib.frame;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.view.BSScrollView;

public class BSWaterFallFrame extends BSFrame {

    public enum PullUpState {
        NORMAL,
        LOADING,
        FAILED,
        FINISHED
    }

    protected final int columnCount;
    private final int columnInterval;
    private final BSScrollView scrollView;
    protected final LinearLayout mainLayout;//scrollView下的主布局，包括body和footer
    private final LinearLayout bodyLayout;
    protected final LinearLayout[] columnLayoutArray;
    protected final int[] columnHeightArray;
    private View footerView, footerNormalView, footerLoadingView, footerFailedView, footerFinishedView;
    private PullUpState pullUpState;

    public BSWaterFallFrame(Context context, int columnCount, int columnInterval) {
        super(context);
        scrollView = new BSScrollView(context);
        getRootView().addView(scrollView);
        if (columnCount < 2) {
            columnCount = 2;
        } else if (columnCount > 9) {
            columnCount = 9;
        }
        this.columnCount = columnCount;
        this.columnInterval = columnInterval;
        columnHeightArray = new int[columnCount];
        columnLayoutArray = new LinearLayout[columnCount];

        mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(0, columnInterval, 0, 0);
        scrollView.addView(mainLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        bodyLayout = new LinearLayout(context);
        bodyLayout.setOrientation(LinearLayout.HORIZONTAL);
    }

    public BSScrollView getScrollView() {
        return scrollView;
    }

    public void setPullupState(PullUpState state) {
        pullUpState = state;
        if (footerNormalView != null) {
            footerNormalView.setVisibility(View.GONE);
        }
        if (footerLoadingView != null) {
            footerLoadingView.setVisibility(View.GONE);
        }
        if (footerFailedView != null) {
            footerFailedView.setVisibility(View.GONE);
        }
        if (footerFinishedView != null) {
            footerFinishedView.setVisibility(View.GONE);
        }
        if (pullUpState == PullUpState.NORMAL) {
            if (footerNormalView != null) {
                footerNormalView.setVisibility(View.VISIBLE);
            }
        } else if (pullUpState == PullUpState.LOADING) {
            if (footerLoadingView != null) {
                footerLoadingView.setVisibility(View.VISIBLE);
            }
        } else if (pullUpState == PullUpState.FAILED) {
            if (footerFailedView != null) {
                footerFailedView.setVisibility(View.VISIBLE);
            }
        } else if (pullUpState == PullUpState.FINISHED) {
            if (footerFinishedView != null) {
                footerFinishedView.setVisibility(View.VISIBLE);
            }
        }
    }

    public PullUpState getPullUpState() {
        return pullUpState;
    }

    public void setFooterView(View footerView, View normalStatusView, View loadingStatusView, View failedStatusView, View finishedStatusView) {
        this.footerView = footerView;
        footerNormalView = normalStatusView;
        footerLoadingView = loadingStatusView;
        footerFailedView = failedStatusView;
        footerFinishedView = finishedStatusView;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        mainLayout.addView(bodyLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (footerView != null) {
            mainLayout.addView(footerView);
        }
        for (int i = 0; i < columnCount; i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            lp.setMargins(i == 0 ? columnInterval : 0, 0, columnInterval, 0);
            bodyLayout.addView(linearLayout, lp);
            columnLayoutArray[i] = linearLayout;
        }
        setPullupState(PullUpState.NORMAL);
    }

    public int getChildViewCount() {
        int count = 0;
        for (int i = 0; i < columnCount; i++) {
            if (columnLayoutArray[i] != null) {
                count += columnLayoutArray[i].getChildCount();
            }
        }
        return count;
    }

    public void addView(View childView, int width, int height, boolean addToHead) {
        int minHeight = columnHeightArray[0];
        int minColumn = 0;
        for (int i = 1; i < columnCount; i++) {
            if (minHeight > columnHeightArray[i] + columnInterval * 4) {
                minHeight = columnHeightArray[i];
                minColumn = i;
            }
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.setMargins(0, 0, 0, columnInterval);
        columnHeightArray[minColumn] += height;
        if (addToHead) {
            columnLayoutArray[minColumn].addView(childView, 0, lp);
        } else {
            columnLayoutArray[minColumn].addView(childView, lp);
        }
    }

    public void removeAllViews() {
        for (int i = 0; i < columnCount; i++) {
            columnHeightArray[i] = 0;
            columnLayoutArray[i].removeAllViews();
        }
    }

}

package com.bstoneinfo.lib.frame;

import java.util.ArrayList;
import java.util.Observer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;

import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSAnimation;
import com.bstoneinfo.lib.common.BSObserverCenter;
import com.bstoneinfo.lib.common.BSTimer;
import com.bstoneinfo.lib.common.BSUtils;

public class BSFrame {

    public enum FrameStatus {
        INIT,
        LOADED,
        SHOWING,
        SHOWN,
        HIDING,
        HIDDEN,
        DISTROY
    }

    FrameStatus frameStatus = FrameStatus.INIT;
    final ArrayList<BSFrame> childFrames = new ArrayList<BSFrame>();
    BSFrame parentFrame;
    private final ViewGroup rootView;
    public BSObserverCenter observerCenter = BSApplication.defaultNotificationCenter;
    private final ArrayList<BSTimer> postRunnableList = new ArrayList<BSTimer>();
    private final ArrayList<BSAnimation> animationList = new ArrayList<BSAnimation>();
    boolean hideBottomBar = false;
    boolean overTopBar = false;
    boolean enabled = true;

    public BSFrame(Context context) {
        this(new FrameLayout(context));
        rootView.setBackgroundColor(Color.WHITE);
    }

    public BSFrame(Context context, int layout) {
        this((ViewGroup) LayoutInflater.from(context).inflate(layout, null));
    }

    public BSFrame(ViewGroup view) {
        rootView = view;
        rootView.setClickable(true);
    }

    public BSFrame(ViewGroup parentView, int layout) {
        LayoutInflater.from(parentView.getContext()).inflate(layout, parentView);
        rootView = (ViewGroup) parentView.getChildAt(parentView.getChildCount() - 1);
        rootView.setClickable(true);
    }

    public Context getContext() {
        return rootView.getContext();
    }

    public BSApplication getApplication() {
        return (BSApplication) getContext().getApplicationContext();
    }

    public BSActivity getActivity() {
        return (BSActivity) getContext();
    }

    public ViewGroup getRootView() {
        return rootView;
    }

    public BSFrame getParentFrame() {
        return parentFrame;
    }

    public ArrayList<BSFrame> getChildFrames() {
        return childFrames;
    }

    public FrameStatus getFrameStatus() {
        return frameStatus;
    }

    public void setOverTopBar(boolean bOver) {
        overTopBar = bOver;
    }

    public void setHideBottomBar(boolean bHide) {
        hideBottomBar = bHide;
    }

    protected void onLoad() {
    }

    protected void onShow() {
    }

    protected void onShown() {
    }

    protected void onHide() {
    }

    protected void onHidden() {
    }

    protected void onDestroy() {
    }

    public void load() {
        frameStatus = FrameStatus.LOADED;
        onLoad();
    }

    public void show() {
        frameStatus = FrameStatus.SHOWING;
        onShow();
        post(new Runnable() {
            @Override
            public void run() {
                frameStatus = FrameStatus.SHOWN;
                onShown();
            }
        });
        for (BSFrame frame : getActiveChildFrames()) {
            frame.show();
        }
    }

    public void hide() {
        frameStatus = FrameStatus.HIDING;
        onHide();
        post(new Runnable() {
            @Override
            public void run() {
                frameStatus = FrameStatus.HIDDEN;
                onHidden();
            }
        });
        for (BSFrame frame : getActiveChildFrames()) {
            frame.hide();
        }
    }

    protected ArrayList<BSFrame> getActiveChildFrames() {
        return childFrames;
    }

    public boolean back() {
        return false;
    }

    void destroy() {
        frameStatus = FrameStatus.DISTROY;
        onDestroy();
        BSApplication.defaultNotificationCenter.removeObservers(this);
        if (observerCenter != null && observerCenter != BSApplication.defaultNotificationCenter) {
            observerCenter.removeObservers(this);
        }

        for (BSTimer asyncRun : postRunnableList) {
            asyncRun.cancel();
        }
        postRunnableList.clear();

        for (BSAnimation animation : animationList) {
            animation.cancel();
        }
        animationList.clear();

        for (int i = childFrames.size() - 1; i >= 0; i--) {
            childFrames.get(i).destroy();
        }

        ViewGroup parentView = (ViewGroup) rootView.getParent();
        if (parentView != null) {
            parentView.removeView(rootView);
        }
        if (parentFrame != null) {
            parentFrame.childFrames.remove(this);
            parentFrame = null;
        }

    }

    public void dismiss() {
        if (parentFrame != null) {
            parentFrame.removeChild(this);
        } else if (this == getActivity().getMainFrame()) {
            getActivity().finish();
        }
    }

    public void addChild(BSFrame childFrame) {
        addChild(childFrame, rootView);
    }

    public void addChild(BSFrame childFrame, ViewGroup parentView) {
        childFrames.add(childFrame);
        childFrame.parentFrame = this;
        if (childFrame.rootView.getParent() == null) {
            parentView.addView(childFrame.rootView);
        }
        childFrame.observerCenter = observerCenter;
        childFrame.load();
        if (frameStatus == FrameStatus.SHOWING || frameStatus == FrameStatus.SHOWN) {
            childFrame.show();
        } else if (frameStatus == FrameStatus.HIDING || frameStatus == FrameStatus.HIDDEN) {
            childFrame.hide();
        }
    }

    public void removeChild(BSFrame childFrame) {
        if (childFrame.parentFrame != this) {
            BSUtils.debugAssert("The subframe [" + childFrame + "] has not added to parent frame [" + this + "]");
            return;
        }
        childFrame.hide();
        childFrame.destroy();
    }

    void setMargin() {
        // 根据tabbar的显示或隐藏 设置panelLayout的bottomMargin以控制panelLayout的高度
        ViewGroup tabbarView = null;
        if (parentFrame instanceof BSTabbedFrame) {
            tabbarView = ((BSTabbedFrame) parentFrame).tabbarLayout;
        }
        MarginLayoutParams lp = (MarginLayoutParams) getRootView().getLayoutParams();
        if (tabbarView == null || hideBottomBar) {
            lp.bottomMargin = 0;
        } else {
            lp.bottomMargin = tabbarView.getLayoutParams().height;
        }
        getRootView().setLayoutParams(lp);
    }

    public void addObserver(String event, Observer observer) {
        if (observerCenter != null) {
            observerCenter.addObserver(this, event, observer);
        }
    }

    public void removeObserver(Observer observer) {
        if (observerCenter != null) {
            observerCenter.removeObserver(observer);
        }
    }

    public void removeObservers(String event) {
        if (observerCenter != null) {
            observerCenter.removeObservers(this, event);
        }
    }

    public void removeObservers() {
        if (observerCenter != null) {
            observerCenter.removeObservers(this);
        }
    }

    public void notifyOnMainThread(String event) {
        if (observerCenter != null) {
            observerCenter.notifyOnMainThread(event);
        }
    }

    public void notifyOnMainThread(String event, final Object data) {
        if (observerCenter != null) {
            observerCenter.notifyOnMainThread(event, data);
        }
    }

    public void post(final Runnable runnable) {
        post(runnable, 0);
    }

    public void post(final Runnable runnable, int delayMillis) {
        BSTimer timer = BSTimer.asyncRun(new Runnable() {
            @Override
            public void run() {
                postRunnableList.remove(runnable);
                runnable.run();
            }
        }, delayMillis);
        postRunnableList.add(timer);
    }
}

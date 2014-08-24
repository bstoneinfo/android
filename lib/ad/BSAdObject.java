package com.bstoneinfo.lib.ad;

import android.app.Activity;
import android.view.View;

public abstract class BSAdObject {

    protected final Activity activity;
    protected final String appKey;
    protected View adView;
    private BSAdListener adListener;
    private boolean adReceived;

    public BSAdObject(Activity activity, String appKey) {
        this.activity = activity;
        this.appKey = appKey;
    }

    public abstract void start();

    public void destroy() {
    }

    public View getAdView() {
        return adView;
    }

    public void setAdListener(BSAdListener listener) {
        this.adListener = listener;
    }

    public boolean isReceived() {
        return adReceived;
    }

    protected void adReceived() {
        adReceived = true;
        if (adListener != null) {
            adListener.adReceived();
        }
    }

    protected void adFailed() {
        adReceived = false;
        if (adListener != null) {
            adListener.adFailed();
        }
    }

}

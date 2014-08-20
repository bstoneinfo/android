package com.bstoneinfo.lib.ad;

import android.app.Activity;
import android.view.View;

abstract class BSAdObject {

    protected final Activity activity;
    protected final String appKey;
    protected View adView;
    private BSAdListener adListener;

    BSAdObject(Activity activity, String appKeyTag) {
        this.activity = activity;
        appKey = BSAdUtils.getAdKey(appKeyTag);
    }

    abstract void start();

    public void destroy() {
    }

    public View getAdView() {
        return adView;
    }

    public void setAdListener(BSAdListener listener) {
        this.adListener = listener;
    }

    protected void adReceived() {
        if (adListener != null) {
            adListener.adReceived();
        }
    }

    protected void adFailed() {
        if (adListener != null) {
            adListener.adFailed();
        }
    }

}

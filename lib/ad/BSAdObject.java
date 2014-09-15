package com.bstoneinfo.lib.ad;

import android.app.Activity;
import android.view.View;

public abstract class BSAdObject {

    protected final Activity activity;
    protected final String adUnit;
    protected final String adType;
    protected final String appKey;
    protected View adView;
    private BSAdListener adListener;
    private boolean adReceived;

    public BSAdObject(Activity activity, String adUnit, String adType) {
        this.activity = activity;
        this.adUnit = adUnit;
        this.adType = adType;
        this.appKey = BSAdUtils.getAdAppKey(adUnit, adType);
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

    protected void adRequested() {
        BSAnalyses.getInstance().event("Ad_Request", adType + "-" + adUnit);
    }

    protected void adReceived() {
        adReceived = true;
        if (adListener != null) {
            adListener.adReceived();
        }
        BSAnalyses.getInstance().event("Ad_Receive", adType + "-" + adUnit);
    }

    protected void adFailed() {
        adReceived = false;
        if (adListener != null) {
            adListener.adFailed();
        }
        BSAnalyses.getInstance().event("Ad_Fail", adType + "-" + adUnit);
    }

    protected void adClicked() {
        BSAnalyses.getInstance().event("Ad_Click", adType + "-" + adUnit);
    }
}

package com.bstoneinfo.lib.ad;

import android.app.Activity;

import com.bstoneinfo.lib.common.BSLog;
import com.google.ads.AdView;

public class BSAdBannerAdmob extends BSAdObject {

    public BSAdBannerAdmob(Activity activity) {
        super(activity, "Admob");
    }

    @Override
    public void start() {
        if (adView != null) {
            return;
        }
        adView = new AdView(activity, com.google.ads.AdSize.SMART_BANNER, appKey);
        ((AdView) adView).loadAd(new com.google.ads.AdRequest());
        ((AdView) adView).setAdListener(new com.google.ads.AdListener() {

            @Override
            public void onReceiveAd(com.google.ads.Ad arg0) {
                BSAnalyses.getInstance().event("AdBanner_Result", "Admob_Received");
                adReceived();
            }

            @Override
            public void onPresentScreen(com.google.ads.Ad arg0) {
                BSLog.d("Admob - onPresentScreen");
            }

            @Override
            public void onLeaveApplication(com.google.ads.Ad arg0) {
                BSLog.d("Admob - onLeaveApplication");
            }

            @Override
            public void onFailedToReceiveAd(com.google.ads.Ad arg0, com.google.ads.AdRequest.ErrorCode arg1) {
                adFailed();
                BSAnalyses.getInstance().event("AdBanner_Result", "Admob_Failed");
            }

            @Override
            public void onDismissScreen(com.google.ads.Ad arg0) {
                BSLog.d("Admob - onDismissScreen");
            }
        });
        BSAnalyses.getInstance().event("AdBanner_Request", "Admob");
    }

    @Override
    public void destroy() {
        if (adView != null) {
            ((AdView) adView).destroy();
        }
    }
}

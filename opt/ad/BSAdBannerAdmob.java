package com.bstoneinfo.opt.ad;

import android.app.Activity;

import com.bstoneinfo.lib.ad.BSAdObject;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSTimer;
import com.google.ads.AdView;

public class BSAdBannerAdmob extends BSAdObject {

    private BSTimer nextLoadTimer;

    public BSAdBannerAdmob(Activity activity, String adUnit) {
        super(activity, adUnit, "admob_banner");
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
                BSLog.d("Admob - onReceiveAd");
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
                BSLog.d("Admob - onFailedToReceiveAd");
                adFailed();
                if (nextLoadTimer != null) {
                    nextLoadTimer.cancel();
                }
                nextLoadTimer = BSTimer.asyncRun(new Runnable() {
                    @Override
                    public void run() {
                        ((AdView) adView).loadAd(new com.google.ads.AdRequest());
                    }
                }, 5000);
            }

            @Override
            public void onDismissScreen(com.google.ads.Ad arg0) {
                BSLog.d("Admob - onDismissScreen");
            }
        });
        adRequested();
    }

    @Override
    public void destroy() {
        if (nextLoadTimer != null) {
            nextLoadTimer.cancel();
        }
        if (adView != null) {
            ((AdView) adView).destroy();
        }
    }
}

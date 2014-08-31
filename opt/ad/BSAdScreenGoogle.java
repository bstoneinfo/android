package com.bstoneinfo.opt.ad;

import android.app.Activity;
import android.content.Intent;

import com.bstoneinfo.lib.ad.BSAdObject;
import com.bstoneinfo.lib.ad.BSAdUtils;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSTimer;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class BSAdScreenGoogle extends BSAdObject {

    private InterstitialAd interstitial;
    private BSTimer stopTimer;

    public BSAdScreenGoogle(Activity activity) {
        super(activity, BSAdUtils.getAdScreenAppKey("Admob"));
    }

    @Override
    public void start() {
        if (interstitial != null) {
            return;
        }
        BSLog.d("Admob - fullscreen start");
        interstitial = new InterstitialAd(activity);
        interstitial.setAdUnitId(appKey);
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                BSLog.d("Admob - fullscreen onAdLoaded");
                interstitial.show();
                adReceived();
                BSAnalyses.getInstance().event("AdScreen_Received", "Admob");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                BSLog.d("Admob - fullscreen onAdFailedToLoad - errorCode=" + errorCode);
                adFailed();
                BSAnalyses.getInstance().event("AdScreen_Failed", "Admob");
            }

            @Override
            public void onAdOpened() {
                BSLog.d("Admob - fullscreen onAdOpened");
                stopTimer = BSTimer.asyncRun(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(activity, activity.getClass());
                        activity.startActivity(intent);
                    }
                }, BSAdUtils.getScreenAdPresentSecond() * 1000);
            }

            @Override
            public void onAdClosed() {
                BSLog.d("Admob - fullscreen onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                BSLog.d("Admob - fullscreen onAdOpened");
            }

        });
        interstitial.loadAd(new AdRequest.Builder().build());
        BSAnalyses.getInstance().event("AdScreen_Request", "Admob");
    }

    @Override
    public void destroy() {
        if (stopTimer != null) {
            stopTimer.cancel();
            stopTimer = null;
        }
    }

}

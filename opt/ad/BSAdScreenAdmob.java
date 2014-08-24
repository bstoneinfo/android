package com.bstoneinfo.opt.ad;

import android.app.Activity;
import android.content.Intent;

import com.bstoneinfo.lib.ad.BSAdObject;
import com.bstoneinfo.lib.ad.BSAdUtils;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSTimer;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;

public class BSAdScreenAdmob extends BSAdObject {

    private InterstitialAd interstitial;
    private BSTimer stopTimer;

    public BSAdScreenAdmob(Activity activity) {
        super(activity, BSAdUtils.getAdScreenAppKey("Admob"));
    }

    @Override
    public void start() {
        if (interstitial != null) {
            return;
        }
        BSLog.d("Admob - fullscreen start");
        interstitial = new InterstitialAd(activity, appKey);
        AdRequest adRequest = new AdRequest();
        interstitial.setAdListener(new AdListener() {

            @Override
            public void onReceiveAd(Ad arg0) {
                BSLog.d("Admob - fullscreen onReceiveAd");
                interstitial.show();
                adReceived();
                BSAnalyses.getInstance().event("AdScreen_Received", "Admob");
            }

            @Override
            public void onPresentScreen(Ad arg0) {
                BSLog.d("Admob - fullscreen onPresentScreen");
                stopTimer = BSTimer.asyncRun(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(activity, activity.getClass());
                        activity.startActivity(intent);
                    }
                }, BSAdUtils.getScreenAdPresentSecond() * 1000);
            }

            @Override
            public void onLeaveApplication(Ad arg0) {
                BSLog.d("Admob - fullscreen onLeaveApplication");
            }

            @Override
            public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
                BSLog.d("Admob - fullscreen onFailedToReceiveAd");
                adFailed();
                BSAnalyses.getInstance().event("AdScreen_Failed", "Admob");
            }

            @Override
            public void onDismissScreen(Ad arg0) {
                BSLog.d("Admob - fullscreen onDismissScreen");
            }
        });
        interstitial.loadAd(adRequest);
        BSAnalyses.getInstance().event("AdScreen_Request", "Admob");
    }

    @Override
    public void destroy() {
        if (interstitial != null) {
            interstitial.stopLoading();
            interstitial = null;
        }
        if (stopTimer != null) {
            stopTimer.cancel();
            stopTimer = null;
        }
    }

}

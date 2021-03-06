package com.bstoneinfo.opt.ad;

import android.app.Activity;
import android.content.Intent;

import com.bstoneinfo.lib.ad.BSAdObject;
import com.bstoneinfo.lib.ad.BSAdUtils;
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

    public BSAdScreenAdmob(Activity activity, String adUnit) {
        super(activity, adUnit, "admob_screen");
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
            }

            @Override
            public void onPresentScreen(Ad arg0) {
                BSLog.d("Admob - fullscreen onPresentScreen");
                int presentSecond = BSAdUtils.getAdUnitConfig(adUnit).optInt("ScreenAdPresentSecond", 10);
                stopTimer = BSTimer.asyncRun(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(activity, activity.getClass());
                        activity.startActivity(intent);
                    }
                }, presentSecond * 1000);

            }

            @Override
            public void onLeaveApplication(Ad arg0) {
                BSLog.d("Admob - fullscreen onLeaveApplication");
            }

            @Override
            public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
                BSLog.d("Admob - fullscreen onFailedToReceiveAd - " + arg1.toString());
                adFailed();
            }

            @Override
            public void onDismissScreen(Ad arg0) {
                BSLog.d("Admob - fullscreen onDismissScreen");
            }
        });
        interstitial.loadAd(adRequest);
        adRequested();
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

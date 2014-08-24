package com.bstoneinfo.opt.ad;

import android.app.Activity;

import com.bstoneinfo.lib.ad.BSAdObject;
import com.bstoneinfo.lib.ad.BSAdUtils;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.common.BSTimer;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class BSAdBannerGoogle extends BSAdObject {

    private BSTimer nextLoadTimer;

    public BSAdBannerGoogle(Activity activity) {
        super(activity, BSAdUtils.getAdBannerAppKey("Admob"));
    }

    @Override
    public void start() {
        if (adView != null) {
            return;
        }
        adView = new AdView(activity);
        ((AdView) adView).setAdUnitId(appKey);
        ((AdView) adView).setAdSize(AdSize.BANNER);
        ((AdView) adView).setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                BSAnalyses.getInstance().event("AdBanner_Received", "Admob");
                adReceived();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                adFailed();
                BSAnalyses.getInstance().event("AdBanner_Failed", "Admob");
                if (nextLoadTimer != null) {
                    nextLoadTimer.cancel();
                }
                //                nextLoadTimer = BSTimer.asyncRun(new Runnable() {
                //                    @Override
                //                    public void run() {
                //                        ((AdView) adView).loadAd(new AdRequest.Builder().build());
                //                    }
                //                }, 5000);
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdLeftApplication() {
            }
        });
        ((AdView) adView).loadAd(new AdRequest.Builder().build());
        BSAnalyses.getInstance().event("AdBanner_Request", "Admob");
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

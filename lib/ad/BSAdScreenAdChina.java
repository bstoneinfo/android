package com.bstoneinfo.lib.ad;

import android.app.Activity;

import com.adchina.android.ads.api.AdFsListener;
import com.adchina.android.ads.api.AdFullScreen;
import com.bstoneinfo.lib.common.BSLog;

class BSAdScreenAdChina extends BSAdObject {

    public BSAdScreenAdChina(Activity activity) {
        super(activity, BSAdUtils.getAdScreenAppKey("AdChina"));
    }

    @Override
    void start() {
        BSLog.d("Adchina - fullscreen start");
        final AdFullScreen adFullScreen = new AdFullScreen(activity, appKey);
        adFullScreen.setAdFsListener(new AdFsListener() {

            @Override
            public void onStartFullScreenLandPage() {
                BSLog.d("Adchina - onStartFullScreenLandPage");
            }

            @Override
            public void onReceiveFullScreenAd() {
                BSLog.d("Adchina - onReceiveFullScreenAd");
                adFullScreen.showFs();
                adReceived();
                BSAnalyses.getInstance().event("AdFull_Result", "AdChina_Received");
            }

            @Override
            public void onFinishFullScreenAd() {
                BSLog.d("Adchina - onFinishFullScreenAd");
            }

            @Override
            public void onFailedToReceiveFullScreenAd() {
                BSLog.d("Adchina - onFailedToReceiveFullScreenAd");
                adFailed();
                BSAnalyses.getInstance().event("AdFull_Result", "AdChina_Failed");
            }

            @Override
            public void onEndFullScreenLandpage() {
                BSLog.d("Adchina - onEndFullScreenLandpage");
            }

            @Override
            public void onDisplayFullScreenAd() {
                BSLog.d("Adchina - onDisplayFullScreenAd");
            }

            @Override
            public void onClickFullScreenAd() {
                BSLog.d("Adchina - onClickFullScreenAd");
                BSAnalyses.getInstance().event("AdFull_Result", "AdChina_Click");
            }
        });
        adFullScreen.start();
        BSAnalyses.getInstance().event("AdFull_Request", "AdChina");
    }

}

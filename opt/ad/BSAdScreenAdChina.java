package com.bstoneinfo.opt.ad;

import android.app.Activity;

import com.adchina.android.ads.api.AdFsListener;
import com.adchina.android.ads.api.AdFullScreen;
import com.bstoneinfo.lib.ad.BSAdObject;
import com.bstoneinfo.lib.common.BSLog;

public class BSAdScreenAdChina extends BSAdObject {

    private AdFullScreen adFullScreen;

    public BSAdScreenAdChina(Activity activity, String adUnit) {
        super(activity, adUnit, "adchina_screen");
    }

    @Override
    public void start() {
        BSLog.d("Adchina - fullscreen start");
        adFullScreen = new AdFullScreen(activity, appKey);
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
            }

            @Override
            public void onFinishFullScreenAd() {
                BSLog.d("Adchina - onFinishFullScreenAd");
            }

            @Override
            public void onFailedToReceiveFullScreenAd() {
                BSLog.d("Adchina - onFailedToReceiveFullScreenAd");
                adFailed();
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
                adClicked();
            }
        });
        adFullScreen.start();
        adRequested();
    }

    @Override
    public void destroy() {
        if (adFullScreen != null) {
            adFullScreen.stop();
        }
        super.destroy();
    }

}

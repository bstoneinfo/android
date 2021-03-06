package com.bstoneinfo.opt.ad;

import android.app.Activity;

import com.adchina.android.ads.AdManager;
import com.adchina.android.ads.api.AdBannerListener;
import com.adchina.android.ads.api.AdView;
import com.bstoneinfo.lib.ad.BSAdObject;

public class BSAdBannerAdChina extends BSAdObject {

    public BSAdBannerAdChina(Activity activity, String adUnit) {
        super(activity, adUnit, "adchina_banner");
    }

    @Override
    public void start() {
        if (adView != null) {
            return;
        }
        adView = new AdView(activity, appKey, true, false);
        AdManager.setEnableLbs(true); // 是否开启lbs精确广告定位
        AdManager.setRelateScreenRotate(activity, false); // 是否关心屏幕旋转，详细请查看文档
        AdManager.setAnimation(false); // banner展示是否需要动画
        AdManager.setLogMode(true); // 显示调试日志，发布时请关闭
        AdManager.setCanHardWare(true); // 是否允许打开硬件加速
        AdManager.setExpandToolBar(true); // 设置拓展页初始时展开还是关闭状态
        ((AdView) adView).setAdBannerListener(new AdBannerListener() {
            @Override
            public void onReceiveAd(AdView arg0) {
                adReceived();
            }

            @Override
            public void onFailedToReceiveAd(AdView arg0) {
                adFailed();
            }

            @Override
            public void onClickBanner(AdView arg0) {
                adClicked();
            }
        });
        ((AdView) adView).start();
        adRequested();
    }

}

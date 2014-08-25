package com.bstoneinfo.lib.ad;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSNotificationCenter.BSNotificationEvent;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.ui.BSViewController;

public class BSAdBannerViewController extends BSViewController {

    private final ArrayList<BSAdObject> adObjectArray = new ArrayList<BSAdObject>();
    private final String bannerType;
    private int adIndex = -1;
    private boolean bVerticalShow;

    public BSAdBannerViewController(Context context, final String bannerType) {
        super(new LinearLayout(context));
        ((LinearLayout) getRootView()).setOrientation(LinearLayout.VERTICAL);
        this.bannerType = bannerType;
        JSONArray adTypes = BSAdUtils.getAdBannerType(bannerType);
        for (int i = 0; i < adTypes.length(); i++) {
            String name = adTypes.optString(i);
            addAdObject(name);
        }
    }

    private void addAdObject(String name) {
        Class<? extends BSAdObject> cls = BSAdUtils.bannerAdClassMap.get(name);
        if (cls == null) {
            BSUtils.debugAssert("AdBanner '" + name + "'" + " not found.");
            return;
        }
        BSAdObject fsObj;
        try {
            fsObj = cls.getConstructor(Activity.class).newInstance(getActivity());
        } catch (Exception e) {
            BSUtils.debugAssert("AdBanner '" + name + "'" + " exception: " + e.getMessage());
            return;
        }
        adObjectArray.add(fsObj);
    }

    public void setVerticalShow(boolean bVerticalShow) {
        this.bVerticalShow = bVerticalShow;
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        MarginLayoutParams params = (MarginLayoutParams) getRootView().getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getRootView().setLayoutParams(params);
        startAd();
        BSApplication.defaultNotificationCenter.addObserver(this, BSNotificationEvent.REMOTE_CONFIG_DID_CHANGE, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if (!adObjectArray.isEmpty()) {
                    return;
                }
                JSONArray adTypes = BSAdUtils.getAdBannerType(bannerType);
                for (int i = 0; i < adTypes.length(); i++) {
                    String type = adTypes.optString(i);
                    Class<? extends BSAdObject> cls = BSAdUtils.bannerAdClassMap.get(type);
                    boolean bExist = false;
                    for (BSAdObject adObj : adObjectArray) {
                        if (adObj.getClass() == cls) {
                            bExist = true;
                            break;
                        }
                    }
                    if (!bExist) {
                        addAdObject(type);
                    }
                }
                startAd();
            }
        });
    }

    @Override
    protected void destroy() {
        for (BSAdObject adObject : adObjectArray) {
            adObject.destroy();
        }
        super.destroy();
    }

    private void startAd() {
        for (int i = 0; i < adObjectArray.size(); i++) {
            final int index = i;
            final BSAdObject adObject = adObjectArray.get(i);
            adObject.setAdListener(new BSAdListener() {
                @Override
                public void adReceived() {
                    if (bVerticalShow) {
                        adObject.getAdView().setVisibility(View.VISIBLE);
                    } else {
                        if (adIndex < 0) {
                            adIndex = index;
                            adObject.getAdView().setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void adFailed() {
                    if (bVerticalShow) {
                        adObject.getAdView().setVisibility(View.GONE);
                    } else {
                        if (adIndex == index) {
                            adObject.getAdView().setVisibility(View.GONE);
                            adIndex = -1;
                            for (int i = 0; i < adObjectArray.size(); i++) {
                                BSAdObject adObject = adObjectArray.get(i);
                                if (adObject.isReceived()) {
                                    adIndex = i;
                                    adObject.getAdView().setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
            });
            adObject.start();
            if (adObject.getAdView().getParent() == null) {
                getRootView().addView(adObject.getAdView());
            }
            adObject.getAdView().setVisibility(View.GONE);
        }
    }

}

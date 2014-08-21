package com.bstoneinfo.lib.ad;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import com.bstoneinfo.lib.ui.BSViewController;

public class BSAdBannerViewController extends BSViewController {

    private final ArrayList<BSAdObject> adObjectArray = new ArrayList<BSAdObject>();
    private int adIndex = -1;

    public BSAdBannerViewController(Context context, String bannerName) {
        super(context);
        JSONArray adTypes = BSAdUtils.getAdBannerType(bannerName);
        for (int i = 0; i < adTypes.length(); i++) {
            String type = adTypes.optString(i);
            BSAdObject fsObj;
            if ("Admob".equalsIgnoreCase(type)) {
                fsObj = new BSAdBannerAdmob(getActivity());
            } else if ("AdChina".equalsIgnoreCase(type)) {
                fsObj = new BSAdBannerAdChina(getActivity());
            } else {
                continue;
            }
            adObjectArray.add(fsObj);
        }
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        MarginLayoutParams params = (MarginLayoutParams) getRootView().getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getRootView().setLayoutParams(params);
        startAd();
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
                    if (adIndex < 0) {
                        adIndex = index;
                        adObject.getAdView().setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void adFailed() {
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
            });
            adObject.start();
            if (adObject.getAdView().getParent() == null) {
                getRootView().addView(adObject.getAdView());
            }
            adObject.getAdView().setVisibility(View.GONE);
        }
    }

}

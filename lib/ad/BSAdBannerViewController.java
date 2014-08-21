package com.bstoneinfo.lib.ad;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import com.bstoneinfo.lib.ui.BSViewController;

public class BSAdBannerViewController extends BSViewController {

    private final ArrayList<BSAdObject> adObjectArray = new ArrayList<BSAdObject>();
    private int adIndex = -1;

    public BSAdBannerViewController(Context context, String adPositionTag) {
        super(context);
        JSONArray adTypes = BSAdUtils.getAdType(adPositionTag);
        if (adTypes != null) {
            for (int i = 0; i < adTypes.length(); i++) {
                BSAdObject fsObj;
                if (TextUtils.equals(adPositionTag, "admob")) {
                    fsObj = new BSAdBannerAdmob(getActivity());
                } else if (TextUtils.equals(adPositionTag, "adchina")) {
                    fsObj = new BSAdBannerAdChina(getActivity());
                } else {
                    continue;
                }
                adObjectArray.add(fsObj);
            }
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

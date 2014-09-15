package com.bstoneinfo.lib.ad;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.frame.BSFrame;

public class BSAdBanner extends BSFrame {

    private final ArrayList<BSAdObject> adObjectArray = new ArrayList<BSAdObject>();
    private final String adUnit;
    private int adIndex = -1;
    private boolean bVerticalShow;

    public BSAdBanner(Context context, final String adUnit) {
        super(new LinearLayout(context));
        ((LinearLayout) getRootView()).setOrientation(LinearLayout.VERTICAL);
        this.adUnit = adUnit;
        JSONArray adTypes = BSAdUtils.getAdTypes(adUnit);
        for (int i = 0; i < adTypes.length(); i++) {
            String name = adTypes.optString(i);
            addAdObject(name);
        }
    }

    private void addAdObject(String name) {
        BSAdObject fsObj = BSAdUtils.createAdObject(getActivity(), adUnit, name);
        if (fsObj != null) {
            BSLog.d("addAdObject " + fsObj.toString());
            adObjectArray.add(fsObj);
        }
    }

    public void setVerticalShow(boolean bVerticalShow) {
        this.bVerticalShow = bVerticalShow;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        MarginLayoutParams params = (MarginLayoutParams) getRootView().getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getRootView().setLayoutParams(params);
        startAd();
        BSApplication.defaultNotificationCenter.addObserver(this, BSObserverEvent.REMOTE_CONFIG_DID_CHANGE, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if (!adObjectArray.isEmpty()) {
                    return;
                }
                JSONArray adTypes = BSAdUtils.getAdTypes(adUnit);
                for (int i = 0; i < adTypes.length(); i++) {
                    String type = adTypes.optString(i);
                    Class<? extends BSAdObject> cls = BSAdUtils.adClassMap.get(type);
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
    protected void onDestroy() {
        for (BSAdObject adObject : adObjectArray) {
            adObject.destroy();
        }
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
            if (adObject.getAdView() != null) {
                if (adObject.getAdView().getParent() == null) {
                    getRootView().addView(adObject.getAdView());
                }
                adObject.getAdView().setVisibility(View.GONE);
            }
        }
    }

}

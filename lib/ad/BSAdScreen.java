package com.bstoneinfo.lib.ad;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;

import com.bstoneinfo.lib.common.BSTimer;
import com.bstoneinfo.lib.common.BSUtils;

public class BSAdScreen {

    private final ArrayList<BSAdObject> adObjectArray = new ArrayList<BSAdObject>();
    private int adIndex = 0;
    private BSTimer asyncRun;

    public BSAdScreen(Activity activity) {
        JSONArray adArray = BSAdUtils.getAdScreenType();
        for (int i = 0; i < adArray.length(); i++) {
            String type = adArray.optString(i);
            Class<? extends BSAdObject> cls = BSAdUtils.bannerAdClassMap.get(type);
            if (cls == null) {
                BSUtils.debugAssert("AdBanner Type '" + type + "'" + " not found.");
                continue;
            }
            BSAdObject fsObj;
            try {
                fsObj = cls.getConstructor(Activity.class).newInstance(activity);
            } catch (Exception e) {
                BSUtils.debugAssert("AdBanner Type '" + type + "'" + " exception: " + e.getMessage());
                continue;
            }
            adObjectArray.add(fsObj);
        }
    }

    public void start() {
        if (adIndex < 0 || adObjectArray.isEmpty()) {
            return;
        }
        if (adIndex >= adObjectArray.size()) {//第一轮所有ad都取失败，等待一段时间重新再取
            adIndex = -1;
            JSONObject fsConfig = BSAdUtils.getAdScreenConfig();
            int delaySeconds = fsConfig.optInt("CycleInterval", 8);
            if (delaySeconds > 0) {
                delayRun(new Runnable() {
                    @Override
                    public void run() {
                        adIndex = 0;
                        start();
                    }
                }, delaySeconds * 1000);
            }
            return;
        }
        BSAdObject adObject = adObjectArray.get(adIndex);
        adObject.setAdListener(new BSAdListener() {
            @Override
            public void adReceived() {
                adIndex = -1;
            }

            @Override
            public void adFailed() {
                adIndex++;
                start();
            }
        });
        adObject.start();
    }

    private void delayRun(Runnable runnable, int delayMillis) {
        if (asyncRun != null) {
            asyncRun.cancel();
        }
        asyncRun = BSTimer.asyncRun(runnable, delayMillis);
    }

    public void destroy() {
        if (asyncRun != null) {
            asyncRun.cancel();
            asyncRun = null;
        }
    }

}

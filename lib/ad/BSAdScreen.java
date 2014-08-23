package com.bstoneinfo.lib.ad;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;

import com.bstoneinfo.lib.common.BSTimer;

public class BSAdScreen {

    private final ArrayList<BSAdObject> adObjectArray = new ArrayList<BSAdObject>();
    private int adIndex = 0;
    private BSTimer asyncRun;

    public BSAdScreen(Activity activity) {
        JSONArray adArray = BSAdUtils.getAdScreenType();
        for (int i = 0; i < adArray.length(); i++) {
            String name = adArray.optString(i);
            if ("Admob".equalsIgnoreCase(name)) {
                adObjectArray.add(new BSAdScreenAdmob(activity));
            } else if ("AdChina".equalsIgnoreCase(name)) {
                adObjectArray.add(new BSAdScreenAdChina(activity));
            }
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

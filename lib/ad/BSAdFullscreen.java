package com.bstoneinfo.lib.ad;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;

import com.bstoneinfo.lib.common.BSTimer;

public class BSAdFullscreen {

    private final ArrayList<BSAdObject> adObjectArray = new ArrayList<BSAdObject>();
    private int adIndex = 0;
    private BSTimer asyncRun;

    public BSAdFullscreen(Activity activity) {
        JSONObject fsJson = BSAdUtils.getFullScreenConfig();
        if (fsJson != null) {
            JSONArray adArray = fsJson.optJSONArray("AdType");
            if (adArray != null) {
                for (int i = 0; i < adArray.length(); i++) {
                    String name = adArray.optString(i);
                    if (TextUtils.equals(name, "adchina")) {
                        adObjectArray.add(new BSAdFSAdChina(activity));
                    }
                }
            }
        }
    }

    public void start() {
        if (adIndex < 0 || adObjectArray.isEmpty()) {
            return;
        }
        if (adIndex >= adObjectArray.size()) {//第一轮所有ad都取失败，等待一段时间重新再取
            adIndex = -1;
            int delaySeconds = 5;
            JSONObject fsConfig = BSAdUtils.getFullScreenConfig();
            if (fsConfig != null) {
                delaySeconds = fsConfig.optInt("CycleInterval", 5);
            }
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

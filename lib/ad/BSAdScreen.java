package com.bstoneinfo.lib.ad;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;

import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSNotificationCenter.BSNotificationEvent;
import com.bstoneinfo.lib.common.BSTimer;
import com.bstoneinfo.lib.common.BSUtils;

public class BSAdScreen {

    private final ArrayList<BSAdObject> adObjectArray = new ArrayList<BSAdObject>();
    private int adIndex = 0;
    private BSTimer asyncRun;

    public BSAdScreen(final Activity activity) {
        JSONArray adArray = BSAdUtils.getAdScreenType();
        for (int i = 0; i < adArray.length(); i++) {
            String type = adArray.optString(i);
            addAdObject(type, activity);
        }
        start();
        BSApplication.defaultNotificationCenter.addObserver(this, BSNotificationEvent.REMOTE_CONFIG_DID_CHANGE, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if (!adObjectArray.isEmpty()) {
                    return;
                }
                JSONArray adTypes = BSAdUtils.getAdScreenType();
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
                        addAdObject(type, activity);
                    }
                }
                start();
            }
        });
    }

    private void addAdObject(String type, Activity activity) {
        Class<? extends BSAdObject> cls = BSAdUtils.screenAdClassMap.get(type);
        if (cls == null) {
            BSUtils.debugAssert("AdScreen Type '" + type + "'" + " not found.");
            return;
        }
        BSAdObject fsObj;
        try {
            fsObj = cls.getConstructor(Activity.class).newInstance(activity);
        } catch (Exception e) {
            BSUtils.debugAssert("AdScreen Type '" + type + "'" + " exception: " + e.getMessage());
            return;
        }
        adObjectArray.add(fsObj);
    }

    private void start() {
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

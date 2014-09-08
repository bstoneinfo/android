package com.bstoneinfo.lib.ad;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;

import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.common.BSTimer;

public class BSAdScreen {

    private final ArrayList<BSAdObject> adObjectArray = new ArrayList<BSAdObject>();
    private final String adUnit;
    private int adIndex = 0;
    private BSTimer asyncRun;

    public BSAdScreen(final Activity _activity, String _adUnit) {
        adUnit = _adUnit;
        JSONArray adArray = BSAdUtils.getAdTypes(_adUnit);
        for (int i = 0; i < adArray.length(); i++) {
            String type = adArray.optString(i);
            addAdObject(type, _activity);
        }
        start();
        BSApplication.defaultNotificationCenter.addObserver(this, BSObserverEvent.REMOTE_CONFIG_DID_CHANGE, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                BSLog.d("REMOTE_CONFIG_DID_CHANGE");
                BSLog.d("adObjectArray:" + adObjectArray.toString());
                if (!adObjectArray.isEmpty()) {
                    return;
                }
                JSONArray adTypes = BSAdUtils.getAdTypes(adUnit);
                BSLog.d("adTypes:" + adTypes.toString());
                for (int i = 0; i < adTypes.length(); i++) {
                    String type = adTypes.optString(i);
                    addAdObject(type, _activity);
                }
                start();
            }
        });
    }

    private void addAdObject(String name, Activity activity) {
        BSAdObject fsObj = BSAdUtils.createAdObject(activity, adUnit, name);
        if (fsObj != null) {
            BSLog.d("addAdObject " + fsObj.toString());
            adObjectArray.add(fsObj);
        }
    }

    private void start() {
        BSLog.d("start(): adIndex=" + adIndex);
        if (adIndex < 0 || adObjectArray.isEmpty()) {
            return;
        }
        if (adIndex >= adObjectArray.size()) {//第一轮所有ad都取失败，等待一段时间重新再取
            adIndex = -1;
            JSONObject fsConfig = BSAdUtils.getAdUnitConfig(adUnit);
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
        final BSAdObject adObject = adObjectArray.get(adIndex);
        adObject.setAdListener(new BSAdListener() {
            @Override
            public void adReceived() {
                BSLog.d("adReceived " + adObject.getClass());
                adIndex = -1;
            }

            @Override
            public void adFailed() {
                BSLog.d("adFailed index=" + adIndex + " " + adObject.getClass());
                adIndex++;
                start();
            }
        });
        adObject.start();
        BSLog.d("adObject.start() " + adObject.getClass());
    }

    private void delayRun(Runnable runnable, int delayMillis) {
        if (asyncRun != null) {
            asyncRun.cancel();
        }
        asyncRun = BSTimer.asyncRun(runnable, delayMillis);
    }

    public void destroy() {
        BSApplication.defaultNotificationCenter.removeObservers(this);
        if (asyncRun != null) {
            asyncRun.cancel();
            asyncRun = null;
        }
    }

}

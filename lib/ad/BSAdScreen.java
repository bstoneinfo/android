package com.bstoneinfo.lib.ad;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSLog;
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
                BSLog.d("adScreen", "REMOTE_CONFIG_DID_CHANGE");
                BSLog.d("adScreen", "adObjectArray:" + adObjectArray.toString());
                if (!adObjectArray.isEmpty()) {
                    return;
                }
                JSONArray adTypes = BSAdUtils.getAdScreenType();
                BSLog.d("adScreen", "adTypes:" + adTypes.toString());
                for (int i = 0; i < adTypes.length(); i++) {
                    String type = adTypes.optString(i);
                    addAdObject(type, activity);
                }
                start();
            }
        });
    }

    private void addAdObject(String type, Activity activity) {
        Class<? extends BSAdObject> cls = BSAdUtils.screenAdClassMap.get(type);
        BSLog.d("adScreen", "type=" + type + " cls=" + cls);
        if (cls == null) {
            String msg = "AdScreen Type '" + type + "'" + " not found.";
            Log.d("adScreen", msg);
            BSUtils.debugAssert(msg);
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        BSAdObject fsObj;
        try {
            fsObj = cls.getConstructor(Activity.class).newInstance(activity);
        } catch (Exception e) {
            String msg = "AdScreen Type '" + type + "'" + " exception: " + e.getMessage() + " " + e.toString();
            Log.d("adScreen", msg);
            BSUtils.debugAssert(msg);
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        BSLog.d("adScreen", "add " + fsObj.toString());
        adObjectArray.add(fsObj);
    }

    private void start() {
        BSLog.d("adScreen", "start(): adIndex=" + adIndex);
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
                BSLog.d("adScreen", "adReceived");
                adIndex = -1;
            }

            @Override
            public void adFailed() {
                BSLog.d("adScreen", "adFailed index=" + adIndex);
                adIndex++;
                start();
            }
        });
        adObject.start();
        BSLog.d("adScreen", "adObject.start() " + adObject.getClass());
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

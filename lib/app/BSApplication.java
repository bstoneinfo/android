package com.bstoneinfo.lib.app;

import org.json.JSONObject;

import android.app.Application;
import android.content.SharedPreferences;

import com.bstoneinfo.lib.common.BSLooperThread;
import com.bstoneinfo.lib.common.BSObserverCenter;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.connection.BSConnectionQueue;

public class BSApplication extends Application {

    private static BSApplication instance;
    public static final BSObserverCenter defaultNotificationCenter = new BSObserverCenter();
    public static final BSConnectionQueue defaultConnnectionQueue = new BSConnectionQueue(10);
    public static final BSLooperThread fileThread = new BSLooperThread("FileThread");
    public static final BSLooperThread databaseThread = new BSLooperThread("DatabaseThread");
    private BSRemoteConfig mRemoteConfig;
    private BSVersionManager mVersionManager;
    private boolean bRunningForeground;

    public BSApplication() {
        super();
        instance = this;
    }

    public static BSApplication getApplication() {
        return instance;
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return getSharedPreferences(getPackageName(), 0);
    }

    public BSVersionManager getVersionManager() {
        return mVersionManager;
    }

    public JSONObject getRemoteConfig() {
        return mRemoteConfig.mConfigJSON;
    }

    public void setRemoteConfigURL(String url) {
        mRemoteConfig.mRemoteConfigUrl = url;
    }

    /*
     * 返回程序是否在前台运行
     */
    public boolean isRunningForeground() {
        return bRunningForeground;
    }

    public void checkAppStateEvent() {
        boolean lastState = bRunningForeground;
        bRunningForeground = BSUtils.isAppInForeground();
        if (lastState != bRunningForeground) {
            if (bRunningForeground) {
                defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.APP_ENTER_FOREGROUND);
            } else {
                defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.APP_ENTER_BACKGROUND);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mVersionManager = new BSVersionManager();
        mRemoteConfig = new BSRemoteConfig();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        BSApplication.defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.LOW_MEMORY_WARNING);
    }

}

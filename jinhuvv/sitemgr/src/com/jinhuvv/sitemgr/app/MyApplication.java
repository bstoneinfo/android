package com.jinhuvv.sitemgr.app;

import com.bstoneinfo.lib.app.BSApplication;

import custom.Config;

public class MyApplication extends BSApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        setRemoteConfigURL(Config.remoteConfigURL);
        Config.init();
    }
}

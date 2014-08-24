package com.bstoneinfo.fashion.app;

import com.bstoneinfo.lib.common.BSApplication;

import custom.Config;

public class MyApplication extends BSApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        setRemoteConfigURL(Config.remoteConfigURL);
        Config.init();
    }
}

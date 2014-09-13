package com.bstoneinfo.fashion.app;

import com.bstoneinfo.fashion.data.MailManager;
import com.bstoneinfo.lib.app.BSApplication;

import custom.Config;

public class MyApplication extends BSApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        setRemoteConfigURL(Config.remoteConfigURL);
        Config.init();
        MailManager.getInstance();
    }

}

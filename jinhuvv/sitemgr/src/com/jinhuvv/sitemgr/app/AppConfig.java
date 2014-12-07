package com.jinhuvv.sitemgr.app;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.app.BSApplication;

public class AppConfig {

    public static long getServerTime() {
        return System.currentTimeMillis();
    }

    private static String host = null;

    public static String getHost() {
        if (host != null) {
            return host;
        }
        int hostIndex = BSApplication.getApplication().getDefaultPreferences().getInt("hostIndex", -1);
        if (hostIndex < 0) {
            hostIndex = (int) (Math.random() * 100);
            BSApplication.getApplication().getDefaultPreferences().edit().putInt("hostIndex", hostIndex).commit();
        }
        JSONObject jsonConfig = BSApplication.getApplication().getRemoteConfig();
        JSONArray jsonArray = jsonConfig.optJSONArray("server");
        if (jsonArray != null && jsonArray.length() > 0) {
            host = jsonArray.optString(hostIndex % jsonArray.length());
        }
        if (TextUtils.isEmpty(host)) {
            host = "www.fa1000.com";
        }
        BSAnalyses.getInstance().event("server", host);
        return host;
    }

}

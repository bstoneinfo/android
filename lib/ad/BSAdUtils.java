package com.bstoneinfo.lib.ad;

import org.json.JSONObject;

import com.bstoneinfo.lib.common.BSApplication;

public class BSAdUtils {

    public static JSONObject getAdConfig() {
        return BSApplication.getApplication().getRemoteConfig().optJSONObject("Ad");
    }

    public static String getAdKey(String keyName) {
        JSONObject jsonAd = getAdConfig();
        if (jsonAd != null) {
            JSONObject jsonKey = jsonAd.optJSONObject("AppKey");
            if (jsonKey != null) {
                return jsonKey.optString(keyName);
            }
        }
        return "";
    }

    public static JSONObject getFullScreenConfig() {
        JSONObject jsonAd = getAdConfig();
        if (jsonAd != null) {
            return jsonAd.optJSONObject("FullScreen");
        }
        return null;
    }

}

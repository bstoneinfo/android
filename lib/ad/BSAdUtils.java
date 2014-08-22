package com.bstoneinfo.lib.ad;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bstoneinfo.lib.common.BSApplication;

public class BSAdUtils {

    public static JSONObject optJsonObject(JSONObject jsonObject, String name) {
        JSONObject jo = jsonObject.optJSONObject(name);
        if (jo == null) {
            jo = new JSONObject();
        }
        return jo;
    }

    public static JSONArray optJsonArray(JSONObject jsonObject, String name) {
        JSONArray ja = jsonObject.optJSONArray(name);
        if (ja == null) {
            ja = new JSONArray();
        }
        return ja;
    }

    public static JSONObject getAdConfig() {
        JSONObject jsonAd = optJsonObject(BSApplication.getApplication().getRemoteConfig(), "Ad");
        return jsonAd;
    }

    public static JSONObject getAdScreenConfig() {
        JSONObject jsonAd = getAdConfig();
        return optJsonObject(jsonAd, "Screen");
    }

    public static String getAdScreenAppKey(String adType) {
        JSONObject jsonKey = optJsonObject(getAdScreenConfig(), "AppKey");
        return jsonKey.optString(adType);
    }

    public static JSONArray getAdScreenType() {
        return optJsonArray(getAdScreenConfig(), "AdType");
    }

    public static JSONObject getAdBannerConfig() {
        JSONObject jsonAd = getAdConfig();
        return optJsonObject(jsonAd, "Banner");
    }

    public static String getAdBannerAppKey(String adType) {
        JSONObject jsonKey = optJsonObject(getAdBannerConfig(), "AppKey");
        return jsonKey.optString(adType);
    }

    public static JSONArray getAdBannerType(String bannerName) {
        JSONObject jsonBanner = optJsonObject(getAdBannerConfig(), bannerName);
        return optJsonArray(jsonBanner, "AdType");
    }
}
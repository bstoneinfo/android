package com.bstoneinfo.lib.ad;

import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSLog;

public class BSAdUtils {

    static final HashMap<String, Class<? extends BSAdObject>> screenAdClassMap = new HashMap<String, Class<? extends BSAdObject>>();
    static final HashMap<String, Class<? extends BSAdObject>> bannerAdClassMap = new HashMap<String, Class<? extends BSAdObject>>();

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

    public static boolean checkAdScreenFilter(String adType) {
        JSONObject jsonFilter = optJsonObject(getAdScreenConfig(), "Filter");
        return checkAdFilter(jsonFilter, "adScreen", adType);
    }

    public static boolean checkAdBannerFilter(String bannerName, String adType) {
        JSONObject jsonBanner = optJsonObject(getAdBannerConfig(), bannerName);
        JSONObject jsonFilter = optJsonObject(jsonBanner, "Filter");
        return checkAdFilter(jsonFilter, "adBanner", adType);
    }

    private static boolean checkAdFilter(JSONObject jsonFilter, String unitName, String adType) {
        JSONObject jsonAd = optJsonObject(jsonFilter, adType);
        JSONArray languageArray = optJsonArray(jsonAd, "language");
        if (languageArray.length() > 0) {
            int index = 0;
            while (index < languageArray.length()) {
                if (TextUtils.equals(Locale.getDefault().getLanguage(), languageArray.optString(index))) {
                    break;
                }
                index++;
            }
            if (index == languageArray.length()) {
                BSLog.e(unitName, adType + " not in language filter " + languageArray.toString());
                return false;
            }
        }
        return true;
    }

    public static int getScreenAdPresentSecond() {
        return getAdScreenConfig().optInt("ScreenAdPresentSecond", 10);
    }

    public static void registerAdScreen(String adType, Class<? extends BSAdObject> cls) {
        screenAdClassMap.put(adType.toLowerCase(), cls);
    }

    public static void registerAdBanner(String adType, Class<? extends BSAdObject> cls) {
        bannerAdClassMap.put(adType.toLowerCase(), cls);
    }
}

package com.bstoneinfo.lib.ad;

import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSUtils;

public class BSAdUtils {

    static final HashMap<String, Class<? extends BSAdObject>> adClassMap = new HashMap<String, Class<? extends BSAdObject>>();

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

    public static JSONObject getAdUnitConfig(String adUnit) {
        JSONObject jsonAd = getAdConfig();
        return optJsonObject(jsonAd, adUnit);
    }

    public static String getAdAppKey(String adUnit, String adType) {
        JSONObject jsonKey = optJsonObject(getAdUnitConfig(adUnit), "AppKey");
        return jsonKey.optString(adType);
    }

    public static JSONArray getAdTypes(String adUnit) {
        return optJsonArray(getAdUnitConfig(adUnit), "AdType");
    }

    public static boolean checkAdFilter(String adUnit, String adType) {
        JSONObject jsonFilter = optJsonObject(getAdUnitConfig(adUnit), "Filter");
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
                BSLog.e(adType + "of '" + adUnit + "' not in language filter " + languageArray.toString());
                return false;
            }
        }
        return true;
    }

    public static BSAdObject createAdObject(Activity activity, String adUnit, String adName) {
        Class<? extends BSAdObject> cls = BSAdUtils.adClassMap.get(adName);
        if (cls == null) {
            String msg = "AdBanner '" + adName + "'" + " not found.";
            Log.e("adBanner", msg);
            BSUtils.debugAssert(msg);
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            return null;
        }
        //检查filter
        if (!BSAdUtils.checkAdFilter(adUnit, adName)) {
            return null;
        }
        BSAdObject fsObj;
        try {
            fsObj = cls.getConstructor(Activity.class, String.class).newInstance(activity, adUnit);
        } catch (Exception e) {
            String msg = "AdBanner '" + adName + "' " + cls + " exception: " + e.getMessage() + " " + e.toString();
            Log.e("adBanner", msg);
            BSUtils.debugAssert(msg);
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            return null;
        }
        return fsObj;
    }

    public static void registerAdClass(String adType, Class<? extends BSAdObject> cls) {
        adClassMap.put(adType, cls);
    }
}

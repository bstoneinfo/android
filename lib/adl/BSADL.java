package com.bstoneinfo.lib.adl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.frame.BSActivity;
import com.bstoneinfo.lib.frame.BSFrame;
import com.bstoneinfo.lib.frame.BSTabbedFrame;

public class BSADL {

    public static void loadUI(Context context, String adlName) {

    }

    private static void parseAdlUIFile(Context context, String adlName) {
        final String adlString = BSApplication.getApplication().getFilesDir() + "/adl/ui/" + adlName + ".json";
        JSONObject jsonADL = new JSONObject();
        try {
            jsonADL = new JSONObject(adlString);
        } catch (JSONException e) {
        }
        String sClass = jsonADL.optString("class");
        if (TextUtils.equals(sClass, "tabview")) {
            parseTabViewUI(context, jsonADL);
        }
    }

    private static BSTabbedFrame parseTabViewUI(Context context, JSONObject jsonADL) {
        JSONArray jsonItems = BSUtils.optJsonArray(jsonADL, "items");
        BSFrame childFrames[] = new BSFrame[jsonItems.length()];
        String titles[] = new String[jsonItems.length()];
        int drawableIDs[] = new int[jsonItems.length()];
        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject jsonItem = jsonItems.optJSONObject(i);
            if (jsonItem == null) {
                continue;
            }
            titles[i] = jsonItem.optString("title");
        }
        return new BSTabbedFrame(context, childFrames, titles, drawableIDs, jsonADL.optInt("tabbarHeight", BSActivity.dip2px(60)), jsonADL.optInt("defaultIndex", 0));
    }
}

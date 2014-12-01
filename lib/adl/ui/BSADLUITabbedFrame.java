package com.bstoneinfo.lib.adl.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.bstoneinfo.lib.adl.BSADL;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.frame.BSActivity;
import com.bstoneinfo.lib.frame.BSFrame;
import com.bstoneinfo.lib.frame.BSTabbedFrame;

public class BSADLUITabbedFrame extends BSADLUIFrame {

    public BSADLUITabbedFrame(Context context, JSONObject jsonADL, String adlPathName) {
        super(context, jsonADL, adlPathName);
    }

    @Override
    public BSFrame parse() {
        JSONArray jsonItems = BSUtils.optJsonArray(jsonADL, "items");
        BSFrame childFrames[] = new BSFrame[jsonItems.length()];
        String titles[] = new String[jsonItems.length()];
        int normalDrawableIDs[] = new int[jsonItems.length()];
        int selectedDrawableIDs[] = new int[jsonItems.length()];
        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject jsonItem = jsonItems.optJSONObject(i);
            if (jsonItem == null) {
                BSLog.d(BSADL.TAG, "");
                continue;
            }
            titles[i] = jsonItem.optString("title");
            //            normalDrawableIDs
        }
        return new BSTabbedFrame(context, childFrames, titles, normalDrawableIDs, selectedDrawableIDs, jsonADL.optInt("tabbarHeight", BSActivity.dip2px(60)), jsonADL.optInt(
                "defaultIndex", 0));

    }
}

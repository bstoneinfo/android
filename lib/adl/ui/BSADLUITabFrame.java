package com.bstoneinfo.lib.adl.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.bstoneinfo.lib.frame.BSActivity;
import com.bstoneinfo.lib.frame.BSFrame;
import com.bstoneinfo.lib.frame.BSTabbedFrame;

public class BSADLUITabFrame extends BSADLUIFrame {

    public BSADLUITabFrame(Context context, String adlName, JSONObject jsonADL) {
        super(context, adlName, jsonADL);
    }

    @Override
    public BSFrame parse() {
        JSONArray jsonItems = getArray(jsonADL, "items", true);
        BSFrame childFrames[] = new BSFrame[jsonItems.length()];
        String titles[] = new String[jsonItems.length()];
        int normalDrawableIDs[] = new int[jsonItems.length()];
        int selectDrawableIDs[] = new int[jsonItems.length()];
        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject jsonItem = getArrayObject("items", jsonItems, i);
            titles[i] = jsonItem.optString("title");
            normalDrawableIDs[i] = getDrawableID(jsonItem, "iconNormal", true);
            selectDrawableIDs[i] = getDrawableID(jsonItem, "iconSelect", false);
            childFrames[i] = new BSFrame(context);
        }
        return new BSTabbedFrame(context, childFrames, titles, normalDrawableIDs, selectDrawableIDs, jsonADL.optInt("tabbarHeight", BSActivity.dip2px(60)), jsonADL.optInt(
                "defaultIndex", 0));
    }

}

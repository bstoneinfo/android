package com.bstoneinfo.lib.adl.ui;

import org.json.JSONObject;

import android.content.Context;

import com.bstoneinfo.lib.frame.BSFrame;

public class BSADLUIFrame extends BSADLUIObject {

    public BSADLUIFrame(Context context, String adlName, JSONObject jsonADL) {
        super(context, adlName, jsonADL);
    }

    public BSFrame parse() {
        BSFrame frame = new BSFrame(context);
        parseCommonAttribute(frame.getRootView());
        return frame;
    }

}

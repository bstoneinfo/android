package com.bstoneinfo.lib.adl.ui;

import org.json.JSONObject;

import android.content.Context;

import com.bstoneinfo.lib.frame.BSFrame;

public abstract class BSADLUIFrame extends BSADLUIObject {

    public BSADLUIFrame(Context context, JSONObject jsonADL, String adlPathName) {
        super(context, jsonADL, adlPathName);
    }

    public abstract BSFrame parse();

}

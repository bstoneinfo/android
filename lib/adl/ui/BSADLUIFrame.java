package com.bstoneinfo.lib.adl.ui;

import org.json.JSONObject;

import android.content.Context;

import com.bstoneinfo.lib.frame.BSFrame;

public abstract class BSADLUIFrame {

    protected final Context context;
    protected final JSONObject jsonADL;
    protected final String adlPathName;

    public BSADLUIFrame(Context context, JSONObject jsonADL, String adlPathName) {
        this.context = context;
        this.jsonADL = jsonADL;
        this.adlPathName = adlPathName;
    }

    public abstract BSFrame parse();
}

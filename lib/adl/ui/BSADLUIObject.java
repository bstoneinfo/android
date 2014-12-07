package com.bstoneinfo.lib.adl.ui;

import org.json.JSONObject;

import android.content.Context;

public abstract class BSADLUIObject {

    protected final Context context;
    protected final JSONObject jsonADL;
    protected final String adlPathName;

    public BSADLUIObject(Context context, JSONObject jsonADL, String adlPathName) {
        this.context = context;
        this.jsonADL = jsonADL;
        this.adlPathName = adlPathName;
    }

    protected int getDrawableID(String name) {
        return context.getResources().getIdentifier(context.getPackageName() + ":drawable/" + name, null, null);
    }
}

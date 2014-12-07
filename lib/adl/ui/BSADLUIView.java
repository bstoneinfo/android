package com.bstoneinfo.lib.adl.ui;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;

public abstract class BSADLUIView extends BSADLUIObject {

    public BSADLUIView(Context context, JSONObject jsonADL, String adlPathName) {
        super(context, jsonADL, adlPathName);
    }

    public abstract View parse();

}

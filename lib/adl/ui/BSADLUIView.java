package com.bstoneinfo.lib.adl.ui;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;

public abstract class BSADLUIView extends BSADLUIObject {

    public BSADLUIView(Context context, String adlName, JSONObject jsonADL) {
        super(context, adlName, jsonADL);
    }

    public abstract View parse();

}

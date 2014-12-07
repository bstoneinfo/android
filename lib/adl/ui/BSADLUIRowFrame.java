package com.bstoneinfo.lib.adl.ui;

import org.json.JSONObject;

import android.content.Context;

public class BSADLUIRowFrame extends BSADLUILinearFrame {

    public BSADLUIRowFrame(Context context, String adlName, JSONObject jsonADL) {
        super(context, adlName, jsonADL, true);
    }

}

package com.bstoneinfo.lib.adl.ui;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;

public class BSADLUIView extends BSADLUIObject {

    public BSADLUIView(Context context, String adlName, JSONObject jsonADL) {
        super(context, adlName, jsonADL);
    }

    public View parse() {
        View view = new View(context);
        parseCommonAttribute(view);
        return view;
    }

}

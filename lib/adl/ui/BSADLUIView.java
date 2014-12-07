package com.bstoneinfo.lib.adl.ui;

import org.json.JSONObject;

import android.view.View;

import com.bstoneinfo.lib.frame.BSFrame;

public class BSADLUIView extends BSADLUIObject {

    protected BSFrame parentFrame;

    public BSADLUIView(BSFrame parentFrame, String adlName, JSONObject jsonADL) {
        super(parentFrame.getContext(), adlName, jsonADL);
        this.parentFrame = parentFrame;
    }

    public View parse() {
        View view = new View(context);
        parseCommonAttribute(view);
        return view;
    }

}

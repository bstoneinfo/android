package com.bstoneinfo.lib.adl.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.adl.BSADL;
import com.bstoneinfo.lib.frame.BSFrame;

public class BSADLUILinearFrame extends BSADLUIFrame {

    private boolean bVert;

    public BSADLUILinearFrame(Context context, String adlName, JSONObject jsonADL, boolean bVert) {
        super(context, adlName, jsonADL);
        this.bVert = bVert;
    }

    @Override
    public BSFrame parse() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(bVert ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        parseCommonAttribute(linearLayout);
        JSONArray jsonItems = getArray(jsonADL, "items", false);
        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject jsonItem = getArrayObject("items", jsonItems, i);
            View view = BSADL.loadView(context, adlName, "items[" + i + "]", jsonItem);
            LinearLayout.LayoutParams lp;
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            int weight = jsonItem.optInt("weight", 0);
            if (bVert) {
                height = jsonItem.optInt("height", 0);
            } else {
                width = jsonItem.optInt("width", 0);
            }
            if (weight > 0) {
                lp = new LinearLayout.LayoutParams(width, height, weight);
            } else {
                lp = new LinearLayout.LayoutParams(width, height);
            }
            linearLayout.addView(view, lp);
        }
        return new BSFrame(linearLayout);
    }
}

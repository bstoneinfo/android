package com.bstoneinfo.lib.adl.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.adl.BSADL;
import com.bstoneinfo.lib.frame.BSActivity;
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
            String itemNodeName = "items[" + i + "]";
            View view = BSADL.loadView(context, adlName, itemNodeName, jsonItem, true);
            if (view == null) {
                BSFrame frame = BSADL.loadFrame(context, adlName, itemNodeName, jsonItem, false);
                view = frame.getRootView();
                view.setTag(frame);
            }
            LinearLayout.LayoutParams lp;
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            int weight = jsonItem.optInt("weight", 0);
            if (bVert) {
                height = BSActivity.dip2px(jsonItem.optInt("height", 0));
            } else {
                width = BSActivity.dip2px(jsonItem.optInt("width", 0));
            }
            if (weight > 0) {
                if (bVert) {
                    height = 0;
                } else {
                    width = 0;
                }
                lp = new LinearLayout.LayoutParams(width, height, weight);
            } else {
                lp = new LinearLayout.LayoutParams(width, height);
            }
            parseMargin(lp, jsonItem);
            linearLayout.addView(view, lp);
        }
        return new BSFrame(linearLayout);
    }

}

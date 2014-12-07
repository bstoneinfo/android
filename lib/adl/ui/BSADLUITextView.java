package com.bstoneinfo.lib.adl.ui;

import org.json.JSONObject;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.bstoneinfo.lib.frame.BSFrame;

public class BSADLUITextView extends BSADLUIView {

    public BSADLUITextView(BSFrame parentFrame, String adlName, JSONObject jsonADL) {
        super(parentFrame, adlName, jsonADL);
    }

    @Override
    public View parse() {
        TextView textView = new TextView(context);
        parseCommonAttribute(textView);
        String textString = jsonADL.optString("text", null);
        if (textString != null) {
            textView.setText(textString);
        }
        int fontsize = jsonADL.optInt("fontsize", 12);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontsize);
        int fontcolor = getColor(jsonADL, "fontcolor", Color.BLACK);
        textView.setTextColor(fontcolor);
        int gravity = getAlign(jsonADL, "");
        if (gravity > 0) {
            textView.setGravity(gravity);
        }
        return textView;
    }
}

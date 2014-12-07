package com.bstoneinfo.lib.adl.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.TextView;

import com.bstoneinfo.lib.frame.BSFrame;

public class BSADLUIButton extends BSADLUITextView {

    public BSADLUIButton(BSFrame parentFrame, String adlName, JSONObject jsonADL) {
        super(parentFrame, adlName, jsonADL);
    }

    @Override
    public View parse() {
        try {
            jsonADL.put("halign", "center");
            jsonADL.put("valign", "center");
        } catch (JSONException e) {
        }
        TextView textView = (TextView) super.parse();
        int normalColor = getColor(jsonADL, "colorNormal", Integer.MIN_VALUE);
        if (normalColor != Integer.MIN_VALUE) {
            int pressColor = getColor(jsonADL, "colorPress", Integer.MIN_VALUE);
            if (pressColor != Integer.MIN_VALUE) {
                StateListDrawable selectorDrawable = new StateListDrawable();
                selectorDrawable.addState(new int[] { -android.R.attr.state_pressed }, new ColorDrawable(normalColor));
                selectorDrawable.addState(new int[] { android.R.attr.state_pressed }, new ColorDrawable(pressColor));
                textView.setBackgroundDrawable(selectorDrawable);
            } else {
                textView.setBackgroundColor(normalColor);
            }
        }
        textView.setClickable(true);
        return textView;
    }
}

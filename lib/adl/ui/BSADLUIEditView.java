package com.bstoneinfo.lib.adl.ui;

import org.json.JSONObject;

import android.view.View;
import android.widget.EditText;

import com.bstoneinfo.lib.frame.BSFrame;

public class BSADLUIEditView extends BSADLUIView {

    public BSADLUIEditView(BSFrame parentFrame, String adlName, JSONObject jsonADL) {
        super(parentFrame, adlName, jsonADL);
    }

    @Override
    public View parse() {
        EditText editView = new EditText(context);
        parseCommonAttribute(editView);
        String textString = jsonADL.optString("text", null);
        if (textString != null) {
            editView.setText(textString);
        }
        String hint = jsonADL.optString("hint", null);
        if (hint != null) {
            editView.setHint(hint);
        }
        return editView;
    }
}

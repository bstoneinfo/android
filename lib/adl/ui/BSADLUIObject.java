package com.bstoneinfo.lib.adl.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.bstoneinfo.lib.adl.BSADL;

public abstract class BSADLUIObject {

    protected final Context context;
    protected final String adlName;
    protected final JSONObject jsonADL;

    public BSADLUIObject(Context context, String adlName, JSONObject jsonADL) {
        this.context = context;
        this.adlName = adlName;
        this.jsonADL = jsonADL;
    }

    protected void parseCommonAttribute(View view) {
        int drawableID = getDrawableID(jsonADL, "bgimage", false);
        if (drawableID > 0) {
            view.setBackgroundResource(drawableID);
        }
        int bgColor = getColor(jsonADL, "bgcolor", false);
        if (bgColor >= 0) {
            view.setBackgroundColor(bgColor);
        }
    }

    protected int getColor(JSONObject jsonADL, String node, boolean bFatal) {
        String value = jsonADL.optString(node, null);
        if (value == null) {
            if (bFatal) {
                errorNodeNotFound(node, "String");
            }
            return -1;
        }
        try {
            return Color.parseColor(value);
        } catch (Exception e) {
            error("bgcolor", "String", "Color %1 Parse Error");
            return 0;
        }
    }

    protected int getDrawableID(JSONObject jsonADL, String node, boolean bFatal) {
        String value = jsonADL.optString(node, null);
        if (value == null) {
            if (bFatal) {
                errorNodeNotFound(node, "String");
            }
            return -1;
        }
        int drawableID = context.getResources().getIdentifier(context.getPackageName() + ":drawable/" + value, null, null);
        if (drawableID > 0) {
            return drawableID;
        }
        if (bFatal) {
            errorResourceNotFound(node, "String", value);
        }
        return 0;
    }

    protected int getInteger(JSONObject jsonItem, String node, int defValue) {
        return jsonItem.optInt(node, defValue);
    }

    protected JSONArray getArray(JSONObject jsonADL, String node, boolean bFatal) {
        JSONArray ja = jsonADL.optJSONArray(node);
        if (ja == null) {
            if (bFatal) {
                errorNodeNotFound(node, "Array");
            }
            return new JSONArray();
        }
        if (ja.length() == 0 && bFatal) {
            error(node, "Array", "Array empty");
        }
        return ja;
    }

    protected JSONObject getArrayObject(String node, JSONArray jsonItems, int i) {
        JSONObject jsonItem = jsonItems.optJSONObject(i);
        if (jsonItem == null) {
            error(node + "[" + i + "]", "Object", "Not a JSONObject");
            return new JSONObject();
        }
        return jsonItem;
    }

    protected void error(String node, String type, String errMsg) {
        BSADL.error(context, adlName, node, type, errMsg, null);
    }

    protected void error(String node, String type, String errMsg, Exception e) {
        BSADL.error(context, adlName, node, type, errMsg, e);
    }

    protected void error(String node, String type, String errMsg, String param1) {
        BSADL.error(context, adlName, node, type, errMsg.replace("%1", "'" + param1 + "'"), null);
    }

    protected void errorNodeNotFound(String node, String type) {
        error(node, type, "Node not found");
    }

    protected void errorResourceNotFound(String node, String type, String resName) {
        error(node, type, "Resouce %1 not found", resName);
    }

}

package com.bstoneinfo.lib.adl.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
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
        int bgColor = getColor(jsonADL, "bgcolor", Integer.MIN_VALUE);
        if (bgColor != Integer.MIN_VALUE) {
            view.setBackgroundColor(bgColor);
        }
    }

    protected int getColor(JSONObject jsonADL, String node, int defColor) {
        String value = jsonADL.optString(node, null);
        if (value == null) {
            return defColor;
        }
        try {
            return Color.parseColor(value);
        } catch (Exception e) {
            error("bgcolor", "String", "Color %1 Parse Error");
            return defColor;
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

    protected int getAlign(JSONObject jsonItem, String itemNodeName) {
        String preNode = itemNodeName;
        if (preNode == null) {
            preNode = "";
        } else if (!TextUtils.isEmpty(preNode) && !preNode.endsWith(".")) {
            preNode += ".";
        }
        int gravity = 0;
        String halign = jsonItem.optString("halign", null);
        String valign = jsonItem.optString("valign", null);
        if (halign != null) {
            if (TextUtils.equals(halign, "left")) {
                gravity = Gravity.LEFT;
            } else if (TextUtils.equals(halign, "center")) {
                gravity = Gravity.CENTER_HORIZONTAL;
            } else if (TextUtils.equals(halign, "right")) {
                gravity = Gravity.RIGHT;
            } else {
                error(preNode + "halign", "String", "%1 is not a valid value of 'halign', must be one of 'left', 'center' or 'right'", halign);
            }
        }
        if (valign != null) {
            if (TextUtils.equals(valign, "top")) {
                gravity |= Gravity.TOP;
            } else if (TextUtils.equals(valign, "center")) {
                gravity |= Gravity.CENTER_VERTICAL;
            } else if (TextUtils.equals(valign, "bottom")) {
                gravity |= Gravity.BOTTOM;
            } else {
                error(preNode + "valign", "String", "%1 is not a valid value of 'valign', must be one of 'top', 'center' or 'bottom'", valign);
            }
        }
        return gravity;
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

package com.bstoneinfo.lib.adl;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.bstoneinfo.lib.adl.ui.BSADLUIFrame;
import com.bstoneinfo.lib.adl.ui.BSADLUITabbedFrame;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSLog;

public class BSADL {

    public static final String TAG = "BSADL";

    public static void loadUI(Context context, String adlName) {

    }

    private static void parseAdlUIFile(Context context, String adlFileName) {
        final String adlPathName = "ui/" + adlFileName;
        final String adlFilePath = BSApplication.getApplication().getFilesDir() + "/adl/" + adlPathName + ".json";
        JSONObject jsonADL = new JSONObject();
        try {
            jsonADL = new JSONObject(adlFilePath);
        } catch (JSONException e) {
        }
        String sClass = jsonADL.optString("class");
        BSADLUIFrame adlUIFrame;
        if (TextUtils.isEmpty(sClass)) {
            BSLog.d(BSADL.TAG, adlPathName + ": Not found JSONString 'class'");
            return;
        }
        if (TextUtils.equals(sClass, "tabview")) {
            adlUIFrame = new BSADLUITabbedFrame(context, jsonADL, adlPathName);
        } else {
            BSLog.d(BSADL.TAG, adlPathName + ": Not implement UIFrame class '" + sClass + "'");
            return;
        }
        adlUIFrame.parse();
    }

}

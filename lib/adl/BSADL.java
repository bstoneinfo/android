package com.bstoneinfo.lib.adl;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.bstoneinfo.lib.adl.ui.BSADLUIFrame;
import com.bstoneinfo.lib.adl.ui.BSADLUITabbedFrame;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.frame.BSFrame;

public class BSADL {

    public static final String TAG = "BSADL";

    public static BSFrame loadUI(Context context, String adlName) {
        final String adlPathName = "ui/" + adlName;
        JSONObject jsonADL = loadAdlFile(adlPathName);
        String sClass = jsonADL.optString("class");
        BSADLUIFrame adlUIFrame;
        if (TextUtils.isEmpty(sClass)) {
            BSUtils.debugAssert(adlPathName + ": Not found JSONString 'class'");
            return null;
        }
        if (TextUtils.equals(sClass, "tabview")) {
            adlUIFrame = new BSADLUITabbedFrame(context, jsonADL, adlPathName);
        } else {
            BSUtils.debugAssert(adlPathName + ": Not implement UIFrame class '" + sClass + "'");
            return null;
        }
        return adlUIFrame.parse();
    }

    private static JSONObject loadAdlFile(String adlPathName) {
        String adlString;
        final String adlFilePath = BSApplication.getApplication().getFilesDir() + "/adl/" + adlPathName + ".json";
        if (new File(adlFilePath).exists()) {
            adlString = BSUtils.readStringFromFile(adlFilePath);
        } else {
            adlString = BSUtils.readStringFromAsset("adl/" + adlPathName + ".json");
        }
        try {
            return new JSONObject(adlString);
        } catch (JSONException e) {
            return new JSONObject();
        }

    }

}

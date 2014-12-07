package com.bstoneinfo.lib.adl;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.bstoneinfo.lib.adl.ui.BSADLUIFrame;
import com.bstoneinfo.lib.adl.ui.BSADLUITabbedFrame;
import com.bstoneinfo.lib.adl.ui.BSADLUIView;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.frame.BSFrame;

public class BSADL {

    static final HashMap<String, Class<? extends BSADLUIFrame>> frameClassMap = new HashMap<String, Class<? extends BSADLUIFrame>>();
    static final HashMap<String, Class<? extends BSADLUIView>> viewClassMap = new HashMap<String, Class<? extends BSADLUIView>>();

    static {
        registerADLUIFrameClass("tablayout", BSADLUITabbedFrame.class);
    }

    public static BSFrame loadUI(Context context, String adlName) {
        final String adlPathName = "ui/" + adlName;
        JSONObject jsonADL = loadAdlFile(adlPathName);
        String sClass = jsonADL.optString("class");
        BSADLUIFrame adlUIFrame;
        if (TextUtils.isEmpty(sClass)) {
            BSUtils.debugAssert(adlPathName + ": Not found JSONString 'class'");
            return null;
        }
        if (TextUtils.equals(sClass, "tab")) {
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

    public static void registerADLUIFrameClass(String clsName, Class<? extends BSADLUIFrame> cls) {
        frameClassMap.put(clsName, cls);
    }

    public static void registerADLUIViewClass(String clsName, Class<? extends BSADLUIView> cls) {
        viewClassMap.put(clsName, cls);
    }
}

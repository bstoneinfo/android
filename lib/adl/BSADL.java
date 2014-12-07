package com.bstoneinfo.lib.adl;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.bstoneinfo.lib.adl.ui.BSADLUIFrame;
import com.bstoneinfo.lib.adl.ui.BSADLUIRowFrame;
import com.bstoneinfo.lib.adl.ui.BSADLUITabFrame;
import com.bstoneinfo.lib.adl.ui.BSADLUIView;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.frame.BSActivity;
import com.bstoneinfo.lib.frame.BSFrame;

public class BSADL {

    static final HashMap<String, Class<? extends BSADLUIFrame>> frameClassMap = new HashMap<String, Class<? extends BSADLUIFrame>>();
    static final HashMap<String, Class<? extends BSADLUIView>> viewClassMap = new HashMap<String, Class<? extends BSADLUIView>>();

    static {
        registerADLUIFrameClass("tabframe", BSADLUITabFrame.class);
        registerADLUIFrameClass("rowframe", BSADLUIRowFrame.class);
    }

    public static BSFrame loadFrame(Context context, String adlName) {
        JSONObject jsonADL = loadAdlFile(context, adlName);
        return loadFrame(context, adlName, jsonADL);
    }

    public static BSFrame loadFrame(Context context, String adlName, JSONObject jsonADL) {
        String className = jsonADL.optString("class");
        if (TextUtils.isEmpty(className)) {
            error(context, adlName, "class", "String", "Not found", null);
            return new BSFrame(context);
        }
        Class<? extends BSADLUIFrame> frameClass = frameClassMap.get(className);
        if (frameClass == null) {
            return new BSFrame(context);
        }
        try {
            BSADLUIFrame adlFrame = frameClass.getConstructor(Context.class, String.class, JSONObject.class).newInstance(context, adlName, jsonADL);
            return adlFrame.parse();
        } catch (Exception e) {
            error(context, adlName, "class", "String", "Not create instance of '" + className + "'", e);
            return new BSFrame(context);
        }
    }

    public static View loadView(Context context, String adlName, String node, JSONObject jsonADL) {
        String classNode;
        if (node == null) {
            classNode = "class";
        } else {
            classNode = node + ".class";
        }
        String className = jsonADL.optString("class");
        if (TextUtils.isEmpty(className)) {
            error(context, adlName, classNode, "String", "Not found", null);
            return new View(context);
        }
        Class<? extends BSADLUIView> viewClass = viewClassMap.get(className);
        if (viewClass == null) {
            return new View(context);
        }
        try {
            BSADLUIView adlView = viewClass.getConstructor(Context.class, String.class, JSONObject.class).newInstance(context, adlName, jsonADL);
            return adlView.parse();
        } catch (Exception e) {
            error(context, adlName, classNode, "String", "Not create instance of '" + className + "'", e);
            return new View(context);
        }
    }

    private static JSONObject loadAdlFile(Context context, String adlName) {
        String adlString;
        final String adlFilePath = BSApplication.getApplication().getFilesDir() + "/adl/" + adlName + ".json";
        if (new File(adlFilePath).exists()) {
            adlString = BSUtils.readStringFromFile(adlFilePath);
        } else {
            adlString = BSUtils.readStringFromAsset("adl/" + adlName + ".json");
        }
        if (TextUtils.isEmpty(adlString)) {
            error(context, adlName, null, null, "file not found or empty", null);
            return new JSONObject();
        }
        try {
            return new JSONObject(adlString);
        } catch (JSONException e) {
            error(context, adlName, null, null, "json parse error", e);
            return new JSONObject();
        }
    }

    public static void error(Context context, String adlName, String node, String type, String errMsg, Exception e) {
        final StringBuilder sb = new StringBuilder();
        sb.append("ADLError [").append(adlName).append("]");
        if (!TextUtils.isEmpty(node)) {
            sb.append(" ").append(node).append(":").append(type);
        }
        if (!TextUtils.isEmpty(errMsg)) {
            sb.append(" - ").append(errMsg);
        }
        if (e != null) {
            sb.append(" - ").append(e.toString()).append(e.getMessage());
        }
        BSLog.e("BSADL", sb.toString());
        if (context instanceof BSActivity) {
            ((BSActivity) context).alert("ADL Error", sb.toString(), "OK", null);
        }
    }

    public static void registerADLUIFrameClass(String clsName, Class<? extends BSADLUIFrame> cls) {
        frameClassMap.put(clsName, cls);
    }

    public static void registerADLUIViewClass(String clsName, Class<? extends BSADLUIView> cls) {
        viewClassMap.put(clsName, cls);
    }
}

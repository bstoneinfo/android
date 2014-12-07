package com.jinhuvv.sitemgr.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.bstoneinfo.lib.app.BSApplication;

public class ProfileManager {

    public static SharedPreferences getUserPreferences() {
        return BSApplication.getApplication().getSharedPreferences("profile", Context.MODE_PRIVATE);
    }

    public static boolean islogin() {
        return !TextUtils.isEmpty(getUserID());
    }

    public static String getUserID() {
        return getUserPreferences().getString("uid", "");
    }

}

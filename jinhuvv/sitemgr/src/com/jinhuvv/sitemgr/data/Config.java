package com.jinhuvv.sitemgr.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.bstoneinfo.lib.app.BSApplication;

public class Config {

    public static SharedPreferences getUserPreferences() {
        return BSApplication.getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    public static boolean islogin() {
        return !TextUtils.isEmpty(getLoginUserID());
    }

    public static String getLoginUserID() {
        return getUserPreferences().getString("loginUid", "");
    }

}

package com.bstoneinfo.lib.app;


import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;

public class BSVersionManager {

    public final int lastVersionCode;
    public final String lastVersionName;
    public final int thisVersionCode;
    public final String thisVersionName;

    BSVersionManager() {
        lastVersionCode = BSApplication.getApplication().getDefaultPreferences().getInt("AppVersionCode", 0);
        lastVersionName = BSApplication.getApplication().getDefaultPreferences().getString("AppVersionName", "");
        int piCode = lastVersionCode;
        String piName = lastVersionName;
        try {
            PackageInfo pi = BSApplication.getApplication().getPackageManager().getPackageInfo(BSApplication.getApplication().getPackageName(), 0);
            piCode = pi.versionCode;
            piName = pi.versionName;
        } catch (Exception e) {
        }
        thisVersionCode = piCode;
        thisVersionName = piName;
        if (isUpgrade()) {
            Editor editor = BSApplication.getApplication().getDefaultPreferences().edit();
            editor.putInt("AppVersionCode", thisVersionCode);
            editor.putString("AppVersionName", thisVersionName);
            editor.commit();
        }
    }

    public boolean isUpgrade() {
        return lastVersionCode < thisVersionCode;
    }

}

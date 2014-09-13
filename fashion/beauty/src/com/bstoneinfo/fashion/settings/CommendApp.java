package com.bstoneinfo.fashion.settings;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.widget.Toast;

import com.bstoneinfo.beauty.R;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.app.BSActivity;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.common.BSUtils.DownloadApkListener;

public class CommendApp {

    private final BSActivity activity;
    private final JSONObject jsonConfig;
    private final File commendLocalFile;

    public CommendApp(BSActivity _activity) {
        activity = _activity;
        jsonConfig = BSUtils.optJsonObject(BSApplication.getApplication().getRemoteConfig(), "Commend");
        commendLocalFile = new File(BSUtils.getCacheFolder(), jsonConfig.optString("tag"));
    }

    public boolean isCommendAppOn() {
        String channel = BSUtils.getManifestMetaData("UMENG_CHANNEL");
        JSONArray jsonArray = BSUtils.optJsonArray(jsonConfig, "Disable");
        for (int i = 0; i < jsonArray.length(); i++) {
            String tag = jsonArray.optString(i);
            if (TextUtils.equals(channel, tag)) {
                return false;
            }
        }
        return true;
    }

    public void download() {
        String title = activity.getString(R.string.commend_title);
        String appName = jsonConfig.optString("Name");
        String state;
        if (isCommendAppExist()) {
            state = "已安装";
            ComponentName componetName = new ComponentName(jsonConfig.optString("PackageName"), jsonConfig.optString("LaunchActivity"));
            Intent intent = new Intent();
            intent.setComponent(componetName);
            activity.startActivity(intent);
        } else if (installCommendApp()) {
            state = "安装已存在的程序包";
        } else if (getCommendPreferences().getBoolean("commend_accept", false)) {
            if (BSUtils.isWifiConnected() || getCommendPreferences().getBoolean("commend_mobile", false)) {
                state = "开始下载";
                downloadCommendApp();
                Toast.makeText(activity, R.string.commend_continue, Toast.LENGTH_LONG).show();
            } else {
                state = "询问继续下载";
                activity.confirm(title, activity.getString(R.string.commend_iscontinue).replace("#1", appName), R.string.commend_cancel, R.string.commend_ok, new Runnable() {
                    @Override
                    public void run() {
                        BSAnalyses.getInstance().event("Commend_Confirm", "Continue_NO");
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        BSAnalyses.getInstance().event("Commend_Confirm", "Continue_YES");
                        downloadCommendApp();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        BSAnalyses.getInstance().event("Commend_Confirm", "Continue_Cancel");
                    }
                });
            }
        } else {
            state = "询问下载";
            activity.confirm(title, jsonConfig.optString("Tips"), R.string.commend_cancel, R.string.commend_ok, new Runnable() {
                @Override
                public void run() {
                    BSAnalyses.getInstance().event("Commend_Confirm", "NO");
                }
            }, new Runnable() {
                @Override
                public void run() {
                    boolean isMobileConnected = BSUtils.isMobileConnected();
                    BSAnalyses.getInstance().event("Commend_Confirm", "YES");
                    Editor editor = getCommendPreferences().edit();
                    editor.putBoolean("commend_accept", true);
                    editor.putBoolean("commend_mobile", isMobileConnected);
                    editor.commit();
                    downloadCommendApp();
                }
            }, new Runnable() {
                @Override
                public void run() {
                    BSAnalyses.getInstance().event("Commend_Confirm", "Cancel");
                }
            });
        }
        String tag = jsonConfig.optString("Tag");
        BSAnalyses.getInstance().event("Commend_Clicked" + (TextUtils.isEmpty(tag) ? "" : "_" + tag), state);
    }

    private boolean isCommendAppExist() {
        try {
            activity.getPackageManager().getPackageInfo(jsonConfig.optString("PackageName"), 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    private boolean installCommendApp() {
        if (!commendLocalFile.exists()) {
            return false;
        }
        PackageInfo info = null;
        try {
            info = activity.getPackageManager().getPackageArchiveInfo(commendLocalFile.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
        }
        if (info == null) {
            commendLocalFile.delete();
            downloadCommendApp();// 重新下载
            return false;
        }
        BSUtils.installApk(commendLocalFile.getAbsolutePath());
        return true;
    }

    private void downloadCommendApp() {
        String tag = jsonConfig.optString("Tag");
        final String eventName = "Commend_Clicked" + (TextUtils.isEmpty(tag) ? "" : "_" + tag);
        BSUtils.downloadApk(jsonConfig.optString("Url"), new DownloadApkListener() {
            @Override
            public void onSuccess() {
                BSAnalyses.getInstance().event(eventName, "成功");
            }

            @Override
            public void onFail() {
                BSAnalyses.getInstance().event(eventName, "失败");
            }

            @Override
            public void onExist() {
                BSAnalyses.getInstance().event(eventName, "已存在");
            }

            @Override
            public void onDownloading() {
                BSAnalyses.getInstance().event(eventName, "已下载");
            }
        });
    }

    private SharedPreferences getCommendPreferences() {
        return BSApplication.getApplication().getDefaultSharedPreferences();
    }
}

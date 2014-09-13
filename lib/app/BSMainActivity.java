package com.bstoneinfo.lib.app;

import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;

import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.common.BSUtils.DownloadApkListener;

import custom.R;

public class BSMainActivity extends BSActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BSAnalyses.getInstance().init(this);

        BSApplication.defaultNotificationCenter.addObserver(this, BSObserverEvent.REMOTE_CONFIG_DID_CHANGE, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                checkUpgrade();
            }
        });

        checkUpgrade();
    }

    private void checkUpgrade() {
        JSONObject configJSON = BSApplication.getApplication().getRemoteConfig();
        JSONObject upgradeJsonObject = configJSON.optJSONObject("Upgrade");
        if (upgradeJsonObject == null) {
            return;
        }
        int newVersionCode = upgradeJsonObject.optInt("versionCode");
        PackageInfo pi;
        try {
            pi = BSApplication.getApplication().getPackageManager().getPackageInfo(getPackageName(), 0);
            if (pi.versionCode >= newVersionCode) {
                return;
            }
        } catch (NameNotFoundException e) {
            return;
        }
        final String url = upgradeJsonObject.optString("url");
        if (TextUtils.isEmpty(url)) {
            return;
        }

        final DownloadApkListener downloadListener = new DownloadApkListener() {
            @Override
            public void onDownloading() {
                BSAnalyses.getInstance().event("Upgrade_Download", "Downloading");
            }

            @Override
            public void onSuccess() {
                BSAnalyses.getInstance().event("Upgrade_Download", "Exist");
            }

            @Override
            public void onFail() {
                BSAnalyses.getInstance().event("Upgrade_Download", "Success");
            }

            @Override
            public void onExist() {
                BSAnalyses.getInstance().event("Upgrade_Download", "Fail");
            }

        };

        final SharedPreferences preferences = getSharedPreferences(getPackageName(), 0);
        if (preferences.getBoolean("UpgradeDidConfirm", false)) {
            BSUtils.downloadApk(url, downloadListener);
            return;
        }
        long lastUpgradeCheckTime = preferences.getLong("LastUpgradeCheckTime", 0);
        if (System.currentTimeMillis() - lastUpgradeCheckTime < 3600 * 1000 * 4) {
            return;
        }

        String alert = upgradeJsonObject.optString("ForceAlert");
        if (!TextUtils.isEmpty(alert)) {
            alert(getString(R.string.app_name), alert, getString(R.string.ok), new Runnable() {
                @Override
                public void run() {
                    BSUtils.downloadApk(url, downloadListener);
                }
            });
            return;
        }

        alert = upgradeJsonObject.optString("alert" + pi.versionCode);
        if (TextUtils.isEmpty(alert)) {
            alert = upgradeJsonObject.optString("alert");
        }
        if (TextUtils.isEmpty(alert)) {
            return;
        }
        alert = alert.replace("[CurrentVersion]", pi.versionName);
        confirm(getString(R.string.app_name), alert, R.string.cancel, R.string.ok, new Runnable() {
            @Override
            public void run() {
                BSAnalyses.getInstance().event("Upgrade_Confirm", "Cancel");
                preferences.edit().putLong("LastUpgradeCheckTime", System.currentTimeMillis()).commit();
            }
        }, new Runnable() {
            @Override
            public void run() {
                BSAnalyses.getInstance().event("Upgrade_Confirm", "OK");
                preferences.edit().putBoolean("UpgradeDidConfirm", true).commit();
                BSUtils.downloadApk(url, downloadListener);
            }
        }, null);
    }
}

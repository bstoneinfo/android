package com.bstoneinfo.lib.app;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.frame.BSFrame;

import custom.R;

public abstract class BSActivity extends Activity {

    private static DisplayMetrics displayMetrics;
    private ArrayList<Dialog> autoDestroyDialogs = new ArrayList<Dialog>();
    private BSFrame mainFrame;

    public static DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public static int dip2px(float dpValue) {
        return (int) (dpValue * displayMetrics.density + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return (int) (pxValue / displayMetrics.density + 0.5f);
    }

    public BSFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(BSFrame _mainFrame) {
        setContentView(_mainFrame.getRootView());
        mainFrame = _mainFrame;
        mainFrame.load();
        mainFrame.show();
    }

    private void initDisplayMetrics() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDisplayMetrics();
        BSAnalyses.getInstance().init(this);

        BSApplication.defaultNotificationCenter.addObserver(this, BSObserverEvent.REMOTE_CONFIG_DID_CHANGE, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                checkUpgrade();
            }
        });

        checkUpgrade();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initDisplayMetrics();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BSApplication.defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.ACTIVITY_START, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BSApplication.defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.ACTIVITY_RESUME, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BSApplication.defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.ACTIVITY_PAUSE, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BSApplication.defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.ACTIVITY_STOP, this);
        if (isFinishing()) {// 关闭附属于本Activity的Dialog
            autoDestroyDialogs();
        }
    }

    @Override
    protected void onDestroy() {
        if (mainFrame != null) {
            mainFrame.destroy();
        }
        BSApplication.defaultNotificationCenter.removeObservers(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mainFrame == null || !mainFrame.back()) {
            super.onBackPressed();
        }
    }

    @Override
    final public void finish() {
        super.finish();
    }

    public void autoDestroyDialogs() {
        for (Dialog dialog : autoDestroyDialogs) {
            dialog.dismiss();
        }
        autoDestroyDialogs.clear();
    }

    public void addToAutoDestroyDialogList(AlertDialog alertDialog) {
        if (alertDialog != null) {
            autoDestroyDialogs.add(alertDialog);
        }
    }

    public void removeFromAutoDestroyDialogList(AlertDialog alertDialog) {
        autoDestroyDialogs.remove(alertDialog);
    }

    /*
     * 显示信息框
     */
    public AlertDialog alert(int titleResId, int alertTextResId, int buttonTextResId, final Runnable callback) {
        return alert(titleResId == 0 ? null : getString(titleResId), getString(alertTextResId), getString(buttonTextResId), callback);
    }

    public AlertDialog alert(String title, String text, String buttonText, final Runnable callback) {

        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(text);

        builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autoDestroyDialogs.remove(dialog);
                dialog.dismiss();
                if (callback != null) {
                    callback.run();
                }
            }
        });

        AlertDialog alert = builder.create();
        if (!TextUtils.isEmpty(title)) {
            alert.setTitle(title);
        }
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        autoDestroyDialogs.add(alert);
        alert.show();
        return alert;
    }

    public AlertDialog confirm(int titleResId, int alertTextResId, int btnTxtResId1, int btnTxtResId2, final Runnable btnCallback1, final Runnable btnCallback2,
            final OnCancelListener onCancelListener) {
        return confirm(getString(titleResId), getString(alertTextResId), btnTxtResId1, btnTxtResId2, btnCallback1, btnCallback2, onCancelListener);
    }

    public AlertDialog confirm(String title, String text, int btnTxtResId1, int btnTxtResId2, final Runnable btnCallback1, final Runnable btnCallback2,
            final OnCancelListener onCancelListener) {

        AlertDialog.Builder builder = new Builder(this);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(text);

        builder.setPositiveButton(btnTxtResId1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autoDestroyDialogs.remove(dialog);
                dialog.dismiss();
                if (btnCallback1 != null) {
                    btnCallback1.run();
                }
            }
        });

        builder.setNegativeButton(btnTxtResId2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autoDestroyDialogs.remove(dialog);
                dialog.dismiss();
                if (btnCallback2 != null) {
                    btnCallback2.run();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                autoDestroyDialogs.remove(dialog);
                dialog.dismiss();
                if (onCancelListener != null) {
                    onCancelListener.onCancel(dialog);
                }
            }
        });
        autoDestroyDialogs.add(alert);
        alert.show();
        return alert;
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

        final SharedPreferences preferences = getSharedPreferences(getPackageName(), 0);
        if (preferences.getBoolean("UpgradeDidConfirm", false)) {
            BSUtils.downloadApk(url, true);
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
                    BSUtils.downloadApk(url, true);
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
                BSUtils.downloadApk(url, true);
            }
        }, null);
    }

}

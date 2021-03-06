package com.bstoneinfo.lib.frame;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;

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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initDisplayMetrics();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BSApplication.getApplication().checkAppStateEvent();
        BSApplication.defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.ACTIVITY_START, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BSApplication.defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.ACTIVITY_RESUME, this);
    }

    @Override
    protected void onPause() {
        BSApplication.defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.ACTIVITY_PAUSE, this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        BSApplication.defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.ACTIVITY_STOP, this);
        BSApplication.getApplication().checkAppStateEvent();
        if (isFinishing()) {// 关闭附属于本Activity的Dialog
            autoDestroyDialogs();
        }
        super.onStop();
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
    @SuppressLint("NewApi")
    private AlertDialog.Builder createAlertBuilder(String title, String text) {
        AlertDialog.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(text);
        return builder;
    }

    public AlertDialog alert(int titleResId, int alertTextResId, int buttonTextResId, final Runnable callback) {
        return alert(titleResId == 0 ? null : getString(titleResId), getString(alertTextResId), getString(buttonTextResId), callback);
    }

    public AlertDialog alert(String title, String text, String buttonText, final Runnable callback) {

        AlertDialog.Builder builder = createAlertBuilder(title, text);
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
        autoDestroyDialogs.add(alert);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
        return alert;
    }

    public AlertDialog confirm(int titleResId, int alertTextResId, int btnTxtResId1, int btnTxtResId2, final Runnable btnCallback1, final Runnable btnCallback2,
            final Runnable cancelCallback) {
        return confirm(getString(titleResId), getString(alertTextResId), btnTxtResId1, btnTxtResId2, btnCallback1, btnCallback2, cancelCallback);
    }

    public AlertDialog confirm(String title, String text, int btnTxtResId1, int btnTxtResId2, final Runnable btnCallback1, final Runnable btnCallback2,
            final Runnable cancelCallback) {

        AlertDialog.Builder builder = createAlertBuilder(title, text);
        builder.setNegativeButton(btnTxtResId1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autoDestroyDialogs.remove(dialog);
                dialog.dismiss();
                if (btnCallback1 != null) {
                    btnCallback1.run();
                }
            }
        });

        builder.setPositiveButton(btnTxtResId2, new DialogInterface.OnClickListener() {
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
        autoDestroyDialogs.add(alert);
        alert.setCanceledOnTouchOutside(false);
        alert.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                autoDestroyDialogs.remove(dialog);
                dialog.dismiss();
                if (cancelCallback != null) {
                    cancelCallback.run();
                }
            }
        });
        alert.show();
        return alert;
    }

}

package com.bstoneinfo.lib.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.connection.BSFileConnection;
import com.bstoneinfo.lib.connection.BSFileConnection.BSFileConnectionListener;

import custom.R;

public class BSUtils {

    public static String getManifestMetaData(String metaName) {
        String tag = null;
        try {
            ApplicationInfo info = BSApplication.getApplication().getPackageManager()
                    .getApplicationInfo(BSApplication.getApplication().getPackageName(), PackageManager.GET_META_DATA);
            tag = info.metaData.getString(metaName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }

    public static boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) BSApplication.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = BSApplication.getApplication().getPackageName();
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public static final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            action.run();
        } else {
            new Handler(Looper.getMainLooper()).post(action);
        }
    }

    public static JSONObject optJsonObject(JSONObject jsonObject, String name) {
        JSONObject jo = jsonObject != null ? jsonObject.optJSONObject(name) : null;
        if (jo == null) {
            jo = new JSONObject();
        }
        return jo;
    }

    public static JSONArray optJsonArray(JSONObject jsonObject, String name) {
        JSONArray ja = jsonObject != null ? jsonObject.optJSONArray(name) : null;
        if (ja == null) {
            ja = new JSONArray();
        }
        return ja;
    }

    // 通过设定时间间隔来避免某些按钮的重复点击
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = SystemClock.elapsedRealtime();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isDebug() {
        return R.buildConfig.DEBUG;
    }

    public static void debugAssert(String assertDesc) {
        if (isDebug()) {
            throw new AssertionError(assertDesc);
        }
    }

    public static void debugAssert(boolean assertValue, String assertDesc) {
        if (!assertValue && isDebug()) {
            throw new AssertionError(assertDesc);
        }
    }

    public static String readStringFromInput(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }

    public static String readStringFromFile(String filePath) {
        try {
            return readStringFromInput(new FileInputStream(filePath));
        } catch (Exception e) {
            return "";
        }
    }

    public static void saveStringToFile(String string, String filePath) {
        new File(filePath.substring(0, filePath.lastIndexOf(File.separator))).mkdirs();
        try {
            File outFile = new File(filePath);
            OutputStream output = new FileOutputStream(outFile);
            byte[] b = string.getBytes();
            output.write(b, 0, b.length);
            output.close();
        } catch (Exception e) {
        }
    }

    public static String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte b : digest.digest()) {
                builder.append(Integer.toHexString(b >> 4 & 0xf));
                builder.append(Integer.toHexString(b & 0xf));
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String cacheFolder;
    private static String fileFolder = null;

    public static String getCachePath(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        String folder = getCacheFolder();
        if (key.startsWith("http://")) {
            key = key.substring(7);
        } else if (key.startsWith("https://")) {
            key = key.substring(8);
        }
        key = key.substring(key.indexOf('/'));
        String name = getMD5(key);
        return folder + name;
    }

    public static String getCacheFolder() {
        if (cacheFolder == null) {
            File folder = null;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                folder = BSApplication.getApplication().getExternalCacheDir();
            }
            if (folder == null) {
                folder = BSApplication.getApplication().getCacheDir();
                if (folder == null) {
                    return null;
                }
            }
            cacheFolder = folder.getPath() + File.separator;
        }
        new File(cacheFolder).mkdir();
        return cacheFolder;
    }

    public static String getFileFolder() {
        if (fileFolder != null) {
            return fileFolder;
        }
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = BSApplication.getApplication().getExternalFilesDir(null);
        }
        if (file == null) {
            file = BSApplication.getApplication().getFilesDir();
            if (file == null) {
                return null;
            }
        }
        if (!file.exists()) {
            file.mkdir();
        }
        return fileFolder = file.getPath() + File.separator;
    }

    public static boolean isHttpUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    public static boolean isFileUrl(String url) {
        return url.startsWith("file://");
    }

    public static String getDiskPath(String url) {
        if (isHttpUrl(url)) {
            return BSUtils.getCachePath(url);
        }
        if (isFileUrl(url)) {
            return url.substring(6);
        }
        return url;
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) BSApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) BSApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi != null && wifi.isConnected();
    }

    public static boolean isMobileConnected() {
        ConnectivityManager cm = (ConnectivityManager) BSApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifi != null && wifi.isConnected();
    }

    public interface DownloadApkListener {
        public void onExist();

        public void onDownloading();

        public void onSuccess();

        public void onFail();

    }

    public static void downloadApk(String url, final DownloadApkListener listener) {
        String localPath = BSUtils.getDiskPath(url) + ".apk";
        if (new File(localPath).exists()) {
            installApk(localPath);
            if (listener != null) {
                listener.onExist();
            }
            return;
        }
        BSFileConnection fileConnection = new BSFileConnection(url);
        fileConnection.setLocalPath(localPath);
        fileConnection.setConnectionQueue(BSApplication.getApplication().defaultConnnectionQueue);
        if (BSApplication.getApplication().defaultConnnectionQueue.containsConnection(url)) {
            if (listener != null) {
                listener.onDownloading();
            }
            return;
        }
        fileConnection.start(new BSFileConnectionListener() {
            @Override
            public void finished(String localPath) {
                installApk(localPath);
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void failed(Exception exception) {
                if (listener != null) {
                    listener.onFail();
                }
            }
        });
    }

    public static void installApk(String localPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(localPath)), "application/vnd.android.package-archive");
        BSApplication.getApplication().startActivity(intent);
    }
}

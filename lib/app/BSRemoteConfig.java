package com.bstoneinfo.lib.app;

import java.io.File;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.connection.BSJsonConnection;
import com.bstoneinfo.lib.connection.BSJsonConnection.BSJsonConnectionListener;

class BSRemoteConfig extends JSONObject {

    private BSJsonConnection mRemoteConfigConnection;
    String mRemoteConfigUrl;
    JSONObject mConfigJSON = new JSONObject();

    BSRemoteConfig() {
        String configString = null;
        final String localPath = BSApplication.getApplication().getFilesDir() + File.separator + "RemoteConfig.json";
        if (BSApplication.getApplication().getVersionManager().isUpgrade()) {
            new File(localPath).delete();
        }
        if (new File(localPath).exists()) {
            configString = BSUtils.readStringFromFile(localPath);
            BSLog.d("load RemoteConfig.json from file: " + configString);
        } else {
            try {
                AssetManager assetManager = BSApplication.getApplication().getAssets();
                InputStream input = assetManager.open("RemoteConfig.json");
                if (input != null) {
                    configString = BSUtils.readStringFromInput(input);
                    BSLog.d("load RemoteConfig.json from assets: " + configString);
                    input.close();
                }
            } catch (Exception e) {
                BSLog.d("load RemoteConfig.json from assets failed: " + e.toString());
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(configString)) {
            try {
                mConfigJSON = new JSONObject(configString);
            } catch (JSONException e) {
                BSLog.d("load RemoteConfig.json string failed: " + e.toString());
            }
        }

        BSApplication.defaultNotificationCenter.addObserver(this, BSObserverEvent.APP_ENTER_FOREGROUND, new Observer() {

            @Override
            public void update(Observable observable, Object data) {
                if (mRemoteConfigConnection != null || TextUtils.isEmpty(mRemoteConfigUrl)) {
                    return;
                }
                BSLog.d("will download RemoteConfig : " + mRemoteConfigUrl);
                mRemoteConfigConnection = new BSJsonConnection(mRemoteConfigUrl);
                mRemoteConfigConnection.start(new BSJsonConnectionListener() {
                    @Override
                    public void finished(JSONObject jsonObject) {
                        String jsonString = jsonObject.toString();
                        BSLog.d("load RemoteConfig from server success: " + jsonString);
                        mRemoteConfigConnection = null;
                        if (!TextUtils.equals(mConfigJSON.toString(), jsonString)) {
                            mConfigJSON = jsonObject;
                            BSUtils.saveStringToFile(jsonString, localPath);
                            BSApplication.defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.REMOTE_CONFIG_DID_CHANGE, jsonObject);
                        }
                    }

                    @Override
                    public void failed(Exception exception) {
                        BSLog.d("load RemoteConfig from server failed: " + exception.toString());
                        mRemoteConfigConnection = null;
                    }
                });
            }
        });

    }

}

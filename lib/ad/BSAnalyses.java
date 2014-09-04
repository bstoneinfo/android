package com.bstoneinfo.lib.ad;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.text.TextUtils;

import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.common.BSUtils;
import com.umeng.analytics.MobclickAgent;

public class BSAnalyses {

    private static BSAnalyses instance = new BSAnalyses();
    private Context context;

    public static BSAnalyses getInstance() {
        return instance;
    }

    private BSAnalyses() {
        BSApplication.defaultNotificationCenter.addObserver(this, BSObserverEvent.ACTIVITY_RESUME, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                MobclickAgent.onResume(context);
                //        StatService.onResume(context);
            }
        });
        BSApplication.defaultNotificationCenter.addObserver(this, BSObserverEvent.ACTIVITY_PAUSE, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                MobclickAgent.onPause(context);
                //        StatService.onPause(context);
            }
        });
    }

    public void init(Context context) {
        this.context = context;
        MobclickAgent.updateOnlineConfig(context);
        if (BSUtils.isDebug()) {
            MobclickAgent.setDebugMode(true);
        }
    }

    public void event(String eventId) {
        event(eventId, null);
    }

    public void event(String eventId, String label) {
        BSLog.d("BSAnalyses", "eventID=" + eventId + " label=" + label);
        if (TextUtils.isEmpty(label)) {
            MobclickAgent.onEvent(context, eventId);
            label = "0";
        } else {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Label", label);
            MobclickAgent.onEvent(context, eventId, map);
        }
        //        StatService.onEvent(context, eventId, label);
    }

    //    public void eventStart(String eventId, String label) {
    //        BSLog.d("BSAnalyses", "start eventID=" + eventId + " label=" + label);
    //        StatService.onEventStart(context, eventId, label);
    //    }
    //
    //    public void eventEnd(String eventId, String label) {
    //        BSLog.d("BSAnalyses", "end eventID=" + eventId + " label=" + label);
    //        StatService.onEventEnd(context, eventId, label);
    //    }

}

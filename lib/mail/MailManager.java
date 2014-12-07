package com.bstoneinfo.lib.mail;

import java.util.Observable;
import java.util.Observer;

import javax.mail.AuthenticationFailedException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.mail.SimpleMailSender.MailSenderListener;

public class MailManager {

    public static int mailServerPort = 25;
    public static boolean mailValidate = false;
    public static String mailServerHost;
    public static String mailUserName;
    public static String mailPassword;
    public static String mailFromAddress;
    public static String mailToAddress;

    private static MailManager instance = new MailManager();
    private JSONArray mailList = new JSONArray();

    public static MailManager getInstance() {
        return instance;
    }

    private MailManager() {
        String s = BSApplication.getApplication().getDefaultPreferences().getString("MailQueue", "");
        if (!TextUtils.isEmpty(s)) {
            try {
                mailList = new JSONArray(s);
            } catch (JSONException e) {
            }
        }
        BSApplication.defaultNotificationCenter.addObserver(this, BSObserverEvent.APP_ENTER_FOREGROUND, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                sendMail();
            }
        });
    }

    private void save() {
        BSApplication.getApplication().getDefaultPreferences().edit().putString("MailQueue", mailList.toString()).commit();
    }

    public void sendMail(String subject, String body) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("subject", subject).put("body", body);
            mailList.put(jsonObject);
            save();
            sendMail();
        } catch (JSONException e) {
        }
    }

    private void removeFirst() {
        JSONArray newMailList = new JSONArray();
        for (int i = 1; i < mailList.length(); i++) {
            try {
                newMailList.put(mailList.get(i));
            } catch (JSONException e) {
            }
        }
        mailList = newMailList;
        save();
    }

    private void sendMail() {
        if (mailList.length() == 0) {
            return;
        }
        JSONObject jsonObject = mailList.optJSONObject(0);
        String subject = jsonObject != null ? jsonObject.optString("subject") : null;
        final String body = jsonObject != null ? jsonObject.optString("body") : null;
        if (TextUtils.isEmpty(subject) || TextUtils.isEmpty(body)) {
            removeFirst();
            sendMail();
            return;
        }
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost(mailServerHost);
        mailInfo.setMailServerPort(mailServerPort);
        mailInfo.setValidate(mailValidate);
        mailInfo.setUserName(mailUserName);
        mailInfo.setPassword(mailPassword);
        mailInfo.setFromAddress(mailFromAddress);
        mailInfo.setToAddress(mailToAddress);
        mailInfo.setSubject(subject);
        mailInfo.setContent(body);
        SimpleMailSender sms = new SimpleMailSender();
        sms.sendTextMail(mailInfo, new MailSenderListener() {
            @Override
            public void finished() {
                BSAnalyses.getInstance().event("SendMailResult", "Success");
                removeFirst();
                sendMail();
            }

            @Override
            public void failed(Exception e) {
                BSAnalyses.getInstance().event("SendMailResult", e.toString());
                if (e instanceof AuthenticationFailedException) {
                    JSONObject jsonContactUsConfig = BSUtils.optJsonObject(BSApplication.getApplication().getRemoteConfig(), "ContactUs");
                    String event = jsonContactUsConfig.optString("MailFailBodyEvent");
                    if (!TextUtils.isEmpty(event)) {
                        BSAnalyses.getInstance().event(event, e.toString() + "\n" + body);
                    }
                    removeFirst();
                    sendMail();
                }
            }
        });
    }
}

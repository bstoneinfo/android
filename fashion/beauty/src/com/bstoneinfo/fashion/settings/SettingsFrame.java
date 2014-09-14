package com.bstoneinfo.fashion.settings;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.frame.BSFrame;

import custom.R;

public class SettingsFrame extends BSFrame {

    private final View commendButton;
    private final View feedbackButton;
    private final View cooperationButton;
    private final View contactusButton;

    public SettingsFrame(Context context) {
        super(context, R.layout.settings);
        feedbackButton = getRootView().findViewById(R.id.feedback);
        commendButton = getRootView().findViewById(R.id.commend);
        cooperationButton = getRootView().findViewById(R.id.cooperation);
        contactusButton = getRootView().findViewById(R.id.contactus);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        final CommendApp commendApp = new CommendApp(getActivity());
        if (isFunctionOn("Commend", "Enable")) {
            commendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BSAnalyses.getInstance().event("Settings", "Commend");
                    commendApp.download();
                }
            });
        } else {
            commendButton.setVisibility(View.GONE);
        }

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSAnalyses.getInstance().event("Settings", "FeedBack");
                Intent intent = new Intent(getContext(), FeedbackActivity.class);
                getActivity().startActivity(intent);
            }
        });

        if (isFunctionOn("ContactUs", "EnableCooperation")) {
            cooperationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BSAnalyses.getInstance().event("Settings", "Cooperation");
                    Intent intent = new Intent(getContext(), CooperationActivity.class);
                    getActivity().startActivity(intent);
                }
            });
        } else {
            cooperationButton.setVisibility(View.GONE);
        }

        contactusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSAnalyses.getInstance().event("Settings", "ContactUs");
                JSONObject jsonContactUsConfig = BSUtils.optJsonObject(BSApplication.getApplication().getRemoteConfig(), "ContactUs");
                String email = jsonContactUsConfig.optString("email");
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + email));
                intent.putExtra(Intent.EXTRA_SUBJECT, getContext().getString(R.string.contactus_email_subject) + " - Android");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                getActivity().startActivity(intent);
            }
        });
    }

    public boolean isFunctionOn(String group, String name) {
        JSONObject jsonConfig = BSUtils.optJsonObject(BSApplication.getApplication().getRemoteConfig(), group);
        String channel = BSUtils.getManifestMetaData("UMENG_CHANNEL");
        JSONArray jsonArray = BSUtils.optJsonArray(jsonConfig, name);
        for (int i = 0; i < jsonArray.length(); i++) {
            String tag = jsonArray.optString(i);
            if (TextUtils.equals(channel, tag)) {
                return true;
            }
        }
        return false;
    }

}

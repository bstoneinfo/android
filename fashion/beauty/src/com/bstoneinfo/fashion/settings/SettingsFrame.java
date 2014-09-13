package com.bstoneinfo.fashion.settings;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
        if (!commendApp.isCommendAppOn()) {
            commendButton.setVisibility(View.GONE);
        } else {
            commendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BSAnalyses.getInstance().event("Settings", "Commend");
                    commendApp.download();
                }
            });
        }

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSAnalyses.getInstance().event("Settings", "FeedBack");
                Intent intent = new Intent(getContext(), FeedbackActivity.class);
                getActivity().startActivity(intent);
            }
        });

        cooperationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

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

}

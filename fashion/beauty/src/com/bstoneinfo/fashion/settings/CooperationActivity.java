package com.bstoneinfo.fashion.settings;

import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.frame.BSActivity;
import com.bstoneinfo.lib.mail.MailManager;

import custom.R;

public class CooperationActivity extends BSActivity {

    final SharedPreferences preferences = BSApplication.getApplication().getDefaultSharedPreferences();
    private EditText cooperationEdit;

    private void savePreference() {
        Editor editor = preferences.edit();
        editor.putString("FeedbackCooperate", cooperationEdit.getEditableText().toString());
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cooperation);

        JSONObject jsonContactUsConfig = BSUtils.optJsonObject(BSApplication.getApplication().getRemoteConfig(), "ContactUs");
        cooperationEdit = (EditText) findViewById(R.id.cooperationEdit);
        cooperationEdit.setText(preferences.getString("FeedbackCooperate", ""));
        ((TextView) findViewById(R.id.cooperationTips)).setText(jsonContactUsConfig.optString("cooperate_tips", getString(R.string.cooperation_tips)));

        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSAnalyses.getInstance().event("Cooperate", "Cancel");
                savePreference();
                finish();
            }
        });
        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cooperationEdit.getEditableText().length() == 0) {
                    BSAnalyses.getInstance().event("Cooperate", "Empty");
                    Toast.makeText(CooperationActivity.this, R.string.feedback_marketinfo_empty, Toast.LENGTH_LONG).show();
                    return;
                }
                savePreference();
                BSAnalyses.getInstance().event("Cooperate", "Submited");
                MailManager.getInstance().sendMail(getString(R.string.cooperation_email_subject) + " - Beauty - Android ", cooperationEdit.getEditableText().toString());
                cooperationEdit.setText("");
                savePreference();
                finish();
            }
        });
        BSApplication.defaultNotificationCenter.addObserver(this, BSObserverEvent.APP_ENTER_BACKGROUND, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                savePreference();
            }
        });
    }

    @Override
    public void onBackPressed() {
        savePreference();
        BSAnalyses.getInstance().event("Cooperate", "Back");
        super.onBackPressed();
    }
}

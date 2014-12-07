package com.jinhuvv.sitemgr.settings;

import java.util.Observable;
import java.util.Observer;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.frame.BSActivity;
import com.bstoneinfo.lib.mail.MailManager;

import custom.R;

public class FeedbackActivity extends BSActivity {

    final SharedPreferences preferences = BSApplication.getApplication().getDefaultPreferences();
    EditText suggestEdit;

    private void savePreference() {
        Editor editor = preferences.edit();
        editor.putString("FeedbackSuggest", suggestEdit.getEditableText().toString());
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        suggestEdit = (EditText) findViewById(R.id.suggestEdit);
        suggestEdit.setText(preferences.getString("FeedbackSuggest", ""));

        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSAnalyses.getInstance().event("Feedback", "Cancel");
                savePreference();
                finish();
            }
        });
        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreference();
                BSAnalyses.getInstance().event("Feedback", "Submited");
                MailManager.getInstance().sendMail(getString(R.string.feedback_email_subject) + " - LAW - Android ", suggestEdit.getEditableText().toString());
                suggestEdit.setText("");
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
        BSAnalyses.getInstance().event("Feedback", "Back");
        super.onBackPressed();
    }
}

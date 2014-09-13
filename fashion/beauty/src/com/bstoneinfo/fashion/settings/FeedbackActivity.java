package com.bstoneinfo.fashion.settings;

import java.util.Observable;
import java.util.Observer;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bstoneinfo.fashion.data.MailManager;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.app.BSActivity;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;

import custom.R;

public class FeedbackActivity extends BSActivity {

    final SharedPreferences preferences = BSApplication.getApplication().getDefaultSharedPreferences();
    EditText marketEdit;
    EditText suggestEdit;

    private void savePreference() {
        Editor editor = preferences.edit();
        editor.putString("FeedbackMarket", marketEdit.getEditableText().toString());
        editor.putString("FeedbackSuggest", suggestEdit.getEditableText().toString());
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        marketEdit = (EditText) findViewById(R.id.marketEdit);
        suggestEdit = (EditText) findViewById(R.id.suggestEdit);
        marketEdit.setText(preferences.getString("FeedbackMarket", ""));
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
                if (marketEdit.getEditableText().length() == 0 && suggestEdit.getEditableText().length() == 0) {
                    BSAnalyses.getInstance().event("Feedback", "Empty");
                    Toast.makeText(FeedbackActivity.this, R.string.feedback_marketinfo_empty, Toast.LENGTH_LONG).show();
                    return;
                }
                savePreference();
                BSAnalyses.getInstance().event("Feedback", "Submited");
                String market = marketEdit.getEditableText().toString();
                MailManager.getInstance().sendMail(getString(R.string.feedback_email_subject) + " - Android " + market, market + "\n\n" + suggestEdit.getEditableText().toString());
                marketEdit.setText("");
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

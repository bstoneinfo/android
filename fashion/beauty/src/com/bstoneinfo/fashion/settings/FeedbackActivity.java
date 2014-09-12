package com.bstoneinfo.fashion.settings;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSUtils;

import custom.R;

public class FeedbackActivity extends Activity {

    private JSONObject jsonConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        jsonConfig = BSUtils.optJsonObject(BSApplication.getApplication().getRemoteConfig(), "Feedback");

        BSAnalyses.getInstance().event("Feedback", "Clicked");
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSAnalyses.getInstance().event("Feedback", "Cancel");
                finish();
            }
        });
        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSAnalyses.getInstance().event("Feedback", "Submit");
                EditText marketEdit = (EditText) findViewById(R.id.marketEdit);
                EditText suggestEdit = (EditText) findViewById(R.id.suggestEdit);
                if (marketEdit.getEditableText().length() == 0) {
                    Toast.makeText(FeedbackActivity.this, R.string.feedback_marketinfo_empty, Toast.LENGTH_LONG).show();
                    return;
                }
                finish();
            }
        });

        TextView tv_tips = (TextView) findViewById(R.id.tv_tips);
        String tips = jsonConfig.optString("tips");
        if (TextUtils.isEmpty(tips)) {
            tv_tips.setVisibility(View.GONE);
        } else {
            tv_tips.setText(tips);
        }
    }
}

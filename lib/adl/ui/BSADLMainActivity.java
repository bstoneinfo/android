package com.bstoneinfo.lib.adl.ui;

import android.os.Bundle;

import com.bstoneinfo.lib.adl.BSADL;
import com.bstoneinfo.lib.frame.BSLayerFrame;
import com.bstoneinfo.lib.frame.BSMainActivity;

public class BSADLMainActivity extends BSMainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BSLayerFrame mainFrame = new BSLayerFrame(BSADL.loadUI(this, "main")) {
            @Override
            public boolean back() {
                if (super.back()) {
                    return true;
                }
                return BSADLMainActivity.this.back();
            };
        };
        setMainFrame(mainFrame);
    }

    protected boolean back() {
        finish();
        return true;
    }

}

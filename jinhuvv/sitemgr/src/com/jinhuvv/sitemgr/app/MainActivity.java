package com.jinhuvv.sitemgr.app;

import android.os.Bundle;

import com.bstoneinfo.lib.ad.BSAdScreen;
import com.bstoneinfo.lib.adl.BSADL;
import com.bstoneinfo.lib.frame.BSFrame;
import com.bstoneinfo.lib.frame.BSMainActivity;
import com.jinhuvv.sitemgr.data.ProfileManager;

import custom.R;

public class MainActivity extends BSMainActivity {

    private BSAdScreen adFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BSFrame mainFrame = new BSFrame(this) {
            @Override
            public boolean back() {
                if (super.back()) {
                    return true;
                }
                return MainActivity.this.back();
            }
        };
        setMainFrame(mainFrame);

        if (!ProfileManager.islogin()) {
            mainFrame.addChild(BSADL.loadFrame(this, "ui/login"));
        } else {
            mainFrame.addChild(BSADL.loadFrame(this, "ui/main"));
        }

        //        MainDBHelper.createSingleton(this);

        //        BSFrame newestFrame = new BSFrame(this);
        //        BSFrame searchFame = new BSFrame(this);
        //        FavoriteFrame favoriteFrame = new FavoriteFrame(this);
        //        SettingsFrame settingsFrame = new SettingsFrame(this);
        //
        //        String[] titles = new String[] { getString(R.string.tab_newest), getString(R.string.tab_search), getString(R.string.tab_favorite), getString(R.string.tab_settings) };
        //        int[] normalTabItemDrawableIDs = new int[] { R.drawable.tab_explore_normal, R.drawable.tab_search_normal, R.drawable.tab_favorite_normal, R.drawable.tab_settings_normal };
        //        int[] selectedTabItemDrawableIDs = new int[] { R.drawable.tab_explore_selected, R.drawable.tab_search_selected, R.drawable.tab_favorite_selected,
        //                R.drawable.tab_settings_selected };
        //        BSTabbedFrame tabbedFrame = new BSTabbedFrame(this, new BSFrame[] { newestFrame, searchFame, favoriteFrame, settingsFrame }, titles, normalTabItemDrawableIDs,
        //                selectedTabItemDrawableIDs, BSActivity.dip2px(50), 0);

        adFullscreen = new BSAdScreen(this, "Screen");
    }

    protected boolean back() {
        confirm(R.string.app_name, R.string.confirm_exit, R.string.ok, R.string.cancel, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, null, null);
        return true;
    }

    @Override
    protected void onDestroy() {
        adFullscreen.destroy();
        super.onDestroy();
    }

}

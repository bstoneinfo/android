package com.fa1000.law.app;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.bstoneinfo.lib.ad.BSAdScreen;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.frame.BSFrame;
import com.bstoneinfo.lib.frame.BSLayerFrame;
import com.bstoneinfo.lib.frame.BSMainActivity;
import com.fa1000.law.favorite.FavoriteFrame;
import com.fa1000.law.settings.SettingsFrame;

import custom.R;

public class MainActivity extends BSMainActivity {

    private BSAdScreen adFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        MainDBHelper.createSingleton(this);

        BSFrame newestFrame = new BSFrame(this);
        BSFrame searchFame = new BSFrame(this);
        FavoriteFrame favoriteFrame = new FavoriteFrame(this);
        SettingsFrame settingsFrame = new SettingsFrame(this);

        String[] titles = new String[] { getString(R.string.tab_newest), getString(R.string.tab_search), getString(R.string.tab_favorite), getString(R.string.tab_settings) };
        MyPagerFrame pagerFrame = new MyPagerFrame(this, new BSFrame[] { newestFrame, searchFame, favoriteFrame, settingsFrame }, titles, 0);
        pagerFrame.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (arg0 == 0) {
                    BSAnalyses.getInstance().event("MainTabClick", "Explore");
                } else if (arg0 == 1) {
                    BSAnalyses.getInstance().event("MainTabClick", "Histroy");
                } else if (arg0 == 2) {
                    BSAnalyses.getInstance().event("MainTabClick", "Favorite");
                } else if (arg0 == 3) {
                    BSAnalyses.getInstance().event("MainTabClick", "Settings");
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        BSLayerFrame mainFrame = new BSLayerFrame(pagerFrame) {
            @Override
            public boolean back() {
                if (super.back()) {
                    return true;
                }
                return MainActivity.this.back();
            };

        };
        setMainFrame(mainFrame);

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

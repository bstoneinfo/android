package com.bstoneinfo.fashion.app;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.bstoneinfo.fashion.data.CategoryManager;
import com.bstoneinfo.fashion.data.MainDBHelper;
import com.bstoneinfo.fashion.favorite.FavoriteFrame;
import com.bstoneinfo.fashion.favorite.FavoriteManager;
import com.bstoneinfo.fashion.ui.main.CategoryFrame;
import com.bstoneinfo.fashion.ui.main.ExploreWaterFallFrame;
import com.bstoneinfo.fashion.ui.main.HistroyWaterFallFrame;
import com.bstoneinfo.fashion.ui.main.SettingsFrame;
import com.bstoneinfo.lib.ad.BSAdScreen;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.app.BSActivity;
import com.bstoneinfo.lib.frame.BSFrame;
import com.bstoneinfo.lib.frame.BSLayerFrame;

import custom.Config;
import custom.R;

public class MainActivity extends BSActivity {

    private BSAdScreen adFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainDBHelper.createSingleton(this);

        BSFrame frame1, frame2;
        if (Config.isPro) {
            frame1 = new CategoryFrame(this, "51");
            frame2 = new CategoryFrame(this, "52");
        } else {
            frame1 = new ExploreWaterFallFrame(this, "51");
            frame2 = new HistroyWaterFallFrame(this, "51");
        }
        FavoriteFrame favoriteFrame = new FavoriteFrame(this);
        SettingsFrame settingsFrame = new SettingsFrame(this);

        if (Config.isPro) {
        } else {
            String[] titles = new String[] { getString(R.string.tab_explore), getString(R.string.tab_history), getString(R.string.tab_favorite), getString(R.string.tab_settings) };
            MyPagerFrame pagerFrame = new MyPagerFrame(this, new BSFrame[] { frame1, frame2, favoriteFrame, settingsFrame }, titles);
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
        }

        adFullscreen = new BSAdScreen(this);

        BSAnalyses.getInstance().event("language", Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry());
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
        CategoryManager.getInstance().reset();
        FavoriteManager.getInstance().reset();
        super.onDestroy();
    }

}

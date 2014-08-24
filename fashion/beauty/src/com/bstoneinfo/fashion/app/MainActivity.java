package com.bstoneinfo.fashion.app;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.bstoneinfo.fashion.data.CategoryManager;
import com.bstoneinfo.fashion.data.MainDBHelper;
import com.bstoneinfo.fashion.favorite.FavoriteManager;
import com.bstoneinfo.fashion.favorite.FavoriteViewController;
import com.bstoneinfo.fashion.ui.main.CategoryViewController;
import com.bstoneinfo.fashion.ui.main.ExploreWaterFallViewController;
import com.bstoneinfo.fashion.ui.main.HistroyWaterFallViewController;
import com.bstoneinfo.fashion.ui.main.MyPagerBarViewController;
import com.bstoneinfo.lib.ad.BSAdScreen;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.ui.BSActivity;
import com.bstoneinfo.lib.ui.BSTabBarController;
import com.bstoneinfo.lib.ui.BSViewController;

import custom.Config;
import custom.R;

public class MainActivity extends BSActivity {

    private BSAdScreen adFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainDBHelper.createSingleton(this);

        BSViewController mainViewController, viewController1, viewController2;
        if (Config.isPro) {
            viewController1 = new CategoryViewController(this, "51");
            viewController2 = new CategoryViewController(this, "52");
        } else {
            viewController1 = new ExploreWaterFallViewController(this, "51");
            viewController2 = new HistroyWaterFallViewController(this, "51");
        }
        FavoriteViewController favoriteViewController = new FavoriteViewController(this);
        //        SettingsViewController settingsViewController = new SettingsViewController(this);

        ArrayList<BSViewController> childViewControllers = new ArrayList<BSViewController>();
        childViewControllers.add(viewController1);
        childViewControllers.add(viewController2);
        childViewControllers.add(favoriteViewController);
        //        childViewControllers.add(settingsViewController);

        if (Config.isPro) {
            mainViewController = new BSTabBarController(this, R.layout.maintabbar, childViewControllers, 0) {
                @Override
                public boolean back() {
                    if (super.back()) {
                        return true;
                    }
                    return MainActivity.this.back();
                };
            };
        } else {
            ArrayList<String> titles = new ArrayList<String>();
            titles.add(getString(R.string.tab_explore));
            titles.add(getString(R.string.tab_history));
            titles.add(getString(R.string.tab_favorite));
            mainViewController = new MyPagerBarViewController(this, childViewControllers, titles) {
                @Override
                public boolean back() {
                    if (super.back()) {
                        return true;
                    }
                    return MainActivity.this.back();
                };
            };
            ((MyPagerBarViewController) mainViewController).setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageSelected(int arg0) {
                    if (arg0 == 0) {
                        BSAnalyses.getInstance().event("MainTabClick", "Explore");
                    } else if (arg0 == 1) {
                        BSAnalyses.getInstance().event("MainTabClick", "Histroy");
                    } else if (arg0 == 2) {
                        BSAnalyses.getInstance().event("MainTabClick", "Favorite");
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            });
        }

        setMainViewController(mainViewController);

        adFullscreen = new BSAdScreen(this);
        adFullscreen.start();
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

package com.bstoneinfo.fashion.app;

import java.util.ArrayList;

import android.os.Bundle;

import com.bstoneinfo.fashion.data.CategoryManager;
import com.bstoneinfo.fashion.data.MainDBHelper;
import com.bstoneinfo.fashion.favorite.FavoriteManager;
import com.bstoneinfo.fashion.favorite.FavoriteViewController;
import com.bstoneinfo.fashion.ui.main.CategoryViewController;
import com.bstoneinfo.fashion.ui.main.ExploreWaterFallViewController;
import com.bstoneinfo.fashion.ui.main.HistroyWaterFallViewController;
import com.bstoneinfo.fashion.ui.main.MyPagerBarViewController;
import com.bstoneinfo.lib.ad.BSAdFSAdChina;
import com.bstoneinfo.lib.ad.BSAdFullscreen;
import com.bstoneinfo.lib.ui.BSActivity;
import com.bstoneinfo.lib.ui.BSTabBarController;
import com.bstoneinfo.lib.ui.BSViewController;

import custom.Constant;
import custom.R;

public class MainActivity extends BSActivity {

    private final BSAdFullscreen adFullscreen = new BSAdFullscreen();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainDBHelper.createSingleton(this);

        BSViewController mainViewController, viewController1, viewController2;
        if (Constant.isPro) {
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

        if (Constant.isPro) {
            mainViewController = new BSTabBarController(this, R.layout.maintabbar, childViewControllers, 0);
        } else {
            ArrayList<String> titles = new ArrayList<String>();
            titles.add(getString(R.string.tab_explore));
            titles.add(getString(R.string.tab_history));
            titles.add(getString(R.string.tab_favorite));
            mainViewController = new MyPagerBarViewController(this, childViewControllers, titles);
        }

        setMainViewController(mainViewController);

        adFullscreen.addAdObject(new BSAdFSAdChina(this));
        adFullscreen.start();
    }

    @Override
    protected void onDestroy() {
        CategoryManager.getInstance().reset();
        FavoriteManager.getInstance().reset();
        super.onDestroy();
    }

}

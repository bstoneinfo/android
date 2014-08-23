package com.bstoneinfo.fashion.ui.main;

import android.content.Context;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.ad.BSAdBannerViewController;
import com.bstoneinfo.lib.ui.BSViewController;

public abstract class ImageAdWaterFallViewController extends BSViewController {

    private final LinearLayout waterfallLinearLayout;
    private final BSAdBannerViewController adBanner;
    protected final ImageWaterFallViewController imageWaterFallViewController;

    abstract protected void loadMore();

    abstract protected void recordFlurry(String event);

    public ImageAdWaterFallViewController(Context context, String dataEventName, String mainAdBannerName, String footerAdBannerName) {
        super(context);
        waterfallLinearLayout = new LinearLayout(context);
        waterfallLinearLayout.setOrientation(LinearLayout.VERTICAL);
        getRootView().addView(waterfallLinearLayout);
        imageWaterFallViewController = new ImageWaterFallViewController(getContext(), dataEventName, footerAdBannerName) {
            @Override
            protected void loadMore() {
                ImageAdWaterFallViewController.this.loadMore();
            }

            @Override
            protected void recordFlurry(String event) {
                ImageAdWaterFallViewController.this.recordFlurry(event);
            }
        };
        adBanner = new BSAdBannerViewController(getActivity(), mainAdBannerName);
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        addChildViewController(imageWaterFallViewController, waterfallLinearLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageWaterFallViewController.getRootView().getLayoutParams();
        params.weight = 1;
        params.height = 0;
        imageWaterFallViewController.getRootView().setLayoutParams(params);
        addChildViewController(adBanner, waterfallLinearLayout);
    }

}

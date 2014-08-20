package com.bstoneinfo.fashion.ui.main;

import android.content.Context;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.ad.BSAdBannerAdmob;
import com.bstoneinfo.lib.ui.BSViewController;

public abstract class ImageAdWaterFallViewController extends BSViewController {

    private final LinearLayout waterfallLinearLayout;
    private final BSAdBannerAdmob admob;
    protected final ImageWaterFallViewController imageWaterFallViewController;

    abstract protected void loadMore();

    abstract protected void recordFlurry(String event);

    public ImageAdWaterFallViewController(Context context, String dataEventName) {
        super(context);
        waterfallLinearLayout = new LinearLayout(context);
        waterfallLinearLayout.setOrientation(LinearLayout.VERTICAL);
        getRootView().addView(waterfallLinearLayout);
        imageWaterFallViewController = new ImageWaterFallViewController(getContext(), dataEventName) {
            @Override
            protected void loadMore() {
                ImageAdWaterFallViewController.this.loadMore();
            }

            @Override
            protected void recordFlurry(String event) {
                ImageAdWaterFallViewController.this.recordFlurry(event);
            }
        };
        admob = new BSAdBannerAdmob(getActivity());
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        addChildViewController(imageWaterFallViewController, waterfallLinearLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageWaterFallViewController.getRootView().getLayoutParams();
        params.weight = 1;
        params.height = 0;
        imageWaterFallViewController.getRootView().setLayoutParams(params);
        admob.start();
        waterfallLinearLayout.addView(admob.getAdView());
    }

    @Override
    protected void destroy() {
        admob.destroy();
        super.destroy();
    }
}

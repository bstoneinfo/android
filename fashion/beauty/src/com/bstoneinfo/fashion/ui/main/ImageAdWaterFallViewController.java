package com.bstoneinfo.fashion.ui.main;

import android.content.Context;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.ad.BSAdBannerAdmob;
import com.bstoneinfo.lib.ui.BSViewController;

public abstract class ImageAdWaterFallViewController extends BSViewController {

    private final String dataEventName;
    private final BSAdBannerAdmob admob;
    protected final ImageWaterFallViewController imageWaterFallViewController;

    abstract protected void loadMore();

    public ImageAdWaterFallViewController(Context context, String dataEventName) {
        super(new LinearLayout(context));
        ((LinearLayout) getRootView()).setOrientation(LinearLayout.VERTICAL);
        this.dataEventName = dataEventName;
        imageWaterFallViewController = new ImageWaterFallViewController(getContext(), dataEventName) {
            @Override
            protected void loadMore() {
                ImageAdWaterFallViewController.this.loadMore();
            }
        };
        admob = new BSAdBannerAdmob(getActivity());
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        addChildViewController(imageWaterFallViewController);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageWaterFallViewController.getRootView().getLayoutParams();
        params.weight = 1;
        params.height = 0;
        imageWaterFallViewController.getRootView().setLayoutParams(params);
        admob.start();
        getRootView().addView(admob.getAdView());
    }

    @Override
    protected void destroy() {
        admob.destroy();
        super.destroy();
    }
}

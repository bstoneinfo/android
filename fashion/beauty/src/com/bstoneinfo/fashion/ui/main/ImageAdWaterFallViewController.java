package com.bstoneinfo.fashion.ui.main;

import android.content.Context;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.ad.BSAdBannerAdChina;
import com.bstoneinfo.lib.ad.BSAdObject;
import com.bstoneinfo.lib.ui.BSViewController;

public abstract class ImageAdWaterFallViewController extends BSViewController {

    private final BSAdObject admob;
    protected final ImageWaterFallViewController imageWaterFallViewController;

    abstract protected void loadMore();

    abstract protected void recordFlurry(String event);

    public ImageAdWaterFallViewController(Context context, String dataEventName) {
        super(new LinearLayout(context));
        ((LinearLayout) getRootView()).setOrientation(LinearLayout.VERTICAL);
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
        admob = new BSAdBannerAdChina(getActivity(), "AppKey_AdChina_Banner2");
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

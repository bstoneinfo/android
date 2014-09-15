package com.bstoneinfo.fashion.ui.main;

import android.content.Context;
import android.widget.LinearLayout;

import com.bstoneinfo.lib.ad.BSAdBanner;
import com.bstoneinfo.lib.frame.BSFrame;

public abstract class ImageAdWaterFallFrame extends BSFrame {

    private final LinearLayout waterfallLinearLayout;
    private final BSAdBanner adBanner;
    protected final ImageWaterFallFrame imageWaterFallFrame;

    abstract protected void loadMore();

    abstract protected void recordFlurry(String event);

    public ImageAdWaterFallFrame(Context context, String dataEventName, String mainAdBannerName) {
        super(context);
        waterfallLinearLayout = new LinearLayout(context);
        waterfallLinearLayout.setOrientation(LinearLayout.VERTICAL);
        getRootView().addView(waterfallLinearLayout);
        imageWaterFallFrame = new ImageWaterFallFrame(getContext(), dataEventName, null) {
            @Override
            protected void loadMore() {
                ImageAdWaterFallFrame.this.loadMore();
            }

            @Override
            protected void recordFlurry(String event) {
                ImageAdWaterFallFrame.this.recordFlurry(event);
            }
        };
        adBanner = new BSAdBanner(getActivity(), mainAdBannerName);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        addChild(imageWaterFallFrame, waterfallLinearLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageWaterFallFrame.getRootView().getLayoutParams();
        params.weight = 1;
        params.height = 0;
        imageWaterFallFrame.getRootView().setLayoutParams(params);
        addChild(adBanner, waterfallLinearLayout);
    }

}

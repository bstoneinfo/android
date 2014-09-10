package com.bstoneinfo.fashion.ui.main;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.bstoneinfo.fashion.app.MyUtils;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.ui.browse.PhotoBrowseFrame;
import com.bstoneinfo.lib.ad.BSAdBanner;
import com.bstoneinfo.lib.app.BSActivity;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSImageLoader.BSImageLoadStatus;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.connection.BSConnectionQueue;
import com.bstoneinfo.lib.frame.BSWaterFallFrame;
import com.bstoneinfo.lib.view.BSImageView;
import com.bstoneinfo.lib.view.BSScrollView.OnScrollChangedListener;

import custom.R;

public abstract class ImageWaterFallFrame extends BSWaterFallFrame {

    public final static int COLUMN_COUNT = 3;
    public final static int COLUMN_INTERVAL_DP = 5;
    private final int columnWidth = (BSActivity.getDisplayMetrics().widthPixels - BSActivity.dip2px(COLUMN_INTERVAL_DP) * (COLUMN_COUNT + 1)) / COLUMN_COUNT;

    protected final BSConnectionQueue connectionQueue = new BSConnectionQueue(10);
    protected final ArrayList<CategoryItemData> itemDataList = new ArrayList<CategoryItemData>();
    private final ArrayList<BSImageView> imageViewList = new ArrayList<BSImageView>();
    private final String dataEventName;
    private boolean memoryWaringReceived = false;
    public final LinearLayout footerView;

    abstract protected void loadMore();

    abstract protected void recordFlurry(String event);

    public ArrayList<CategoryItemData> getDataList() {
        return itemDataList;
    }

    public ImageWaterFallFrame(Context context, String dataEventName, String footerAdBannerName) {
        super(context, COLUMN_COUNT, BSActivity.dip2px(COLUMN_INTERVAL_DP));
        this.dataEventName = dataEventName;
        footerView = new LinearLayout(getContext());
        footerView.setOrientation(LinearLayout.VERTICAL);
        View loadmoreView = LayoutInflater.from(getContext()).inflate(R.layout.loadmore, null);
        setFooterView(footerView, loadmoreView.findViewById(R.id.loadmore_normal), loadmoreView.findViewById(R.id.loadmore_loading),
                loadmoreView.findViewById(R.id.loadmore_failed), null);
        if (!TextUtils.isEmpty(footerAdBannerName)) {
            BSAdBanner adBanner = new BSAdBanner(getActivity(), footerAdBannerName);
            adBanner.setVerticalShow(true);
            addChild(adBanner, footerView);
        }
        footerView.addView(loadmoreView);
        View.OnClickListener loadmoreClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPullupState(PullUpState.LOADING);
                loadMore();
                recordFlurry("WaterFall_LoadMore");
            }
        };
        loadmoreView.findViewById(R.id.loadmore_normal).setOnClickListener(loadmoreClickListener);
        loadmoreView.findViewById(R.id.loadmore_failed).setOnClickListener(loadmoreClickListener);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        recordFlurry("WaterFall_Init");
        setPullupState(PullUpState.LOADING);
        getScrollView().setOnScrollChangedListener(new OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                if (t != oldt) {
                    checkVisible();
                }
            }
        });
        BSApplication.defaultNotificationCenter.addObserver(this, dataEventName, new Observer() {
            @SuppressWarnings("unchecked")
            @Override
            public void update(Observable observable, Object data) {
                ArrayList<CategoryItemData> dataList = (ArrayList<CategoryItemData>) data;
                if (dataList == null) {//fail
                    setPullupState(PullUpState.FAILED);
                } else {//success
                    if (dataList.isEmpty()) {//结束
                        setPullupState(PullUpState.FINISHED);
                    } else {
                        setPullupState(PullUpState.NORMAL);
                        itemDataList.addAll(dataList);
                        for (CategoryItemData itemData : dataList) {
                            addView(itemData, false);
                        }
                    }
                }
            }
        });
        BSApplication.defaultNotificationCenter.addObserver(this, BSObserverEvent.LOW_MEMORY_WARNING, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                memoryWaringReceived = true;
                checkVisible();
            }
        });
        loadMore();
    }

    @Override
    protected void onShow() {
        super.onShow();
        checkVisible();
    }

    @Override
    protected void onHide() {
        super.onHide();
        checkVisible();
    }

    private void checkVisible() {
        if (getFrameStatus() == FrameStatus.SHOWING || getFrameStatus() == FrameStatus.SHOWN) {
            int y1 = getScrollView().getScrollY();
            int y2 = y1 + getRootView().getHeight();
            for (BSImageView imageView : imageViewList) {
                int top = imageView.getTop();
                int bottom = imageView.getBottom();
                if (bottom >= y1 && top <= y2) {
                    if (memoryWaringReceived) {
                        setImageViewVisible(imageView, true);
                    } else if (imageView.getImageLoadStatus() == BSImageLoadStatus.FAILED) {
                        imageView.setUrl(imageView.getUrl());
                    }
                } else {
                    if (memoryWaringReceived) {
                        setImageViewVisible(imageView, false);
                    }
                }
            }
        } else {
            if (memoryWaringReceived) {
                for (BSImageView imageView : imageViewList) {
                    setImageViewVisible(imageView, false);
                }
            }
        }
    }

    private void setImageViewVisible(BSImageView imageView, boolean bVisible) {
        imageView.setVisible(bVisible);
    }

    public void addView(final CategoryItemData itemData, boolean addToHead) {
        final String remoteUrl = "http://" + MyUtils.getHost() + itemData.thumbURL;
        final BSImageView imageView = new BSImageView(getContext());
        imageView.setBackgroundColor(0xFFD0D0D0);
        imageView.setConnectionQueue(connectionQueue);
        imageView.setScaleType(ScaleType.FIT_CENTER);
        int width = columnWidth;
        int height = columnWidth * itemData.thumbHeight / itemData.thumbWidth;
        imageViewList.add(imageView);
        if (memoryWaringReceived) {
            setImageViewVisible(imageView, getFrameStatus() == FrameStatus.SHOWING || getFrameStatus() == FrameStatus.SHOWN);
        }
        super.addView(imageView, width, height, addToHead);
        imageView.setUrl(remoteUrl);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BSImageLoadStatus status = imageView.getImageLoadStatus();
                if (status == BSImageLoadStatus.LOADED) {
                    int position = -1;
                    for (int i = 0; i < itemDataList.size(); i++) {
                        if (itemData == itemDataList.get(i)) {
                            position = i;
                            break;
                        }
                    }
                    if (position < 0) {
                        return;
                    }
                    PhotoBrowseFrame photoBrowseViewController = new PhotoBrowseFrame(getContext(), itemData.category, itemDataList, dataEventName, position,
                            getPullUpState() == PullUpState.FINISHED) {
                        @Override
                        protected void loadMore() {
                            ImageWaterFallFrame.this.loadMore();
                            ImageWaterFallFrame.this.recordFlurry("Browse_LoadMore");
                        }

                        @Override
                        protected void recordFlurry(String event) {
                            ImageWaterFallFrame.this.recordFlurry("Browse_Slide");
                        }
                    };
                    getActivity().getMainFrame().addChild(photoBrowseViewController);
                    recordFlurry("WaterFall_Click");
                } else if (status == BSImageLoadStatus.FAILED) {
                    imageView.setUrl(remoteUrl);
                }
            }
        });
    }

    public void removeView(CategoryItemData itemData) {
        final String remoteUrl = "http://" + MyUtils.getHost() + itemData.thumbURL;
        for (int i = 0; i < columnCount; i++) {
            for (int j = 0; j < columnLayoutArray[i].getChildCount(); j++) {
                BSImageView imageView = (BSImageView) columnLayoutArray[i].getChildAt(j);
                if (TextUtils.equals(imageView.getUrl(), remoteUrl)) {
                    int height = imageView.getHeight();
                    columnHeightArray[i] -= height;
                    columnLayoutArray[i].removeViewAt(j);
                    break;
                }
            }
        }
    }

}

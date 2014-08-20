package com.bstoneinfo.fashion.favorite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bstoneinfo.fashion.app.NotificationEvent;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.ui.main.ImageAdWaterFallViewController;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.common.BSApplication;

import custom.R;

public class FavoriteViewController extends ImageAdWaterFallViewController {

    private boolean dataChanged = false;
    final View emptyTip;

    public FavoriteViewController(Context context) {
        super(context, NotificationEvent.FAVORITE_QUERYLIST_FINISHED);
        emptyTip = LayoutInflater.from(getContext()).inflate(R.layout.empty_tips, null);
        TextView textView = (TextView) emptyTip.findViewById(R.id.textView);

        String textTip = getContext().getString(R.string.favorite_empty_tip);
        int spanIndex = textTip.indexOf("{heart}");
        SpannableString spanString = new SpannableString(textTip);
        ImageSpan imgSpan = new ImageSpan(context, R.drawable.heart_grey);
        spanString.setSpan(imgSpan, spanIndex, spanIndex + 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanString);

        BSApplication.defaultNotificationCenter.addObserver(this, NotificationEvent.FAVORITE_QUERYLIST_FINISHED, new Observer() {
            @SuppressWarnings("unchecked")
            @Override
            public void update(Observable observable, Object data) {
                ArrayList<CategoryItemData> dataList = (ArrayList<CategoryItemData>) data;
                if (dataList != null && dataList.isEmpty() && imageWaterFallViewController.getDataList().isEmpty()) {
                    imageWaterFallViewController.footerView.addView(emptyTip, 0);
                } else {
                    imageWaterFallViewController.footerView.removeView(emptyTip);
                }
            }
        });
    }

    @Override
    protected void loadMore() {
        FavoriteManager.getInstance().favoriteMore();
    }

    @Override
    protected void recordFlurry(String event) {
        BSAnalyses.getInstance().event(event, "Favorite");
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        BSApplication.defaultNotificationCenter.addObserver(this, NotificationEvent.CATEGORY_ITEM_DATA_FINISHED, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                CategoryItemData itemData = (CategoryItemData) data;
                boolean isFavorite = FavoriteManager.getInstance().isFavorite(itemData);
                Iterator<CategoryItemData> iterator = imageWaterFallViewController.getDataList().iterator();
                while (iterator.hasNext()) {
                    CategoryItemData i = iterator.next();
                    if (TextUtils.equals(itemData.getFavoriteKey(), i.getFavoriteKey())) {
                        if (!isFavorite) {
                            iterator.remove();
                            dataChanged = true;
                        }
                        break;
                    }
                }
                if (isFavorite) {
                    imageWaterFallViewController.getDataList().add(0, itemData);
                    dataChanged = true;
                }
                if (imageWaterFallViewController.getDataList().isEmpty()) {
                    imageWaterFallViewController.footerView.addView(emptyTip, 0);
                } else {
                    imageWaterFallViewController.footerView.removeView(emptyTip);
                }
            }
        });
    }

    @Override
    protected void viewWillAppear() {
        super.viewWillAppear();
        if (dataChanged) {
            dataChanged = false;
            imageWaterFallViewController.removeAllViews();
            for (CategoryItemData itemData : imageWaterFallViewController.getDataList()) {
                imageWaterFallViewController.addView(itemData);
            }
        }
    }

}

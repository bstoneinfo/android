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

import com.bstoneinfo.fashion.app.MyObserverEvent;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.ui.main.ImageWaterFallFrame;
import com.bstoneinfo.lib.ad.BSAnalyses;
import com.bstoneinfo.lib.app.BSApplication;

import custom.R;

public class FavoriteFrame extends ImageWaterFallFrame {

    final View emptyTip;

    public FavoriteFrame(Context context) {
        super(context, MyObserverEvent.FAVORITE_QUERYLIST_FINISHED, "FavoriteMain");
        emptyTip = LayoutInflater.from(getContext()).inflate(R.layout.empty_tips, null);
        TextView textView = (TextView) emptyTip.findViewById(R.id.textView);

        String textTip = getContext().getString(R.string.favorite_empty_tip);
        int spanIndex = textTip.indexOf("{heart}");
        SpannableString spanString = new SpannableString(textTip);
        ImageSpan imgSpan = new ImageSpan(context, R.drawable.heart_grey);
        spanString.setSpan(imgSpan, spanIndex, spanIndex + 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanString);

        BSApplication.defaultNotificationCenter.addObserver(this, MyObserverEvent.FAVORITE_QUERYLIST_FINISHED, new Observer() {
            @SuppressWarnings("unchecked")
            @Override
            public void update(Observable observable, Object data) {
                ArrayList<CategoryItemData> dataList = (ArrayList<CategoryItemData>) data;
                if (dataList != null && dataList.isEmpty() && getDataList().isEmpty()) {
                    getRootView().addView(emptyTip);
                } else {
                    getRootView().removeView(emptyTip);
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
    protected void onLoad() {
        super.onLoad();
        BSApplication.defaultNotificationCenter.addObserver(this, MyObserverEvent.CATEGORY_ITEM_DATA_FINISHED, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                CategoryItemData itemData = (CategoryItemData) data;
                boolean isFavorite = FavoriteManager.getInstance().isFavorite(itemData);
                Iterator<CategoryItemData> iterator = getDataList().iterator();
                while (iterator.hasNext()) {
                    CategoryItemData i = iterator.next();
                    if (TextUtils.equals(itemData.getFavoriteKey(), i.getFavoriteKey())) {
                        if (!isFavorite) {
                            iterator.remove();
                            //TODO: remove a view
                        }
                        break;
                    }
                }
                if (isFavorite) {
                    getDataList().add(0, itemData);
                    //TODO : add a view
                }
                if (getDataList().isEmpty()) {
                    getRootView().addView(emptyTip);
                } else {
                    getRootView().removeView(emptyTip);
                }
            }
        });
    }

}

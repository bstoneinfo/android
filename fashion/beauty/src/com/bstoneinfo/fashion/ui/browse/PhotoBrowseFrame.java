package com.bstoneinfo.fashion.ui.browse;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.lib.ad.BSAdBanner;
import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.frame.BSFrame;
import com.bstoneinfo.lib.view.BSPagerView;
import com.bstoneinfo.lib.widget.BSCell;
import com.bstoneinfo.lib.widget.BSCellAdapter;

public abstract class PhotoBrowseFrame extends BSFrame {

    final private String categoryName;
    final private BSPagerView pagerView;
    final private String dataEventName;
    final private ArrayList<CategoryItemData> itemDataList;
    private int position;
    private boolean bLoadmoreEnded = false;
    private boolean bLoadmoreFailed = false;
    final private BSAdBanner adBanner;

    public PhotoBrowseFrame(Context context, String category, ArrayList<CategoryItemData> itemDataList, String dataEventName, int position, boolean loadmoreEnded) {
        super(new LinearLayout(context));
        ((LinearLayout) getRootView()).setOrientation(LinearLayout.VERTICAL);
        this.categoryName = category;
        this.dataEventName = dataEventName;
        this.position = position;
        this.itemDataList = (ArrayList<CategoryItemData>) itemDataList.clone();
        bLoadmoreEnded = loadmoreEnded;
        getRootView().setBackgroundColor(Color.BLACK);
        pagerView = new BSPagerView(getContext());
        adBanner = new BSAdBanner(getActivity(), "PhotoPager");
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        final BSCellAdapter adapter = new BSCellAdapter() {

            @Override
            public Object getData(int position) {
                if (position < itemDataList.size()) {
                    return itemDataList.get(position);
                }
                if (bLoadmoreFailed) {
                    return new CategoryItemData(categoryName);
                }
                return null;
            }

            @Override
            public int getCount() {
                return itemDataList.size() + (bLoadmoreEnded ? 0 : 1);
            }

            @Override
            public BSCell createCell() {
                return new PhotoBrowseCell(getContext());
            }
        };
        pagerView.setAdapter(adapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        getRootView().addView(pagerView, params);

        pagerView.setCurrentItem(position);
        pagerView.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int page) {
                if (page >= itemDataList.size()) {
                    loadMore();
                } else {
                    recordFlurry("Browse_Slide");
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        BSApplication.defaultNotificationCenter.addObserver(this, dataEventName, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                final int loadmorePosition = itemDataList.size();
                final PhotoBrowseCell lastCell = (PhotoBrowseCell) pagerView.getCell(loadmorePosition);
                ArrayList<CategoryItemData> dataList = (ArrayList<CategoryItemData>) data;
                if (dataList == null) {
                    bLoadmoreFailed = true;
                    if (lastCell != null) {
                        lastCell.refreshView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bLoadmoreFailed = false;
                                loadMore();
                                if (lastCell != null) {
                                    lastCell.loadContent(adapter.getData(loadmorePosition));
                                }
                            }
                        });
                    }
                } else {
                    bLoadmoreFailed = false;
                    if (dataList.isEmpty()) {
                        bLoadmoreEnded = true;
                    } else {
                        itemDataList.addAll(dataList);
                    }
                }
                if (lastCell != null) {
                    lastCell.loadContent(adapter.getData(loadmorePosition));
                }
                pagerView.notifyDataSetChanged();
            }
        });

        addChild(adBanner);
    }

    abstract protected void loadMore();

    abstract protected void recordFlurry(String event);

}

package com.fa1000.law.ui;

import android.content.Context;

import com.bstoneinfo.lib.frame.BSFrame;
import com.fa1000.law.data.LawDataSource;

public class NewestFrame extends BSFrame {

    private final LawDataSource dataSource = new LawDataSource();

    public NewestFrame(Context context) {
        super(context);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        dataSource.loadMore();
    }

    @Override
    protected void onDestroy() {
        dataSource.cancel();
        super.onDestroy();
    }

}

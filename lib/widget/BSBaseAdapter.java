package com.bstoneinfo.lib.widget;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BSBaseAdapter extends BaseAdapter {

    protected final Context context;
    protected final ArrayList<?> dataList;
    protected final ArrayList<BSCell> cellList = new ArrayList<BSCell>();

    public abstract BSCell createCell();

    public BSBaseAdapter(Context context, ArrayList<?> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<?> getDataList() {
        return dataList;
    }

    public ArrayList<BSCell> getCellList() {
        return cellList;
    }

    public BSCell getCell(int position) {
        for (BSCell cell : cellList) {
            if (cell.position == position) {
                return cell;
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position >= 0 && position < dataList.size() ? dataList.get(position) : null;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BSCell cell;
        if (convertView == null) {
            cell = createCell();
            cellList.add(cell);
            convertView = cell.getRootView();
            convertView.setTag(cell);
        } else {
            cell = (BSCell) convertView.getTag();
        }
        cell.position = position;
        cell.loadContent(getItem(position));
        return convertView;
    }

}

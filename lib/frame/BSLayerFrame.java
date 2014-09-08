package com.bstoneinfo.lib.frame;

import java.util.ArrayList;

public class BSLayerFrame extends BSFrame {

    public BSLayerFrame(BSFrame baseFrame) {
        super(baseFrame.getContext());
        getChildFrames().add(baseFrame);
        getRootView().addView(baseFrame.getRootView());
    }

    private BSFrame getBaseFrame() {
        return getChildFrames().get(0);
    }

    private BSFrame getTopFrame() {
        return getChildFrames().get(getChildFrames().size() - 1);
    }

    @Override
    protected ArrayList<BSFrame> getActiveChildFrames() {
        ArrayList<BSFrame> frames = new ArrayList<BSFrame>();
        frames.add(getTopFrame());
        return frames;
    }

    @Override
    public void load() {
        super.load();
        getBaseFrame().load();
    }

    @Override
    public boolean back() {
        if (getChildFrames().size() > 1) {
            getTopFrame().dismiss();
            return true;
        }
        return false;
    }

}

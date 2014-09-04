package com.bstoneinfo.lib.frame;

public class BSLayerFrame extends BSFrame {

    public BSLayerFrame(BSFrame baseFrame) {
        super(baseFrame.getContext());
        getChildFrames().add(baseFrame);
    }

    private BSFrame getTopFrame() {
        return getChildFrames().get(getChildFrames().size() - 1);
    }

    @Override
    protected void showChildFrames() {
        getTopFrame().show();
    }

    @Override
    protected void hideChildFrames() {
        getTopFrame().hide();
    }

}

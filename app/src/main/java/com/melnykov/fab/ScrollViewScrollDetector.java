package com.melnykov.fab;

abstract class ScrollViewScrollDetector implements ObservableScrollView.OnScrollChangedListener {
    private int mLastScrollY;
    private int mScrollThreshold;

    abstract void onScrollUp();

    abstract void onScrollDown();

    @Override
    public void onScrollChanged(int t) {
        boolean isSignificantDelta = Math.abs(t - mLastScrollY) > mScrollThreshold;
        if (isSignificantDelta) {
            if (t > mLastScrollY) {
                onScrollUp();
            } else {
                onScrollDown();
            }
        }
        mLastScrollY = t;
    }

    public void setScrollThreshold(int scrollThreshold) {
        mScrollThreshold = scrollThreshold;
    }
}
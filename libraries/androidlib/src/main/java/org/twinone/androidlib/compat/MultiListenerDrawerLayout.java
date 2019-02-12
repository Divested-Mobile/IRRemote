package org.twinone.androidlib.compat;

import android.content.Context;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by twinone on 1/2/15.
 */
public class MultiListenerDrawerLayout extends DrawerLayout implements DrawerLayout.DrawerListener {


    public MultiListenerDrawerLayout(Context context) {
        super(context);
        init();
    }

    public MultiListenerDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiListenerDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        super.setDrawerListener(this);

    }

    private List<DrawerListener> mListeners = new ArrayList<>();

    @Override
    public void setDrawerListener(DrawerListener listener) {
        addDrawerListener(listener);
    }

    public void addDrawerListener(DrawerListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        for (DrawerListener l : mListeners) l.onDrawerSlide(drawerView, slideOffset);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        for (DrawerListener l : mListeners) l.onDrawerOpened(drawerView);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        for (DrawerListener l : mListeners) l.onDrawerClosed(drawerView);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        for (DrawerListener l : mListeners) l.onDrawerStateChanged(newState);
    }
}

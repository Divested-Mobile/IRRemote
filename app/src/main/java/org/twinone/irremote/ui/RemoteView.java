package org.twinone.irremote.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import org.twinone.irremote.components.Remote;

import java.util.ArrayList;
import java.util.List;

public class RemoteView extends RelativeLayout {

    private static final String TAG = RemoteView.class.getSimpleName();

    private Remote mRemote;
    private List<ButtonView> mButtons = new ArrayList<>();


    public RemoteView(Context context, Remote remote) {
        super(context);
        init();
        setRemote(remote);
    }

    public RemoteView(Context context) {
        super(context);
        init();
    }

    public RemoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RemoteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {

    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        /**
         * We're ignoring setPadding since the MaterialDialogs implementation sets 24dp padding on us for some reason
         * And we don't care about padding anyway
         */
//        super.setPadding(left, top, right, bottom);
//        Log.d("NewRemoteView", "Setting padding " + left + " "  + top + " " + right  + " " + bottom );
//        try {
//            throw new Exception();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) params).gravity = Gravity.CENTER_HORIZONTAL;
            ((LinearLayout.LayoutParams) params).width = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else if (params instanceof ScrollView.LayoutParams) {
            ((ScrollView.LayoutParams) params).gravity = Gravity.CENTER_HORIZONTAL;
            ((ScrollView.LayoutParams) params).width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        super.setLayoutParams(params);
    }


    protected final void setupButtons() {
        removeAllViews();

        mButtons = new ArrayList<>(mRemote.buttons.size());
        for (org.twinone.irremote.components.Button b : mRemote.buttons) {
            ButtonView bv = new ButtonView(getContext());
            bv.setButton(b);

            mButtons.add(bv);
            addView(bv);
            // bv.setX(b.x);
            // bv.setY(b.y);
            bv.getLayoutParams().width = (int) b.w;
            bv.getLayoutParams().height = (int) b.h;
            bv.requestLayout();
            setupButton(bv);
        }
    }


    /**
     * Override this method to provide any custom behavior to ButtonViews
     *
     * @param bv
     */
    protected void setupButton(ButtonView bv) {

    }

    public final Remote getRemote() {
        return mRemote;
    }

    protected List<ButtonView> getButtons() {
        return mButtons;
    }

    public void setRemote(Remote remote) {
        mRemote = remote;
        setupButtons();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.makeMeasureSpec(mRemote.details.w, MeasureSpec.EXACTLY);
        int h = MeasureSpec.makeMeasureSpec(mRemote.details.h, MeasureSpec.EXACTLY);
        setMeasuredDimension(w, h);
    }

    /**
     * Returns the ButtonView for a specific UID
     *
     * @param id
     * @return
     */
    public ButtonView findButtonViewById(int uid) {
        return (ButtonView) findViewById(uid);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d(TAG, "SaveInstanceState, Remote.name: " + mRemote.name);
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putSerializable("remote", mRemote);
        return bundle;

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.mRemote = (Remote) bundle.getSerializable("remote");
            super.onRestoreInstanceState(((Bundle) state).getParcelable("instanceState"));
            Log.d(TAG, "RestoreInstanceState, Remote.name: " + mRemote.name);
        }
    }
}

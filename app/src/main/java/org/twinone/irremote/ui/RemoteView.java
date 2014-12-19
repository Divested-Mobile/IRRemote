package org.twinone.irremote.ui;

import org.twinone.irremote.components.Remote;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RemoteView extends RelativeLayout {

	private Remote mRemote;

	public void setRemote(Remote remote) {
		mRemote = remote;
	}

	public Remote getRemote() {
		return mRemote;
	}

	public RemoteView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, mRemote.details.h);
	}

	public RemoteView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RemoteView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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

}

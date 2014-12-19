package org.twinone.irremote.ir.io;

import com.htc.circontrol.CIRControl;
import com.htc.htcircontrol.HtcIrData;

import android.os.Handler;
import android.os.Message;

public class HTCReceiverHandler extends Handler {
	private final OnMessageListener mListener;

	public HTCReceiverHandler(OnMessageListener listener) {
		mListener = listener;
	}

	public interface OnMessageListener {
		public void onReceiveComplete(HtcIrData code);

		public void onReceiveCancel();

		public void onReceiveStart();

		public void onError(int errorCode);

		public void onTimeout();
	}

	@Override
	public void handleMessage(Message msg) {
		int what = msg.what;
		int error = msg.arg1;

		if (what == CIRControl.MSG_RET_CANCEL) {
			mListener.onReceiveCancel();
		} else if (what == CIRControl.MSG_RET_STARTED) {
			mListener.onReceiveStart();
		} else if (what == CIRControl.MSG_RET_LEARN_IR) {
			switch (error) {
			case CIRControl.ERR_NONE:
				HtcIrData code = (HtcIrData) msg.getData().getSerializable(
						CIRControl.KEY_CMD_RESULT);
				mListener.onReceiveComplete(code);
				break;
			case CIRControl.ERR_CANCEL:
				mListener.onReceiveCancel();
				break;
			case CIRControl.ERR_LEARNING_TIMEOUT:
				mListener.onTimeout();
				break;
			default:
				mListener.onError(error);
			}
		}

	}
}

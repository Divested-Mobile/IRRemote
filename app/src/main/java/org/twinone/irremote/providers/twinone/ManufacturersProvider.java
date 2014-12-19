package org.twinone.irremote.providers.twinone;

import org.twinone.androidlib.net.HttpJson;
import org.twinone.androidlib.net.HttpJson.ResponseListener;
import org.twinone.irremote.Constants;
import org.twinone.irremote.providers.twinone.ManufacturersProvider.ManufacturersReq;
import org.twinone.irremote.providers.twinone.ManufacturersProvider.ManufacturersResp;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

public class ManufacturersProvider implements
		ResponseListener<ManufacturersReq, ManufacturersResp> {

	public static class ManufacturersReq {
		@SerializedName("device_type")
		public String deviceType;
	}

	public static class ManufacturersResp {
		public String[] manufacturers;
	}

	private OnManufacturersReceivedListener mListener;

	@Override
	public void onServerResponse(int statusCode, ManufacturersReq req,
			ManufacturersResp resp) {
		mListener.onManufacturersReceived(resp.manufacturers);
	};

	public void getManufacturers(Context context, String deviceType,
			OnManufacturersReceivedListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null");
		mListener = listener;
		HttpJson<ManufacturersReq, ManufacturersResp> hj = new HttpJson<>(
				ManufacturersResp.class);
		hj.setUrl(Constants.URL_MANUFACTURERS);
		ManufacturersReq req = new ManufacturersReq();
		req.deviceType = deviceType;

		hj.execute(req, this);
	}

	public interface OnManufacturersReceivedListener {
		public void onManufacturersReceived(String[] manufacturers);
	}

}

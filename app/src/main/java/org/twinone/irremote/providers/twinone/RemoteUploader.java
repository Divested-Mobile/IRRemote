package org.twinone.irremote.providers.twinone;

import org.twinone.androidlib.net.HttpJson;
import org.twinone.irremote.Constants;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.twinone.RemoteUploader.UploadReq;
import org.twinone.irremote.providers.twinone.RemoteUploader.UploadResp;

import com.google.gson.annotations.SerializedName;

import android.content.Context;

public class RemoteUploader implements
		HttpJson.ResponseListener<UploadReq, UploadResp> {

	private Context mContext;

	public RemoteUploader(Context c) {
		mContext = c;
	}

	public interface UploadListener {
		public void onUpload(int statusCode, String message);
	}

	public static class UploadReq {
		@SerializedName("userinfo")
		public UserInfo userInfo;
		@SerializedName("deviceinfo")
		public DeviceInfo deviceInfo;
		public Remote remote;
	}

	public static class UploadResp {
		int status;
	}

	public void upload(Remote remote) {
		HttpJson<UploadReq, UploadResp> hj = new HttpJson<>(UploadResp.class);
		hj.setUrl(Constants.URL_UPLOAD);
		UploadReq req = new UploadReq();
		req.remote = remote;
		req.userInfo = UserInfo.load(mContext);
		req.userInfo.mask(UserInfo.MASK_ID | UserInfo.MASK_ACCESS_TOKEN);
		req.deviceInfo = new DeviceInfo(mContext);
		hj.execute(req, this);
	}

	@Override
	public void onServerResponse(int statusCode, UploadReq req, UploadResp resp) {

	}

}

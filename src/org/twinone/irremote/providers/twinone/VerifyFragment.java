package org.twinone.irremote.providers.twinone;

import org.twinone.androidlib.net.HttpJson;
import org.twinone.androidlib.net.HttpJson.Listener;
import org.twinone.irremote.Constants;
import org.twinone.irremote.R;
import org.twinone.irremote.providers.twinone.VerifyFragment.VerifyReq;
import org.twinone.irremote.providers.twinone.VerifyFragment.VerifyResp;
import org.twinone.irremote.ui.SettingsActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class VerifyFragment extends Fragment implements OnClickListener,
		Listener<VerifyReq, VerifyResp> {

	private TextView mMessage;
	private Button mTryAgain;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.title_verify_account);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_verify, null);
		mMessage = (TextView) root.findViewById(R.id.vfy_message);
		mTryAgain = (Button) root.findViewById(R.id.vfy_try_again);
		mTryAgain.setOnClickListener(this);
		verifyAccount();
		return root;
	}

	public static class VerifyReq {
		public String username;
		public String token;
	}

	public static class VerifyResp {
		public int status;
		public String access_token;
	}

	private String getToken() {
		Uri uri = getActivity().getIntent().getData();
		if (uri == null)
			return null;
		return uri.getQueryParameter("token");
	}

	private void verifyAccount() {

		if (RegisterActivity.isRegistered(getActivity())) {
			int color = getResources().getColor(
					R.color.abc_primary_text_material_dark);
			mMessage.setTextColor(color);
			String username = RegisterActivity.getUsername(getActivity());
			mMessage.setText(getString(R.string.vfy_already_done, username));
			mTryAgain.setVisibility(View.GONE);
			return;
		}

		VerifyReq req = new VerifyReq();
		SharedPreferences sp = SettingsActivity.getPreferences(getActivity());
		req.username = sp.getString(RegisterActivity.PREF_KEY_USERNAME, null);
		req.token = getToken();

		if (req.username == null) {
			showError(R.string.vfy_err_noreg, false);
			return;
		}

		HttpJson<VerifyReq, VerifyResp> hj = new HttpJson<>(VerifyResp.class);
		hj.setUrl(Constants.URL_VERIFY);
		hj.execute(req, this);
	}

	@Override
	public void onServerResponse(int statusCode, VerifyReq req, VerifyResp resp) {
		if (resp.status == 0) {
			showOkMessage();
			Editor ed = getActivity().getSharedPreferences("default",
					Context.MODE_PRIVATE).edit();
			ed.putString(RegisterActivity.PREF_KEY_ACCESS_TOKEN,
					resp.access_token);
			ed.putBoolean(RegisterActivity.PREF_KEY_REGISTERED, true);
			ed.apply();
		} else {
			showError(R.string.vfy_failed, false);
		}
	}

	@Override
	public void onServerException(Exception e) {
		Log.w("", "Exception", e);
		showError(R.string.network_error, true);
	}

	private void showOkMessage() {
		int color = getResources().getColor(
				R.color.abc_primary_text_material_dark);
		mMessage.setTextColor(color);
		mMessage.setText(R.string.vfy_ok);
		mTryAgain.setVisibility(View.GONE);
	}

	private void showError(int resId, boolean tryAgainVisible) {
		int error = getResources().getColor(R.color.material_red_300);
		mMessage.setTextColor(error);
		mMessage.setText(resId);
		mTryAgain.setVisibility(tryAgainVisible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.vfy_try_again) {
			verifyAccount();
		}
	}

}

package org.twinone.irremote.providers.twinone;

import org.twinone.irremote.R;
import org.twinone.irremote.providers.twinone.ManufacturersProvider.OnManufacturersReceivedListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

public class UploadFragment extends Fragment implements
		OnManufacturersReceivedListener {

	public UploadFragment() {
	}

	public UploadActivity getUploadActivity() {
		return (UploadActivity) getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	private TextView mName;
	private Spinner mDeviceType;
	private AutoCompleteTextView mManufacturer;
	private AutoCompleteTextView mModel;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_upload, null);
		mName = (TextView) root.findViewById(R.id.upl_name);
		mDeviceType = (Spinner) root.findViewById(R.id.upl_device_type);
		mManufacturer = (AutoCompleteTextView) root
				.findViewById(R.id.upl_manufacturer);
		mModel = (AutoCompleteTextView) root.findViewById(R.id.upl_model);

		setupDeviceTypes();
		return root;
	}

	private void setupDeviceTypes() {
		String[] deviceTypes = getResources().getStringArray(
				R.array.device_types);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_dropdown_item_1line, deviceTypes);
		mDeviceType.setAdapter(adapter);

	}

	private void setupManufacturers(String deviceType) {
		new ManufacturersProvider().getManufacturers(getActivity(), deviceType,
				this);
	}

	@Override
	public void onManufacturersReceived(String[] manufacturers) {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_dropdown_item_1line, manufacturers);
		mManufacturer.setAdapter(adapter);
	}

	private static class ManufResp {
		public String[] manufacturers;
	}
}
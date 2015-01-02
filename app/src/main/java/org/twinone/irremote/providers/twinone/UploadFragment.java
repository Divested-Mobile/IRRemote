package org.twinone.irremote.providers.twinone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.twinone.androidlib.net.HttpJson;
import org.twinone.irremote.Constants;
import org.twinone.irremote.R;
import org.twinone.irremote.account.UserInfo;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.twinone.TwinoneProviderFragment.OnManufacturersReceivedListener;
import org.twinone.irremote.util.BaseTextWatcher;

public class UploadFragment extends Fragment implements
        OnManufacturersReceivedListener, View.OnClickListener ,
        HttpJson.ResponseListener<UploadFragment.UploadReq, UploadFragment.UploadResp>  {

    private TextView mMessage;
    private Spinner mDeviceType;
    private EditText mDeviceTypeText;
    private AutoCompleteTextView mManufacturer;
    private EditText mModel;
    private Button mUpload;

    public UploadFragment() {
    }

    public UploadActivity getUploadActivity() {
        return (UploadActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_upload, null);
        mMessage = (TextView) root.findViewById(R.id.upl_message);
        mDeviceType = (Spinner) root.findViewById(R.id.upl_device_type);
        mDeviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resetErrors();
                mDeviceTypeText.setVisibility(isOtherDeviceTypeSelected() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mDeviceTypeText = (EditText) root.findViewById(R.id.upl_device_type_text);
        mManufacturer = (AutoCompleteTextView) root
                .findViewById(R.id.upl_manufacturer);
        mModel = (EditText) root.findViewById(R.id.upl_model);
        mUpload = (Button) root.findViewById(R.id.upl_submit);
        mUpload.setOnClickListener(this);

        BaseTextWatcher tw = new BaseTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resetErrors();
            }
        };
        mDeviceTypeText.addTextChangedListener(tw);
        mManufacturer.addTextChangedListener(tw);
        mModel.addTextChangedListener(tw);

        setupDeviceTypes();
        setupManufacturers();
        return root;
    }

    private void setupDeviceTypes() {
        String[] deviceTypes = getResources().getStringArray(
                R.array.device_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, deviceTypes);
        mDeviceType.setAdapter(adapter);

    }

    private void setupManufacturers() {
        TwinoneProviderFragment.getManufacturers(getActivity(), this);
    }

    @Override
    public void onManufacturersReceived(String[] manufacturers) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, manufacturers);
        mManufacturer.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upl_submit:
                if (!verifyAllFields()) {
                    return;
                }
                upload();
                break;
        }
    }

    private void resetErrors() {
        if (!mHasErrors)
            return;
        int def = getResources().getColor(
                R.color.abc_primary_text_material_dark);
        mMessage.setText(R.string.upl_header);

        mMessage.setTextColor(def);
        mManufacturer.setTextColor(def);
        mModel.setTextColor(def);
        mDeviceTypeText.setTextColor(def);
        mHasErrors = false;
    }

    private void addError(int resId, TextView... vs) {
        addError(getString(resId), vs);
    }

    private boolean mHasErrors;

    private void addError(String err, TextView... vs) {
        int error = getResources().getColor(R.color.material_red_300);
        if (!mHasErrors)
            mMessage.setText(err);
        else
            mMessage.append("\n\n" + err);

        mMessage.setTextColor(error);
        mHasErrors = true;

        if (vs != null) {
            for (TextView v : vs)
                v.setTextColor(error);
        }
    }

    private boolean verifyAllFields() {
        if (isOtherDeviceTypeSelected() && mDeviceTypeText.getText().toString().isEmpty()) {
            addError(R.string.err_all_fields, mDeviceTypeText);
            return false;
        }
        if (mManufacturer.getText().toString().isEmpty()) {
            addError(R.string.err_all_fields, mManufacturer);
            return false;
        }
        return true;
    }

    private boolean isOtherDeviceTypeSelected() {
        return (mDeviceType.getSelectedItemPosition() == mDeviceType.getCount() - 1);
    }

    private String getTypeString() {
        if (isOtherDeviceTypeSelected()) {
            return mDeviceTypeText.getText().toString();
        }
        return null;
    }


    public void upload() {
        Remote r = Remote.load(getActivity(), getUploadActivity().getRemoteName());
        if (isOtherDeviceTypeSelected()) {
            r.details.type = -1;
            r.details.typeString = getTypeString();
        } else {
            r.details.type = mDeviceType.getSelectedItemPosition();
            r.details.typeString = null;
        }
        r.details.model = mModel.getText().toString();
        r.details.manufacturer = mManufacturer.getText().toString();



        HttpJson<UploadReq, UploadResp> hj = new HttpJson<>(UploadResp.class);
        hj.setUrl(Constants.URL_UPLOAD);
        UploadReq req = new UploadReq();
        req.remote = r;
        req.userinfo = UserInfo.getAuthInfo(getActivity());
        req.deviceinfo = new DeviceInfo(getActivity());
        hj.execute(req, this);
    }

    @Override
    public void onServerResponse(UploadReq req, UploadResp resp) {
        if (resp.status == 0) {
            Toast.makeText(getActivity(), R.string.remote_uploaded, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.upload_err, resp.status), Toast.LENGTH_SHORT).show();
        }
    }


    public static class UploadReq {
        public UserInfo userinfo;
        public DeviceInfo deviceinfo;
        public Remote remote;
    }

    public static class UploadResp {
        int status;
    }

}

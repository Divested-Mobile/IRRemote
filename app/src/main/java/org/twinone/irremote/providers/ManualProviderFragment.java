package org.twinone.irremote.providers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.twinone.irremote.R;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalFactory;

public class ManualProviderFragment extends ProviderFragment
    implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private RadioGroup mRadioGroup;
    private TextView mInfoTextView;
    private EditText mCodeEditText;
    private Button mCancelButton;
    private Button mSaveButton;

    private void saveIrCode() {
        final String inputCode = mCodeEditText.getText().toString();
        int format = -1;

        final int checkedId = mRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.provider_manual_freq_pattern) {
            format = Signal.FORMAT_GLOBALCACHE;
        } else if (checkedId == R.id.provider_manual_pronto) {
            format = Signal.FORMAT_PRONTO;
        }

        Signal parsedCode;
        try {
            parsedCode = SignalFactory.parse(format, inputCode);
        } catch(Exception e) {
            Toast.makeText(getActivity(),
                R.string.provider_manual_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        if (parsedCode.getFrequency() > 100000) {
            Toast.makeText(getActivity(),
                R.string.provider_manual_freq_sus, Toast.LENGTH_SHORT).show();
        }

        org.twinone.irremote.components.Button button = new org.twinone.irremote.components.Button(
            getString(R.string.provider_manual_new_button));
        button.code = SignalFactory.toPronto(parsedCode);
        getProvider().requestSaveButton(button);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.provider_manual_pronto) {
            this.mInfoTextView.setText(R.string.provider_manual_pronto_info);
        } else {
            this.mInfoTextView.setText(R.string.provider_manual_freq_info);
        }
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.provider_manual_cancel) {
            getProvider().popAllFragments();
        } else if (id == R.id.provider_manual_save) {
            saveIrCode();
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        getProvider().setSubtitle(R.string.provider_manual);
        View inflate = layoutInflater.inflate(R.layout.fragment_provider_manual, null);
        mRadioGroup = inflate.findViewById(R.id.provider_manual_radio_group);
        mInfoTextView = inflate.findViewById(R.id.provider_manual_info);
        mCodeEditText = inflate.findViewById(R.id.provider_manual_code);
        mCancelButton = inflate.findViewById(R.id.provider_manual_cancel);
        mSaveButton = inflate.findViewById(R.id.provider_manual_save);
        mSaveButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(this);
        return inflate;
    }
}

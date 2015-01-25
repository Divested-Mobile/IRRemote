package org.twinone.irremote.account;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.twinone.androidlib.compat.ToolbarActivity;
import org.twinone.androidlib.net.HttpJson;
import org.twinone.androidlib.net.HttpJson.ExceptionListener;
import org.twinone.androidlib.net.HttpJson.ResponseListener;
import org.twinone.irremote.Constants;
import org.twinone.irremote.R;
import org.twinone.irremote.account.LoginFragment.LoginReq;
import org.twinone.irremote.account.LoginFragment.LoginResp;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.util.BaseTextWatcher;


public class LoginFragment extends BaseLoginRegisterFragment implements OnClickListener,
        ResponseListener<LoginReq, LoginResp>,
        ExceptionListener<LoginReq, LoginResp> {

    private static final int GET_ACCOUNT = 0xCACA;

    private static final int STATUS_OK = 0;
    private static final int STATUS_INVALID_LOGIN = 1;

    private LinearLayout mForm;
    private TextView mMessage;
    private EditText mUsername;
    private EditText mPwd;
    private boolean mHasErrors = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ActionBar getSupportActionBar() {
        return ((ToolbarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setType(LoginRegisterActivity.FRAGMENT_INDEX_LOGIN);

        View root = inflater.inflate(R.layout.fragment_login, null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().setTitle(R.string.title_create_account);

        mForm = (LinearLayout) root.findViewById(R.id.form);
        mMessage = (TextView) root.findViewById(R.id.login_message);
        mUsername = (EditText) root.findViewById(R.id.form_username);
        mPwd = (EditText) root.findViewById(R.id.form_pwd);

        setupLayout(getUserInfo().isLoggedIn());
        Button mSubmit = (Button) root.findViewById(R.id.form_submit);
        mSubmit.setOnClickListener(this);

        BaseTextWatcher tw = new BaseTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resetErrors();
            }
        };
        return root;
    }

    private void setupLayout(boolean isLoggedIn) {
//        mForm.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
//        mLogout.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
//        setupMessageLayout();
    }

    private void setupMessageLayout() {
        mMessage.setVisibility(getUserInfo().isLoggedIn() || mHasErrors ? View.VISIBLE : View.GONE);
    }

    private void startAccountChooser() {
        Intent intent = AccountManager.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, true, null, null, null, null);
        startActivityForResult(intent, GET_ACCOUNT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.form_submit:
                checkFieldsAndSubmit();
                break;
            case R.id.logout:
                setupLayout(false);
                getUserInfo().logout().save(getActivity());
                getLoginRegisterActivity().updateAllFragments();
                break;
        }
    }

    private void addError(int resId, TextView... vs) {
        addError(getString(resId), vs);
    }

    private void addError(String err, TextView... vs) {
        int error = getResources().getColor(R.color.material_red_300);
        Log.d("", "Error: " + err + " , mHasErrors:" + mHasErrors);
        if (!mHasErrors)
            mMessage.setText(err);
        else
            mMessage.append("\n\n" + err);

        mMessage.setVisibility(View.VISIBLE);
        mMessage.setTextColor(error);
        mHasErrors = true;
        setupMessageLayout();
        if (vs != null) {
            for (TextView v : vs)
                v.setTextColor(error);
        }
    }

    private boolean verifyAllFields(TextView... tvs) {
        for (TextView tv : tvs) {
            if (tv.getText().toString().isEmpty()) {
                addError(R.string.err_all_fields, (TextView[]) null);
                return false;
            }
        }
        return true;
    }

    private void checkFieldsAndSubmit() {
        resetErrors();
        if (!verifyAllFields(mUsername, mPwd))
            return;

        if (!mHasErrors)
            submit();
    }

    private void submit() {
        if (!isAdded()) {
            Log.d("LoginFragment", "!isAdded()");
            return;
        }
        LoginReq req = new LoginReq();
        req.username = mUsername.getText().toString();
        req.password = mPwd.getText().toString();

        HttpJson<LoginReq, LoginResp> hj = new HttpJson<>(
                LoginResp.class, Constants.URL_LOGIN);
        hj.execute(req, this);
    }

    @Override
    public void onServerResponse(LoginReq req,
                                 LoginResp resp) {
        if (resp.status == STATUS_OK) {
            getUserInfo().username = req.username;
            getUserInfo().access_token = resp.access_token;
            getUserInfo().save(getActivity());
//            Toast.makeText(getActivity(), R.string.login_done, Toast.LENGTH_LONG).show();
//            setupLayout(true);
            startAccountActivity();
//            getLoginRegisterActivity().updateAllFragments();
        } else {
            if ((STATUS_INVALID_LOGIN & resp.status) != 0)
                addError(R.string.login_err_invalid, (TextView[]) null);
        }
    }

    private void startAccountActivity() {
        Intent i = new Intent(getActivity(), AccountActivity.class);
        AnimHelper.startActivity(getActivity(), i);
        getActivity().finish();
    }

    @Override
    public void onServerException(Exception e) {
//        if (mDialog != null && mDialog.isShowing())
//            mDialog.dismiss();
        addError(R.string.network_error, (TextView[]) null);
    }


    private void resetErrors() {
        if (!mHasErrors)
            return;
        int def = getResources().getColor(
                R.color.abc_primary_text_material_dark);

        mMessage.setVisibility(View.GONE);

        mMessage.setTextColor(def);
        mUsername.setTextColor(def);
        mPwd.setTextColor(def);
        mHasErrors = false;
    }


    @Override
    public void onPageSelected(int position) {
        if (position == LoginRegisterActivity.FRAGMENT_INDEX_LOGIN) {
            onUpdate();
        }
    }

    @Override
    public void onUpdate() {
        setupLayout(getUserInfo().isLoggedIn());
    }

    public static class LoginReq {
        String username;
        String password;
    }

    public static class LoginResp {
        int status;
        String access_token;
    }

}

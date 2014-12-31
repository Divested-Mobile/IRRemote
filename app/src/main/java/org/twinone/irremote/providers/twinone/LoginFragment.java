package org.twinone.irremote.providers.twinone;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.twinone.androidlib.net.HttpJson;
import org.twinone.androidlib.net.HttpJson.ExceptionListener;
import org.twinone.androidlib.net.HttpJson.ResponseListener;
import org.twinone.irremote.Constants;
import org.twinone.irremote.R;
import org.twinone.irremote.compat.ToolbarActivity;
import org.twinone.irremote.providers.twinone.LoginFragment.LoginReq;
import org.twinone.irremote.providers.twinone.LoginFragment.LoginResp;
import org.twinone.irremote.util.BaseTextWatcher;


public class LoginFragment extends BaseLoginRegisterFragment implements OnClickListener,
        ResponseListener<LoginReq, LoginResp>,
        ExceptionListener<LoginReq, LoginResp> {

    private static final int GET_ACCOUNT = 0xCACA;

    private static final int STATUS_OK = 0;
    private static final int STATUS_INVALID_LOGIN = 1;

    private LinearLayout mForm;
    private Button mLogout;
    private TextView mMessage;
    private EditText mUsername;
    private EditText mPwd;
//    private AlertDialog mDialog;
    private boolean mHasErrors = false;
    private boolean mLoggedIn;


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

        mForm = (LinearLayout) root.findViewById(R.id.login_form);
        mMessage = (TextView) root.findViewById(R.id.login_message);
        mLogout = (Button) root.findViewById(R.id.logout);
        mLogout.setOnClickListener(this);
        mUsername = (EditText) root.findViewById(R.id.login_username);
        mPwd = (EditText) root.findViewById(R.id.login_pwd);

        setupLayout(getUserInfo().isLoggedIn());
        Button mSubmit = (Button) root.findViewById(R.id.login_submit);
        mSubmit.setOnClickListener(this);

        BaseTextWatcher tw = new BaseTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resetErrors();
            }
        };


        if (getUserInfo().isLoggedIn()) {
            mMessage.setText(getString(R.string.login_logged_in, getUserInfo().username));
            return root;
        }


        return root;
    }

    private void setupLayout(boolean isLoggedIn) {
        mLoggedIn = isLoggedIn;
        mForm.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        mMessage.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        if (isLoggedIn) {
            mMessage.setText(getString(R.string.login_logged_in, getUserInfo().username));
            hideInputMethod();
        }
        mLogout.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
    }

    private void startAccountChooser() {
        Log.i("RegisterActivity", "startAccountChooser()");
        Intent intent = AccountManager.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, true, null, null, null, null);
        startActivityForResult(intent, GET_ACCOUNT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_submit:
                checkFieldsAndSubmit();
                break;
            case R.id.logout:
                setupLayout(false);
                getUserInfo().logout().save(getActivity());
                getLoginRegisterActivity().updateAllFragments();
                break;
        }
    }

    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mUsername.getWindowToken(), 0);

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

        mMessage.setTextColor(error);
        mHasErrors = true;

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
//        if (mDialog != null && mDialog.isShowing())
//            return;
        if (!isAdded()) {
            Log.d("LoginFragment", "!isAdded()");
            return;
        }
//        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
//        ab.setTitle(R.string.loading);
//        ab.setMessage("");
//        ab.setCancelable(false);
//        ab.setPositiveButton(android.R.string.ok, null);
//        mDialog = ab.show();

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
            Toast.makeText(getActivity(), R.string.login_done, Toast.LENGTH_LONG).show();
            setupLayout(true);
            // finish(); ?
            getLoginRegisterActivity().updateAllFragments();
        } else {
            if ((STATUS_INVALID_LOGIN & resp.status) != 0)
                addError(R.string.login_err_invalid, (TextView[]) null);
        }
//        if (mDialog.isShowing())
//            mDialog.dismiss();
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

        mMessage.setText(null);

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

package org.twinone.irremote.account;

import android.os.Bundle;
import android.widget.TextView;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.ToolbarActivity;
import org.twinone.irremote.components.AnimHelper;

public class AccountActivity extends ToolbarActivity {

    private UserInfo mUserInfo;
    private TextView mUsername;
    private TextView mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = UserInfo.load(this);

        setContentView(R.layout.activity_account);

        mUsername = (TextView)findViewById(R.id.form_username);
        mEmail = (TextView)findViewById(R.id.form_email);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void finish() {
        super.finish();
        AnimHelper.onFinish(this);
    }

}

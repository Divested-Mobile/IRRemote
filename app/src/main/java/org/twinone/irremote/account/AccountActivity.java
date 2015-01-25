package org.twinone.irremote.account;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.twinone.androidlib.compat.ToolbarActivity;
import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;

public class AccountActivity extends ToolbarActivity {

    private UserInfo mUserInfo;
    private TextView mUsername;
    private TextView mEmail;

    private Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = UserInfo.load(this);

        setContentView(R.layout.activity_account);

        mUsername = (TextView)findViewById(R.id.form_username);
        mEmail = (TextView)findViewById(R.id.form_email);
        mLogout = (Button)findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserInfo.logout().save(AccountActivity.this);
                finish();
            }
        });

        mUsername.setText(mUserInfo.username);
        mEmail.setText(mUserInfo.email);

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

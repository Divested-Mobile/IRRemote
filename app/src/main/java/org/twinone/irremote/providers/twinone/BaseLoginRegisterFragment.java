package org.twinone.irremote.providers.twinone;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public class BaseLoginRegisterFragment extends Fragment implements LoginRegisterActivity.OnUpdateListener {



    protected UserInfo getUserInfo() {
        return getLoginRegisterActivity().getUserInfo();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoginRegisterActivity().addOnPageSelectedListener(this);
    }

    private int mType;

    protected void setType(int type) {
        mType = type;
    }

    @Override
    public void onUpdate() {

    }

    protected LoginRegisterActivity getLoginRegisterActivity() {
        return (LoginRegisterActivity) getActivity();
    }

    @Override
    public void onPageSelected(int position) {
        if (position == mType) {
            onUpdate();
        }
    }
}

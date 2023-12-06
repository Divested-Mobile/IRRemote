package org.twinone.irremote.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.twinone.androidlib.NavigationFragment;
import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.io.Receiver;
import org.twinone.irremote.providers.ProviderActivity;

import java.util.ArrayList;

public class ProviderNavFragment extends NavigationFragment implements
        OnItemClickListener {

    private ListView mListView;
    private Integer[] mIds;

    public ProviderNavFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(getActivity() instanceof ProviderActivity)) {
            throw new ClassCastException(
                    "ProviderNavFragment can only be attached to instances of ProviderActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout root = (RelativeLayout) inflater.inflate(
                R.layout.fragment_nav_provider, container, false);

        mListView = (ListView) root.findViewById(R.id.providers_listview);
        setupListView();
        mListView.setOnItemClickListener(this);
        return root;
    }

    private void setupListView() {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();

        list.add(getString(R.string.provider_common));
        ids.add(ProviderActivity.PROVIDER_COMMON);

        if (!Remote.getNames(getActivity()).isEmpty()) {
            list.add(getString(R.string.provider_local));
            ids.add(ProviderActivity.PROVIDER_LOCAL);

            list.add(getString(R.string.provider_manual));
            ids.add(ProviderActivity.PROVIDER_MANUAL);
        }

        if (Receiver.isAvailable(getActivity())) {
            if (getProvider().getAction().equals(ProviderActivity.ACTION_SAVE_REMOTE)) {
                list.add(getString(R.string.provider_learn_remote));
                ids.add(ProviderActivity.PROVIDER_LEARN);
            } else {
//                list.add(getString(R.string.provider_learn_button));
//                ids.add(ProviderActivity.PROVIDER_LEARN);
            }
        }

        String[] mStrings = list.toArray(new String[list.size()]);
        mIds = ids.toArray(new Integer[ids.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.provider_nav_item,
                mStrings);
        mListView.setAdapter(adapter);
    }

    private ProviderActivity getProvider() {
        return (ProviderActivity) getActivity();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        getProvider().switchTo(mIds[position]);
    }

}
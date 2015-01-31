package org.twinone.irremote.providers.globalcache;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.ListableAdapter;
import org.twinone.irremote.providers.ProviderFragment;

public class GCProviderFragment extends ProviderFragment implements
        DBConnector.OnDataReceivedListener, OnItemClickListener {

    public static final String ARG_URI_DATA = "com.twinone.irremote.arg.uri_data";


    private DBConnector mConnector;

    private boolean mCreated;

    private GlobalCacheProviderData mGCData;
    private Object[] mData;

    public GCProviderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_URI_DATA)) {
            mGCData = (GlobalCacheProviderData) getArguments()
                    .getSerializable(ARG_URI_DATA);
        } else {
            mGCData = new GlobalCacheProviderData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        setCurrentState(mGCData.targetType);
        setExitState(GlobalCacheProviderData.TYPE_MANUFACTURER);

        View rootView = inflater.inflate(R.layout.fragment_listable, container,
                false);
        mListView = (ListView) rootView.findViewById(R.id.lvElements);
        mListView.setOnItemClickListener(this);
//        mListView.setOnItemLongClickListener(this);

        mConnector = new DBConnector(getActivity(), this);

        if (mCreated) {
            mListView.setAdapter(mAdapter);
            mAdapter.restoreOriginalDataSet();
        } else if (mGCData.targetType != GlobalCacheProviderData.TYPE_IR_CODE) {
            if (mGCData.isAvailableInCache(getActivity())) {
                queryServer(mGCData, false);
            } else {
                queryServer(mGCData, true);
            }
        }

        String title = mGCData.getFullyQualifiedName(" > ");
        if (title == null) {
            title = getString(R.string.db_select_manufacturer);
        }
        getActivity().setTitle(title);

        mCreated = true;
        return rootView;
    }

    private void queryServer(GlobalCacheProviderData data, boolean showDialog) {
//        mListView.setAdapter(null);

        if (showDialog)
            showLoadingDialog();

        if (mConnector != null)
            mConnector.cancelQuery();

        mConnector = new DBConnector(getActivity(), this);
        mConnector.setOnDataReceivedListener(this);
        mConnector.query(data.clone());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_db, menu);
        setupSearchView(menu);
        mSearchView.setQueryHint(getSearchHint(mGCData));

//        boolean show = mGCData.targetType == GlobalCacheProviderData.TYPE_IR_CODE
//                && ACTION_SAVE_REMOTE.equals(getProvider().getAction());
//        menu.findItem(R.id.menu_db_save).setVisible(show);
    }

    private String getSearchHint(GlobalCacheProviderData data) {
        if (data.targetType == GlobalCacheProviderData.TYPE_MANUFACTURER) {
            return getString(R.string.db_search_hint_manufacturers);
        } else if (data.targetType == GlobalCacheProviderData.TYPE_IR_CODE) {
            return getString(R.string.db_search_hint_buttons);
        } else {
            return getString(R.string.db_search_hint_custom,
                    data.getFullyQualifiedName(" "));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_db_refresh:
                mGCData.removeFromCache(getActivity());
                queryServer(mGCData, true);
                return true;
            case R.id.menu_db_save:
                if (mData != null) {
                    Remote remote = buildRemote();
                    getProvider().requestSaveRemote(remote);
                }
                return true;
        }
        return false;
    }

    private Remote buildRemote() {
        String name = mGCData.manufacturer.Manufacturer + " "
                + mGCData.deviceType.DeviceType;
        Remote remote = IrCode.toRemote(getActivity(), name,
                (IrCode[]) mData);
        remote.details.manufacturer = mGCData.manufacturer.Manufacturer;
        remote.addFlags(Remote.FLAG_GC);
        return remote;
    }

    @Override
    public void onDataReceived(Object[] data) {
        if (!isAdded())
            return;

        hideLoadingDialog();
        mData = data;
        if (data == null) {
            Toast.makeText(getActivity(), R.string.err_gc_invalid_data,
                    Toast.LENGTH_LONG).show();
            mListView.setAdapter(null);
            return;
        }
        if (mData instanceof IrCode[]) {
            getProvider().requestSaveRemote(buildRemote());
        } else {
            mAdapter = new ListableAdapter(getActivity(), data);
            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onPause() {
        hideLoadingDialog();
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        GCBaseListable item = (GCBaseListable) mListView.getAdapter().getItem(
                position);

        if (item.getType() == GlobalCacheProviderData.TYPE_CODESET) {
            select(mGCData, item);
            queryServer(mGCData, true);
            return;
        } else if (item.getType() == GlobalCacheProviderData.TYPE_IR_CODE) {
            if (ACTION_SAVE_REMOTE.equals(getProvider().getAction())) {
                getProvider().transmit(((IrCode) item).getSignal());
            } else {
                Button b = IrCode.toButton(getActivity(), (IrCode) item);
                getProvider().requestSaveButton(b);
            }
        } else {
            GlobalCacheProviderData clone = mGCData.clone();
            select(clone, item);
            addGCProviderFragment(clone);
        }
    }

//    public boolean onItemLongClick(AdapterView<?> parent, View view,
//                                   int position, long id) {
//
//        // mListView.setItemChecked(position, true);
//        // if (mGCData.targetType == UriData.TYPE_IR_CODE) {
//        // // When the user long presses the button he can save it
//        // Button b = IrCode.toButton((IrCode) mData[position]);
//        // SaveButtonDialogFragment.showFor(getActivity(), b);
//        // }
//        // return true;
//        return false;
//    }

    private void select(GlobalCacheProviderData data, GCBaseListable listable) {
        data.targetType = GlobalCacheProviderData.TYPE_MANUFACTURER;
        if (listable != null) {
            if (listable.getType() == GlobalCacheProviderData.TYPE_MANUFACTURER) {
                data.manufacturer = (Manufacturer) listable;
                data.targetType = GlobalCacheProviderData.TYPE_DEVICE_TYPE;
            } else if (listable.getType() == GlobalCacheProviderData.TYPE_DEVICE_TYPE) {
                data.deviceType = (DeviceType) listable;
                data.targetType = GlobalCacheProviderData.TYPE_CODESET;
            } else if (listable.getType() == GlobalCacheProviderData.TYPE_CODESET) {
                data.codeset = (Codeset) listable;
                data.targetType = GlobalCacheProviderData.TYPE_IR_CODE;
            }
        }
    }

    @Override
    protected void onCancelLoading() {
        super.onCancelLoading();
        mConnector.cancelQuery();
    }


    public void addGCProviderFragment(GlobalCacheProviderData data) {
//        setExitState(GlobalCacheProviderData.TYPE_MANUFACTURER);
//        setCurrentState(data.targetType);
        GCProviderFragment frag = new GCProviderFragment();
        Bundle args = new Bundle();
        args.putSerializable(GCProviderFragment.ARG_URI_DATA, data);
        frag.setArguments(args);
        getProvider().addFragment(frag);
    }
}

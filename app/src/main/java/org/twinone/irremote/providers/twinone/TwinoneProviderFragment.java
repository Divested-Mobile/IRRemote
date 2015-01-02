package org.twinone.irremote.providers.twinone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.twinone.androidlib.net.HttpJson;
import org.twinone.irremote.Constants;
import org.twinone.irremote.R;
import org.twinone.irremote.account.UserInfo;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.BaseListable;
import org.twinone.irremote.providers.ListableAdapter;
import org.twinone.irremote.providers.ProviderFragment;

import java.io.Serializable;

public class TwinoneProviderFragment extends ProviderFragment implements ListView.OnItemClickListener, HttpJson.ResponseListener<TwinoneProviderFragment.TwinoneReq, TwinoneProviderFragment.TwinoneResp> {

    private static final String ARG_DATA = "org.twinone.irremote.arg.data";

    private TwinoneReq mRequest;
    private ListView mListView;

    private static final String URL_MANUFACTURERS = "https://www.twinone.org/apps/irremote/api/manufacturers.php";
    private static final String URL_DEVICE_TYPES = "https://www.twinone.org/apps/irremote/api/devicetypes.php";
    private static final String URL_DEVICES = "https://www.twinone.org/apps/irremote/api/devices.php";
    private static final String URL_IR_CODES = "https://www.twinone.org/apps/irremote/api/ircodes.php";

    private static final String PARAM_MANUFACTURER = "manufacturer";
    private static final String PARAM_DEVICE_TYPE = "device_type";
    private static final String PARAM_DEVICE = "device";

    public static class TwinoneReq implements Serializable {

        public static final int TARGET_MANUFACTURER = 1;
        public static final int TARGET_DEVICE_TYPE = 2;
        public static final int TARGET_DEVICE = 3;
        public static final int TARGET_IR_CODE = 4;

        public TwinoneReq(Context context) {
            userinfo = UserInfo.getAuthInfo(context);
        }

        public String getFullyQualifiedName(String separator) {
            StringBuilder sb = new StringBuilder();
            if (manufacturer == null) return sb.toString();
            sb.append(manufacturer);
            if (deviceType == null) return sb.toString();
            sb.append(separator).append(deviceType);
            if (device == null) return sb.toString();
            sb.append(separator).append(device);
            return sb.toString();
        }

        private String getUrl() {
            Uri u = Uri.parse(Constants.URL_DOWNLOAD);
            Uri.Builder ub = u.buildUpon();
            if (manufacturer == null) return ub.build().toString();
            ub.appendQueryParameter(PARAM_MANUFACTURER, manufacturer);
            if (deviceType == null) return ub.build().toString();
            ub.appendQueryParameter(PARAM_DEVICE_TYPE, deviceType);
            if (device == null) return ub.build().toString();
            ub.appendQueryParameter(PARAM_DEVICE, device);
            return ub.build().toString();

//            if (manufacturer == null) return URL_MANUFACTURERS;
//            if (deviceType == null) return URL_DEVICE_TYPES;
//            if (device == null) return URL_DEVICES;
//            return URL_IR_CODES;
        }

        private int getTargetType() {
            if (manufacturer == null) return TARGET_MANUFACTURER;
            if (deviceType == null) return TARGET_DEVICE_TYPE;
            if (device == null) return TARGET_DEVICE;
            return TARGET_IR_CODE;
        }

        private UserInfo userinfo;
        public String manufacturer;
        @SerializedName("device_type")
        public String deviceType;
        public String device;

        public void selectNext(String next) {
            switch (getTargetType()) {
                case TARGET_MANUFACTURER:
                    manufacturer = next;
                    break;
                case TARGET_DEVICE_TYPE:
                    deviceType = next;
                    break;
                case TARGET_DEVICE:
                    device = next;
                    break;
            }
        }

        public TwinoneReq clone() {
            return deserialize(serialize());
        }

        private String serialize() {
            return new Gson().toJson(this);
        }

        private static TwinoneReq deserialize(String data) {
            return new Gson().fromJson(data, TwinoneReq.class);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_db_refresh:
                loadData(false);
                return true;
            case R.id.menu_db_save:
                // TODO
                // Remote r = new Remote();
                // getProvider().saveRemote(r);
                return true;
        }
        return false;
    }

    public static class TwinoneResp {
        int status;
        String[] manufacturers;
        @SerializedName("device_types")
        String[] deviceTypes;
        RemoteDetails[] devices;
        Remote remote;

        private Object getData(TwinoneReq req) {
            switch (req.getTargetType()) {
                case TwinoneReq.TARGET_MANUFACTURER:
                    return manufacturers;
                case TwinoneReq.TARGET_DEVICE_TYPE:
                    return deviceTypes;
                case TwinoneReq.TARGET_DEVICE:
                    return devices;
                case TwinoneReq.TARGET_IR_CODE:
                    return remote;
            }
            return null;
        }
    }

    public static class RemoteDetails extends BaseListable {

        private int id;
        private String name;
        private String author;
        private int size; // number of buttons

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_db, menu);
        setupSearchView(menu);
        mSearchView.setQueryHint(getSearchHint(mRequest));

//        boolean show = mGCData.targetType == GlobalCacheProviderData.TYPE_IR_CODE
//                && ACTION_SAVE_REMOTE.equals(getProvider().getAction());
//        menu.findItem(R.id.menu_db_save).setVisible(show);
    }

    private String getSearchHint(TwinoneReq data) {
        int target = data.getTargetType();
        if (target == TwinoneReq.TARGET_MANUFACTURER) {
            return getString(R.string.db_search_hint_manufacturers);
        } else if (target == TwinoneReq.TARGET_IR_CODE) {
            return getString(R.string.db_search_hint_buttons);
        } else {
            return getString(R.string.db_search_hint_custom,
                    data.getFullyQualifiedName(" "));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_DATA)) {
            mRequest = (TwinoneReq) getArguments()
                    .getSerializable(ARG_DATA);
        } else {
            mRequest = new TwinoneReq(getActivity());
        }
    }

    private boolean mCreated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        setCurrentState(mRequest.getTargetType());
        setExitState(TwinoneReq.TARGET_MANUFACTURER);

        View rootView = inflater.inflate(R.layout.fragment_listable, container,
                false);
        mListView = (ListView) rootView.findViewById(R.id.lvElements);
        mListView.setOnItemClickListener(this);


        if (mCreated) {
            mListView.setAdapter(mAdapter);
            mAdapter.restoreOriginalDataSet();
        } else {
            loadData(true);
        }

        String title = mRequest.getFullyQualifiedName(" > ");
        if (title == null || title.isEmpty()) {
            title = getString(R.string.db_select_manufacturer);
        }
        getActivity().setTitle(title);

        mCreated = true;
        return rootView;
    }

    private void loadData(boolean useCache) {
        HttpJson hj = new HttpJson<TwinoneReq, TwinoneResp>(TwinoneResp.class);
        hj.setUrl(mRequest.getUrl());
        if (useCache)
            hj.enableCache(getActivity());
        hj.execute(mRequest, this);
        showLoadingDialog();
    }

    @Override
    public void onServerResponse(TwinoneReq req, TwinoneResp resp) {
        hideLoadingDialog();
        if (mRequest.getTargetType() == TwinoneReq.TARGET_DEVICE) {
            mAdapter = new RemoteAdapter(getActivity(), (RemoteDetails[]) resp.getData(req));
        } else {
            mAdapter = new ListableAdapter(getActivity(), (Object[])resp.getData(req));
        }
        mListView.setAdapter(mAdapter);
    }

    private static class RemoteAdapter extends ListableAdapter {
        public RemoteAdapter(Context c, RemoteDetails[] items) {
            super(c, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.listable_remote, null);
            TextView name = (TextView) v.findViewById(R.id.listable_remote_name);
            TextView author = (TextView) v.findViewById(R.id.listable_remote_author);
            TextView buttons = (TextView) v.findViewById(R.id.listable_remote_size);
            RemoteDetails d = (RemoteDetails) getItem(position);
            name.setText(d.name);
            author.setText(d.author);
            buttons.setText(getContext().getResources()
                    .getQuantityString(R.plurals.list_remote_button_size, d.size, d.size));
            return v;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = mAdapter.getItem(position);
        TwinoneReq clone = mRequest.clone();
        clone.selectNext(o.toString());
        addTwinoneProviderFragment(clone);
    }

    public void addTwinoneProviderFragment(TwinoneReq req) {
//        setExitState(TwinoneReq.TARGET_MANUFACTURER);
//        setCurrentState(req.getTargetType());
        TwinoneProviderFragment frag = new TwinoneProviderFragment();
        Bundle args = new Bundle();
        args.putSerializable(TwinoneProviderFragment.ARG_DATA, req);
        frag.setArguments(args);
        getProvider().addFragment(frag);
    }


    public static void getManufacturers(Context c, final OnManufacturersReceivedListener listener) {
        if (listener == null)
            throw new NullPointerException("Listener cannot be null");
        HttpJson<TwinoneReq, TwinoneResp> hj = new HttpJson<>(
                TwinoneResp.class);
        hj.setUrl(URL_MANUFACTURERS);
        hj.enableCache(c);
        TwinoneReq req = new TwinoneReq(c);
        hj.execute(req, new HttpJson.ResponseListener<TwinoneReq, TwinoneResp>() {
            @Override
            public void onServerResponse(TwinoneReq req,
                                         TwinoneResp resp) {
                if (listener != null)
                    listener.onManufacturersReceived(resp.manufacturers);
            }

        });
    }


    public interface OnManufacturersReceivedListener {
        public void onManufacturersReceived(String[] manufacturers);
    }
}

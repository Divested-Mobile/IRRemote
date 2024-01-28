package org.twinone.irremote.providers.common;

import android.os.Bundle;
import android.util.Log;
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

import org.twinone.irremote.R;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.ComponentUtils;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.BaseListable;
import org.twinone.irremote.providers.ListableAdapter;
import org.twinone.irremote.providers.ProviderFragment;
import org.twinone.irremote.util.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class CommonProviderFragment extends ProviderFragment implements
        OnItemClickListener {

    public static final String ARG_DATA = "arg.data";
    private static final String COMMON_TV_NAME = "TV";
    private static final String COMMON_BLURAY_NAME = "BluRay";
    private static final String COMMON_CABLE_NAME = "Cable";
    private static final String COMMON_AUDIO_AMPLIFIER = "Audio";
    private CommonProviderData mTarget;
    private Remote mRemote;

    private int getDeviceTypeInt(String deviceType) {
        if (COMMON_TV_NAME.equals(deviceType)) {
            return Remote.TYPE_TV;
        }
        if (COMMON_CABLE_NAME.equals(deviceType)) {
            return Remote.TYPE_CABLE;
        }
        if (COMMON_BLURAY_NAME.equals(deviceType)) {
            return Remote.TYPE_BLU_RAY;
        }
        if (COMMON_AUDIO_AMPLIFIER.equals(deviceType)) {
            return Remote.TYPE_AUDIO;
        }
        throw new IllegalArgumentException("WTF, no such type" + deviceType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_DATA)) {
            mTarget = (CommonProviderData) getArguments().getSerializable(
                    ARG_DATA);
            Log.d("", "mTarget.deviceType = " + mTarget.deviceType);
        } else {
            mTarget = new CommonProviderData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        setHasOptionsMenu(true);

        // For navigation
        setCurrentState(mTarget.targetType);

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_listable, container, false);

        mListView = (ListView) rootView.findViewById(R.id.lvElements);
        mListView.setOnItemClickListener(this);
//        mListView.setOnItemLongClickListener(this);

        mAdapter = new ListableAdapter(getActivity(), getItems());
        mListView.setAdapter(mAdapter);

        String name = getDataName(" > ");
        if (name == null) {
            getActivity().setTitle(R.string.db_select_device_type);
        } else {
            getActivity().setTitle(name);
        }
        return rootView;
    }

    private String getDBPath() {
        final String name = getDataName(File.separator);
        if (name == null) {
            return "db";
        }
        return "db" + File.separator + name;
    }

    private String getDataName(String separator) {
        StringBuilder path = new StringBuilder();
        if (mTarget.deviceType == null)
            return null;
        path.append(mTarget.deviceType);
//        if (mTarget.deviceName == null)
//            return path.toString();
//        path.append(separator).append(mTarget.deviceName);
        return path.toString();
    }

    private MyListable[] getItems() {
        ArrayList<MyListable> items = new ArrayList<>();
//        if (mTarget.targetType == CommonProviderData.TARGET_IR_CODE) {
//            mRemote = buildRemote();
//            for (Button b : mRemote.buttons) {
//                MyListable l = new MyListable(b.text);
//                l.id = b.uid;
//                items.add(l);
//            }
//        } else {
        for (String s : listAssets(getDBPath())) {
            items.add(new MyListable(s));
        }
//        }
        return items.toArray(new MyListable[items.size()]);
    }

    private String[] listAssets(String path) {
        try {
            return getActivity().getAssets().list(path);
        } catch (Exception e) {
            return new String[]{};
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_save:
//                getProvider().requestSaveRemote(mRemote);
//                break;
//        }
//        return false;
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long viewId) {
        MyListable item = (MyListable) mListView.getAdapter().getItem(position);
        if (mTarget.targetType == CommonProviderData.TARGET_DEVICE_TYPE) {
            CommonProviderData clone = mTarget.clone();
            clone.deviceType = item.toString();
            clone.targetType = CommonProviderData.TARGET_DEVICE_NAME;
            addCommonProviderFragment(clone);
        } else if (mTarget.targetType == CommonProviderData.TARGET_DEVICE_NAME) {
            mTarget.deviceName = item.toString();
//            if (ACTION_SAVE_REMOTE.equals(getProvider().getAction())) {
            mRemote = buildRemote();
            getProvider().requestSaveRemote(mRemote);

            // TODO
//            } else {
//                mTarget.targetType = CommonProviderData.TARGET_IR_CODE;
//                addCommonProviderFragment(mTarget.clone());
//            }
//        } else if (mTarget.targetType == CommonProviderData.TARGET_IR_CODE) {
//            Button b = mRemote.getButton(item.id);
//            getProvider().requestSaveButton(b);
        }
    }

    /**
     * Build a remote from our built-in database.
     * Either from JSON definition or from button files.
     */
    private Remote buildRemote() {
        final String remotedir = getDBPath() + File.separator + mTarget.deviceName;


        String[] assets = listAssets(remotedir);
        if (assets.length == 1 && Objects.equals(assets[0], "remote.json")) {
            // Create a remote from JSON
            String json = FileUtils.read(getActivity().getAssets(), remotedir + File.separator + assets[0]);
            return Remote.deserialize(json);
        } else {
            // Create a remote from button files
            Remote r = new Remote();
            r.name = mTarget.deviceName + " " + mTarget.deviceType;
            for (String name : listAssets(remotedir)) {
                int id = Integer.parseInt(name.substring(2).split("\\.")[0]);
                Button b = new Button(id);
                b.code = FileUtils.read(getActivity().getAssets(), remotedir
                        + File.separator + name);
                b.text = ComponentUtils.getCommonButtonDisplayName(b.id,
                        getActivity());
                r.addButton(b);
            }
            r.details.type = getDeviceTypeInt(mTarget.deviceType);
            return r;
        }
    }


//    public boolean onItemLongClick(AdapterView<?> parent, View view,
//                                   int position, long id) {
//        mListView.setItemChecked(position, true);
//        return true;
//    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_common, menu);
//        setupSearchView(menu);
//
//        MenuItem save = menu.findItem(R.id.menu_save);
//        boolean ircode = mTarget.targetType == CommonProviderData.TARGET_IR_CODE;
//        boolean remote = getProvider().getAction().equals(ACTION_SAVE_REMOTE);
//        save.setVisible(ircode && remote);
//    }

    public static class CommonProviderData implements Serializable {

        public static final int TARGET_DEVICE_TYPE = 0;
        public static final int TARGET_DEVICE_NAME = 1;
        public static final int TARGET_IR_CODE = 2;
        /**
         *
         */
        private static final long serialVersionUID = -1889643026103427356L;
        public int targetType;
        String deviceType;
        String deviceName;

        public CommonProviderData() {
            targetType = TARGET_DEVICE_TYPE;
        }

        public CommonProviderData clone() {
            CommonProviderData d = new CommonProviderData();
            d.targetType = targetType;
            d.deviceType = deviceType;
            d.deviceName = deviceName;
            return d;
        }

    }

    @SuppressWarnings("serial")
    private class MyListable extends BaseListable {

        private final String text;
        public int id;

        public MyListable(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

    }

    public void addCommonProviderFragment(CommonProviderData data) {
        setExitState(CommonProviderData.TARGET_DEVICE_TYPE);
        setCurrentState(data.targetType);
        CommonProviderFragment frag = new CommonProviderFragment();
        Bundle args = new Bundle();
        args.putSerializable(CommonProviderFragment.ARG_DATA, data);
        frag.setArguments(args);
        getProvider().addFragment(frag);
    }
}

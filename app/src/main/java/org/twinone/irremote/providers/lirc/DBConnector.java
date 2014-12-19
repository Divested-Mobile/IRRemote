package org.twinone.irremote.providers.lirc;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.twinone.irremote.util.SimpleCache;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class DBConnector {

    private static final String TAG = "DBConnector";
    private final Context mContext;
    /*
     * Listener that will provide callbacks to the user
     */
    private OnDataReceivedListener mListener;

    // private UriData mUriData = new UriData();
    private DBTask mDBTask;

    public DBConnector(Context c) {
        this(c, null);
    }

    public DBConnector(Context c, OnDataReceivedListener listener) {
        this.mListener = listener;
        this.mContext = c;
    }

    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        mListener = listener;
    }

    public void query(LircProviderData data) {
        if (data == null)
            data = new LircProviderData();
        queryServer(data);
    }

    private void queryServer(LircProviderData data) {
        cancelQuery();
        mDBTask = new DBTask(data);
        mDBTask.execute();
    }

    public void cancelQuery() {
        if (mDBTask != null && !mDBTask.isCancelled())
            // Allow task to finish (improving cache) ?
            mDBTask.cancel(false);
    }

    // Result may be null if connection failed!
    void triggerListenerOnReceived(int target, String result) {
        if (mListener == null)
            return;
        LircListable[] data = null;
        if (result != null) {
            switch (target) {
                case LircProviderData.TYPE_MANUFACTURER:
                    data = parseList(result);
                    break;
                case LircProviderData.TYPE_CODESET:
                    data = parseList(result);
                    break;
                case LircProviderData.TYPE_IR_CODE:
                    data = new LircParser(result.split("\n")).parse();
                    break;
            }
        }
        if (data != null)
            for (LircListable ll : data)
                ll.type = target;

        mListener.onDataReceived(data);
    }

    private LircListable[] parseList(String s) {
        ArrayList<LircListable> result = new ArrayList<>();
        final Element table = Jsoup.parse(s).getElementsByTag("table").get(0);
        for (Element td : table.getElementsByTag("td")) {
            if (td.attributes().size() == 0) {
                final LircListable ll = new LircListable();
                final Elements elms = td.getElementsByTag("a");
                if (elms.size() > 0) {
                    final Element a = elms.get(0);
                    String href = a.attr("href");
                    if (href.equals("/") || href.equals("/remotes/")
                            || href.endsWith(".jpg"))
                        continue;
                    ll.href = href;
                    if (href.endsWith("/")) {
                        href = href.substring(0, href.length() - 1);
                    }
                    ll.name = href;
                    result.add(ll);
                }
            }
        }
        return result.toArray(new LircListable[result.size()]);
    }

    public interface OnDataReceivedListener {
        /**
         * Called when data was returned from the database server
         *
         * @param data may be null if the connection failed
         */
        public void onDataReceived(LircListable[] data);
    }

    private class DBTask extends AsyncTask<String, Void, String> {
        private final String mUrl;
        private final String mCacheName;
        private final int mTarget;

        public DBTask(LircProviderData data) {
            mUrl = data.getUrl();
            mCacheName = data.getCacheName();
            mTarget = data.targetType;
        }

        @Override
        protected String doInBackground(String... params) {
            String cached = SimpleCache.readWithNewLines(mContext, mCacheName);
            if (cached != null) {
                Log.d("", "From cached");
                return cached;
            }
            // If not cached, load from http
            try {
                Log.d("", "Not from cached");
                URL url = new URL(mUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();

                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(in));
                StringBuilder data = new StringBuilder();
                String tmp;
                while ((tmp = br.readLine()) != null) {
                    data.append(tmp).append('\n');
                }
                urlConnection.disconnect();
                // Save to cache for future access
                SimpleCache.write(mContext, mCacheName, data.toString());

                return data.toString();
            } catch (Exception e) {
                Log.i(TAG, "Query to server failed ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            triggerListenerOnReceived(mTarget, result);
        }

    }

}

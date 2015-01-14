package org.twinone.androidlib.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.twinone.irremote.util.SimpleCache;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class performs a simple json network http request to a server
 *
 * @author twinone
 */
public class HttpJson<Req, Resp> extends AsyncTask<Void, Void, Void> {

    private static final String TAG = HttpJson.class.getSimpleName();
    private final Class<? extends Resp> mRespClass;
    private String mMethod = "POST";
    private String mUrl;
    private Req mReq;
    private Resp mResp;
    private boolean mHasError;
    private Exception mException;
    private boolean mCache;
    private int mStatusCode;
    private ResponseListener<Req, Resp> mResponseListener;
    private ExceptionListener<Req, Resp> mExceptionListener;

    private Context mContext;
    private long mMaxAgeMillis = 28 * 3600 * 24 * 1000L; // 4 weeks

    public HttpJson(Class<? extends Resp> respClass) {
        mRespClass = respClass;
    }

    public HttpJson(Class<? extends Resp> respClass, String url) {
        this(respClass);
        setUrl(url);
    }

    void setResponseListener(ResponseListener<Req, Resp> listener) {
        mResponseListener = listener;
    }

    public void enableCache(Context c) {
        mCache = true;
        mContext = c;
    }

    /**
     * Sets the maximum age in seconds before the file has to be refreshed
     */
    public void setCacheMaxAge(long maxAgeSeconds) {
        mMaxAgeMillis = maxAgeSeconds * 1000;
    }

    /**
     * Sets the maximum age in milliseconds before the file has to be refreshed
     */
    public void setCacheMaxAgeMillis(long maxAgeMillis) {
        mMaxAgeMillis = maxAgeMillis;
    }

    public void setExceptionListener(ExceptionListener<Req, Resp> listener) {
        mExceptionListener = listener;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void execute(Req req) {
        execute(req, null, null);
    }

    public void execute(Req req, ResponseListener<Req, Resp> responseListener) {
        execute(req, responseListener, null);
    }

    public void execute(Req req, ResponseListener<Req, Resp> responseListener,
                        ExceptionListener<Req, Resp> exceptionListener) {
        if (mUrl == null) {
            throw new IllegalStateException(
                    "You must specify an URL with setUrl()");
        }
        if (mResponseListener == null) {
            Log.w(TAG, "You have not specified a ResponseListener!");
        }
        if (responseListener != null)
            setResponseListener(responseListener);
        if (exceptionListener != null)
            setExceptionListener(exceptionListener);
        mReq = req;
        execute((Void[]) null);

    }

    @Override
    protected Void doInBackground(Void... params) {
        if (!getCachedResponse()) {
            serverRequestImpl();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Log.i("HttpJson", "Response:");
        Log.i("HttpJson", new Gson().toJson(mResp));

        if (!mHasError) {
            if (mResponseListener != null)
                mResponseListener.onServerResponse(mReq, mResp);
        } else {
            if (mExceptionListener != null)
                mExceptionListener.onServerException(mException);
            else
                Log.w(TAG, "Exception in request: ", mException);
        }
    }

    /**
     * Set the request method, default is POST
     */
    public void setMethod(String method) {
        mMethod = method;
    }

    private boolean getCachedResponse() {
        if (!mCache) return false;
        if (!SimpleCache.isAvailable(mContext, mUrl)) return false;
        long currentAge = System.currentTimeMillis() - SimpleCache.getLastModified(mContext, mUrl);
        if (mMaxAgeMillis < currentAge) {
            Log.i(TAG, "Cached response too old, getting fresh copy");
            return false;
        }
        String resp = SimpleCache.read(mContext, mUrl);
        try { // Could be another class...
            mResp = new Gson().fromJson(resp, mRespClass);
            Log.d(TAG, "Returning cached response");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void serverRequestImpl() {
        try {

            final HttpURLConnection conn = (HttpURLConnection) new URL(mUrl)
                    .openConnection();
            conn.setRequestMethod(mMethod);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            if (mReq != null) {
                String json = new Gson().toJson(mReq);
                Log.d("", "Pushing string: " + json);
                conn.getOutputStream().write(json.getBytes());
                conn.getOutputStream().close();
            }

            InputStream in = new BufferedInputStream(conn.getInputStream());
            final BufferedReader br = new BufferedReader(new InputStreamReader(
                    in));
            final StringBuilder data = new StringBuilder();
            String tmp;
            while ((tmp = br.readLine()) != null) {
                data.append(tmp);
            }
            Log.d(TAG, "String response: " + data.toString());
            mResp = new Gson().fromJson(data.toString(), mRespClass);
            if (mCache) {
                SimpleCache.write(mContext, mUrl, data.toString());
            }
            mStatusCode = conn.getResponseCode();
            conn.disconnect();
            mHasError = false;
        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
            Log.w("", "Exception: ", e);
            mHasError = true;
        }
    }

    public interface ResponseListener<Req, Resp> {
        public void onServerResponse(Req req, Resp resp);

    }

    public interface ExceptionListener<Req, Resp> {
        public void onServerException(Exception e);
    }

}

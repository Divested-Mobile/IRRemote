package org.twinone.androidlib.net;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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

    private Class<? extends Resp> mRespClass;

    public HttpJson(Class<? extends Resp> respClass) {
        mRespClass = respClass;
    }

    public HttpJson(Class<? extends Resp> respClass, String url) {
        this(respClass);
        setUrl(url);
    }

    public static boolean installHttpCache(Context context) {
        try {
            File httpCacheDir = new File(context.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
            return true;
        } catch (IOException e) {
            Log.i(TAG, "HTTP response cache installation failed:" + e);
            return false;
        }
    }

    public void setResponseListener(ResponseListener<Req, Resp> listener) {
        mResponseListener = listener;
    }

    public void setCache(boolean cache) {
        mCache = cache;
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
        if (responseListener != null)
            setResponseListener(responseListener);
        if (exceptionListener != null)
            setExceptionListener(exceptionListener);
        mReq = req;
        execute((Void[]) null);

    }

    @Override
    protected Void doInBackground(Void... params) {
        mHasError = !serverRequestImpl();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (!mHasError) {
            if (mResponseListener != null)
                mResponseListener.onServerResponse(mStatusCode, mReq, mResp);
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

    private boolean serverRequestImpl() {
        try {

            final HttpURLConnection conn = (HttpURLConnection) new URL(mUrl)
                    .openConnection();
            conn.setRequestMethod(mMethod);
            conn.setRequestProperty("Content-Type", "application/json");
            if (!mCache) {
                conn.addRequestProperty("Cache-Control", "no-cache");
            }
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
            Log.i("HttpJson", "Response:");
            Log.i("HttpJson", data.toString());
            mResp = (Resp) new Gson().fromJson(data.toString(), mRespClass);
            mStatusCode = conn.getResponseCode();
            conn.disconnect();
            flushCache();
            return true;
        } catch (Exception e) {
            mException = e;
            return false;
        }
    }

    private void flushCache() {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    public interface ResponseListener<Req, Resp> {
        public void onServerResponse(int statusCode, Req req, Resp resp);

    }

    public interface ExceptionListener<Req, Resp> {
        public void onServerException(Exception e);
    }

}

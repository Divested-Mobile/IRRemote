package org.twinone.androidlib;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class AdMobBannerBuilder {
    private static final String TAG = AdMobBannerBuilder.class.getSimpleName();

    private AdRequest.Builder mAdRequestBuilder;
    private AdSize mAdSize = AdSize.BANNER;

    private ViewGroup mParent;

    private String mAdUnitId;

    public void setAdUnitId(String adUnitId) {
        mAdUnitId = adUnitId;
    }

    public void setParent(ViewGroup parent) {
        mParent = parent;
        mAdRequestBuilder = new AdRequest.Builder();
    }

    /**
     * You don't need to add emulator id, it will automatically be added for you
     */
    public void addTestDevice(String id) {
        mAdRequestBuilder.addTestDevice(id);
    }

    public AdMobBannerBuilder setAdSize(AdSize adSize) {
        mAdSize = adSize;
        return this;
    }

    public void show() {
        mAdRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        AdView mAdView = new AdView(mParent.getContext());
        mAdView.setAdUnitId(mAdUnitId);
        mAdView.setAdSize(mAdSize);

        mParent.addView(mAdView);

        AdRequest ar = mAdRequestBuilder.build();
        mAdView.loadAd(ar);
    }

    public AdMobBannerBuilder addThisAsTestDevice(Context c) {
        final String id = Settings.Secure.getString(c.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(id.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte md : messageDigest) {
                String h = Integer.toHexString(0xFF & md);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            addTestDevice(hexString.toString().toUpperCase(Locale.ENGLISH));

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Could not add this as test device", e);
        }
        return this;
    }
}

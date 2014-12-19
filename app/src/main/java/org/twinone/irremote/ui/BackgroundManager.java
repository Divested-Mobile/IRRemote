package org.twinone.irremote.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.view.WindowManager;
import android.widget.ImageView;

import org.twinone.irremote.R;

import java.io.FileNotFoundException;

class BackgroundManager {

    private final Context mContext;

    private final ImageView mTarget;

    public BackgroundManager(Context c, ImageView target) {
        if (target == null) {
            throw new IllegalArgumentException("ImageView cannot be null");
        }
        mContext = c;
        mTarget = target;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        int scale = 1;
        int width = options.outWidth;
        int height = options.outHeight;
        while (true) {
            if (width / 2 < reqWidth || height / 2 < reqHeight) {
                break;
            }
            width /= 2;
            height /= 2;
            scale *= 2;
        }
        return scale;
    }

    public void setBackgroundFromPreference() {
        final SharedPreferences sp = SettingsActivity.getPreferences(mContext);
        final String bgKey = mContext.getString(R.string.pref_key_bg);
        final String gallery = mContext.getString(R.string.pref_val_bg_gallery);
        if (gallery.equals(sp.getString(bgKey, null))) {
            final String uriKey = mContext.getString(R.string.pref_key_bg_uri);
            final String uriString = sp.getString(uriKey, null);
            if (uriString != null)
                setBackgroundFromUri(Uri.parse(uriString));
        } else {
        }
    }

    private void setBackgroundFromUri(Uri uri) {
        if (uri == null)
            return;
        final WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        final Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        try {
            final Bitmap b = decodeSampledBitmapFromUri(uri, size.x, size.y);
            if (b == null) {
                return;
            }
            mTarget.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            return;
        }
    }

    Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth,
                                      int reqHeight) throws FileNotFoundException {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(mContext.getContentResolver()
                .openInputStream(uri), null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        options.inJustDecodeBounds = false;
        @SuppressWarnings("UnnecessaryLocalVariable") Bitmap bm = BitmapFactory.decodeStream(mContext.getContentResolver()
                .openInputStream(uri), null, options);
        return bm;
    }

}

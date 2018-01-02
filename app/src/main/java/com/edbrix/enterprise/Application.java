package com.edbrix.enterprise;


import android.os.Environment;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.edbrix.enterprise.Utils.FontsOverride;
import com.edbrix.enterprise.Volley.OkHttpStack;

import java.io.File;

import timber.log.Timber;

//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Application extends android.app.Application {

    public static final String PACKAGE_NAME = Application.class.getPackage().getName();
    private static final String TAG = Application.class.getSimpleName();
    public static String APP_VERSION = "0.1";
    public static String ANDROID_ID = "0000000000000000";
    private static Application mInstance;
    private RequestQueue mRequestQueue;

    public static synchronized Application getInstance() {
        return mInstance;
    }

    /**
     * Method provides defaultRetryPolice.
     * First Attempt = 14+(14*1)= 28s.
     * Second attempt = 28+(28*1)= 56s.
     * then invoke Response.ErrorListener callback.
     *
     * @return DefaultRetryPolicy object
     */
    public static DefaultRetryPolicy getDefaultRetryPolice() {
        return new DefaultRetryPolicy(14000, 2, 1);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        File yourAppStorageDir = new File(Environment.getExternalStorageDirectory(), "/" + getResources().getString(R.string.app_name) + "/");
        if (!yourAppStorageDir.exists()) {
            boolean isDirCreated = yourAppStorageDir.mkdirs();
            Log.d(TAG, "App mediaStorageDirectory created :" + isDirCreated);
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
//            FirebaseCrash.log("Activity created");
//            Timber.plant(new CrashReportingTree());
        }

        FontsOverride.setDefaultFont(this, "DEFAULT");
        FontsOverride.setDefaultFont(this, "MONOSPACE");
        // FontsOverride.setDefaultFont(this, "SERIF", "MyFontAsset3.ttf");
        // FontsOverride.setDefaultFont(this, "SANS_SERIF", "MyFontAsset4.ttf");

        // Create global configuration and initialize ImageLoader with this config
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
//        ImageLoader.getInstance().init(config);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////// Volley request ///////////////////////////////////////////////////////////////////////////////////////
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this, new OkHttpStack());
        }
        return mRequestQueue;
    }

    @VisibleForTesting
    public void setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}

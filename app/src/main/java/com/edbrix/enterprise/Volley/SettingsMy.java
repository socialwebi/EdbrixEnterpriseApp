package com.edbrix.enterprise.Volley;

import android.content.SharedPreferences;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.Models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

/**
 * Class providing app specific sharedPreference settings.
 */
public class SettingsMy {

    public static final String PREF_ACTIVE_USER = "pref_active_user";
    public static final String PREF_USER_EMAIL = "pref_user_email";

    public static final String ZOOM_USER_ID = "zoom_user_id";
    public static final String ZOOM_USER_TOKEN = "zoom_user_token";

    private static final String TAG = SettingsMy.class.getSimpleName();
    private static User activeUser;
    private static SharedPreferences sharedPref;

    private static Gson gson;

    private SettingsMy() {
    }


    /**
     * Get active user info.
     *
     * @return user or null if nobody logged in.
     */
    public static User getActiveUser() {
        if (activeUser != null) {
            Timber.d("%s - Returned active user " + activeUser, TAG);
            return activeUser;
        } else {
            SharedPreferences prefs = getSettings();
            String json = prefs.getString(PREF_ACTIVE_USER, "");
            if (json.isEmpty() || "null".equals(json)) {
                Timber.d("%s - Returned null", TAG);
                return null;
            } else {
                activeUser = getGsonParser().fromJson(json, User.class);
                Timber.d("%s - Returned active user from memory: %s", TAG, activeUser.toString());
                return activeUser;
            }
        }
    }

    /**
     * Set active user.
     *
     * @param user active user or null for disable user.
     */
    public static void setActiveUser(User user) {
        if (user != null)
            Timber.d("%s - Set active user with name: %s", TAG, user.toString());
        else
            Timber.d("%s - Deleting active user", TAG);
        SettingsMy.activeUser = user;

        String json = getGsonParser().toJson(SettingsMy.activeUser);
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(PREF_ACTIVE_USER, json);
        editor.apply();
    }

    public static void setZoomCredential(String Id, String token){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(ZOOM_USER_ID, Id);
        editor.putString(ZOOM_USER_TOKEN, token);
        editor.apply();
    }

    public String getZoomUserId(){
        SharedPreferences prefs = getSettings();
       return prefs.getString(ZOOM_USER_ID, "");
    }

    public String getZoomUserToken(){
        SharedPreferences prefs = getSettings();
        return prefs.getString(ZOOM_USER_TOKEN, "");
    }


    /**
     * Add specific parsing to gson
     *
     * @return new instance of {@link Gson}
     */
    public static Gson getGsonParser() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            //gsonBuilder.registerTypeAdapter(Filters.class, new DeserializerFilters());
            gson = gsonBuilder.create();
        }
        return gson;
    }


    // encryption
    public static String encrypt(String input) {
        // This is base64 encoding, which is not an encryption
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    // decryption
    public static String decrypt(String input) {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }


    /* Write
    SharedPreferences preferences = getSharedPreferences("some_prefs_name", MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(encrypt("password"), encrypt("dummypass"));
    editor.apply(); // Or commit if targeting old devices

     Read
    SharedPreferences preferences = getSharedPreferences("some_prefs_name", MODE_PRIVATE);
    String passEncrypted = preferences.getString(encrypt("password"), encrypt("default"));
    String pass = decrypt(passEncrypted); */


    /**
     * Get user email. Used for login purpose.
     *
     * @return email of last logged user.
     */
    public static String getUserEmailHint() {
        SharedPreferences prefs = getSettings();
        String userEmail = prefs.getString(PREF_USER_EMAIL, "");
        Timber.d("%s - Obtained user email: %s", TAG, userEmail);
        return userEmail;
    }

    /**
     * Set user email to preferences.
     * Used for login purpose.
     *
     * @param userEmail email of last logged user.
     */
    public static void setUserEmailHint(String userEmail) {
        Timber.d("%s - Set user email: %s", TAG, userEmail);
        putParam(PREF_USER_EMAIL, userEmail);
    }


    /**
     * Obtain preferences instance.
     *
     * @return base instance of app SharedPreferences.
     */
    public static SharedPreferences getSettings() {
        if (sharedPref == null) {
            sharedPref = Application.getInstance().getSharedPreferences(Application.PACKAGE_NAME, MODE_PRIVATE);
        }
        return sharedPref;
    }

    private static boolean putParam(String key, String value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(key, value);
        return editor.commit();
    }

    private static boolean putParam(String key, boolean value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static String getErrorMessage(VolleyError volleyError) {
        String message = null;
        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (volleyError instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
        } else if (volleyError instanceof NoConnectionError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        } else {
            message = "Something went wrong. Please try again later.";
        }
        return message;
    }
}

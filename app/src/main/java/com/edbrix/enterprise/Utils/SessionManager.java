/**
 *
 */

package com.edbrix.enterprise.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Wrapper for managing session data.
 *
 * @author rajk
 */
public class SessionManager {

    private static final String LOGTAG = "SessionManager";

    private static final String APP_SHARED_PREFS_NAME = "Edbrix";

    private static final String PREFS_SESSION_USERNAME = "SessionUsername";
    private static final String PREFS_SESSION_PASSWORD = "SessionPassword";
    private static final String PREFS_SESSION_PROFILE_TOKEN = "SessionProfileToken";
    private static final String PREFS_SESSION_FCM_TOKEN = "SessionFCMToken";
    private static final String PREFS_SESSION_DEVICE_TOKEN = "SessionDeviceToken";
    private static final String PREFS_SESSION_DEVICE_TYPE = "SessionDeviceType";
    private static final String PREFS_SESSION_ORG_ID = "SessionOrgId";
    private static final String PREFS_SESSION_SUB_COURSE_DATA = "SessionSubjectCourseData";
    private static final String PREFS_SESSION_LOGGED_USERDATA = "SessionLoggedUsersData";
    private static final String PREFS_SESSION_LOGGEDUSERSLIST = "SessionLoggedUsersList";
    private static final String PREFS_SESSION_LOGGEDUSERSCREDENTIALSLIST = "SessionLoggedUsersCredentialsList";
    private static final String PREF_SESSION_WALKTHROUGH_SKIP_KEY = "wlkSkip";
    private static final String PREF_SESSION_APPUPDATE_SKIP_KEY = "AppUpdateSkip";
    private static final String PREFS_SESSION_PROFILE_IMAGE_BASE64 = "SessionProfileImageBase64";

    private SharedPreferences sharedPrefs;

    public SessionManager(Context context) {
        if (context != null) {
            this.sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        } else {
            Log.w(LOGTAG, "Invalid context!");
        }
    }

    /**
     * Checks if we have valid saved session credentials.
     *
     * @return
     */
  /*  public Boolean hasSessionCredentials() {
        return ((this.getSessionOrgId().length() > 0) && (this.getSessionProfileToken().length() > 0) && (this.getLoggedUserData() != null) && (this.getLoggedUserData().getId().length() > 0));
    }*/

    /**
     * Gets the session's saved username.
     *
     * @return
     */
    public String getSessionUsername() {
        return this.sharedPrefs.getString(PREFS_SESSION_USERNAME, "");
    }

    /**
     * Gets the session's saved password.
     *
     * @return
     */
    public String getSessionPassword() {
        return this.sharedPrefs.getString(PREFS_SESSION_PASSWORD, "");
    }

    /**
     * Gets the session's saved profileToken.
     *
     * @return
     */
    public String getSessionProfileToken() {
        return this.sharedPrefs.getString(PREFS_SESSION_PROFILE_TOKEN, "");
    }

    /**
     * Gets the session's saved deviceToken.
     *
     * @return
     */
    public String getSessionDeviceToken() {
        return this.sharedPrefs.getString(PREFS_SESSION_DEVICE_TOKEN, "");
    }

    /**
     * Gets the session's saved device FCM Token.
     *
     * @return
     */
    public String getSessionFCMToken() {
        return this.sharedPrefs.getString(PREFS_SESSION_FCM_TOKEN, "");
    }

    /**
     * Gets the session's saved device orientation.
     *
     * @return
     */
    public String getSessionDeviceType() {
        return this.sharedPrefs.getString(PREFS_SESSION_DEVICE_TYPE, "mob");
    }
    /**
     * Gets the session's saved Course Id.
     *
     * @return
     *//*
    public SubjectData getSessionSubjectCourseData() {
        try {
            return (SubjectData) ObjectSerializer.deserialize(this.sharedPrefs.getString(PREFS_SESSION_SUB_COURSE_DATA, ObjectSerializer.serialize(new SubjectData())));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /**
     * Gets the session's saved Organization Id.
     *
     * @return
     */
    public String getSessionOrgId() {
        return this.sharedPrefs.getString(PREFS_SESSION_ORG_ID, "");
    }

   /* public UserData getLoggedUserData() {
        try {
            return (UserData) ObjectSerializer.deserialize(this.sharedPrefs.getString(PREFS_SESSION_LOGGED_USERDATA, ObjectSerializer.serialize(new UserData())));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /**
     * Gets the session's user profile image base64.
     *
     * @return
     */
    public String getPrefsSessionProfileImageBase64() {
        return this.sharedPrefs.getString(PREFS_SESSION_PROFILE_IMAGE_BASE64, "");
    }


  /*  public ArrayList<UserData> getLoggedUsersList() {
        try {
            return (ArrayList<UserData>) ObjectSerializer.deserialize(this.sharedPrefs.getString(PREFS_SESSION_LOGGEDUSERSLIST, ObjectSerializer.serialize(new ArrayList<UserData>())));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<String, String> getLoggedUsersCredentialsHashMap() {
        try {
            return (HashMap<String, String>) ObjectSerializer.deserialize(this.sharedPrefs.getString(PREFS_SESSION_LOGGEDUSERSCREDENTIALSLIST, ObjectSerializer.serialize(new HashMap<String, String>())));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /**
     * Updates the saved session credentials.
     *
     * @param username the username to save.
     */
    public void updateSessionUsername(String username) {

        Editor editor = this.sharedPrefs.edit();
        if ((username != null) && (username.length() > 0)) {
            editor.putString(PREFS_SESSION_USERNAME, username);
        } else {
            editor.remove(PREFS_SESSION_USERNAME);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param password the password to save.
     */
    public void updateSessionPassword(String password) {

        Editor editor = this.sharedPrefs.edit();
        if ((password != null) && (password.length() > 0)) {
            editor.putString(PREFS_SESSION_PASSWORD, password);
        } else {
            editor.remove(PREFS_SESSION_PASSWORD);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param accessToken the AccessToken to save.
     */
    public void updateSessionProfileToken(String accessToken) {

        Editor editor = this.sharedPrefs.edit();
        if ((accessToken != null) && (accessToken.length() > 0)) {
            editor.putString(PREFS_SESSION_PROFILE_TOKEN, accessToken);
        } else {
            editor.remove(PREFS_SESSION_PROFILE_TOKEN);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param deviceToken the DeviceToken to save.
     */
    public void updateSessionDeviceToken(String deviceToken) {

        Editor editor = this.sharedPrefs.edit();
        if ((deviceToken != null) && (deviceToken.length() > 0)) {
            editor.putString(PREFS_SESSION_DEVICE_TOKEN, deviceToken);
        } else {
            editor.remove(PREFS_SESSION_DEVICE_TOKEN);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param deviceFCMToken the DeviceFCMToken to save.
     */
    public void updateSessionFCMToken(String deviceFCMToken) {

        Editor editor = this.sharedPrefs.edit();
        if ((deviceFCMToken != null) && (deviceFCMToken.length() > 0)) {
            editor.putString(PREFS_SESSION_FCM_TOKEN, deviceFCMToken);
        } else {
            editor.remove(PREFS_SESSION_FCM_TOKEN);
        }
        editor.commit();
    }

    public void updateSessionDeviceType(String deviceType) {

        Editor editor = this.sharedPrefs.edit();
        if ((deviceType != null) && (deviceType.length() > 0)) {
            editor.putString(PREFS_SESSION_DEVICE_TYPE, deviceType);
        } else {
            editor.remove(PREFS_SESSION_DEVICE_TYPE);
        }
        editor.commit();
    }


    /**
     * Updates the saved session credentials.
     *
     * @param orgId the Organization Id to save.
     */
    public void updateSessionOrgID(String orgId) {

        Editor editor = this.sharedPrefs.edit();
        if ((orgId != null) && (orgId.length() > 0)) {
            editor.putString(PREFS_SESSION_ORG_ID, orgId);
        } else {
            editor.remove(PREFS_SESSION_ORG_ID);
        }
        editor.commit();
    }

    /**
     * Updates the saved session credentials.
     *
     * @param subjectData the Subject_Course Data to save.
     */
//    public void updateSessionSubjectCourseData(SubjectData subjectData) {
//        Editor editor = this.sharedPrefs.edit();
//        try {
//            if ((subjectData != null)) {
//                editor.putString(PREFS_SESSION_SUB_COURSE_DATA, ObjectSerializer.serialize(subjectData));
//            } else {
//                editor.remove(PREFS_SESSION_SUB_COURSE_DATA);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        editor.commit();
//    }

    /**
     * Updates the saved session credentials.
     *
     * @param base64String the base64String to save.
     */
    public void updateSessionProfileImageBase64(String base64String) {

        Editor editor = this.sharedPrefs.edit();
        if ((base64String != null) && (base64String.length() > 0)) {
            editor.putString(PREFS_SESSION_PROFILE_IMAGE_BASE64, base64String);
        } else {
            editor.remove(PREFS_SESSION_PROFILE_IMAGE_BASE64);
        }
        editor.commit();
    }


    /**
     * Updates the saved session credentials.
     *
     * @param username the username to save.
     * @param password the password to save.
     */
    public void updateSessionCredentials(String username, String password, String profileToken) {

        Editor editor = this.sharedPrefs.edit();
        if ((username != null) && (username.length() > 0)) {
            editor.putString(PREFS_SESSION_USERNAME, username);
        } else {
            editor.remove(PREFS_SESSION_USERNAME);
        }

        if ((password != null) && (password.length() > 0)) {
            editor.putString(PREFS_SESSION_PASSWORD, password);
        } else {
            editor.remove(PREFS_SESSION_PASSWORD);
        }

        if ((profileToken != null) && (profileToken.length() > 0)) {
            editor.putString(PREFS_SESSION_PROFILE_TOKEN, profileToken);
        } else {
            editor.remove(PREFS_SESSION_PROFILE_TOKEN);
        }

        editor.commit();
    }

   /* public void updateLoggedUserData(UserData loggedUserData) {
        Editor editor = this.sharedPrefs.edit();
        try {
            if ((loggedUserData != null)) {
                editor.putString(PREFS_SESSION_LOGGED_USERDATA, ObjectSerializer.serialize(loggedUserData));
            } else {
                editor.remove(PREFS_SESSION_LOGGED_USERDATA);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }


    public void updateLoggedUserList(ArrayList<UserData> loggedUserList) {
        Editor editor = this.sharedPrefs.edit();
        try {
            if ((loggedUserList != null) && (loggedUserList.size() > 0)) {
                editor.putString(PREFS_SESSION_LOGGEDUSERSLIST, ObjectSerializer.serialize(loggedUserList));
            } else {
                editor.remove(PREFS_SESSION_LOGGEDUSERSLIST);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public void updateLoggedUsersCredentials(HashMap<String, String> usercredentialHashMap) {
        Editor editor = this.sharedPrefs.edit();
        try {
            if ((usercredentialHashMap != null) && (usercredentialHashMap.size() > 0)) {
                editor.putString(PREFS_SESSION_LOGGEDUSERSCREDENTIALSLIST, ObjectSerializer.serialize(usercredentialHashMap));
            } else {
                editor.remove(PREFS_SESSION_LOGGEDUSERSCREDENTIALSLIST);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }*/

    public void addUpdateSkipWalkthroughPref(boolean flag) {
        Editor editor = this.sharedPrefs.edit();
        editor.putBoolean(PREF_SESSION_WALKTHROUGH_SKIP_KEY, flag);
        editor.commit();
    }

    public boolean getWalkthroughSkipValue() {
        return this.sharedPrefs.getBoolean(PREF_SESSION_WALKTHROUGH_SKIP_KEY, false);
    }

    public void updateAppUpdatesPref(boolean flag) {
        Editor editor = this.sharedPrefs.edit();
        editor.putBoolean(PREF_SESSION_APPUPDATE_SKIP_KEY, flag);
        editor.commit();
    }

    public boolean getAppUpdatesPrefValue() {
        return this.sharedPrefs.getBoolean(PREF_SESSION_APPUPDATE_SKIP_KEY, false);
    }

    /**
     * Clears stored session credentials if any.
     */
    public void clearSessionCredentials() {
//        this.updateSessionCredentials(null, null, null);
        this.updateSessionPassword(null);
        this.updateSessionOrgID(null);
        this.updateSessionProfileToken(null);
        this.updateSessionDeviceToken(null);
//        this.updateLoggedUserList(null);
//        this.updateLoggedUsersCredentials(null);
//        this.updateLoggedUserData(null);
//        this.updateSessionSubjectCourseData(null);
//        this.updateAppUpdatesPref(false);
    }
}

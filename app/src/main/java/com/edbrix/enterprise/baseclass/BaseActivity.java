package com.edbrix.enterprise.baseclass;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.edbrix.enterprise.Utils.SessionManager;
import com.edbrix.enterprise.commons.GlobalMethods;
import com.edbrix.enterprise.commons.ToastMessage;

/**
 * Created by rajk
 */
public class BaseActivity extends AppCompatActivity {

    //    DialogManager dialogManager;
    GlobalMethods globalMethods;
    //    ConnectivityMonitor connectivityMonitor;
    protected Context mContext;
    private ToastMessage toastMessage;
    private SessionManager sessionManager;

    protected OnFragmentBackPressedListener onFragmentBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        if (!isTablet()) {
            // stop screen rotation on phones
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            sessionManager.updateSessionDeviceType("mob");

        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            sessionManager.updateSessionDeviceType("tab");
        }
//        dialogManager = new DialogManager(this);
        globalMethods = new GlobalMethods();
        toastMessage = new ToastMessage(this);
//        connectivityMonitor = new ConnectivityMonitor(this, erisConnectionListener);
        mContext = this;
    }

    protected void showToast(String msg, int timeDuration) {
        toastMessage.showToastMsg(msg, timeDuration);
    }

    protected void showToast(String msg) {
        toastMessage.showToastMsg(msg, Toast.LENGTH_LONG);
    }

  /*  protected AlertDialogManager getAlertDialogManager() {
        return dialogManager.getAlertDialogManager();
    }*/

    protected GlobalMethods getGlobalMethods() {
        return globalMethods;
    }

   /* protected void showBusyProgress() {
        dialogManager.showBusyProgress();
    }

    protected void hideBusyProgress() {
        dialogManager.hideBusyProgress();
    }*/

    protected void onNetworkStatusChanged(boolean status) {

    }


    public interface OnFragmentBackPressedListener {
        public void doBack();
    }

    public void setOnFragmentBackPressedListener(OnFragmentBackPressedListener onFragmentBackPressedListener) {
        this.onFragmentBackPressedListener = onFragmentBackPressedListener;
    }


    protected boolean isTablet() {
        boolean xlarge = ((this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
        Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
        Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    protected int getScreenOrientation() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getRotation();
    }
}

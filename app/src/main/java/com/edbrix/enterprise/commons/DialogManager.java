package com.edbrix.enterprise.commons;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by rajk
 */
public class DialogManager {


    protected ProgressDialog progressDialog;
    Context mContext;
    private AlertDialogManager alertDialogManager;
    private int activeBusyTasks = 0;

    public static DialogManager getInstance(Context context){
        DialogManager dialogManager = new DialogManager(context);
        return dialogManager;
    }

    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Show the busy progress overlay with a default message.
     * <p/>
     * Note that the show and hide calls need to be balanced.
     */
    public void showBusyProgress() {
        this.showBusyProgress(null);
    }

    /**
     * Show the busy progress overlay with the specified message
     * <p/>
     * Note that the show and hide calls need to be balanced.
     */
    public void showBusyProgress(String message) {
        this.activeBusyTasks++;
        try {
            if (this.progressDialog == null) {
                this.progressDialog = new ProgressDialog(mContext);
                this.progressDialog.setCancelable(false);
                this.progressDialog.show();
            }

            if (message == null) {
                this.progressDialog.setMessage("Please wait....");
            } else {
                this.progressDialog.setMessage(message);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public ProgressDialog getCurrentPorgressDialog() {
        return progressDialog;
    }

    public void showDownloadProgress() {

        this.activeBusyTasks++;
        try {
            if (this.progressDialog == null) {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMax(100);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setProgress(0);
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hide the busy progress overlay.
     * <p/>
     * Note that the show and hide calls need to be balanced.
     */
    public void hideBusyProgress() {
        this.activeBusyTasks--;
        try {
            if (this.activeBusyTasks < 0) {
                this.activeBusyTasks = 0;
            }

            if (this.activeBusyTasks == 0) {
                if (this.progressDialog != null) {
                    this.progressDialog.dismiss();
                    this.progressDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AlertDialogManager getAlertDialogManager() {
        if (this.alertDialogManager != null) {
            return this.alertDialogManager;
        } else {
            this.alertDialogManager = new AlertDialogManager(mContext);
            return this.alertDialogManager;
        }
    }

}

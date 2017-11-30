
package com.edbrix.enterprise.commons;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;

import com.edbrix.enterprise.R;


/**
 * @author rajk
 *
 * Usage
 * AlertDialogManager dialogManager=new AlertDialogManager(mContext);
 * dialogManager.Dialog("Please select state first.").show();
 * ***NOTE*** don't forget to show() after dialog's end parentheses
 */

public class AlertDialogManager {

    private Context context;
    private AlertDialog alertDialog;

    public interface onSingleButtonClickListner {
        void onPositiveClick();
    }

    public interface onTwoButtonClickListner extends onSingleButtonClickListner {
        void onNegativeClick();
    }

    public AlertDialogManager(Context context) {
        this.alertDialog = new AlertDialog.Builder(context).create();
        this.context = context;
    }

    public void setAlertDialogCancellable(boolean cancellable){
        this.alertDialog.setCancelable(cancellable);
    }

    public AlertDialog Dialog(String message) {
       return Dialog(message, null);
    }

    public AlertDialog Dialog(String message, onSingleButtonClickListner listener) {
        return Dialog(this.context.getResources().getString(R.string.app_name), message, false, listener);
    }

    public AlertDialog Dialog(int titleResourceId, int messageResourceId, boolean showIcon, onSingleButtonClickListner listener) {
        return Dialog(this.context.getResources().getString(titleResourceId), this.context.getResources().getString(messageResourceId), showIcon, listener);
    }

    public AlertDialog Dialog(String title, String message, boolean showIcon, onSingleButtonClickListner listener) {
        return Dialog(title, message, "", "", showIcon, listener);
    }

    public AlertDialog Dialog(int titleResourceId, String message, boolean showIcon, onSingleButtonClickListner listener) {
        return Dialog(this.context.getResources().getString(titleResourceId), message, showIcon, listener);
    }

    public AlertDialog Dialog(String title, int messageResourceId, boolean showIcon, onSingleButtonClickListner listener) {
        return Dialog(title, this.context.getResources().getString(messageResourceId), showIcon, listener);
    }

    public AlertDialog Dialog(int titleResourceId, String message, onSingleButtonClickListner listener) {
        return Dialog(this.context.getResources().getString(titleResourceId), message, listener);
    }

    public AlertDialog Dialog(String title, String message, onSingleButtonClickListner listener) {
        return Dialog(title, message, "", "", listener);
    }

    public AlertDialog Dialog(int messageResourceId, onSingleButtonClickListner listener) {
        return Dialog("", context.getResources().getString(messageResourceId), listener);
    }

    public AlertDialog Dialog(int titleResourceId, int messageResourceId, String posButton, String negButton, boolean showIcon, final onSingleButtonClickListner listener) {
        return Dialog(this.context.getResources().getString(titleResourceId), this.context.getResources().getString(messageResourceId), posButton, negButton, showIcon, listener);
    }

    public AlertDialog Dialog(int titleResourceId, int messageResourceId, String posButton, String negButton, final onSingleButtonClickListner listener) {
        return Dialog(this.context.getResources().getString(titleResourceId), this.context.getResources().getString(messageResourceId), posButton, negButton, listener);
    }

    public AlertDialog Dialog(int titleResourceId, String message, String posButton, String negButton, boolean showIcon, final onSingleButtonClickListner listener) {
        return Dialog(this.context.getResources().getString(titleResourceId), message, posButton, negButton, showIcon, listener);
    }

    public AlertDialog Dialog(int titleResourceId, String message, String posButton, String negButton, final onSingleButtonClickListner listener) {
        return Dialog(this.context.getResources().getString(titleResourceId), message, posButton, negButton, listener);
    }

    public AlertDialog Dialog(String title, int messageResourceId, String posButton, String negButton, boolean showIcon, final onSingleButtonClickListner listener) {
        return Dialog(title, this.context.getResources().getString(messageResourceId), posButton, negButton, showIcon, listener);
    }

    public AlertDialog Dialog(String title, int messageResourceId, String posButton, String negButton, final onSingleButtonClickListner listener) {
        return Dialog(title, this.context.getResources().getString(messageResourceId), posButton, negButton, listener);
    }

    public AlertDialog Dialog(String title, String messge, String posButton, String negButton, final onSingleButtonClickListner listener) {
        return Dialog(title, messge, posButton, negButton, false, listener);
    }

    public AlertDialog Dialog(String title, String message, String posButton, String negButton, boolean showIcon, final onSingleButtonClickListner listener) {

        if (!title.isEmpty()) {
            this.alertDialog.setTitle(title);
        } else {
            this.alertDialog.setTitle(R.string.app_name);
        }

        if (!message.isEmpty()) {
            this.alertDialog.setMessage(message);
        } else {
            this.alertDialog.setMessage("");
        }

        if (showIcon) {
            this.alertDialog.setIcon(ContextCompat.getDrawable(context, R.mipmap.edbrix));
        }
        if (posButton.isEmpty()) {
            posButton = "OK";
        }
        if (negButton.isEmpty()) {
            negButton = "Cancel";
        }

        this.alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, posButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onPositiveClick();
                }
                dialog.dismiss();
            }
        });

        if (listener != null) {
            if (listener instanceof onTwoButtonClickListner) {
                this.alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, negButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((onTwoButtonClickListner) listener).onNegativeClick();
                        dialog.dismiss();
                    }
                });

            }
        }

        return alertDialog;
    }
}

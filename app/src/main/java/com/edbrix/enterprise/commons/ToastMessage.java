package com.edbrix.enterprise.commons;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edbrix.enterprise.R;


public class ToastMessage extends ViewGroup {

    Context mContext;

    public ToastMessage(Context context) {
        super(context);
        mContext = context;
    }

    public void showToastMsg(String message, int timeDuration) {
        try {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View toastLayout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.toast_layout_root));


            ImageView image = (ImageView) toastLayout.findViewById(R.id.image);
//            image.setImageResource(R.drawable.white);
            image.setVisibility(GONE);
            TextView text = (TextView) toastLayout.findViewById(R.id.text);
            text.setText(message);

            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            Toast toast = new Toast(mContext);
            toast.setGravity(Gravity.BOTTOM, 0, height / 4);
            toast.setDuration(timeDuration);
            toast.setView(toastLayout);
            toast.show();
        } catch (Exception e) {
//            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }

    }

	/*	public Dialog getCustomeDialog(String message){
        Dialog dialog = null;
		try 
		{
			dialog = new Dialog(mContext, android.R.style.Theme_Translucent);
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.custome_progressdialog,
			                               (ViewGroup)findViewById(R.id.cust_progress));
			dialog.setContentView(view);
			dialog.setCancelable(true);
			TextView text = (TextView) dialog.findViewById(R.id.txtProgressMsg);
			text.setText(message);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return dialog;
	}*/


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub

    }
}

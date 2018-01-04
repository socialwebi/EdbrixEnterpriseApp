package com.edbrix.enterprise.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.edbrix.enterprise.R;

/**
 * Created by rajk on 04/01/18.
 */

public class AddContentDialog extends Dialog {

    public static final String OPT_RECORD_VIDEO = "RECORD_VIDEO";
    public static final String OPT_ADD_VIDEO = "ADD_VIDEO";
    public static final String OPT_ADD_DOCUMENT = "ADD_DOCUMENT";

    private LinearLayout lnrRecordVideo;
    private LinearLayout lnrAddVideo;
    private LinearLayout lnrAddDoc;

    private OnActionButtonListener onActionButtonListener;

    public interface OnActionButtonListener {
        abstract void onOptionPressed(String optionType);
    }

    public AddContentDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_add_content);
        setCancelable(true);
    }

    public void setOnActionButtonListener(OnActionButtonListener listener) {
        this.onActionButtonListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lnrRecordVideo = (LinearLayout) findViewById(R.id.lnrRecordVideo);
        lnrAddVideo = (LinearLayout) findViewById(R.id.lnrAddVideo);
        lnrAddDoc = (LinearLayout) findViewById(R.id.lnrAddDoc);

        lnrRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionButtonListener.onOptionPressed(OPT_RECORD_VIDEO);
                dismiss();
            }
        });

        lnrAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionButtonListener.onOptionPressed(OPT_ADD_VIDEO);
                dismiss();
            }
        });

        lnrAddDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionButtonListener.onOptionPressed(OPT_ADD_DOCUMENT);
                dismiss();
            }
        });

//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
    }

    @Override
    public void onAttachedToWindow() {
        getWindow().setBackgroundDrawableResource(R.color.ColorBlackTransparent);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        super.onAttachedToWindow();
    }


    public void showMe() {
        this.show();
    }
}

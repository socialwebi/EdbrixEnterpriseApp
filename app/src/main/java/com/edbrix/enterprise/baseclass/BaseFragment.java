package com.edbrix.enterprise.baseclass;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.edbrix.enterprise.commons.GlobalMethods;
import com.edbrix.enterprise.commons.ToastMessage;


/**
 * Created by rajk
 */
public class BaseFragment extends Fragment {

//    private DialogManager dialogManager;
    private GlobalMethods globalMethods;
    private ToastMessage toastMessage;

    protected Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toastMessage = new ToastMessage(getContext());
//        dialogManager = new DialogManager(getContext());
//        globalMethods = new GlobalMethods();
        mContext = this.getContext();

    }

    protected void showToast(String msg, int timeDuration) {
        toastMessage.showToastMsg(msg, timeDuration);
    }

    protected void showToast(String msg) {
        toastMessage.showToastMsg(msg, Toast.LENGTH_LONG);
    }

    protected void onNetworkStatusChanged(boolean status) {

    }

    @Override
    public void onResume() {
        super.onResume();
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    protected AlertDialogManager getAlertDialogManager() {
//        return dialogManager.getAlertDialogManager();
//    }

    protected void pickImage(int requestCode, Uri fileUri) {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"); document intent

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Upload Icon Image");
        if (getGlobalMethods().isDeviceSupportCamera(getContext())) {
            Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{imageIntent});
        }

        try {
            startActivityForResult(chooserIntent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No image sources available", Toast.LENGTH_SHORT).show();
        }
    }

    protected GlobalMethods getGlobalMethods() {
        return globalMethods;
    }

//    protected void showBusyProgress() {
//        dialogManager.showBusyProgress();
//    }
//
//    protected void hideBusyProgress() {
//        dialogManager.hideBusyProgress();
//    }

    protected void requestReadWriteExternalStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GlobalMethods.external_storage_permission_request_code);
    }
//    protected void replaceFragment(Fragment fragment) {
//        globalMethods.replaceFragment(fragment, getActivity());
//    }


}

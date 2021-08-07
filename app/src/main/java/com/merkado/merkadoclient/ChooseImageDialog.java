package com.merkado.merkadoclient;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.merkado.merkadoclient.Interfaces.chooseImageInterface;
import com.merkado.merkadoclient.Views.MainActivity;
import com.merkado.merkadoclient.databinding.ChooseImageDialogBinding;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class ChooseImageDialog extends Dialog  implements View.OnClickListener {

    Context context;
    chooseImageInterface chooseImageInterface;

    public ChooseImageDialog(@NonNull Context context, chooseImageInterface chooseImageInterface) {
        super(context);
        this.context = context;
        this.chooseImageInterface = chooseImageInterface;
    }

    ChooseImageDialogBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = ChooseImageDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        attachButtonsToListener();



    }

    private void attachButtonsToListener() {
        binding.openCamera.setOnClickListener(this);
        binding.chooseFromGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int buttonId = v.getId();
        switch (buttonId){
            case R.id.open_camera:{
                chooseImageInterface.onOpenCameraPressed();
            } break;
            case R.id.choose_from_gallery:{
                chooseImageInterface.onOpenGalleryPressed();
            } break;
        }
    }



}

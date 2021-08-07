package com.merkado.merkadoclient.Views;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.merkado.merkadoclient.Adapters.PharmacyAdapter;
import com.merkado.merkadoclient.BuildConfig;
import com.merkado.merkadoclient.ChooseImageDialog;
import com.merkado.merkadoclient.Interfaces.chooseImageInterface;
import com.merkado.merkadoclient.Interfaces.setOnPharmacyItemClicked;
import com.merkado.merkadoclient.Model.PharmacyOrder;
import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.ViewModel.HomeViewModel;
import com.merkado.merkadoclient.databinding.ActivityPharmacyBinding;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.BitmapCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class PharmacyActivity extends AppCompatActivity implements View.OnClickListener,chooseImageInterface,OnCompleteListener {
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int ROCHETTA_CAMERA_REQUEST_ID = 200;
    private static final int ROCHETTA_GALLERY_REQUEST_ID = 300;
    private static final int THERAPY_CAMERA_REQUEST_ID = 400;
    private static final int THERAPY_GALLERY_REQUEST_ID = 500;
    ActivityPharmacyBinding binding;
    BottomSheetBehavior addRochettaSheet;
    BottomSheetBehavior addTherapySheet;
    ActivityResultLauncher<Intent> openCameraResultLauncher;
    ActivityResultLauncher<Intent> openGalleryResultLauncher;
    boolean isRochetta;
    Uri bottomSheetImageUri,cameraImageUri;
    private File output=null;
    StorageReference storage;
    FirebaseDatabase database;
    DatabaseReference reference;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    String userId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
    HomeViewModel homeViewModel;
    List<PharmacyOrder> cartOrders = new ArrayList<>();
    PharmacyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPharmacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle("الصيدلية");

        addRochettaSheet = BottomSheetBehavior.from(binding.addRochettaSheet);
        addTherapySheet  = BottomSheetBehavior.from(binding.addOneTherapySheet);

        addRochettaSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        addTherapySheet.setState(BottomSheetBehavior.STATE_HIDDEN);

        storage = FirebaseStorage.getInstance().getReference("Pharmacy");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Pharmacy Cart").child(userId);

        attachButtonsToListener();
        addTextWatcher();
        try {
            initViewModel();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        initPharmacyAdapter();
        getMyPharmacyCart();
        chooseImageDialog = new ChooseImageDialog(this, this);

        Tiny.getInstance().init(getApplication());

        openCameraResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if (isRochetta){
                                binding.addRochettaImage.setVisibility(View.GONE);
                                binding.rochettaImageContainer.setVisibility(View.VISIBLE);
                                setPic(binding.rochettaImage);
                            } else {
                                binding.addTherapyImage.setVisibility(View.GONE);
                                binding.therapyImageContainer.setVisibility(View.VISIBLE);
                                setPic(binding.therapyImage);
                            }
                            bottomSheetImageUri = galleryAddPic();
                            chooseImageDialog.dismiss();
                        }
                    }
                });
        openGalleryResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            if (isRochetta)
                                 binding.addRochettaImageProgress.setVisibility(View.VISIBLE);
                            else
                                binding.addTherapyImageProgress.setVisibility(View.VISIBLE);
                            if (data != null){
                                Uri choosenImageUri = data.getData();
                                if (isRochetta){
                                    binding.addRochettaImageProgress.hide();
                                    binding.addRochettaImage.setVisibility(View.GONE);
                                    binding.rochettaImageContainer.setVisibility(View.VISIBLE);
                                    binding.rochettaImage.setImageURI(choosenImageUri);
                                } else {
                                    binding.addTherapyImageProgress.hide();
                                    binding.addTherapyImage.setVisibility(View.GONE);
                                    binding.therapyImageContainer.setVisibility(View.VISIBLE);
                                    binding.therapyImage.setImageURI(choosenImageUri);
                                }
                                bottomSheetImageUri = choosenImageUri;

                                chooseImageDialog.dismiss();
                            } else {
                                FancyToast.makeText(PharmacyActivity.this, "لم يتم اختيار أي صورة!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    private void initPharmacyAdapter() {
        adapter = new PharmacyAdapter(cartOrders,this);
        binding.pharmacyOrdered.setAdapter(adapter);
    }

    private void getMyPharmacyCart() {
        binding.pharmacyOrderedLoading.show();
        homeViewModel.getMyPharmacyCart().observe(this, new Observer<List<PharmacyOrder>>() {
            @Override
            public void onChanged(List<PharmacyOrder> pharmacyOrders) {
                cartOrders.clear();
                binding.pharmacyOrderedLoading.hide();
                cartOrders.addAll(pharmacyOrders);
                adapter.notifyDataSetChanged();
                if (cartOrders.isEmpty()){
                    binding.goToCart.setVisibility(View.GONE);
                } else {
                    binding.goToCart.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private Uri galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        return contentUri;
    }

    public void initViewModel() throws ExecutionException, InterruptedException {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()).create(HomeViewModel.class);
        homeViewModel.init();
    }

    private void addTextWatcher() {
        binding.numberOfItems.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.numberOfItems.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void attachButtonsToListener() {
        binding.addRochetta.setOnClickListener(this);
        binding.addTherapy.setOnClickListener(this);
        binding.addRochettaImage.setOnClickListener(this);
        binding.addTherapyImage.setOnClickListener(this);
        binding.deleteRochettaImage.setOnClickListener(this);
        binding.deleteTherapyImage.setOnClickListener(this);
        binding.sendProduct.setOnClickListener(this);
        binding.sendRochetta.setOnClickListener(this);
        binding.goToCart.setOnClickListener(this);
    }

    ChooseImageDialog chooseImageDialog;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_rochetta:{
                    addRochettaSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    addTherapySheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                    bottomSheetImageUri = null;
                    binding.addTherapyImage.setVisibility(View.VISIBLE);
                    binding.therapyImageContainer.setVisibility(View.GONE);
            } break;
            case R.id.add_therapy:{
                    addRochettaSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                    addTherapySheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetImageUri = null;
                    binding.addRochettaImage.setVisibility(View.VISIBLE);
                    binding.rochettaImageContainer.setVisibility(View.GONE);

            } break;
            case R.id.add_rochetta_image:{
                isRochetta = true;
                chooseImageDialog.show();
            } break;
            case R.id.add_therapy_image: {
                isRochetta = false;
                chooseImageDialog.show();
            } break;
            case R.id.delete_rochetta_image:{
                bottomSheetImageUri = null;
                binding.addRochettaImage.setVisibility(View.VISIBLE);
                binding.rochettaImageContainer.setVisibility(View.GONE);
            } break;
            case R.id.delete_therapy_image:{
                bottomSheetImageUri = null;
                binding.addTherapyImage.setVisibility(View.VISIBLE);
                binding.therapyImageContainer.setVisibility(View.GONE);
            } break;
            case R.id.send_rochetta:{
                if (bottomSheetImageUri == null){
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("صورة الروشتة غير موجودة!");
                    alertDialog.setMessage("لا يمكن إضافة الروشتة والصورة غير موجودة..");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "حسنا",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    uploadImageAndData(bottomSheetImageUri);
                }
            } break;
            case R.id.send_product:{
                String therapyName = binding.therapyName.getEditText().toString().trim();

                if (bottomSheetImageUri == null&&therapyName.isEmpty()){
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("صورة المنتج واسمه غير موجودان!");
                    alertDialog.setMessage("من فضلك أضف صورة المنتج أو اسمه..");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "حسنا",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

                String numberOfItems = binding.numberOfItems.getEditText().toString().trim();



                if (bottomSheetImageUri != null ){
                    if (numberOfItems.isEmpty()){
                        binding.numberOfItems.setError("من فضلك اختار العدد المطلوب..");
                    } else {
                        uploadImageAndData(bottomSheetImageUri);
                    }
                } else if (!therapyName.isEmpty()){
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("صورة المنتج غير موجودة!");
                    alertDialog.setMessage("هل تريد طلب المنتج بدون صورة؟");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "نعم",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadData();
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            } break;
            case R.id.goToCart:{
                Intent intent = new Intent(PharmacyActivity.this,MainActivity.class);
                intent.putExtra("cart","cart");
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(PharmacyActivity.this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                startActivity(intent, options.toBundle());
            } break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    ProgressDialog progressDialog;
    private void uploadImageAndData(Uri bottomSheetImageUri) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جار التحميل ....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        String imageFileName;
        if (bottomSheetImageUri != null){
            imageFileName = FirebaseAuth.getInstance().getUid() + System.currentTimeMillis()+"." + getFileExtension(bottomSheetImageUri);
            final StorageReference fileReference = storage.child(imageFileName);
            uploadTask = fileReference.putFile(bottomSheetImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        uploadData(downloadUri,imageFileName);
                    } else {
//                        FancyToast.makeText(getApplicationContext(), "فشل في تحميل الصورة، برجاء المحاولة في وقت آخر"
//                                , FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();

                        Log.e("upload error",task.getException().getMessage());
                    }
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FancyToast.makeText(getApplicationContext(), "فشل في تحميل الصورة، برجاء المحاولة في وقت آخر"
                            , FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

                    progressDialog.dismiss();
                }
            });
        }
    }

    PharmacyOrder order;
    private void uploadData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جار التحميل ....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        String therapyName   = binding.therapyName.getEditText().getText().toString().trim();
        String numberOfItems = binding.numberOfItems.getEditText().getText().toString().trim();
        String describtion   = binding.therapyDescribtion.getEditText().getText().toString().trim();
        String itemType      = binding.typeOfItem.getSelectedItem().toString();
        boolean acceptAlternative = binding.acceptAlternative.isChecked();
        String pharmacyCartId = reference.push().getKey();

        order = new PharmacyOrder(userId,"",describtion,therapyName,numberOfItems,itemType,String.valueOf(acceptAlternative),"",pharmacyCartId);


        reference.push().setValue(order).addOnCompleteListener(this);

    }

    private void uploadData(Uri downloadUri,String imageFileName) {
        if (isRochetta){
            String describtion = binding.rochettaDescribtion.getEditText().getText().toString().trim();
            String pharmacyCartId = reference.push().getKey();
            order = new PharmacyOrder(userId,
                    downloadUri.toString(),
                    describtion,
                    "",
                    "",
                    "",
                    "",
                    imageFileName,
                    pharmacyCartId);

        } else {
            String therapyName   = binding.therapyName.getEditText().getText().toString().trim();
            String numberOfItems = binding.numberOfItems.getEditText().getText().toString().trim();
            String describtion   = binding.therapyDescribtion.getEditText().getText().toString().trim();
            String itemType      = binding.typeOfItem.getSelectedItem().toString();
            boolean acceptAlternative = binding.acceptAlternative.isChecked();
            String pharmacyCartId = reference.push().getKey();
            order  = new PharmacyOrder(userId,
                    downloadUri.toString(),
                    describtion,
                    therapyName,
                    numberOfItems,
                    itemType,
                    String.valueOf(acceptAlternative),
                    imageFileName,
                    pharmacyCartId);
        }
        reference.push().setValue(order).addOnCompleteListener(this);
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = Objects.requireNonNull(getApplicationContext()).getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        openGalleryResultLauncher.launch(intent);
        chooseImageDialog.dismiss();
    }

    private void openCamera() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onOpenCameraPressed() {
        openCamera();
    }

    @Override
    public void onOpenGalleryPressed() {
        openGallery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                dispatchTakePictureIntent();
            } break;
        }
    }

    private void setPic(ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getLayoutParams().width;
        int targetH = imageView.getLayoutParams().height;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1,
                Math.min(photoW/targetW,
                        photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
               openCameraResultLauncher.launch(takePictureIntent);
            }
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        progressDialog.dismiss();
        if (task.isSuccessful()){
            binding.rochettaImageContainer.setVisibility(View.GONE);
            binding.rochettaImage.setImageURI(null);
            binding.therapyImageContainer.setVisibility(View.GONE);
            binding.therapyImage.setImageURI(null);
            binding.numberOfItems.getEditText().setText("");
            binding.rochettaDescribtion.getEditText().setText("");
            binding.therapyDescribtion.getEditText().setText("");
            binding.addRochettaImage.setVisibility(View.VISIBLE);
            binding.addTherapyImage.setVisibility(View.VISIBLE);
            bottomSheetImageUri = null;
        } else {
            FancyToast.makeText(this,"فشل في إضافة المطلوب!",FancyToast.LENGTH_SHORT,FancyToast.CONFUSING,false).show();
        }

    }

}
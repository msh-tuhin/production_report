package com.productionreport.bd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.zelory.compressor.Compressor;
import myapp.utils.FirestoreFieldNames;
import myapp.utils.RealPathUtil;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddOrder extends AppCompatActivity {

    final int IMAGE_CHOOSE_REQUEST_CODE = 1;
    final int REQUEST_EXTERNAL_STORAGE_READ_PERM = 1;

    LinearLayout formLayout;
    LinearLayout progressLayout;
    TextInputEditText nameEditText;
    TextInputEditText quantityEditText;
    LinearLayout addImageLayout;
    ImageButton addImageButton;
    ImageButton deleteImage;
    ImageView photoIV;
    Button saveButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Uri uploadUri = null;
    private boolean imagePrevious = false;
    private boolean imageCurrent = false;
    private boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        formLayout = findViewById(R.id.form_layout);
        progressLayout = findViewById(R.id.progress_layout);
        nameEditText = findViewById(R.id.name);
        quantityEditText = findViewById(R.id.quantity);
        addImageLayout = findViewById(R.id.add_image_layout);
        addImageButton = findViewById(R.id.add_image);
        deleteImage = findViewById(R.id.delete_image);
        photoIV = findViewById(R.id.photo);
        saveButton = findViewById(R.id.save_button);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.i("Permission", "External storage : denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_READ_PERM);
        }else{
            // permission granted
            // TODO maybe nothing
        }

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setEnabled(!s.toString().trim().isEmpty());
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntentForImage();
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUri = null;
                photoIV.setImageResource(R.drawable.ltgray);
                deleteImage.setClickable(false);
                deleteImage.setVisibility(View.INVISIBLE);
                addImageLayout.setVisibility(View.VISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("saving", "clicked");
                saveButton.setEnabled(false);
                Log.i("saving", "order");
                if(shoulCoverPhotoBeSaved()){
                    Log.i("saving", "with_photo");
                    uploadPhotoAndUpdateDB(uploadUri);
                }else{
                    Log.i("saving", "without_photo");
                    createVital(null);
                }
                formLayout.setVisibility(View.INVISIBLE);
                progressLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_EXTERNAL_STORAGE_READ_PERM){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // permission granted
                // TODO maybe nothing
            }else{
                // permission denied
                // TODO launch next activity
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri contentUri;
        if(resultCode == Activity.RESULT_OK ){
            if(requestCode == IMAGE_CHOOSE_REQUEST_CODE){
                if(data.getClipData() != null){
                    Log.i("image-source", "clipdata");
                    ClipData clipData = data.getClipData();
                    ClipData.Item item = clipData.getItemAt(0);
                    contentUri = item.getUri();
                    Log.i("Content-Uri", contentUri.toString());
                }else {
                    Log.i("image-source", "data");
                    contentUri = data.getData();
                    Log.i("Content-Uri", contentUri.toString());
                }
                String path = getPathFromContentUri(contentUri);
                Uri uri = Uri.fromFile(new File(path));
                Log.i("Uri", uri.toString());

                try{
                    File compressedFile = new Compressor(this).compressToFile(new File(path));
                    Uri compressedFileUri = Uri.fromFile(compressedFile);
                    Log.i("compressed_uri", compressedFileUri.toString());
                    uploadUri = compressedFileUri;
                    photoIV.setImageURI(compressedFileUri);
                    imageChanged = true;
                    imageCurrent = true;

                    //imageSourceChooser.setVisibility(View.INVISIBLE);
                    addImageLayout.setVisibility(View.INVISIBLE);
                    deleteImage.setClickable(true);
                    deleteImage.setVisibility(View.VISIBLE);
                }catch (IOException e){

                    // TODO handle the unsuccessful image compression
                    // TODO maybe show a dialog(cant compress the selected image)

                    Log.e("compression-error", e.getMessage());
                    // profilePicture.setImageURI(uri);
                }

                String msg = data.getClipData() != null ? "not null" : "null";
                Log.i("CLIPDATA", msg);
            }
        }
    }

    private boolean shoulCoverPhotoBeSaved(){
        if(imageChanged){
            return (imagePrevious || imageCurrent);
        }
        return false;
    }

    private String getPathFromContentUri(Uri contentUri){
        String path = RealPathUtil.getRealPath(this, contentUri);
        Log.i("PATH", path);
        return path;
    }

    private void uploadPhotoAndUpdateDB(Uri uploadUri){
        if(uploadUri != null){
            Log.i("upload_uri", "not null");
            Date now = new Date();
            String timestampString = String.valueOf(now.getTime()) + "_";
            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference storageReference = storage.getReference()
                    .child("order-pictures").child(timestampString + uploadUri.getLastPathSegment());
            UploadTask uploadTask = storageReference.putFile(uploadUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    createVital(uri);
                }
            }).addOnFailureListener(AddOrder.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressLayout.setVisibility(View.INVISIBLE);
                    formLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(AddOrder.this, "Couldn't upload image!",
                            Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.i("upload_uri", "null");
            progressLayout.setVisibility(View.INVISIBLE);
            formLayout.setVisibility(View.VISIBLE);
            Toast.makeText(AddOrder.this, "Update Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void sendIntentForImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        // choose only from local images
        // not working!
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//        if(Build.VERSION.SDK_INT >= 18) {
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        }
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_CHOOSE_REQUEST_CODE);
        }
    }

    private void createVital(Uri uri){
        Long quantity = Long.valueOf(quantityEditText.getText().toString().trim());
        Map<String, Object> orderData = new HashMap<>();
        orderData.put(FirestoreFieldNames.ORDER_NAME, nameEditText.getText().toString().trim());
        orderData.put(FirestoreFieldNames.ORDER_QUANTITY, quantity);
        if(uri != null){
            orderData.put(FirestoreFieldNames.ORDER_PICTURE, uri.toString());
        }
        orderData.put(FirestoreFieldNames.ORDER_ADD_TIME, new Date());
        orderData.put(FirestoreFieldNames.ORDER_CREATOR, mAuth.getCurrentUser().getUid());
        db.collection("orders")
                .add(orderData)
                .addOnSuccessListener(AddOrder.this, new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("dish_creation", "successful");
                        AddOrder.this.finish();
                    }
                }).addOnFailureListener(AddOrder.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("dish_creation", "failed");
                formLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
                Toast.makeText(AddOrder.this, "Order creation failed! Try again.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

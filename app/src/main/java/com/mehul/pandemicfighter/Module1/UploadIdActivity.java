package com.mehul.pandemicfighter.Module1;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mehul.pandemicfighter.Module3.User;
import com.mehul.pandemicfighter.R;
import at.markushi.ui.CircleButton;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class UploadIdActivity extends AppCompatActivity {
    private EditText address,state,district;
    private ImageView imageView;
    private ImageView choose, upload;
    private CircleButton circleButton;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    // private ProgressBar progressBar;
    private Uri ImageUri;
    private long time = System.currentTimeMillis();
//    private LatLng latLng;
//    private String MOB_NUMBER="ashu";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_upload);
        address = findViewById(R.id.editText6);
        state = findViewById(R.id.state);
        district = findViewById(R.id.district);
        imageView = findViewById(R.id.imageView);
        // progressBar = findViewById(R.id.progressBar2);
        choose = findViewById(R.id.button2);
        upload = findViewById(R.id.button4);
//        circleButton = findViewById(R.id.goBack);
//        Intent intent = getIntent();
//        //MOB_NUMBER = intent.getStringExtra("MOB_NUMBER");
//        double[] location=intent.getDoubleArrayExtra("Location");
//        assert location != null;
//        latLng=new LatLng(location[0],location[1]);

        Bundle bundle = getIntent().getExtras();
        user = bundle.getParcelable("Module3.UserDetails");

        FirebaseApp.initializeApp(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        choose.setOnClickListener(view -> openFileChooser());
        upload.setOnClickListener(view -> uploadFile());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImageUri = data.getData();
            imageView.setImageURI(ImageUri);
            imageView.setBackgroundColor(Color.WHITE);
        }
    }

    private void uploadFile()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if(ImageUri!=null){
            StorageReference fileReference = storageReference.child(time+"."+getFileExtension(ImageUri));
            fileReference.putFile(ImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();

                        storageReference.child(time+"."+getFileExtension(ImageUri)).getDownloadUrl().addOnSuccessListener(uri -> {
                            String url = uri.toString();
//                            Food food = new Food(address.getText().toString().trim(), latLng.latitude,latLng.longitude,suffFor.getText().toString().trim(),url);
//                            String uploadID = time+"";
//                            databaseReference.child(uploadID).setValue(food);

                            String addressText=address.getText().toString();
                            String stateText=state.getText().toString().toLowerCase();
                            String districtText=district.getText().toString().toLowerCase();
                            if(addressText.isEmpty() || stateText.isEmpty() || districtText.isEmpty()){
                                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Please fill all the details!")
                                        .show();
                            }
                            else{
                                // add requested user's aadhaar number in admin mode
                                databaseReference.child("Users").child(stateText).child(districtText).child("admin").child("requests").child(user.getAadharNumber()).setValue(url);

                                // create normal user but in address add -9999 infront to know verification status
                                user.setAddress("-9999"+addressText);
                                databaseReference.child("Users").child(stateText).child(districtText).child(user.getAadharNumber()).setValue(user);

                                new SweetAlertDialog(UploadIdActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Admin Verification Pending")
                                        .setContentText("User registered!")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                Intent i=new Intent(UploadIdActivity.this, LoginActivity.class);
                                                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(i);
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        new SweetAlertDialog(UploadIdActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Upload failed!")
                                .setContentText("Something went wrong!")
                                .show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded "+ (int)progress + "%...");
                    });
        }
        else
        {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("No image selected!")
                    .show();
        }
    }

//    public void createAndShare(View view) {
//        Intent x = new Intent(UploadIdActivity.this, MapsMarkLocation.class);
//        startActivity(x);
//        finish();
//    }
}
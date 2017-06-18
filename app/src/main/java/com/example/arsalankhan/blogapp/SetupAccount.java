package com.example.arsalankhan.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupAccount extends AppCompatActivity {

    private EditText editTextName;
    private ImageButton profileImageBtn;
    private Button submitBtn;

    private static final int Gallery_Request_CODE=1;

    private Uri imageUri=null;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;
    private StorageReference mStorageUser;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        editTextName= (EditText) findViewById(R.id.setupNameField);

        mDatabaseUser= FirebaseDatabase.getInstance().getReference().child("User");
        mStorageUser= FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);

        profileImageBtn= (ImageButton) findViewById(R.id.setupImageBtn);

        submitBtn= (Button) findViewById(R.id.setupSubmitBtn);

        // for picking the profile image from gallery
        profileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent= new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                Intent chooser=Intent.createChooser(galleryIntent,"Pick image from ....");
                startActivityForResult(chooser,Gallery_Request_CODE);
            }
        });

        //for submitting the profile setup
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storeProfileData();

            }
        });
    }

    private void storeProfileData() {

        final String name= editTextName.getText().toString().trim();
        if(!TextUtils.isEmpty(name) && imageUri!=null){

            progressDialog.setMessage("Setup the profile");
            progressDialog.show();
            StorageReference filepath=mStorageUser.child("profile_images").child(imageUri.getLastPathSegment());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final String downloadImageUri=taskSnapshot.getDownloadUrl().toString();

                    String userId=mAuth.getCurrentUser().getUid();
                    DatabaseReference databaseReference= mDatabaseUser.child(userId);

                    databaseReference.child("name").setValue(name);
                    databaseReference.child("image").setValue(downloadImageUri);

                    progressDialog.dismiss();

                    Intent mainIntent=new Intent(SetupAccount.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Request_CODE && resultCode==RESULT_OK){
            Uri imageUri=data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                profileImageBtn.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

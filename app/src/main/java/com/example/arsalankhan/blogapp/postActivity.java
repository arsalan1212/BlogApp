package com.example.arsalankhan.blogapp;

import android.app.Activity;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class postActivity extends AppCompatActivity {

    private ImageButton mselectImageBtn;
    private EditText et_title, et_description;
    private Button submitBtn;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri=null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        et_title = (EditText) findViewById(R.id.editTextTitle);
        et_description = (EditText) findViewById(R.id.editTextPostDescription);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Blog");
        storageReference= FirebaseStorage.getInstance().getReference();

        progressDialog=new ProgressDialog(this);

        mselectImageBtn = (ImageButton) findViewById(R.id.imageButtonAdd);
        mselectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent, "Open with ..."),IMAGE_REQUEST);
                CropImage.startPickImageActivity(postActivity.this);
            }
        });

        //submit button click listener
        submitBtn = (Button) findViewById(R.id.buttonSubmit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postBlog();
            }
        });
    }


    //posting the the image, title and description into database and storage
    private void postBlog() {
        final String title_val=et_title.getText().toString().trim();
        final String description_val=et_description.getText().toString().trim();

        //showing the progress dialog
        progressDialog.setMessage("Posting to Blog....");
        progressDialog.setCancelable(false);


        //storing the image into firbase storage
        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(description_val) && imageUri!=null){

            progressDialog.show();

            StorageReference filepath=storageReference.child("blog_images").child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri=taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost=databaseReference.push();

                    newPost.child("title").setValue(title_val);
                    newPost.child("description").setValue(description_val);
                    newPost.child("image").setValue(downloadUri.toString());

                    progressDialog.dismiss();

                    startActivity(new Intent(postActivity.this,MainActivity.class));
                }
            });
        }
        else{
            Toast.makeText(this, "Please Fill the Fields", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK){
//            Uri imageUri=data.getData();
//            mselectImageBtn.setImageURI(null);
//            mselectImageBtn.setImageURI(imageUri);
//
//        }

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            startCropImageActivity(imageUri);

        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mselectImageBtn.setImageURI(result.getUri());
                imageUri = result.getUri();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }
}



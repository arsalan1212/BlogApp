package com.example.arsalankhan.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginUser extends AppCompatActivity {


    private EditText editTextUserName, editTextPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        editTextUserName= (EditText) findViewById(R.id.LoginUserNameField);
        editTextPassword= (EditText) findViewById(R.id.LoginPasswordField);

        progressDialog=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("User");
        mDatabaseUser.keepSynced(true);
    }


    //User Login

    public void UserLogin(View view){

        String username=editTextUserName.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();


        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){

            progressDialog.setMessage("Signing Up.....");
            progressDialog.show();
            //Login user

            mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        CheckUserExist();

                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginUser.this, "Signing Error....", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else{
            Toast.makeText(this, "Fill the Fields.....", Toast.LENGTH_SHORT).show();

        }



    }

    private void CheckUserExist() {

        final String userId=mAuth.getCurrentUser().getUid();

        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userId)){

                    Intent intent=new Intent(LoginUser.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }else{

                    Intent intent=new Intent(LoginUser.this,SetupAccount.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //User Register Btn
    public void UserRegister(View view){

        Intent intent=new Intent(LoginUser.this,RegisterUser.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}

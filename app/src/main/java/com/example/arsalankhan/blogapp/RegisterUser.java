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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private EditText editTextName,editTextEmail,editTextPassword;

    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth=FirebaseAuth.getInstance();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("User");

        progressDialog=new ProgressDialog(this);

        editTextName= (EditText) findViewById(R.id.RegisterUserName);
        editTextEmail= (EditText) findViewById(R.id.RegisterUserEmail);
        editTextPassword= (EditText) findViewById(R.id.RegisterUserPassword);

    }


    public void RegisterUser(View view){

        final String name= editTextName.getText().toString().trim();
        String email= editTextEmail.getText().toString().trim();
        String password= editTextPassword.getText().toString().trim();

        progressDialog.setMessage("Registering User.....");

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        String UserID= mAuth.getCurrentUser().getUid();

                        DatabaseReference databaseReference=mDatabase.child(UserID);

                        databaseReference.child("name").setValue(name);
                        databaseReference.child("image").setValue("default");

                        progressDialog.dismiss();

                        Intent RegisterIntent= new Intent(RegisterUser.this,LoginUser.class);
                        RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(RegisterIntent);
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(RegisterUser.this, "User Registrating Error...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "Fill the Fields...", Toast.LENGTH_SHORT).show();
        }

    }
}

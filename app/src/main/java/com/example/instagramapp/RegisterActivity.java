package com.example.instagramapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText userame, fullname, email, password;
    Button register;
    TextView txt_login;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userame=findViewById(R.id.username);
        fullname=findViewById(R.id.fullname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register);
        txt_login=findViewById(R.id.txt_login);

        auth=FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog=new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                String str_username=userame.getText().toString();
                String str_fullname=fullname.getText().toString();
                String str_email=email.getText().toString();
                String str_password=password.getText().toString();

                if (TextUtils.isEmpty(str_username)||TextUtils.isEmpty(str_fullname)||TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password)){
                    Toast.makeText(getApplicationContext(),"All fields are required!",Toast.LENGTH_LONG).show();
                }
                else if (str_password.length()<6){
                    Toast.makeText(getApplicationContext(),"password must contain atleast 6 characters",Toast.LENGTH_LONG).show();
                }
                else {
                        register(str_username,str_fullname,str_email,str_password);
                }

            }
        });
    }
    private void register(final String username, final String fullname, String email, String password){
    auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser=auth.getCurrentUser();
                        String userid=firebaseUser.getUid();

                        reference= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                        HashMap<String ,Object> hashMap=new HashMap<>();
                        hashMap.put("id",userid);
                        hashMap.put("username",username.toLowerCase());
                        hashMap.put("fullname",fullname);
                        hashMap.put("bio","");
                        hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/instagramapp-6bc0a.appspot.com/o/images.png?alt=media&token=2adf5358-a900-4e00-bf46-e1927556eebd");
                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        });


                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"You can't register with this email and password ",Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
}
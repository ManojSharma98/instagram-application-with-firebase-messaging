package com.example.instagramapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramapp.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    ImageView close,image_profile;
    TextView save,tv_change;
    MaterialEditText fullname,username,bio;

    FirebaseUser firebaseUser;
    private Uri mImageUri;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close=findViewById(R.id.close);
        image_profile=findViewById(R.id.image_profile);
        save=findViewById(R.id.save);
        tv_change=findViewById(R.id.tv_change);
        fullname=findViewById(R.id.fullname);
        username=findViewById(R.id.username);
        bio=findViewById(R.id.bio);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("uploads");

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user=snapshot.getValue(User.class);
                    assert user != null;
                    fullname.setText(user.getFullname());
                    username.setText(user.getUsername());
                    bio.setText(user.getBio());

                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            tv_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CropImage.activity()
                            .setAspectRatio(1,1)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .start(EditProfileActivity.this);


                }
            });
            image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CropImage.activity()
                            .setAspectRatio(1,1)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .start(EditProfileActivity.this);

                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateProfile(fullname.getText().toString(),
                    username.getText().toString(),
                            bio.getText().toString());

                }
            });
    }

    private void updateProfile(String fullname, String username, String bio) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("fullname",fullname);
        hashMap.put("username",username);
        hashMap.put("bio",bio);

        reference.updateChildren(hashMap);

        finish();

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
    private void uploadImage(){
        ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("uploading...");
        pd.show();
        if (mImageUri!=null){
            StorageReference filerefernce=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));

            StorageTask uploadTask = filerefernce.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();

                    }
                    return filerefernce.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        assert downloadUri != null;
                        String myUrl=downloadUri.toString();

                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("imageurl",""+myUrl);
                        reference.updateChildren(hashMap);
                        pd.dismiss();

                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Failed to uplaod",Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }else {
            Toast.makeText(getApplicationContext(),"No image selected",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            mImageUri=result.getUri();

            uploadImage();
        }
        else {
            Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_LONG).show();

        }
    }
}
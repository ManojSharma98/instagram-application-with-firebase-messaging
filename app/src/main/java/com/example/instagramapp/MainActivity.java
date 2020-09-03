package com.example.instagramapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.instagramapp.Fragment.HomeFragment;
import com.example.instagramapp.Fragment.NotificationFragment;
import com.example.instagramapp.Fragment.ProfileFragment;
import com.example.instagramapp.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    Fragment selectedFragment=null;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent=getIntent().getExtras();
        if (intent!=null){

            String publisher=intent.getString("publisherid");
            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();


        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }

    public BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener= menuItem -> {

        switch (menuItem.getItemId()){

            case R.id.nav_home:
                    selectedFragment=new HomeFragment();
                break;

            case R.id.nav_message:
                selectedFragment=new SearchFragment();
                break;


            case R.id.nav_add:
                selectedFragment=null;
                startActivity(new Intent(MainActivity.this,PostActivity.class));
                finish();
                break;


            case R.id.nav_heart:
                selectedFragment=new NotificationFragment();
                break;


            case R.id.nav_profile:

                SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                editor.putString("profileid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                editor.apply();
                selectedFragment=new ProfileFragment();
                break;

        }
        if (selectedFragment!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
        }
        return true;
    };
}
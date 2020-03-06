package com.example.coen_elec_390_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    EditText name, gender, height, weight;
    Button save, edit;
    Spinner genderSelect;
    DatabaseReference reff, reffname, reffgender, reffheight, reffweight;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpBottomNavigationView();

        name = findViewById(R.id.profileName);
        gender = findViewById(R.id.profileGender);
        genderSelect = findViewById(R.id.profileSelectGender);
        height = findViewById(R.id.profileHeight);
        weight = findViewById(R.id.profileWeight);
        save = findViewById(R.id.save);
        edit = findViewById(R.id.edit);

        name.setEnabled(false);
        gender.setEnabled(false);
        height.setEnabled(false);
        weight.setEnabled(false);
        genderSelect.setEnabled(false);
        gender.setVisibility(View.VISIBLE);
        genderSelect.setVisibility(View.INVISIBLE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reff = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fileName = dataSnapshot.child("Fullname").getValue(String.class);
                String fileGender = dataSnapshot.child("Gender").getValue(String.class);
                String fileHeight = dataSnapshot.child("Height").getValue(String.class);
                String fileWeight = dataSnapshot.child("Weight").getValue(String.class);

                name.setText(fileName);
                height.setText(fileHeight);
                weight.setText(fileWeight);
                gender.setText(fileGender);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // catch error
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(false);
                gender.setEnabled(false);
                height.setEnabled(false);
                weight.setEnabled(false);
                genderSelect.setEnabled(false);
                gender.setVisibility(View.VISIBLE);
                genderSelect.setVisibility(View.INVISIBLE);

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(true);
                gender.setEnabled(true);
                height.setEnabled(true);
                weight.setEnabled(true);
                genderSelect.setEnabled(true);
                gender.setVisibility(View.INVISIBLE);
                genderSelect.setVisibility(View.VISIBLE);

                // TODO: hashmap
                //      using one ref obj


                reffname = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("Fullname");
//                reffgender = FirebaseDatabase.getInstance().getReference().child("Users").child("4CVTBgvTdOV5hJnY7783hrNKjOA3").child("Gender");
//                reffheight = FirebaseDatabase.getInstance().getReference().child("Users").child("4CVTBgvTdOV5hJnY7783hrNKjOA3").child("Height");
//                reffweight = FirebaseDatabase.getInstance().getReference().child("Users").child("4CVTBgvTdOV5hJnY7783hrNKjOA3").child("Weight");

                    reffname.setValue("john");


            }
        });
    }


    private void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        break;

                    case R.id.profile:
                        break;

                    case R.id.statistics:
                        startActivity(new Intent(ProfileActivity.this, StatisticsActivity.class));
                        break;


                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                }

                return false;
            }
        });
    }
}

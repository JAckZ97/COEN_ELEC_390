package com.example.coen_elec_390_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    EditText name, gender, height, weight;
    Button save, edit, profileSaveButton;
    Spinner genderSelect;
    String selectGender;
    DatabaseReference reff;
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
        profileSaveButton = findViewById(R.id.profileSaveButton);

        name.setEnabled(false);
        gender.setEnabled(false);
        height.setEnabled(false);
        weight.setEnabled(false);
        genderSelect.setEnabled(false);
        gender.setVisibility(View.VISIBLE);
        genderSelect.setVisibility(View.INVISIBLE);
        profileSaveButton.setVisibility(View.INVISIBLE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reff = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        basicRead();


        genderSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectGender=parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ProfileActivity.this, "Gender is not selected. ", Toast.LENGTH_SHORT).show();

            }
        });


        profileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                basicWrite();

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
                profileSaveButton.setVisibility(View.INVISIBLE);

                basicRead();

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
                profileSaveButton.setVisibility(View.VISIBLE);

            }
        });
    }

    public void basicRead(){
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
    }

    private void basicWrite () {
        String userid = firebaseUser.getUid();
        String str_fullname = name.getText().toString();
        String str_gender = selectGender;
        String str_weight = weight.getText().toString();
        String str_height = height.getText().toString();

        HashMap<String, Object> updateresult = new HashMap<>();
        updateresult.put("Id", userid);
        updateresult.put("Fullname", str_fullname);
        updateresult.put("Gender", str_gender);
        updateresult.put("Weight", str_weight);
        updateresult.put("Height", str_height);
        updateresult.put("Imageur", "https://firebasestorage.googleapis.com/v0/b/coen-elec-390-98dd3.appspot.com/o/placeholder.png?alt=media&token=deb0ea3a-dc94-4093-a187-19590f61894b");

        reff.setValue(updateresult);
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

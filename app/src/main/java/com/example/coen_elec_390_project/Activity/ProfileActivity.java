package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.R;
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
    EditText fullname, height, weight, age;
    Button save, edit;
    Spinner genderSelect;
    DatabaseHelper databaseHelper;
    RadioButton cm, ft, kg, lb;
    RadioGroup weightGroup, heightGroup;
    String email;
    String selectGender;
    ProgressDialog pd;
    ProgressDialog newPd;
    //DatabaseReference reff;
    //FirebaseUser firebaseUser;
    //User user;
    private boolean user_editting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpBottomNavigationView();

        fullname = findViewById(R.id.profileName);
        height = findViewById(R.id.profileHeight);
        weight = findViewById(R.id.profileWeight);
        age = findViewById(R.id.profileAge);
        genderSelect = findViewById(R.id.profileSelectGender);
        save = findViewById(R.id.save);
        edit = findViewById(R.id.edit);
        cm = findViewById(R.id.heightCm);
        ft = findViewById(R.id.heightFeet);
        kg = findViewById(R.id.weightKG);
        lb = findViewById(R.id.weightLB);
        weightGroup = findViewById(R.id.weightRadioGroup);
        heightGroup = findViewById(R.id.heightRadioGroup);

        fullname.setEnabled(false);
        height.setEnabled(false);
        age.setEnabled(false);
        weight.setEnabled(false);
        genderSelect.setEnabled(false);
        cm.setEnabled(false);
        ft.setEnabled(false);
        kg.setEnabled(false);
        lb.setEnabled(false);

        email = getIntent().getStringExtra("email");
        databaseHelper = new DatabaseHelper(this);
        final User user = databaseHelper.getUser(email);
        fullname.setText(user.getFullname());
        age.setText(user.getAge());
        height.setText(user.getHeight());
        weight.setText(user.getWeight());

        if(user.getHeightUnit()==1){ kg.setChecked(true); lb.setChecked(false);}
        else{ lb.setChecked(true); kg.setChecked(false);}
        if(user.getWeightUnit()==1){ cm.setChecked(true); ft.setChecked(false);}
        else{ ft.setChecked(true); cm.setChecked(false);}

        // set gender spinner and catch error
        if(user.getGender() == null){
            genderSelect.setSelection(2);
        }
        else {
            genderSelect.setSelection(genderGenerate(user.getGender()));
        }

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

        /**firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
        });*/

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullname.setEnabled(false);
                height.setEnabled(false);
                weight.setEnabled(false);
                genderSelect.setEnabled(false);
                age.setEnabled(false);
                cm.setEnabled(false);
                ft.setEnabled(false);
                kg.setEnabled(false);
                lb.setEnabled(false);

                if(user_editting){
                    writeProfile(user);
                    readProfile();
                    user_editting=false;
                }

//                basicRead();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullname.setEnabled(true);
                height.setEnabled(true);
                weight.setEnabled(true);
                genderSelect.setEnabled(true);
                age.setEnabled(true);
                cm.setEnabled(true);
                ft.setEnabled(true);
                kg.setEnabled(true);
                lb.setEnabled(true);
                user_editting=true;
//               writeProfile();
            }
        });
    }

    /**
    public void basicRead(){
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fileName = dataSnapshot.child("Fullname").getValue(String.class);
                String fileGender = dataSnapshot.child("Gender").getValue(String.class);
                String fileHeight = dataSnapshot.child("Height").getValue(String.class);
                String fileWeight = dataSnapshot.child("Weight").getValue(String.class);

                fullname.setText(fileName);
                height.setText(fileHeight);
                weight.setText(fileWeight);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // catch error
            }
        });
    }

    private void basicWrite () {
        String userid = firebaseUser.getUid();
        String str_fullname = fullname.getText().toString();
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
    }*/

    // TODO: heightUnit cm: 1/ ft: 0
    //       weightUnit kg: 1/ lb: 0

    private void readProfile(){
        String email = getIntent().getStringExtra("email");
        databaseHelper = new DatabaseHelper(this);
        User user = databaseHelper.getUser(email);

        fullname.setText(user.getFullname());
        genderSelect.setSelection(genderGenerate(user.getGender()));
        age.setText(user.getAge());
        height.setText(user.getHeight());
        weight.setText(user.getWeight());

        if(user.getWeightUnit()==1){
            kg.setChecked(true); }
        else{
            lb.setChecked(true); }

        if(user.getHeightUnit()==1){
            cm.setChecked(true); }
        else{
            ft.setChecked(true); }
    }


    private void writeProfile(User user) {

        String str_fullname = fullname.getText().toString();
        String str_gender = selectGender;
        String str_age = age.getText().toString();
        String str_weight = weight.getText().toString();
        String str_height = height.getText().toString();

        user.setFullname(str_fullname);
        user.setGender(str_gender);
        user.setAge(str_age);
        user.setWeight(str_weight);
        user.setHeight(str_height);

        int checkWeightId = weightGroup.getCheckedRadioButtonId();
        int checkHeightId = heightGroup.getCheckedRadioButtonId();


//        findRadioButtonHeight(checkHeightId);
//        findRadioButtonWeight(checkWeightId);
        //Log.e("Tag","<PROFILE> weight id" + checkWeightId);
        //Log.e("Tag","<PROFILE> height id" + checkHeightId);
        switch (checkWeightId){
            case R.id.weightKG:
                kg.setChecked(true);
                lb.setChecked(false);
                user.setWeightUnit(1);
                //Log.e("Tag","<PROFILE> kg checked");

                break;
            case R.id.weightLB:
                lb.setChecked(true);
                kg.setChecked(false);
                user.setWeightUnit(0);
                //Log.e("Tag","<PROFILE> lb checked");
                break;
        }

        switch (checkHeightId){
            case R.id.heightCm:
                cm.setChecked(true);
                ft.setChecked(false);
                user.setHeightUnit(1);
                //Log.e("Tag","<PROFILE> cm checked");
                break;
            case R.id.heightFeet:
                ft.setChecked(true);
                cm.setChecked(false);
                user.setHeightUnit(0);
                //Log.e("Tag","<PROFILE> ft checked");
                break;
        }

        //Log.e("Tag","<PROFILE> Weight unit "+ user.getWeightUnit());
        //Log.e("Tag","<PROFILE> Height unit "+ user.getHeightUnit());

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.updateProfile(user);



    }

    private int genderGenerate (String selectGender){
        switch (selectGender){
            case "Male":
                return 0;

            case "Female":
                return 1;

            case "Other":
                return 2;
        }
        return 2;
    }

    private void findRadioButtonWeight (int checkID){
        pd = new ProgressDialog(ProfileActivity.this);
        String email = getIntent().getStringExtra("email");
        databaseHelper = new DatabaseHelper(this);
        User user = databaseHelper.getUser(email);

        switch (checkID){
            case R.id.weightKG:
                Toast.makeText(ProfileActivity.this, "Selected kg", Toast.LENGTH_SHORT).show();
                kg.setChecked(true);
                pd.dismiss();

                user.setWeightUnit(1);

                break;
            case R.id.weightLB:
                Toast.makeText(ProfileActivity.this, "Selected lb", Toast.LENGTH_SHORT).show();
                lb.setChecked(true);
                pd.dismiss();

                user.setWeightUnit(0);

                break;
        }
    }

    private void findRadioButtonHeight (int checkID){
        newPd = new ProgressDialog(ProfileActivity.this);
        String email = getIntent().getStringExtra("email");
        databaseHelper = new DatabaseHelper(this);
        User user = databaseHelper.getUser(email);

        switch (checkID){
            case R.id.heightCm:
                Toast.makeText(ProfileActivity.this, "Selected cm", Toast.LENGTH_SHORT).show();
                cm.setChecked(true);
                newPd.dismiss();

                user.setHeightUnit(1);

                break;
            case R.id.heightFeet:
                Toast.makeText(ProfileActivity.this, "Selected ft", Toast.LENGTH_SHORT).show();
                ft.setChecked(true);
                newPd.dismiss();

                user.setHeightUnit(0);

                break;
        }
    }

    private void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()){
                    case R.id.home:
                        intent = new Intent(new Intent(ProfileActivity.this, MainActivity.class));
                        intent.putExtra("email", email);
                        startActivity(intent);
                        break;

                    case R.id.statistics:
                        intent = new Intent(new Intent(ProfileActivity.this, StatisticsActivity.class));
                        intent.putExtra("email", email);
                        startActivity(intent);
                        break;

                    case R.id.profile:
                        break;

                    case R.id.logout:
//                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                }

                return false;
            }
        });
    }
}

package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
import com.example.coen_elec_390_project.Model.Statistic;
import com.example.coen_elec_390_project.Model.Temp;
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

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class ProfileActivity extends AppCompatActivity  {
    EditText fullname, height, weight, age, heightFeet, heightInch;
    Button save, edit;
    Spinner genderSelect;
    DatabaseHelper databaseHelper;
    RadioButton cm, ft, kg, lb;
    RadioGroup weightGroup, heightGroup;
    String email;
    String selectGender;
    ProgressDialog pd;
    DatabaseReference reff;
    FirebaseUser firebaseUser;
    double tempFeet, tempInch = 0;
    double tempHeight = 0;
    private boolean user_editting = false;
    private Boolean insert_temp=false;
    public static int dev_count=0;
    int indexOfDecimal;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpBottomNavigationView();
        fullname = findViewById(R.id.profileName);
        height = findViewById(R.id.profileHeight);
        heightFeet = findViewById(R.id.profileHeightFeet);
        heightInch = findViewById(R.id.profileHeightInch);
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
        heightFeet.setEnabled(false);
        heightInch.setEnabled(false);

        cm.setEnabled(false);
        ft.setEnabled(false);
        kg.setEnabled(false);
        lb.setEnabled(false);
        databaseHelper = new DatabaseHelper(this);
        boolean tutorial = getIntent().getBooleanExtra("tutorial",false);
        email = getIntent().getStringExtra("email");
        user = databaseHelper.getUser(email);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null)
            reff = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
      
        if(!tutorial){
            Bundle bundle = getIntent().getExtras();
            insert_temp = bundle.getBoolean("temp",false);


            fullname.setText(user.getFullname());
            age.setText(user.getAge());
            weight.setText(user.getWeight());


            if(user.getWeightUnit()==1){
                kg.setChecked(true);
                lb.setChecked(false);

            }
            else{
                lb.setChecked(true);
                kg.setChecked(false);
            }

            if(user.getHeightUnit()==1){
                cm.setChecked(true);
                height.setVisibility(View.VISIBLE);
                heightFeet.setVisibility(View.INVISIBLE);
                heightInch.setVisibility(View.INVISIBLE);
                height.setText(user.getHeight());
            }
            if(user.getHeightUnit()==0){
                ft.setChecked(true);
                height.setVisibility(View.INVISIBLE);
                heightFeet.setVisibility(View.VISIBLE);
                heightInch.setVisibility(View.VISIBLE);
                indexOfDecimal = user.getHeight().indexOf(".");
                heightFeet.setText(user.getHeight().substring(0,indexOfDecimal));
                heightInch.setText(user.getHeight().substring(indexOfDecimal+1));

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
          
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(writeProfile(user)) {
                    fullname.setEnabled(false);
                    height.setEnabled(false);
                    weight.setEnabled(false);
                    genderSelect.setEnabled(false);
                    age.setEnabled(false);
                    cm.setEnabled(false);
                    ft.setEnabled(false);
                    kg.setEnabled(false);
                    lb.setEnabled(false);
                    heightFeet.setEnabled(false);
                    heightInch.setEnabled(false);

                    readProfile();
                    user_editting=false;
                }

                if(user_editting){
                    writeProfile(user);
                }
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
                heightFeet.setEnabled(true);
                heightInch.setEnabled(true);
                user_editting=true;


            heightGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(i == R.id.heightCm){
                        height.setVisibility(View.VISIBLE);
                        heightFeet.setVisibility(View.INVISIBLE);
                        heightInch.setVisibility(View.INVISIBLE);
                    }
                    if(i == R.id.heightFeet){
                        height.setVisibility(View.INVISIBLE);
                        heightFeet.setVisibility(View.VISIBLE);
                        heightInch.setVisibility(View.VISIBLE);
                    }
                }
              });
          }
        });
    }
      else{
          fullname.setText("Name");
          age.setText("Age");
          height.setText("Height");
          weight.setText("Weight");
          fullname.setEnabled(false);
          height.setEnabled(false);
          weight.setEnabled(false);
          genderSelect.setEnabled(false);
          age.setEnabled(false);
          cm.setEnabled(false);
          ft.setEnabled(false);
          kg.setEnabled(false);
          lb.setEnabled(false);
          heightFeet.setEnabled(false);
          heightInch.setEnabled(false);
          heightFeet.setVisibility(View.INVISIBLE);
          heightInch.setVisibility(View.INVISIBLE);
          kg.setChecked(true); lb.setChecked(false);
          cm.setChecked(true); ft.setChecked(false);

          // set gender spinner and catch error
          genderSelect.setSelection(2);
          selectGender="Other";
          Log.e("Tag","<PROFILE> initialize selectGender = "+selectGender);
          tutorialSequence();
      }


      
    }

//    public void basicRead(){
//        reff.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String fileName = dataSnapshot.child("Fullname").getValue(String.class);
//                String fileGender = dataSnapshot.child("Gender").getValue(String.class);
//                String fileHeight = dataSnapshot.child("Height").getValue(String.class);
//                String fileWeight = dataSnapshot.child("Weight").getValue(String.class);
//
//                fullname.setText(fileName);
//                height.setText(fileHeight);
//                weight.setText(fileWeight);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // catch error
//            }
//        });
//    }
//    private void writeProfileCnline () {
//        String userid = firebaseUser.getUid();
//        String str_fullname = fullname.getText().toString();
//        String str_gender = selectGender;
//        String str_weight = weight.getText().toString();
//        String str_height = height.getText().toString();
//
//        HashMap<String, Object> updateresult = new HashMap<>();
//        updateresult.put("Id", userid);
//        updateresult.put("Fullname", str_fullname);
//        updateresult.put("Gender", str_gender);
//        updateresult.put("Weight", str_weight);
//        updateresult.put("Height", str_height);
//        updateresult.put("Imageur", "https://firebasestorage.googleapis.com/v0/b/coen-elec-390-98dd3.appspot.com/o/placeholder.png?alt=media&token=deb0ea3a-dc94-4093-a187-19590f61894b");
//
//        reff.setValue(updateresult);
//    }

    // TODO: heightUnit cm: 1/ ft: 0
    //       weightUnit kg: 1/ lb: 0

    private void readProfile(){
        String email = getIntent().getStringExtra("email");
        databaseHelper = new DatabaseHelper(this);
        User user = databaseHelper.getUser(email);

        fullname.setText(user.getFullname());
        Log.e("Tag","<PROFILE>");
        genderSelect.setSelection(genderGenerate(user.getGender()));
        age.setText(user.getAge());
        weight.setText(user.getWeight());

        if(user.getWeightUnit()==1){
            kg.setChecked(true); }
        else{
            lb.setChecked(true); }

        if(user.getHeightUnit()==1){
            cm.setChecked(true);
            height.setVisibility(View.VISIBLE);
            heightFeet.setVisibility(View.INVISIBLE);
            heightInch.setVisibility(View.INVISIBLE);
            height.setText(user.getHeight());
        }
        if(user.getHeightUnit()==0){
            ft.setChecked(true);
            height.setVisibility(View.INVISIBLE);
            heightFeet.setVisibility(View.VISIBLE);
            heightInch.setVisibility(View.VISIBLE);
            indexOfDecimal = user.getHeight().indexOf(".");
            heightFeet.setText(user.getHeight().substring(0,indexOfDecimal));
            heightInch.setText(user.getHeight().substring(indexOfDecimal+1));

        }
    }

    private boolean writeProfile(User user) {
        pd = new ProgressDialog(ProfileActivity.this);

        String str_fullname = fullname.getText().toString();
        String str_gender = selectGender;
        Log.e("Tag","<PROFILE> "+selectGender);
        String str_age = age.getText().toString();
        String str_weight = weight.getText().toString();
        String str_heightCm = height.getText().toString();

        String str_height = null;

        int checkWeightId = weightGroup.getCheckedRadioButtonId();
        int checkHeightId = heightGroup.getCheckedRadioButtonId();

        if(checkHeightId == R.id.heightCm){
            if(height.getText().toString().isEmpty()){
                Toast.makeText(ProfileActivity.this, "Invalid input! Please enter your information properly.", Toast.LENGTH_SHORT).show();
                pd.dismiss(); }
            else{
                str_height = str_heightCm;
            }
        }

        if(checkHeightId == R.id.heightFeet){
            if(heightFeet.getText().toString().isEmpty() && heightInch.getText().toString().isEmpty()){
                Toast.makeText(ProfileActivity.this, "Invalid input! Please enter your information properly.", Toast.LENGTH_SHORT).show();
                pd.dismiss(); }
            else{
                tempFeet = Integer.parseInt(heightFeet.getText().toString());
                tempInch = Integer.parseInt(heightInch.getText().toString());
                tempHeight = tempFeet + tempInch/100;
                String str_heightFeet = String.valueOf(tempHeight);
                str_height = str_heightFeet;
            }

        }

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
                if(Double.parseDouble(str_height)<=8 && Double.parseDouble(str_height)>=4){
                    user.setHeightUnit(0);
                }
                else{
                    user.setHeightUnit(1);
                }
                height.setVisibility(View.VISIBLE);
                heightFeet.setVisibility(View.INVISIBLE);
                heightInch.setVisibility(View.INVISIBLE);
                //Log.e("Tag","<PROFILE> cm checked");
                break;
            case R.id.heightFeet:
                ft.setChecked(true);
                cm.setChecked(false);
                if(tempFeet<= 4 || tempFeet >=7 || tempInch>=13){
                    user.setHeightUnit(1);
                }
                else {
                    user.setHeightUnit(0);
                }
                height.setVisibility(View.INVISIBLE);
                heightFeet.setVisibility(View.VISIBLE);
                heightInch.setVisibility(View.VISIBLE);
                //Log.e("Tag","<PROFILE> ft checked");
                break;
        }

        Log.e("Tag","<PROFILE> height is number -> "+Temp.isNumeric(str_height));
        Log.e("Tag","<PROFILE> weight is number -> "+Temp.isNumeric(str_weight));
        if(Temp.isNumeric(str_height) && Temp.isNumeric(str_weight)) {
            try {
                if (Integer.parseInt(str_age) >= 150) {
                    Toast.makeText(ProfileActivity.this, "You are too old to exist", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else if (user.getWeightUnit() == 1 && (Double.parseDouble(str_weight) <= 30 || Double.parseDouble(str_weight) >= 200)) {
                    Toast.makeText(ProfileActivity.this, "Weight is out off range", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else if (user.getWeightUnit() == 0 && (Double.parseDouble(str_weight) <= 75 || Double.parseDouble(str_weight) >= 450)) {
                    Toast.makeText(ProfileActivity.this, "Weight is out off range", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else if (user.getHeightUnit() == 1 && (Double.parseDouble(str_height) <= 120 || Double.parseDouble(str_height) >= 215)) {
                    Toast.makeText(ProfileActivity.this, "Height is out off range", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else if (user.getHeightUnit() == 0 && (Double.parseDouble(str_height) <= 4 || Double.parseDouble(str_height) >= 7 || tempInch >= 13)) {
                    Toast.makeText(ProfileActivity.this, "Height is out of range", Toast.LENGTH_SHORT).show();
                    pd.dismiss();

                } else {
                    user.setFullname(str_fullname);
                    user.setGender(str_gender);
                    user.setAge(str_age);
                    user.setWeight(str_weight);
                    user.setHeight(str_height);

                    /*
                    if(firebaseUser!=null) {

                        reff.child("Fullnmae").setValue(str_fullname);
                        reff.child("Gender").setValue(str_gender);
                        reff.child("Age").setValue(str_age);
                        reff.child("Weight").setValue(str_weight);
                        reff.child("Height").setValue(str_height);
                        reff.child("height unit").setValue(Integer.toString(user.getHeightUnit()));
                        reff.child("weight unit").setValue(Integer.toString(user.getWeightUnit()));

                        HashMap<String, Object> updateresult = new HashMap<>();
                        updateresult.put("Id", userid);
                        updateresult.put("Fullname", str_fullname);
                        updateresult.put("Gender", str_gender);
                        updateresult.put("Age", str_age);
                        updateresult.put("Weight", str_weight);
                        updateresult.put("Height", str_height);
                        updateresult.put("height unit", Integer.toString(user.getHeightUnit()));
                        updateresult.put("weight unit", Integer.toString(user.getWeightUnit()));
                        updateresult.put("Password",user.getPassword());


                    }
                    */

                }
                databaseHelper = new DatabaseHelper(this);
                databaseHelper.updateProfile(user);

                if (insert_temp) {
                    double user_weight, calories, prebpm, postbpm;
                    long duration;
                    float speed;
                    int step_counter;
                    String str_date;
                    for (int i = 0; i < Temp.session_counter; i++) {
                        speed = Temp.Speeds.get(i);
                        str_date = Temp.Dates.get(i);
                        duration = Temp.Durations.get(i);
                        prebpm = Temp.PreBPMs.get(i);
                        postbpm = Temp.PostBPMs.get(i);
                        step_counter=Temp.Step_Counters.get(i);
                        if (user.getWeightUnit() == 1) {
                            user_weight = Double.parseDouble(user.getWeight());
                            calories = Statistic.getCaloriesBurned(user_weight, (duration) / 1000 / 60, speed);
                        } else {
                            user_weight = Double.parseDouble(user.getWeight()) * 0.45359237;
                            calories = Statistic.getCaloriesBurned(user_weight, (duration) / 1000 / 60, speed);
                        }
                        databaseHelper.insertStatistic(new Statistic(user.getId(), str_date, Statistic.getperformanceindex(prebpm, postbpm), (double) speed, calories,step_counter),user);
                    }
                    Temp.clear();
                }
                return true;

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Invalid input! Please enter your information properly.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Invalid input! Please enter your information properly.", Toast.LENGTH_LONG).show();
            return false;
        }

//        user.setFullname(str_fullname);
//        user.setGender(str_gender);
//        user.setAge(str_age);
//        user.setWeight(str_weight);
//        user.setHeight(str_height);
//
//        Log.e("Tag","<PROFILE> Weight unit "+ user.getWeightUnit());
//        Log.e("Tag","<PROFILE> Height unit "+ user.getHeightUnit());



    }

    private int genderGenerate (@org.jetbrains.annotations.NotNull String selectGender){
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

    private void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                Fragment fragment;
                switch (menuItem.getItemId()){
                    case R.id.map:
                        intent = new Intent(new Intent(ProfileActivity.this, MapsActivity.class));
                        intent.putExtra("email", email);
                        startActivity(intent);
                        break;

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
                        if(!MainActivity.developer_mode)
                            dev_count++;
                        break;

                    case R.id.logout:
                        startActivity(new Intent(ProfileActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                }

                return true;
            }
        });
    }

    public void tutorialSequence(){


        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);

        sequence.setConfig(config);

        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(new View(getApplicationContext()))
                .setDismissOnTouch(true)
                .setContentText("Finally, the profile page allows you to input your information.\n\n"
                        + "Make sure you register and enter your information here otherwise your running session data will not be stored and Statistic won't be able to track it!")
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView showcaseView) {
                        Intent intent;
                        Log.e("Tag", "<MAIN> entering statistic");
                        intent = new Intent(new Intent(ProfileActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        intent.putExtra("tutorial",true);
                        if(user!=null)
                            intent.putExtra("email",email);
                        startActivity(intent);
                    }
                })
                .build()
        );

        //sequence.addSequenceItem(new View(getApplicationContext()),"Make sure sensor is connected!","GOT IT");



        sequence.start();

    }
}


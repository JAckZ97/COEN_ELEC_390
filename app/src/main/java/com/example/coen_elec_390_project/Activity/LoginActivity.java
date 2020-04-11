package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.Model.Statistic;
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button login;
    TextView txt_signup;
    ProgressDialog pd;
    DatabaseHelper databaseHelper;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.e("Tag","<LOGIN> get called");

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        txt_signup = findViewById(R.id.txt_signup);
        databaseHelper = new DatabaseHelper(this);
        auth = FirebaseAuth.getInstance();

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(LoginActivity.this);

                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else {

                    if (checkNetworkConnection()) {
                        loginOnline(str_email, str_password);
                    } else {
                        loginOffline(str_email, str_password);
                    }

                }
            }
        });
    }

    private boolean checkforAt(String email) {
        boolean hasAt = false;
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                hasAt = true;
            }
        }

        return hasAt;
    }

    private boolean checkNetworkConnection() {
        pd = new ProgressDialog(LoginActivity.this);
        boolean wifiConnected;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();

        if (activeInfo != null && activeInfo.isConnected()) { // wifi connected
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;

            if (wifiConnected) {
//                Toast.makeText(RegisterActivity.this, "Wifi is connected", Toast.LENGTH_SHORT).show();
                Log.e("Tag", "wifi is connected");
//                pd.dismiss();

                return true;
            }
        } else { // no internet connected
//            Toast.makeText(RegisterActivity.this, "No internet connect", Toast.LENGTH_SHORT).show();
            Log.e("Tag", "no internet connect ");
//            pd.dismiss();

            return false;
        }
        return false;
    }

    public void loginOnline(final String str_email, String str_password) {
        auth.signInWithEmailAndPassword(str_email, str_password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    final String temp_email=str_email;
                    //reading data
                    final FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();

                    Log.e("Tag", "<LOGIN> get user uid " + fbuser.getUid());
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(fbuser.getUid());

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e("Tag", "<LOGIN> on DataChange");
                            String fileName = dataSnapshot.child("Fullname").getValue(String.class);
                            String fileGender = dataSnapshot.child("Gender").getValue(String.class);
                            String fileHeight = dataSnapshot.child("Height").getValue(String.class);
                            String fileWeight = dataSnapshot.child("Weight").getValue(String.class);
                            String fileHeightunit = dataSnapshot.child("height unit").getValue(String.class);
                            String fileWeightunit = dataSnapshot.child("weight unit").getValue(String.class);
                            String fileAge = dataSnapshot.child("Age").getValue(String.class);
                            String fileEmail = fbuser.getEmail();
                            String filePass = dataSnapshot.child("Password").getValue(String.class);
                            int stat_counter = Integer.parseInt(dataSnapshot.child("Stat_Counter").getValue(String.class));

                            Log.e("Tag","<LOGIN> gender->"+fileGender+" age->"+fileAge+" weight->"+fileWeight+" height->"+fileHeight);
                            //public User(String fullname, String email, String password, String age, String weight, String height, String gender, int heightUnit, int weightUnit,int stat_counter,String fbuid) {
                            databaseHelper.insertOldUser(new User(fileName, fileEmail, filePass, fileAge, fileWeight, fileHeight, fileGender, Integer.parseInt(fileHeightunit), Integer.parseInt(fileWeightunit), stat_counter, fbuser.getUid()));
                            Log.e("Tag","<LOGIN> Database success");

                            final User user = databaseHelper.getUser(temp_email);
                            Log.e("Tag","<LOGIN> gender->"+user.getGender()+" age->"+user.getAge()+" weight->"+user.getWeight()+" height->"+user.getHeight());
                            Log.e("Tag", "<LOGIN> user email " + user.getEmail());
                            DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("Stats").child(fbuser.getUid());
                            reff2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int counter = 0;
                                    ArrayList<Statistic> mystat_list = new ArrayList<Statistic>();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        int stat_id = Integer.parseInt(ds.child("stat_id").getValue(String.class));
                                        String stat_date = ds.child("stat_date").getValue(String.class);
                                        double stat_speed = Double.parseDouble(ds.child("stat_speed").getValue(String.class));
                                        double stat_calory = Double.parseDouble(ds.child("stat_calory").getValue(String.class));
                                        double stat_perf_index = Double.parseDouble(ds.child("stat_perf_index").getValue(String.class));
                                        int stat_step_counter = Integer.parseInt(ds.child("stat_step_counter").getValue(String.class));
                                        mystat_list.add(new Statistic(stat_id, user.getId(), stat_date, stat_perf_index, stat_speed, stat_calory, stat_step_counter));
                                    }
                                    databaseHelper.UpdateStatistic(mystat_list);
                                    Log.e("TAG", "<LOGIN> database finished here");
                                    loginOffline(user.getEmail(), user.getPassword());
                                    //databaseHelper.insertUser(new User(fileName,fileEmail,filePass,fileAge,fileWeight,fileHeight, fileGender, Integer.parseInt(fileHeightunit),  Integer.parseInt(fileWeightunit)));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // catch error
                                }
                            });
                            pd.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Tag","<LOGIN> database error "+databaseError.getMessage());
                            pd.dismiss();
                        }
                    });



                } else {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loginOffline(String str_email, String str_password) {
        Log.e("Tag", "<email> " + str_email);
        if (databaseHelper.checkIfExisting(str_email)) {
            if (databaseHelper.checkPassword(str_email, str_password)) {
                pd.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("email", str_email);
                User.Global_email = str_email;
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "This password is incorrect", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        } else {
            Toast.makeText(LoginActivity.this, "This email is not registered", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }
}

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

                    }
//                        loginOnline(str_email, str_password);
//                    }else {
                    loginOffline(str_email, str_password);
                    //}
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

    public void loginOnline(String str_email, String str_password) {
        auth.signInWithEmailAndPassword(str_email, str_password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //reading data
                    final FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Users").child(fbuser.getUid());
                            reff.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    /*
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("Id", temp_user.getId());
                                    hashMap.put("Email",temp_user.getEmail());
                                    hashMap.put("Fullname", temp_user.getFullname());
                                    hashMap.put("Gender", temp_user.getGender());
                                    hashMap.put("Age", temp_user.getAge());
                                    hashMap.put("Weight", temp_user.getWeight());
                                    hashMap.put("Height", temp_user.getHeight());`
                                    hashMap.put("height unit",temp_user.getHeightUnit());
                                    hashMap.put("weight unit", temp_user.getWeightUnit());
                                    hashMap.put("Password",temp_user.getPassword());
                                    hashMap.put("Stat_Counter",temp_user.getStat_counter()+stat_counter);
                                     */
                                    String fileName = dataSnapshot.child("Fullname").getValue(String.class);
                                    String fileGender = dataSnapshot.child("Gender").getValue(String.class);
                                    String fileHeight = dataSnapshot.child("Height").getValue(String.class);
                                    String fileWeight = dataSnapshot.child("Weight").getValue(String.class);
                                    String fileHeightunit = dataSnapshot.child("height unit").getValue(String.class);
                                    String fileWeightunit = dataSnapshot.child("weight unit").getValue(String.class);
                                    String fileAge = dataSnapshot.child("Age").getValue(String.class);
                                    String fileEmail = dataSnapshot.child("Email").getValue(String.class);
                                    String filePass = dataSnapshot.child("Password").getValue(String.class);
                                    int stat_counter = Integer.parseInt(dataSnapshot.child("Stat_Counter").getValue(String.class));
                                    databaseHelper.insertUser(new User(fileName, fileEmail, filePass, fileAge, fileWeight, fileHeight, fileGender, Integer.parseInt(fileHeightunit), Integer.parseInt(fileWeightunit), stat_counter, fbuser.getUid()));

                                    final User user = databaseHelper.getUser(fileEmail);
                                    DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference().child("Stats").child(fbuser.getUid());
                                    reff2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int counter = 0;
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                int stat_id = Integer.parseInt(ds.child("stat_id").getValue(String.class));
                                                String stat_date = ds.child("stat_date").getValue(String.class);
                                                double stat_speed = Double.parseDouble(ds.child("stat_speed").getValue(String.class));
                                                double stat_calory = Double.parseDouble(ds.child("stat_calory").getValue(String.class));
                                                double stat_perf_index = Double.parseDouble(ds.child("stat_perf_index").getValue(String.class));
                                                int stat_step_counter = Integer.parseInt(ds.child("stat_step_counter").getValue(String.class));
                                                databaseHelper.insertStatistic(new Statistic(stat_id, user.getId(), stat_date, stat_perf_index, stat_speed, stat_calory, stat_step_counter), user);
                                            }

                                            //databaseHelper.insertUser(new User(fileName,fileEmail,filePass,fileAge,fileWeight,fileHeight, fileGender, Integer.parseInt(fileHeightunit),  Integer.parseInt(fileWeightunit)));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // catch error
                                        }
                                    });
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

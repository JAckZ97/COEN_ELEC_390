package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;


// TODO: Register with signInAnonymously

public class RegisterActivity extends AppCompatActivity {
    EditText fullname, email, password, password2;
//    EditText height, weight;
//    String selectGender;
//    Spinner gender;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;
    Context context;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
//        gender = findViewById(R.id.gender);
//        weight = findViewById(R.id.weight);
//        height = findViewById(R.id.height);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();
        databaseHelper = new DatabaseHelper(this);

//        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectGender=parent.getSelectedItem().toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                Toast.makeText(RegisterActivity.this, "Gender is not selected. ", Toast.LENGTH_SHORT).show();
//                pd.dismiss();
//            }
//        });

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                fullname.setText("987");
                email.setText("987@gmail.com");
                password.setText("qwerty");
                password2.setText("qwerty");

                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();
                String str_password2 = password2.getText().toString();

                if(TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_password2)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }

                else if(str_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }

                else if(!str_password.equals(str_password2)) {
                    Toast.makeText(RegisterActivity.this, "The passwords don't match", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }

                else if(!checkforAt(str_email)) {
                    Toast.makeText(RegisterActivity.this, "This is not a valid email", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }

                else {
                    if(isNetworkConnected()) {
                        registerOnline(str_fullname, str_email, str_password);
                    }

                    else {
                        registerOffline(str_fullname, str_email, str_password);
                    }
                }


            }
        });
    }

    private boolean checkforAt(String email) {
        boolean hasAt = false;
        for(int i = 0; i < email.length(); i++) {
            if(email.charAt(i) == '@') {
                hasAt = true;
            }
        }

        return hasAt;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void registerOnline(final String fullname, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("Id", userid);
                            hashMap.put("Fullname", fullname);
                            hashMap.put("Gender", "");
                            hashMap.put("Weight", "");
                            hashMap.put("Height", "");
                            hashMap.put("Imageur", "https://firebasestorage.googleapis.com/v0/b/coen-elec-390-98dd3.appspot.com/o/placeholder.png?alt=media&token=deb0ea3a-dc94-4093-a187-19590f61894b");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }

                        else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                            task.getException().printStackTrace();
                        }
                    }
                });
    }

    public void registerOffline(final String fullname, String email, String password) {
        if(databaseHelper.checkIfExisting(email)) {
            Toast.makeText(RegisterActivity.this, email, Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }

        else {
            databaseHelper.insertUser(new User(fullname, email, password));

            pd.dismiss();
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}

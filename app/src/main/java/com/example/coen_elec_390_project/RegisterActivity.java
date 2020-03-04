package com.example.coen_elec_390_project;

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
    EditText fullname, email, password, password2, gender, height, weight;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        gender = findViewById(R.id.gender);
        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();

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

                String str_fullname = fullname.getText().toString();
                String str_gender = gender.getText().toString();
                String str_weight = weight.getText().toString();
                String str_height = height.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();
                String str_password2 = password2.getText().toString();

                if(TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_password2) || TextUtils.isEmpty(str_gender) || TextUtils.isEmpty(str_height) || TextUtils.isEmpty(str_weight)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
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

                else {
                    register(str_fullname, str_email, str_password, str_height, str_weight, str_gender);
                }
            }
        });
    }

    public void register(final String fullname, String email, String password, final String height, final String weight, final String gender) {
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
                            hashMap.put("Gender", gender);
                            hashMap.put("Weight", weight);
                            hashMap.put("Height", height);
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
}

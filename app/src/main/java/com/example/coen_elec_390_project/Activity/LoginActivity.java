package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button login;
    TextView txt_signup;

    FirebaseAuth auth;
    ProgressDialog pd;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        txt_signup = findViewById(R.id.txt_signup);

        auth = FirebaseAuth.getInstance();
        databaseHelper = new DatabaseHelper(this);

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
                pd.setMessage("Please wait...");
                pd.show();

                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                /*if(TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }

                else if(!checkforAt(str_email)) {
                    Toast.makeText(LoginActivity.this, "This is not a valid email", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }*/


               // else {
                    loginOffline("gabriel.juteau7@gmail.com", "123456");
                    //loginOffline(str_email, str_password);
                    /**if(isNetworkConnected()) {
                        loginOnline(str_email, str_password);
                    }

                    else {
                        loginOffline(str_email, str_password);
                    }*/
               // }

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

    public void loginOnline(String str_email, String str_password) {
        auth.signInWithEmailAndPassword(str_email, str_password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            pd.dismiss();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            pd.dismiss();
                        }
                    });
                }

                else {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loginOffline(String str_email, String str_password) {
        if (databaseHelper.checkIfExisting(str_email)) {
            if (databaseHelper.checkPassword(str_email, str_password)) {
                pd.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("email", str_email);
                startActivity(intent);
            }

            else {
                Toast.makeText(LoginActivity.this, "This password is incorrect", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        }

        else {
            Toast.makeText(LoginActivity.this, "This email is not registered", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }
}

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
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.R;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText fullname, email, password, password2;
    Button register;
    TextView txt_login;
    ProgressDialog pd;
    Context context;
    DatabaseHelper databaseHelper;
    String age, height, weight, gender = null;
    int weightUnit, heightUnit = 1;
    FirebaseAuth auth;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);
        databaseHelper = new DatabaseHelper(this);
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
                    registerOffline(str_fullname, str_email, str_password, gender, age, height, weight, heightUnit, weightUnit);

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

    public void registerOnline(final String str_fullname, final String str_email, final String str_password) {
        auth.createUserWithEmailAndPassword(str_email, str_password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("Id", userId);
                            hashMap.put("Email",str_email);
                            hashMap.put("Password",str_password);
                            hashMap.put("Fullname", str_fullname);
                            hashMap.put("Gender", "");
                            hashMap.put("Age", "");
                            hashMap.put("Weight", "");
                            hashMap.put("Height", "");
                            hashMap.put("height unit","1");
                            hashMap.put("weight unit", "1");

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

    private boolean checkNetworkConnection(){
        pd = new ProgressDialog(RegisterActivity.this);
        boolean wifiConnected;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();

        if (activeInfo != null && activeInfo.isConnected()){ // wifi connected
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;

            if(wifiConnected){
//                Toast.makeText(RegisterActivity.this, "Wifi is connected", Toast.LENGTH_SHORT).show();
                Log.e("Tag", "wifi is connected");
//                pd.dismiss();

                return true;
            }
        }
        else{ // no internet connected
//            Toast.makeText(RegisterActivity.this, "No internet connect", Toast.LENGTH_SHORT).show();
            Log.e("Tag", "no internet connect ");
//            pd.dismiss();

            return false;
        }
        return false;
    }

    public void registerOffline(final String fullname, String email, String password, String gender, String age, String height, String weight, int heightUnit, int weightUnit) {
        if(databaseHelper.checkIfExisting(email)) {
            Toast.makeText(RegisterActivity.this, "This email is already registered", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }

        else {

            //User(String fullname, String email, String password, String age, String weight, String height, String gender, int heightUnit, int weightUnit,int stat_counter,String fbuid) {
            databaseHelper.insertUser(new User(fullname, email, password, age, weight, height, gender, heightUnit, weightUnit,0,""));
            pd.dismiss();
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("email", email);
            intent.putExtra("status","first");
            startActivity(intent);
        }
    }
}

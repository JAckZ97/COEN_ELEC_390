package com.example.coen_elec_390_project.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {
    EditText fullname, email, password, password2;
    Button register;
    TextView txt_login;
    ProgressDialog pd;
    Context context;
    DatabaseHelper databaseHelper;
    String age, height, weight, gender = null;
    int weightUnit, heightUnit = 1;

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

     /**private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }/

    /**public void registerOnline(final String fullname, String email, String password) {
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
    }*/

    public void registerOffline(final String fullname, String email, String password, String gender, String age, String height, String weight, int heightUnit, int weightUnit) {
        if(databaseHelper.checkIfExisting(email)) {
            Toast.makeText(RegisterActivity.this, "This email is already registered", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }

        else {
            databaseHelper.insertUser(new User(fullname, email, password, gender, age, height, weight, heightUnit, weightUnit));
            pd.dismiss();
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("email", email);
            startActivity(intent);
        }
    }
}

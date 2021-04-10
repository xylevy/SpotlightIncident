package com.spotlight.incident;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    private EditText Name;
    private EditText Password;
    private Button Login;
    private int counter = 5;
    ImageView backbutton;
    TextView actionReg2;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference userDatabaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        Name = (EditText) findViewById(R.id.editText);
        Password = (EditText) findViewById(R.id.editText2);
        TextView info = (TextView) findViewById(R.id.tvInfo);
        Login = (Button) findViewById(R.id.button);
        TextView forgotPassword = (TextView) findViewById(R.id.tvForgetPassword);
//        Info.setText("No of attempts remaining: 5");
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        try {
            SharedPreferences sharedPreferences = Login.this.getSharedPreferences("SaveData", Context.MODE_PRIVATE);
            if (sharedPreferences.contains("Email")){
                String email = sharedPreferences.getString("Email", null);

                if (email == null) {
                    // the key does not exist
                } else {
                    Name.setText(email);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user != null) {

            SharedPreferences sh_Pref=getSharedPreferences("SaveData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sh_Pref.edit();
            editor.putBoolean(getString(R.string.logged_in_key), true);
            editor.apply();

            finish();
            startActivity(new Intent(Login.this, Dashboard.class));

        }

        else{

        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Name.getText().toString().length()<= 0 && (Password.getText().toString().length()<= 0)){
                    Toast.makeText(Login.this, "Enter Login Credentials", Toast.LENGTH_LONG).show();
                }
                else if ((Name.getText().toString().length()<= 0))
                {Toast.makeText(Login.this, "Enter Email to Login", Toast.LENGTH_LONG).show();
                }
                else if ((Password.getText().toString().length()<= 0)){
                    Toast.makeText(Login.this, "Enter Password to Login", Toast.LENGTH_LONG).show();
                }
                else{
                    validate(Name.getText().toString(), Password.getText().toString());
                }}
        });
        backbutton = findViewById(R.id.imageView6);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        actionReg2 = findViewById(R.id.action);
        actionReg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, SignUp.class));

            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ResetPassword.class));
            }
        });
    }

    private void validate(String Name,String Password) {
        progressDialog.setMessage("Processing");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(Name, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    //Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    checkEmailVerification();
                } else {
                    Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
                    counter--;
//                    Info.setText("No of attempts remaining: " + counter);
                    progressDialog.dismiss();
                    if (counter == 0) {
                        Login.setEnabled(false);
                        startActivity(new Intent(Login.this, ResetPassword.class));
                        finish();
                    }
                }

            }
        });

    }
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        if (emailflag) {
            String UID =firebaseUser.getUid();
            userDatabaseReference.child(UID).child("verified").setValue("true");
            SharedPreferences sh_Pref=getSharedPreferences("SaveData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sh_Pref.edit();
            editor.putBoolean(getString(R.string.logged_in_key), true);
            editor.apply();
            startActivity(new Intent(Login.this, Dashboard.class));
            finish();
        } else {
            Toast.makeText(Login.this, "Verify your Email First to Login", Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
        }


    }



}



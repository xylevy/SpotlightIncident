package com.spotlight.incident;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.spotlight.incident.models.User;


import org.jetbrains.annotations.NotNull;

import java.util.Date;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class SignUp extends AppCompatActivity implements View.OnClickListener{
    EditText name,email,password,phone,confirmPassword;
    Button mRegisterbtn;
    TextView mLoginPageBack;
    FirebaseAuth mAuth;
    DatabaseReference mdatabase;
    String Name,Email,Password,PhoneNumber,apartmentID;
    ProgressDialog mDialog;
    DatabaseReference defaultValuesDatabaseReference;
    SharedPreferences.Editor editor;
    Boolean isValidName = false , isValidEmail = false , isValidPassword = false , isValidMobileNumber = false , isValidCPass=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name = (EditText)findViewById(R.id.etSignUpName);
        email = (EditText)findViewById(R.id.etSignUpEmail);
        password = (EditText)findViewById(R.id.etSignUpPassword);
        confirmPassword=(EditText)findViewById(R.id.etConfirmSignUpPassword);
        phone=(EditText)findViewById(R.id.etSignUpPhoneNumber);
        mRegisterbtn = (Button)findViewById(R.id.btnSignUpRegister);
        mLoginPageBack = (TextView)findViewById(R.id.btnLogin);

        // for authentication using FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        mRegisterbtn.setOnClickListener(this);
        mLoginPageBack.setOnClickListener(this);
        mDialog = new ProgressDialog(this);
        mdatabase = FirebaseDatabase.getInstance().getReference().child("users");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            apartmentID = extras.getString("apartment");

        }


        // for TextView validation
        textWatcher();

    }

    @Override
    public void onClick(View v) {
        if (v==mRegisterbtn){
            textWatcher();
            if(isValidEmail && isValidPassword && isValidMobileNumber && isValidName &&isValidCPass) {
                UserRegister();
            }else{
                Toast.makeText(SignUp.this,"One or more fields invalid",Toast.LENGTH_SHORT).show();
            }

        }else if (v== mLoginPageBack){
            startActivity(new Intent(SignUp.this,Login.class));
        }
    }

    private void textWatcher() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(ValidationManager.isFieldEmpty(Objects.requireNonNull(name.getText().toString()))){
                    name.setError("Field Cannot Be Empty");
                    Log.d("Error","Empty");
                    isValidName = false;
                }
                else isValidName = true;

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(ValidationManager.isFieldEmpty(email.getText().toString())){
                    email.setError("Field Cannot Be Empty");
                }
                else if(ValidationManager.isEmailValid(email.getText().toString())){
                    email.setError("Enter a valid Email Address");
                }
                else isValidEmail = true;

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(ValidationManager.isFieldEmpty(password.getText().toString())){
                    password.setError("Field Cannot Be Empty");
                }
                else if (ValidationManager.isValidPassword(password.getText().toString())){
                    password.setError("Password must contain a letter and a number with length between 8-16 characters");
                }
                else  isValidPassword = true;

            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!password.getText().toString().equals(confirmPassword.getText().toString())){
                    confirmPassword.setError("Passwords do not match");
                    isValidCPass=false;
                }
                else isValidCPass =true;

            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(ValidationManager.isFieldEmpty(phone.getText().toString())){
                    phone.setError("Field Cannot Be Empty");
                }
                else if (ValidationManager.isValidMobileNumber(phone.getText().toString())){
                    phone.setError("Enter Valid Mobile Number");
                }
                else isValidMobileNumber = true;

            }
        });

    }

    private void UserRegister() {
        Name = name.getText().toString().trim();
        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();
        PhoneNumber=phone.getText().toString().trim();
        mDialog.setMessage("Signing up...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    sendEmailVerification();
                    mDialog.dismiss();
                    saveDetails();
                    Toast.makeText(SignUp.this,"Account created successfully",Toast.LENGTH_SHORT).show();
                    OnAuth(task.getResult().getUser());
//                    mAuth.signOut();
//                    finish();
//                    startActivity(new Intent(SignUp.this, Login.class));

                }else{
                    email.setError("The email address is already in use by another account.");
                    Toast.makeText(SignUp.this,"Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Email verification code using FirebaseUser object and using isSuccessful()function.
    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SignUp.this,"Check your Email for verification",Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }

    private void writeNewUser(String userId, String name, String email,String password,String phoneNumber ) {
        long cDate=new Date().getTime();
        Date createdDate = new Date(cDate);
        String createDate=createdDate.toString();
        Log.d("User created",email+userId);
        User user = new User(name, email,PassHash(password),createDate,phoneNumber);
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        mdatabase.child(userId).setValue(user,completionListener);
        defaultValuesDatabaseReference =FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        defaultValuesDatabaseReference.child("created_at").setValue(ServerValue.TIMESTAMP);
        defaultValuesDatabaseReference.child("device_token").setValue(deviceToken);
        defaultValuesDatabaseReference.child("is_admin").setValue("false")


                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            mAuth.signOut();
                            finish();
                            startActivity(new Intent(SignUp.this, Login.class));

                        }
                    }
                });

//   TODO     mAuth.signOut();

    }
    private void saveDetails(){
        String mobileNumber = ("+254" + phone.getText().toString().replaceFirst("^0+(?!$)", ""));
        String emailAddy=(email.getText().toString());
        SharedPreferences sharedPreferences = SignUp.this.getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("PhoneNumber",mobileNumber);
        editor.putString("Email",emailAddy);
        editor.commit();


    }




    private void OnAuth(FirebaseUser user) {
        writeNewUser(user.getUid(), Name, user.getEmail(), Password, PhoneNumber);
    }

    //Hash the plaintext password
    private String PassHash(String input){

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // digest() -to calculate message digest of the input string
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String generatedPassword= no.toString(16);

            while (generatedPassword.length() < 32) {
                generatedPassword = "0" + generatedPassword;
            }

            // return the HashText
//            Log.d("SignUp PassHash",generatedPassword);
            return generatedPassword;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    DatabaseReference.CompletionListener completionListener =
            new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       @NotNull DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Toast.makeText(SignUp.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.e("SignUp Data write error",databaseError.getMessage());
                    }else{
                        //TODO --Handle This

                    }
                }
            };

}


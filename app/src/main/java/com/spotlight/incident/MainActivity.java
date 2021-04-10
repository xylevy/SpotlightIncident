package com.spotlight.incident;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showSnack();

        Button registerButton = (Button) findViewById(R.id.registerBtn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
        Button loginButton = (Button) findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });


    }

    public class GuestLoginListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(MainActivity.this, Dashboard.class);
            startActivity(intent);
        }
    }

    public void showSnack(){
        Snackbar snackBar = Snackbar.make(findViewById(R.id.myMainLayout), "You can login as guest", Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction("Click Me",new GuestLoginListener());
        snackBar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSnack();
    }
}
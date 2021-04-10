package com.spotlight.incident;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.rxgps.RxGps;
import com.patloew.rxlocation.RxLocation;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Splash extends BaseActivity {

    private static final String TAG = "ProgressBarStatus";
    ProgressBar splashProgressBar;
    private int progressBarStatus = 0;
    private long waitStatus = 0;
    private Animation topAnim, bottomAnim, rightToCenter;
    private SharedPreferences sh_Pref;
    private boolean check;

    private ImageView appLogo;
    private TextView appTitle;
    private TextView tagLine;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashProgressBar= findViewById(R.id.splash_screen_progress_bar);
        splashProgressBar.setProgress(progressBarStatus);

        //TODO - Place this in  LoginActivity

        sh_Pref =getSharedPreferences("SaveData", MODE_PRIVATE);
        check = sh_Pref.getBoolean("IS_LOGIN", false);

//        SharedPreferences.Editor editor = sh_Pref.edit();
//        editor.putBoolean(getString(R.string.logged_in_key), true);
//        editor.commit();

//        Log.d("NotCheck",String.valueOf(check));
        initAnimation();
        iniView();
        blinkTextView();
        getLocation();
//        new test().start();

    }

    private void blinkTextView() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;
                try{Thread.sleep(timeToBlink);}catch (Exception ignored) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = findViewById(R.id.tag_line);
                        if(txt.getVisibility() == View.VISIBLE){
                            fadeOut();
                            txt.setVisibility(View.INVISIBLE);

                        } else {
                            fadeIn();
                            txt.setVisibility(View.VISIBLE);

                        }
                        blinkTextView();
                    }
                });
            }
        }).start();
    }

    //Initialisation of all the views
    private void iniView() {

        appLogo = findViewById(R.id.imageView);
        appTitle = findViewById(R.id.title);
        tagLine = findViewById(R.id.tag_line);

        //Setting the Animation
        appLogo.setAnimation(rightToCenter);
        appTitle.setAnimation(topAnim);
        tagLine.setAnimation(bottomAnim);
    }

    private void initAnimation() {
        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
        rightToCenter = AnimationUtils.loadAnimation(this, R.anim.right_to_center);
    }

    private void fadeIn(){
        tagLine = findViewById(R.id.tag_line);
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        tagLine.startAnimation(animFadeIn);
    }

    private void fadeOut(){
        tagLine = findViewById(R.id.tag_line);
        Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        tagLine.startAnimation(animFadeOut);
    }

    @Override
    protected void onPause () {
        super.onPause();
//        getLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        getLocation();

    }

    public void displayError(String message) {
        Toast.makeText(Splash.this, message, Toast.LENGTH_SHORT).show();
    }

    private String getAddressText(Address address) {
        String addressText = "";
        final int maxAddressLineIndex = address.getMaxAddressLineIndex();

        for (int i = 0; i <= maxAddressLineIndex; i++) {
            addressText += address.getAddressLine(i);
            if (i != maxAddressLineIndex) {
                addressText += "\n";
            }
        }

        return addressText;
    }

    @SuppressLint("CheckResult")
    public void getLocation(){

//        Log.e(TAG,"GETLOCATON");

        RxLocation rxLocation = new RxLocation(this);
        rxLocation.setDefaultTimeout(10, TimeUnit.SECONDS);

        final RxGps rxGps = new RxGps(this);

        sh_Pref=getSharedPreferences("SaveData", MODE_PRIVATE);


        SharedPreferences.Editor editor = sh_Pref.edit();


        rxGps.lastLocation()

                .doOnSubscribe(this::addDisposable)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(location -> {
                    String message=location.getLatitude() + ", " + location.getLongitude();
                    Log.d("Location",message+ ", " + location.getProvider());
                    editor.putString("cityid",message);
                    editor.apply();


                      // Show location Coordinates
//                    Toast.makeText(Splash.this, message, Toast.LENGTH_SHORT).show();

                }, throwable -> {
                    if (throwable instanceof RxGps.PermissionException) {
                        displayError(throwable.getMessage());
                    } else if (throwable instanceof RxGps.PlayServicesNotAvailableException) {
                        displayError(throwable.getMessage());
                    }
                });

        new test().start();



    }

    class test extends Thread
    {
        public void run() {

            try {

                while (waitStatus < 3000) {
//                            getLocation();
                    sleep(300);
                    waitStatus+=300;
                    progressBarStatus+=10;
//                            Log.d(TAG, Integer.toString(progressBarStatus));
                    splashProgressBar.setProgress(progressBarStatus);

                }

            } catch (Exception e) {
                e.printStackTrace();

            } finally {

                if(check) {
//                            getLocation();
                    Intent abc = new Intent(Splash.this,Dashboard.class);
                    startActivity(abc);
//                            Log.d("Check",String.valueOf(check));
                }

                if(!check){
                    Intent abc = new Intent(Splash.this,MainActivity.class);
                    startActivity(abc);
                }
            }

        }

    }

}




package com.cryptonym0.firebase3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import static android.R.attr.animation;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final MediaPlayer s = MediaPlayer.create(this, R.raw.dokimomov3);
        s.start();
//        spin();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent startMain = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(startMain);
                }//finally
            }//run
        };//timerThread
        timerThread.start();
    }
    @Override
    protected void onPause(){
        super.onPause();
        finish();

    }

}
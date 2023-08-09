package com.example.stocks;

import static com.example.stocks.R.color.white;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toolbar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(white)));

        //Implement splash screen using delay in ms
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//            }
//        }, 3000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), SpinnerActivity.class));
            }
        }, 3000);

    }
}
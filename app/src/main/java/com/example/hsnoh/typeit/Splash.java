package com.example.hsnoh.typeit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by HSNOH on 2016-12-07.
 */
public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Thread myThread =  new Thread(){
            @Override
            public void run() {
                try{
                    sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();}
                finally {
                    Intent startmain = new Intent(getApplicationContext(), TypeIt.class);
                    startActivity(startmain);
                }
            }
        };
        myThread.start();
    }
@Override
    protected void onPause(){
    super.onPause();
    finish();
    }
}

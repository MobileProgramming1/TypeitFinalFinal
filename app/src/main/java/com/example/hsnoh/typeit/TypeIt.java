package com.example.hsnoh.typeit;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import com.example.hsnoh.typeit.DBManager;

public class TypeIt extends AppCompatActivity {
   // public DBManager dbManager;
    MediaPlayer bkgrdmsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_type_it);
       // final MediaPlayer mediaPlayer= MediaPlayer.create(TypeIt.this, R.raw.mainbgm);
            //    mediaPlayer.start();
      //  bkgrdmsc=MediaPlayer.create(TypeIt.this,R.raw.mainbgm);
      // bkgrdmsc.setLooping(true);
      // bkgrdmsc.start();

        ImageButton vocabButton = (ImageButton) findViewById(R.id.vocabButton);
        vocabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), vocab.class);
                startActivityForResult(intent, 0);
            }
        });
        ImageButton sentenceButton = (ImageButton) findViewById(R.id.sentenceButton);
        sentenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), sentence.class);
                startActivityForResult(intent, 0);
            }
        });
        ImageButton eggButton = (ImageButton) findViewById(R.id.eggButton);
        eggButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), Egg.class);
                startActivityForResult(intent, 0);
            }
        });
        ImageButton airButton = (ImageButton) findViewById(R.id.airButton);
        airButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), air.class);
                startActivityForResult(intent, 0);
            }
        });
        ImageButton statButton = (ImageButton) findViewById(R.id.statButton);
        statButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), stat.class);
                startActivityForResult(intent, 0);
            }
        });
    }
  // @Override
  // protected void onPause(){
       //super.onPause();
      //  bkgrdmsc.release();
      //  finish();
    //}
}

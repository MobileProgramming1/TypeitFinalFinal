package com.example.hsnoh.typeit;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.SoundPool;

import com.example.hsnoh.typeit.DBManager;


public class Egg extends AppCompatActivity {
    public randomWord answerWord;
    public blank blank;
    public tried tried;
    public TextView currentStatus;
    public ImageView egg;
    public TextView triedTextView;
    public EditText guess;
    // private static MediaPlayer mp;
    static boolean ON = true;
    static boolean OFF = false;
    public DBManager dbManager;
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg);
        currentStatus = (TextView) findViewById(R.id.currentStatus);
        egg = (ImageView) findViewById(R.id.egg1);
        triedTextView = (TextView) findViewById(R.id.triedTextView);
        dbManager = new DBManager(getApplicationContext(), "Word.db", null, 1);

        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputMethodManager.toggleSoftInputFromWindow(null, inputMethodManager.SHOW_FORCED, 0);
        answerWord = new randomWord(dbManager);
        blank = new blank(answerWord.getWord());
        tried = new tried(answerWord.lengthOfWord);
        guess = (EditText) findViewById(R.id.guess);
        blank.displayCurrentStatusAt(currentStatus);
        guess.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    String temp = guess.getText().toString();
                    if (keyCode == event.KEYCODE_ENTER && !temp.toUpperCase().equals(temp.toLowerCase())) {
                        guessWord(v);
                        return true;
                    }
                }
                return false;
            }
        });//엔터를 누르면 입력받은 글자를 이용하여 실행한다
    }


    public void display() {
        blank.displayCurrentStatusAt(currentStatus);
        tried.displayTriedCharAt(triedTextView);
        tried.displayEggAt(egg);
        guess.setText("");
    }// 현재 빈칸, 시도한 글자, 남은 라이프를 보여준다

    public void restartGame() {
        answerWord = new randomWord(dbManager);
        blank = new blank(answerWord.getWord());
        tried = new tried(answerWord.lengthOfWord);
    }// 게임을 처음부터 다시 시작한다
    public boolean isAlphabet(char letter){
        letter = Character.toLowerCase(letter);
        if (letter >= 'a' && letter <= 'z') return true;
        return false;
    }//입력 받은 문자가 알파벳인지 검사한다

    public void guessWord(View view) {

        char guessLetter = ' ';
        String input = guess.getText().toString();

        if (input == null) return;
        if(isAlphabet(input.charAt(0))) guessLetter = input.charAt(0);
        else return;//입력을 받지 않았겄나 알파벳이 아닌 문자일 경우 리턴

        if (tried.isTriedBefore(guessLetter)) {
        } else {//전에 시도했던 문자가 아니면
            if (answerWord.isLetterInWord(guessLetter)) {//입력받은 문자가 단어 안에 있는지 검사
                blank.fillBlank(guessLetter);//있으면 빈칸 채우기
            } else {//없으면
                tried.storeNotValidLetter(guessLetter);//유효하지 않은(단어에 없는) 단어라고 저장
                tried.increaseFailCount();//실패 횟수 증가
            }
            tried.setTried(guessLetter);//시도 했다고 저장
        }
        display();//현재 상태 표시
        if (blank.isClear() || tried.isGameOver()) {
            MediaPlayer sound;
            if(blank.isClear()){
                sound = MediaPlayer.create(this,R.raw.chick);
                egg.setImageResource(R.drawable.egg8);
            } else{
                sound = MediaPlayer.create(this,R.raw.frying);
            }//게임 클리어인지 게임 오버인지에 따라 음악 설정
            sound.start();
            dialog(this);//다이얼로그 띄우기
        }
    }

    public void dialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Retry?")
                .setMessage("Answer is " + answerWord.getWord() + " : " + answerWord.getWordInKorean() + "\nTry again?")
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        restartGame();//리게임
                        display();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.cancel();
                        finish();//메인 화면으로
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

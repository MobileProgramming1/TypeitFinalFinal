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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.SoundPool;

import com.example.hsnoh.typeit.DBManager;

class word extends Egg {

    protected String word;
    protected String wordInKorean;


    public word(DBManager dbManager) {
        Random rand = new Random();
        int random = rand.nextInt(9);
        word = dbManager.getWord(random);
        wordInKorean = dbManager.getWordInKorean(random);
    }

    public String getWord() {
        return word;
    }

    public String getWordInKorean() {
        return wordInKorean;
    }
}

class randomWord extends word {
    //  private String word;
    //  private String wordInKorean;
    public final int lengthOfWord;

    public randomWord(DBManager dbManager) {
        super(dbManager);
        lengthOfWord = word.length();
    }

    //  public String getWord() {
    //      return word;
    // }

    // public String getWordInKorean() {
    //     return wordInKorean;
    // }

    public boolean isLetterInWord(char letter) {
        if (word.indexOf(letter) < 0) return false;
        return true;
    }

    @Override
    public boolean equals(Object other) {
        return word.equals(other);
    }

    public char charAt(int index) {
        return word.charAt(index);
    }

}

class tried extends Activity {
    static final int MAXFAIL = 6;
    private char[] notValidLetter;
    private int failCount;
    private String stringForDisplay;
    private boolean[] isTried;
    private int eggForDisplay;
    MediaPlayer eggcrack;

    public tried(int maxTryNumber) {
        notValidLetter = new char[maxTryNumber];
        isTried = new boolean[26];
        for (int i = 0; i < 26; i++) isTried[i] = false;
        failCount = 0;
        stringForDisplay = "";
        eggForDisplay = R.drawable.egg1;
    }

    public void increaseFailCount() {
        failCount++;
        return;
    }

    public void storeNotValidLetter(char guessLetter) {
        int index = guessLetter - 'a';
        notValidLetter[failCount] = guessLetter;
        isTried[index] = true;
        return;
    }

    public boolean isTriedBefore(char guessLetter) {
        int index = guessLetter - 'a';
        return isTried[index];
    }

    public boolean setTried(char guessLetter) {
        int index = guessLetter - 'a';
        return isTried[index] = true;
    }

    public boolean isGameOver() {
        if (failCount == MAXFAIL) return true;
        else return false;
    }

    public void displayTriedCharAt(TextView tried) {
        stringForDisplay = "";
        for (int i = 0; i < failCount; i++) {
            stringForDisplay += notValidLetter[i];
            stringForDisplay += ", ";
        }
        tried.setText("Tried : " + stringForDisplay);
    }

    public void displayEggAt(ImageView egg) {


        switch (failCount) {
            case 1:
                eggForDisplay = R.drawable.egg2;
                break;
            case 2:
                eggForDisplay = R.drawable.egg3;
                break;
            case 3:
                eggForDisplay = R.drawable.egg4;
                break;
            case 4:
                eggForDisplay = R.drawable.egg5;
                break;
            case 5:
                eggForDisplay = R.drawable.egg6;
                break;
            case 6:
                eggForDisplay = R.drawable.egg7;
                break;
            default:
                eggForDisplay = R.drawable.egg1;
                break;
        }
        egg.setImageResource(eggForDisplay);
    }

}

class blank {

    private char[] blank;
    private int remainedBlank;
    private String answerWord;
    private String stringForDisplay;

    public blank(String word) {
        answerWord = word;
        blank = new char[word.length()];
        for (int i = 0; i < word.length(); i++) blank[i] = '-';
        remainedBlank = word.length();
        stringForDisplay = "";
    }

    public void fillBlank(char letter) {
        for (int i = 0; i < answerWord.length(); i++) {
            if (answerWord.charAt(i) == letter) {
                blank[i] = letter;
                remainedBlank--;
            }
        }
    }

    public void decreaseRemainedBlank() {
        remainedBlank--;
    }

    public void displayCurrentStatusAt(TextView CurrentStatus) {
        stringForDisplay = "";
        for (int i = 0; i < answerWord.length(); i++) {
            stringForDisplay += blank[i];
            stringForDisplay += " ";
        }
        CurrentStatus.setText(stringForDisplay);
    }

    public boolean isClear() {
        if (remainedBlank == 0) return true;
        else return false;
    }
}

public class Egg extends TypeIt {

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
    private SoundPool sounds;

    private int msprite = 6;


    public DBManager dbManager;
    public DBManager dbManager2;
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg);


        currentStatus = (TextView) findViewById(R.id.currentStatus);
        egg = (ImageView) findViewById(R.id.egg1);
        triedTextView = (TextView) findViewById(R.id.triedTextView);
        dbManager = new DBManager(getApplicationContext(), "Word.db", null, 1);
        dbManager.insert("insert into WORD_SET values(null, 'apple', '사과');");
        dbManager.insert("insert into WORD_SET values(null, 'banana', '바나나');");
        dbManager.insert("insert into WORD_SET values(null, 'grape', '포도');");
        dbManager.insert("insert into WORD_SET values(null, 'melon', '멜론');");
        dbManager.insert("insert into WORD_SET values(null, 'orange', '오렌지');");
        dbManager.insert("insert into WORD_SET values(null, 'strawberry', '딸기');");
        dbManager.insert("insert into WORD_SET values(null, 'watermelon', '수박');");
        dbManager.insert("insert into WORD_SET values(null, 'mobile', '모바일');");
        dbManager.insert("insert into WORD_SET values(null, 'system', '시스템');");
        dbManager2 = dbManager;
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
            //public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //   final boolean isEnterEvent = event != null
            //          && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
            // final boolean isEnterUpEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_UP;
            // final boolean isEnterDownEvent = isEnterEvent && event.getAction() == KeyEvent.ACTION_DOWN;
            // if (isEnterDownEvent || isEnterUpEvent || isEnterEvent) guessWord();
            //  return true;
            //  }
        });
    }


    public void display() {
        blank.displayCurrentStatusAt(currentStatus);
        tried.displayTriedCharAt(triedTextView);
        tried.displayEggAt(egg);
        guess.setText("");
    }

    public void restartGame() {
        //answerWord = null;
        //blank = null;
        //tried = null;
        answerWord = new randomWord(dbManager2);
        blank = new blank(answerWord.getWord());
        tried = new tried(answerWord.lengthOfWord);
    }

    public void guessWord(View view) {//TODO more modulation
        MediaPlayer chick;

        char guessLetter = ' ';
        String temp2 = guess.getText().toString();
        String temp = "";
        if (temp2.toUpperCase().equals(temp2.toLowerCase())) {
            return;
        } else temp2 = temp2.toLowerCase();

        if (temp2.equals(null)) return;
        else guessLetter = temp2.charAt(0);
        //TODO is guessed char alphabetic character?

        if (tried.isTriedBefore(guessLetter)) {
        } else {
            if (answerWord.isLetterInWord(guessLetter)) {
                blank.fillBlank(guessLetter);
                // blank.decreaseRemainedBlank();//빈칸을 채우면서 남은 빈칸의 수를 감소 시키는 것은 단일책임의 원칙에 위배?
            } else {
                tried.storeNotValidLetter(guessLetter);
                tried.increaseFailCount();
            }
            tried.setTried(guessLetter);
        }

        display();
        if (blank.isClear()) {
            egg.setImageResource(R.drawable.egg8);
            chick = MediaPlayer.create(this, R.raw.chick);
            chick.start();


        }
        if (tried.isGameOver()) {
            MediaPlayer frying;
            frying = MediaPlayer.create(this, R.raw.frying);
            frying.start();


        }
        if (blank.isClear() || tried.isGameOver()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Retry?")
                    .setMessage("Answer is " + answerWord.getWord() + " : " + answerWord.getWordInKorean() + "\nTry again?")
                    .setCancelable(false)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            restartGame();
                            display();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

}

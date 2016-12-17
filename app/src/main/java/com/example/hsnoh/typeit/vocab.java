package com.example.hsnoh.typeit;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class vocab extends AppCompatActivity {

    Originator originator;
    Caretaker caretaker;
    Accuracy acc;
    TextView typingRate;
    TextView text;
    TextView accuracy;
    EditText input;
    int i = 0;
    boolean isStarted = false;
    String definition;
    DBManager dbManager;
    TypeRateCalculator typeRateCalculator;
    String titleMsg;
    AlertDialog.Builder builder;
    boolean isDel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocab);
        builder = new AlertDialog.Builder(this);
        dbManager = new DBManager(getApplicationContext(), "Word.db", null, 1);
        typeRateCalculator = new TypeRateCalculator();
        accuracy = (TextView) findViewById(R.id.accuracy);
        text = (TextView) findViewById(R.id.word);
        typingRate = (TextView) findViewById(R.id.typingrate);
        input = (EditText) findViewById(R.id.input);
        Random random = new Random();
        int rand = random.nextInt(9);
        acc = new Accuracy(0.0, 0, 0);
        definition = dbManager.getWordInKorean(rand);
        text.setText(dbManager.getWord(rand));
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isStarted == false) {
                    typeRateCalculator.startMeasuring();
                    input.setTextColor(Color.WHITE);

                    originator = new Originator();
                    originator.setAccuracy(new Accuracy(0.0, 0, 0));

                    caretaker = new Caretaker();
                    caretaker.addMemento(originator.createMemento());
                    //length.setText("" + typeRateCalculator.startTime.getTime());
                    isStarted = true;
                } else if (input.getText().length() == text.getText().toString().length()) {
                    for (int i = 0; i < text.getText().toString().length(); i++) {
                        if (text.getText().toString().charAt(i) != input.getText().toString().charAt(i))
                            ;

                    }
                    accuracy.setText("Accuracy : " + acc.getAccuracy() + "%");

                    if (input.getText().toString().equals(text.getText().toString())) {
                        titleMsg = "correct";
                    } else {
                        titleMsg = "wrong";
                    }

                    dialog();
                    return;
                }
                int length = input.getText().toString().length();
                if (length > 0) {
                    if (text.getText().toString().substring(0, length).equals(input.getText().toString()))
                        input.setTextColor(Color.WHITE);
                    else {
                        input.setTextColor(Color.RED);
                    }
                    if (acc.getCharacterCount() > length) {
                        int w = 0;
                        for (int i = 0; i < length; i++) {
                            if (input.getText().toString().charAt(i) == text.getText().toString().charAt(i))
                                ;
                            else w++;
                        }
                        acc.setWrongCount(w);

                        acc.setCharacterCount(length);
                        acc.updateAccuracy();
                        accuracy.setText("Accuracy : "+acc.getAccuracy() + "%");
                        double a = typeRateCalculator.updateTypingRate();
                        if (a != 0)
                            typingRate.setText("Typing Rate : " + (int) (typeRateCalculator.updateTypingRate()));
                        return;
                    }
                    acc.setCharacterCount(length);
                } else return;


                int characterCount = acc.getCharacterCount();
                if (input.getText().toString().charAt(characterCount - 1) == text.getText().toString().charAt(characterCount - 1)) {
                    //acc.setCharacterCount(characterCount + 1);
                    acc.updateAccuracy();
                    //originator.setAccuracy(new Accuracy(acc.getAccuracy(), acc.getCharacterCount(), acc.getWrongCount()));
                    //caretaker.addMemento(originator.createMemento());
                } else {
                    int wrongCount = acc.getWrongCount();
                    acc.setWrongCount(wrongCount + 1);
                    //acc.setCharacterCount(characterCount + 1);
                    //acc.setWrongCount(wrongCount + 1);
                    acc.updateAccuracy();
                    //originator.setAccuracy(new Accuracy(acc.getAccuracy(), acc.getCharacterCount(), acc.getWrongCount()));
                    //caretaker.addMemento(originator.createMemento());
                }
                accuracy.setText("Accuracy : "+acc.getAccuracy() + "%");
                typeRateCalculator.setCharacterCount(acc.getCharacterCount());
                double a = typeRateCalculator.updateTypingRate();
                if (a != 0)
                    typingRate.setText("Typing Rate : " + (int) (typeRateCalculator.updateTypingRate()));
            }
        });

        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_DEL) {
                        /*int characterCount = input.getText().length();
                        if (characterCount <= 0) return false;
                        else {
                            originator.restoreMemento(caretaker.getMemento(characterCount - 1));
                            acc = originator.getAccuracy();
                            caretaker.removeMemento(characterCount);
                            isDel = true;
                            accuracy.setText(acc.getAccuracy() + "%");
                            double a = typeRateCalculator.updateTypingRate();
                            if (a != 0)
                                typingRate.setText("Typing Rate : " + (int) (typeRateCalculator.updateTypingRate()));
                        }*/
                    } else if (i == KeyEvent.KEYCODE_BACK) {

                        titleMsg = "Go back to Main Menu?";

                        //dialog();
                        builder.setTitle("Go back to Main Menu?")
                                .setMessage("")
                                .setCancelable(false)
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        finish();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        dialog.cancel();

                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return false;
                    }

                }

                return false;
            }

        });


    }

    public void nextText() {
        Random random = new Random();
        int rand = random.nextInt(90);
        text.setText(dbManager.getWord(rand));
        definition = dbManager.getWordInKorean(rand);
        i++;
        input.setText("");
    }


    public String dialogMessage() {
        return text.getText().toString() + " : " + definition + "\n" + accuracy.getText().toString() + "\n" + typingRate.getText().toString() + "\nContinue?";
    }


    public void dialog() {
        builder.setTitle(titleMsg)
                .setMessage(dialogMessage())
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        nextText();
                        input.setText("");
                        isStarted = false;
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




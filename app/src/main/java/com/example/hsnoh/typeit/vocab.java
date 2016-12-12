package com.example.hsnoh.typeit;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hsnoh.typeit.R;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.Random;

public class vocab extends AppCompatActivity{

    TextView typingRate;
    TextView word;
    TextView accuracy;
    EditText input;
    int i = 0;
    int wrongCount = 0;
    boolean isStarted = false;
    String definition;
    DBManager dbManager;
    TypeRateCalculator typeRateCalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocab);
        dbManager = new DBManager(getApplicationContext(), "Word.db", null, 1);
        typeRateCalculator = new TypeRateCalculator();
        accuracy = (TextView) findViewById(R.id.accuracy);
        word = (TextView) findViewById(R.id.word);
        typingRate = (TextView) findViewById(R.id.typingrate);
        input = (EditText) findViewById(R.id.input);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Random random = new Random();
        int rand = random.nextInt(100);
        definition = dbManager.getWordInKorean(rand);
        word.setText(dbManager.getWord(rand));
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
                    typeRateCalculator.characterCount = 0;
                    input.setTextColor(Color.WHITE);
                    wrongCount = 0;
                    //length.setText("" + typeRateCalculator.startTime.getTime());
                    isStarted = true;
                } else if (input.getText().length() == word.getText().toString().length()) {
                    for (int i = 0; i < word.getText().toString().length(); i++) {
                        if (word.getText().toString().charAt(i) != input.getText().toString().charAt(i))
                            wrongCount++;
                    }
                    accuracy.setText("Accuracy : " + (word.getText().toString().length() - wrongCount) / (double) word.getText().toString().length() * 100 + "%");
                    input.setText("");

                    isStarted = false;
                    return;
                }
                int b = typeRateCalculator.characterCount = input.getText().toString().length();
                if (b > 0) {
                    if (word.getText().toString().substring(0, b).equals(input.getText().toString()))
                        input.setTextColor(Color.WHITE);
                    else {
                        input.setTextColor(Color.RED);
                    }
                }

                double a = typeRateCalculator.updateTypingRate();
                if (a != 0)
                    typingRate.setText("Typing Rate : " + (int) (typeRateCalculator.updateTypingRate()));
            }
        });

        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
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
                    if (i == KeyEvent.KEYCODE_ENTER) {

                    }
                }

                return false;
            }

        });


    }

    public void nextWord() {
        Random random = new Random();
        int rand = random.nextInt(9);
        word.setText(dbManager.getWord(rand));
        definition = dbManager.getWordInKorean(rand);
        i++;
        input.setText("");
    }

    class TypeRateCalculator {
        public Date startTime;
        private Date elapsedTime;
        public int characterCount = 0;

        public void startMeasuring() {
            startTime = new Date();
        }

        public double updateTypingRate() {
            //characterCount++;
            elapsedTime = new Date();
            double delta = (elapsedTime.getTime() - startTime.getTime()) / 1000.0 / 60;
            double typingRate = (delta == 0 ? 0 : characterCount / delta);

            return typingRate;
        }

    }

    public void dialog(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String msg = "헬로";
        if (input.getText().toString().equals(word.getText().toString())) {
            msg = "correct";
        } else {
            msg = "wrong";
        }

        builder.setTitle("You are " + msg + " Retry?")
                .setMessage(word.getText().toString() + " : " + definition + "\nTry again?")
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        nextWord();
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


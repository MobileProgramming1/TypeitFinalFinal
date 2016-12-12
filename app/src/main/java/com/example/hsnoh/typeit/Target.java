package com.example.hsnoh.typeit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hsnoh.typeit.R;

import java.util.Random;

/**
 * Created by type on 2016-12-06.
 */

public class Target extends air {

    private int item;
    public float currentY;
    public ValueAnimator animator;
    private char firstLetter;
    public LinearLayout target;
    public TextView targetWord;
    public ImageView targetImage;

    public Target(final Context context) {
        int randomNumber;
        Random random = new Random();
        randomNumber = random.nextInt(100);
        target = new LinearLayout(context);
        targetWord = new TextView(context);
        targetImage = new ImageView(context);
        targetImage.setImageResource(R.drawable.target);
        targetWord.setText("apple");
        firstLetter = getWord().charAt(0);
        targetWord.setTextSize(22);
        targetWord.setTextColor(Color.RED);
        targetWord.setTypeface(null, Typeface.BOLD);
        target.setOrientation(LinearLayout.HORIZONTAL);


        if (randomNumber < PROBABILITY_BOMB) {
            item = BOMB;
        } else if (randomNumber < PROBABILITY_BOMB + PROBABILITY_STOP) {
            item = STOP;
        } else {
            item = NORMAL;
            targetWord.setTextColor(Color.BLACK);
        }
        target.addView(targetImage);
        target.addView(targetWord);
        target.setVisibility(View.INVISIBLE);

        animator = ValueAnimator.ofInt(0, 650);
        int duration = 25000 - 3000 * (currentStage - 1);
        animator.setDuration((currentStage <= 7 ? duration : 4000));


    }

    public void setX(float x) {
        target.setX(x);
    }

    public void setWord(String word) {
        targetWord.setText(word);
    }

    public void start() {
        animator.start();
    }

    public void setStartDelay(long startDelay) {
        animator.setStartDelay(startDelay);
    }

    public float getCurrentY() {
        return currentY;
    }

    @SuppressLint("NewApi")
    public void pause() {
        animator.pause();
    }

    @SuppressLint("NewApi")
    public void resume() {
        animator.resume();
    }

    public void cancel() {
        targetWord.setText("");
        target.removeAllViews();
        animator.cancel();
    }

    public boolean isRunning() {
        return animator.isRunning();
    }

    public float getX() {
        return target.getX() + targetImage.getWidth() / 2;
    }

    public String getWord() {
        return targetWord.getText().toString();
    }

    public int length() {
        return getWord().length();
    }

    public int getItem() {
        return item;
    }

    public void addAt(RelativeLayout layout) {
        layout.addView(target);
    }

    public char getFirstLetter() {
        return getWord().charAt(0);
    }

    public void deleteFirstLetter() {
        String newWord = getWord();
        newWord = newWord.substring(1, newWord.length());
        targetWord.setText(newWord);
    }



}

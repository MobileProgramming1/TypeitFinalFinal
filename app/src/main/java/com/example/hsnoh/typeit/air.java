package com.example.hsnoh.typeit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hsnoh.typeit.R;

import java.util.Random;

public class air extends AppCompatActivity {

    final static int NORMAL = 0;
    final static int BOMB = 1;
    final static int STOP = 2;
    final static int PROBABILITY_BOMB = 1;
    final static int PROBABILITY_STOP = 4;
    final static int PROBABILITY_ITEM = PROBABILITY_BOMB + PROBABILITY_STOP;
    final static int MAX_LIFE = 3;
    char inputLetter;
    int count;
    int remainingTarget;
    boolean temp = false;
    ProgressDialog pausingDialog;
    TextView score;
    TextView countDown;
    TextView life;
    TextView stage;
    EditText input;
    ImageView bullet;
    ImageView plane;
    ImageButton itemButton;
    RelativeLayout field;
    private CountDownTimer countDownTimer;
    protected int remainLife;
    protected int currentScore;
    private int item;
    InputMethodManager inputMethodManager;
    final int numberOfTarget = 15;
    protected int currentStage = 1;
    Target foundTarget;
    Target[] target;
    ValueAnimator anim;
    boolean mPaused;
    boolean mFinished;
    boolean aaa = true;
    ObjectAnimator objectAnimator;

    protected void onResume() {
        super.onResume();

        final View view = findViewById(R.id.input);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(findViewById(R.id.input), 0);
            }
        }, 100);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air);
        remainLife = MAX_LIFE;
        currentScore = 0;
        count = 3;
        score = (TextView) findViewById(R.id.score);
        life = (TextView) findViewById(R.id.life);
        field = (RelativeLayout) findViewById(R.id.field);
        countDown = (TextView) findViewById(R.id.countDown);
        itemButton = (ImageButton) findViewById(R.id.itemBUtton);
        input = (EditText) findViewById(R.id.input);
        stage = (TextView) findViewById(R.id.stage);
        itemButton.setClickable(false);
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        //input.setPrivateImeOptions("defaultInputmode=english;");
        target = new Target[numberOfTarget];
        remainingTarget = numberOfTarget;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
                countDown.setText("" + count);
                if (count == 0) {
                    countDown.setText("Start!");
                    countDown.setTextColor(Color.RED);
                }
                count--;
            }

            @Override
            public void onFinish() {
                countDown.setText("");
                createTarget();
            }
        };
        countDownTimer.start();
        bullet = new ImageView(this);
        bullet.setImageResource(R.drawable.bullet);
        bullet.setVisibility(View.INVISIBLE);
        plane = (ImageView)findViewById(R.id.plane);

        field.addView(bullet);

        foundTarget = null;
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (input.getText().toString().length() == 0) return;
                inputLetter = input.getText().toString().charAt(0);
                if (input.getText().toString().charAt(0) >= 'a' && input.getText().toString().charAt(0) <= 'z') {
                    if (foundTarget == null) foundTarget = findTarget();
                    if (foundTarget == null) return;
                    if (foundTarget.getCurrentY() >= 650) {
                        foundTarget = findTarget();
                    }
                    if (foundTarget == null) return;
                    plane.setX(foundTarget.getX());
                    if (foundTarget.getCurrentY() < 650 && inputLetter == foundTarget.getFirstLetter()) {
                        bullet.setX(plane.getX());
                        anim = ValueAnimator.ofFloat(plane.getY(), foundTarget.getCurrentY());
                        anim.setDuration(450);
                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                bullet.setY((Float) valueAnimator.getAnimatedValue());
                            }
                        });
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                bullet.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                bullet.setVisibility(View.VISIBLE);
                                bullet.setY(plane.getY());

                            }
                        });
                        anim.start();
                        foundTarget.deleteFirstLetter();

                        if (foundTarget.length() == 0) {
                            currentScore += 100;
                            remainingTarget--;
                            foundTarget.target.setVisibility(View.INVISIBLE);
                            score.setText("score : " + currentScore);
                            item = foundTarget.getItem();
                            if (item == BOMB) {
                                itemButton.setImageResource(R.drawable.bomb);
                                itemButton.setClickable(true);
                            } else if (item == STOP) {
                                itemButton.setImageResource(R.drawable.stop);
                                stop();
                            }
                            item = NORMAL;
                            foundTarget.cancel();
                            foundTarget = null;

                            if (remainingTarget <= 0) {
                                remainingTarget = numberOfTarget;
                                currentStage++;
                                stage.setText("stage : " + currentStage);
                                createTarget();
                            }
                        }
                    }

                }
                input.setText("");
            }
        });
   /*     input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                //if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i >= KeyEvent.KEYCODE_A && i <= KeyEvent.KEYCODE_Z) {

                    inputLetter = (char) (i + 68);
                    if (foundTarget == null) foundTarget = findTarget();
                    if (foundTarget == null) return true;
                    if (foundTarget.getCurrentY() >= 1000) {
                        foundTarget = findTarget();
                    }
                    if (foundTarget == null) return false;
                    mTextView.setX(foundTarget.getX());
                    if (foundTarget.getCurrentY() < 1000 && inputLetter == foundTarget.getFirstLetter()) {
                        bullet.setX(mTextView.getX());
                        anim = ValueAnimator.ofFloat(mTextView.getY(), foundTarget.getCurrentY());
                        anim.setDuration(450);
                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                bullet.setY((Float) valueAnimator.getAnimatedValue());
                            }
                        });
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                bullet.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                bullet.setVisibility(View.VISIBLE);
                                bullet.setY(mTextView.getY());

                            }
                        });
                        anim.start();
                        foundTarget.deleteFirstLetter();

                        if (foundTarget.length() == 0) {
                            currentScore += 100;
                            score.setText("score : " + currentScore);
                            item = foundTarget.getItem();
                            if (item == BOMB) {
                                itemButton.setImageResource(R.drawable.bomb);
                                itemButton.setClickable(true);
                            } else if (item == SLOW) {
                                itemButton.setImageResource(R.drawable.slow);
                                slow();
                            }
                            item = NORMAL;
                            foundTarget.cancel();
                            foundTarget = null;
                        }
                    }


                    return true;
                }
                //}
                return false;
            }
        });*/

    }


    public void createTarget() {
        Random random = new Random();
        int width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        int temp;
        for (int i = 0; i < numberOfTarget; i++) {
            target[i] = new Target(this);
            target[i].addAt(field);
            final Target tempTarget = target[i];
            temp = ((random.nextInt(10) * 100) % (field.getWidth() - 300 - target[i].target.getWidth()));
            target[i].animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (tempTarget.isRunning()) tempTarget.target.setVisibility(View.VISIBLE);
                    tempTarget.target.setY((Integer) valueAnimator.getAnimatedValue());
                    tempTarget.currentY = tempTarget.target.getY();

                    if (tempTarget.currentY >= 650 && tempTarget.length() != 0) {
                        remainLife--;
                        remainingTarget--;
                        life.setText("life : " + remainLife);
                        tempTarget.targetWord.setText("");
                        tempTarget.target.removeAllViews();
                        //tempTarget.target = null;
                        if (remainingTarget <= 0) {
                            remainingTarget = numberOfTarget;
                            currentStage++;
                            stage.setText("stage : " + currentStage);
                            createTarget();
                        }
                        //foundTarget = null;
                    }
                }
            });
            target[i].setX((temp <= 0 ? 0 : temp));
            target[i].start();
            long delay = 5000 - 500 * (currentStage - 1);
            target[i].setStartDelay(i * (currentStage <= 7 ? delay : 1500));
        }
    }

    Target findTarget() {
        for (int i = 0; i < numberOfTarget; i++) {
            if (target[i].isRunning() && target[i].length() != 0) {
                if (inputLetter == target[i].getFirstLetter()) return target[i];
            }
        }
        return null;
    }

    public void bomb() {
        item = NORMAL;
        for (int i = 0; i < numberOfTarget; i++) {
            if (target[i].target.getVisibility() == View.VISIBLE) {
                target[i].cancel();
                remainingTarget--;
                target[i].target.setVisibility(View.INVISIBLE);
                currentScore += 100;
            }
        }
        score.setText("" + currentScore);
        if (remainingTarget <= 0) {
            remainingTarget = numberOfTarget;
            currentStage++;
            stage.setText("stage : " + currentStage);
            createTarget();
        }
    }

    public void stop() {
        item = NORMAL;
        ValueAnimator stop;
        stop = ValueAnimator.ofFloat(0, 1000);
        stop.setDuration(10000);


        stop.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                itemButton.setImageResource(R.drawable.stop);
                for (int i = 0; i < numberOfTarget; i++)
                    if (target[i].target.getVisibility() == View.VISIBLE)
                        target[i].pause();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                itemButton.setImageResource(R.drawable.noitem);
                for (int i = 0; i < numberOfTarget; i++) {

                    if (target[i].target.getVisibility() == View.VISIBLE) {
                        target[i].resume();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        stop.start();
    }

    public void useItem(View view) {
        bomb();
        itemButton.setImageResource(R.drawable.noitem);
        itemButton.setClickable(false);

    }

    public void pause() {

        for (int i = 0; i < numberOfTarget; i++)
            if (target[i].isRunning()) target[i].pause();

    }

    public void resume() {
        for (int i = 0; i < numberOfTarget; i++)
            if (target[i].isRunning()) target[i].resume();
    }

    public void test(View view) {
        if (aaa == true) pause();
        else resume();
        aaa ^= true;
    }

}


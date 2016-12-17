package com.example.hsnoh.typeit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AlertDialog;
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
    final static int PROBABILITY_BOMB = 50;
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
    AlertDialog.Builder builder;
    DBManager dbManager;
    MediaPlayer shootingSound;
    MediaPlayer bombSound;
    boolean bbb = false;

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
        dbManager = new DBManager(getApplicationContext(), "Word.db", null, 1);
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
        builder = new AlertDialog.Builder(this);
        shootingSound = MediaPlayer.create(this, R.raw.laser);
        bombSound = MediaPlayer.create(this, R.raw.bombsound);
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
                bbb = true;
                createTarget();
            }
        };//카운트 다운
        countDownTimer.start();
        bullet = new ImageView(this);
        bullet.setImageResource(R.drawable.bullet);
        bullet.setVisibility(View.INVISIBLE);
        plane = (ImageView) findViewById(R.id.plane);

        field.addView(bullet);

        foundTarget = null;
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

                }

                return false;
            }

        });

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
                if (aaa == false) return;
                if (bbb == false) return;
                inputLetter = input.getText().toString().charAt(0);
                if (input.getText().toString().charAt(0) >= 'a' && input.getText().toString().charAt(0) <= 'z') {//입력받은 문자가 알파벳 소문자인지 검사
                    if (foundTarget == null) foundTarget = findTarget();//공격중인 타겟이 없으면 타겟 찾기
                    if (foundTarget == null) return;//타겟을 못 찾았으면 리턴
                    if (foundTarget.getCurrentY() >= 650) {
                        foundTarget = findTarget();
                    }//공격중인 타겟이 기준선에 도달하면 다른 타겟 찾기
                    if (foundTarget == null) return;
                    plane.setX(foundTarget.getX());//타겟의 수평 위치로 이동
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
                                shootingSound.stop();
                                shootingSound.start();
                                bullet.setVisibility(View.VISIBLE);
                                bullet.setY(plane.getY());

                            }
                        });
                        anim.start();
                        //shootingSound.start();
                        foundTarget.deleteFirstLetter();//타겟의 (현재) 첫 글자 지우기

                        if (foundTarget.length() == 0) {//타겟의 모든 글자를 지우면
                            currentScore += 100;//점수 획득
                            remainingTarget--;//타겟 수 감소
                            foundTarget.target.setVisibility(View.INVISIBLE);
                            score.setText("score : " + currentScore);
                            item = foundTarget.getItem();//아이템 획득
                            if (item == BOMB) {
                                itemButton.setImageResource(R.drawable.bomb);
                                itemButton.setClickable(true);//폭탄은 아이템 창에 저장
                            } else if (item == STOP) {
                                itemButton.setImageResource(R.drawable.stop);
                                stop();//스탑의 경우 자동 사용
                            }
                            //item = NORMAL;
                            foundTarget.cancel();
                            foundTarget = null;

                            if (remainingTarget <= 0) {
                                remainingTarget = numberOfTarget;
                                currentStage++;
                                stage.setText("stage : " + currentStage);
                                createTarget();
                            }//타겟을 모두 제거하면 다음 스테이지로
                        }
                    }

                }
                input.setText("");
            }
        });


    }

    public void createTarget() {//타겟 생성
        final Random random = new Random();
        int width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        int temp;
        for (int i = 0; i < numberOfTarget; i++) {
            target[i] = new Target(this);
            target[i].addAt(field);
            final Target tempTarget = target[i];
            temp = ((random.nextInt(10) * 100) % (field.getWidth() - 300 - target[i].target.getWidth()));//타겟 위치 설정
            target[i].setWord(dbManager.getWord(random.nextInt(90)));//타겟 단어 설정
            target[i].firstLetter = tempTarget.getWord().charAt(0);
            target[i].animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    //final int randNum = random.nextInt(9);
                    if (tempTarget.isRunning()) tempTarget.target.setVisibility(View.VISIBLE);
                    tempTarget.target.setY((Integer) valueAnimator.getAnimatedValue());
                    tempTarget.currentY = tempTarget.target.getY();

                    if (tempTarget.currentY >= 650 && tempTarget.length() != 0) {
                        remainLife--;//타겟이 기주선에 닿으면 라이프 감소
                        if (remainLife <= 0) {
                            for (int i = 0; i < numberOfTarget; i++) {
                                target[i].cancel();
                                field.removeView(target[i].target);
                            }//라이프가 모두 감소하면 게임 오버
                            builder.setTitle("GameOver")
                                    .setMessage("Play again?")
                                    .setCancelable(false)
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            currentScore = 0;
                                            remainLife = MAX_LIFE;
                                            currentStage = 1;
                                            itemButton.setImageResource(R.drawable.noitem);
                                            itemButton.setClickable(false);
                                            count = 3;
                                            stage.setText("stage : " + currentStage);
                                            score.setText("score : " + currentScore);
                                            life.setText("life : " + remainLife);
                                            createTarget();
                                        }
                                    })
                                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            dialog.cancel();
                                            finish();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        remainingTarget--;//타겟 수 감소
                        life.setText("life : " + remainLife);
                        tempTarget.targetWord.setText("");
                        tempTarget.target.removeAllViews();
                        //tempTarget.target = null;
                        if (remainingTarget <= 0) {//타겟이 모두 없어지면 다음 스테이지로
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
        for (int i = 0; i < numberOfTarget; i++) {//입력한 글자와 타겟의 첫 글자와 비교하여 타겟 찾기
            if (target[i].isRunning() && target[i].length() != 0) {
                if (inputLetter == target[i].getFirstLetter()) return target[i];
            }
        }
        return null;
    }

    public void bomb() {
        item = NORMAL;
        bombSound.start();
        for (int i = 0; i < numberOfTarget; i++) {//사용 당시 화면에 보이는 타겟 모두 제거후 제거한 수에 비례하여 점수 획득
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

    public void stop() {//사용 당시 화면에 보이는 타겟 모두 정지
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


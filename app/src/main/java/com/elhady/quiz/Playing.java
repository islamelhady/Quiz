package com.elhady.quiz;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elhady.quiz.Common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Playing extends AppCompatActivity implements View.OnClickListener {

    final static long INTERVAL = 1000; // 1 sec
    final static long TIMEOUT = 7000; // 7sec
    int progressValue = 0;

    CountDownTimer mCoutDown;

    int index=0,score=0,thisQuestion=0,totalQuestion,correctAnswer;



    ProgressBar progressBar;
    ImageView question_image;
    Button btnA,btnB,btnC,btnD;
    TextView txtScore,txtQuestionNum,question_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);


        //Views
        txtScore = (TextView)findViewById(R.id.txtScore);
        txtQuestionNum = (TextView)findViewById(R.id.txtTotalQuestion);
        question_text = (TextView)findViewById(R.id.question_text);
        question_image = (ImageView)findViewById(R.id.question_image);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        btnA = (Button)findViewById(R.id.btnAnswerA);
        btnB = (Button)findViewById(R.id.btnAnswerB);
        btnC = (Button)findViewById(R.id.btnAnswerC);
        btnD = (Button)findViewById(R.id.btnAnswerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        mCoutDown.cancel();
        if (index < totalQuestion) // still have question in list
        {
            Button clickButton = (Button)view;
            if(clickButton.getText().equals(Common.qustionList.get(index).getCorrectAnswer()))
            {

                //Choose correct answer
                score+=10;
                correctAnswer++;
                showQuestion(++index); // next question
            }
            else
            {
                //Choose wrong answer
                Intent intent = new Intent(this,Done.class);
                Bundle dataSend = new Bundle();
                dataSend.putInt("SCORE",score);
                dataSend.putInt("TOTAL",totalQuestion);
                dataSend.putInt("CORRECT",correctAnswer);
                intent.putExtras(dataSend);
                startActivity(intent);
                finish();
            }
            txtScore.setText(String.format("%d",score));
        }

    }

    private void showQuestion(int index) {
        if(index < totalQuestion)
        {
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d / %d",thisQuestion,totalQuestion));
            progressBar.setProgress(0);
            progressValue = 0;

            if (Common.qustionList.get(index).getIsImageQuestion().equals("true"))
            {
                //if is image
                Picasso.with(getBaseContext())
                        .load(Common.qustionList.get(index).getQuestion())
                        .into(question_image);
                question_image.setVisibility(View.VISIBLE);
                question_text.setVisibility(View.INVISIBLE);
            }
            else
            {
                question_text.setText(Common.qustionList.get(index).getQuestion());

                //if question is text , we will set image to invisible
                question_image.setVisibility(View.INVISIBLE);
                question_text.setVisibility(View.VISIBLE);
            }

            btnA.setText(Common.qustionList.get(index).getAnswerA());
            btnB.setText(Common.qustionList.get(index).getAnswerB());
            btnC.setText(Common.qustionList.get(index).getAnswerC());
            btnD.setText(Common.qustionList.get(index).getAnswerD());

            mCoutDown.start(); // Start timer
        }
        else
        {
            // If it is final question
            Intent intent = new Intent(this,Done.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE",score);
            dataSend.putInt("TOTAL",totalQuestion);
            dataSend.putInt("CORRECT",correctAnswer);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
    }

    //Ctrl o

    @Override
    protected void onResume() {
        super.onResume();

        totalQuestion = Common.qustionList.size();

        mCoutDown = new CountDownTimer(TIMEOUT,INTERVAL) {
            @Override
            public void onTick(long minisec) {
                progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                mCoutDown.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }
}

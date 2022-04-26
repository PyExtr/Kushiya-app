package com.example.trivia;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity{
    private static final String ID_KEY = "id_key";
    private  boolean clicked=true;
    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    private String max = "0";
    private int tempCurrent = 1000;
    private double tempScore = 0;
    private List<Integer> rand=new ArrayList<Integer>();
    private TextView showMessageTextView;
    List<Question> questionsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for(int i=0;i<20;i++){
            rand.add(new Random().nextInt(912));

        }

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        SharedPreferences getSharedPrefs = getSharedPreferences(ID_KEY, MODE_PRIVATE);
        max = getSharedPrefs.getString("max", "0");
        showMessageTextView = findViewById(R.id.HighScore);
        if(max != "0"){
            showMessageTextView.setText("Highest score: "+max);
       }

        questionsList = new Repository().getQuestions(questionArrayList -> {
            binding.questionTextview.setText(questionArrayList.get(currentQuestionIndex)
                    .getAnswer());
            upadeCounter((ArrayList<Question>) questionsList);
        });

        binding.buttonNext.setOnClickListener(view -> {
            currentQuestionIndex = (currentQuestionIndex + 1) ;
            updateQuestion();
        });
        binding.buttonPrev.setOnClickListener(view -> {
            currentQuestionIndex--;
            if (currentQuestionIndex<0){
                currentQuestionIndex = questionsList.size() - 1;
            }
            updateQuestion();
        });

        binding.buttonAnswer.setOnClickListener(view -> {
            if(clicked){
                binding.buttonFalse.setVisibility(View.VISIBLE);
                binding.button0.setVisibility(View.VISIBLE);
                binding.button1.setVisibility(View.VISIBLE);
                binding.buttonAnswer.setText("back to question");
                clicked=false;
            }
            else{
                clicked=true;
                binding.buttonFalse.setVisibility(View.GONE);
                binding.button0.setVisibility(View.GONE);
                binding.button1.setVisibility(View.GONE);
                binding.buttonAnswer.setText("Answer");
            }



          //  checkAnswer(true);
            updateQuestion();

        });

          ///////////     in all above wil change the text to back to answer ///////////
        binding.button0.setOnClickListener(view ->{
            clicked=true;
            updateQuestion();
            binding.buttonFalse.setVisibility(View.GONE);
            binding.button0.setVisibility(View.GONE);
            binding.button1.setVisibility(View.GONE);
            binding.buttonAnswer.setText("Answer");
        });
        binding.button1.setOnClickListener(view ->{
            tempScore=tempScore+0.5;
            clicked=true;
            updateQuestion();
            binding.buttonFalse.setVisibility(View.GONE);
            binding.button0.setVisibility(View.GONE);
            binding.button1.setVisibility(View.GONE);
            binding.buttonAnswer.setText("Answer");
        });
        binding.buttonFalse.setOnClickListener(view -> {

            tempScore++;
            clicked=true;
            updateQuestion();
            binding.buttonFalse.setVisibility(View.GONE);
            binding.button0.setVisibility(View.GONE);
            binding.button1.setVisibility(View.GONE);
            binding.buttonAnswer.setText("Answer");


            //checkAnswer(false);


        });

        //SharedPreferences getShareData = getPreferences(ID_KEY, MODE_PRIVATE);
    }

    private void checkAnswer(boolean b) {
        boolean answer = questionsList.get(currentQuestionIndex).isAnswerTrue();
        int snackMessageID = 0;
        if (b==answer){
            fadeAnimation();
            snackMessageID = R.string.correct_answer;
            if (currentQuestionIndex != tempCurrent){
                tempCurrent = currentQuestionIndex;
                tempScore = tempScore+10;
            }
        }else{
            if (currentQuestionIndex != tempCurrent){
                tempCurrent = currentQuestionIndex;
                tempScore = tempScore-5;
            }
            shakeAnimation();
            snackMessageID = R.string.correct_false;
        }
        Snackbar.make(binding.cardView, snackMessageID, Snackbar.LENGTH_LONG ).show();
    }

    private void upadeCounter(ArrayList<Question> questionsList) {
        binding.textViewOutOf.setText(String.format(getString(R.string.text_formated),
                currentQuestionIndex, questionsList.size()-1));
        +*
    }

    private void fadeAnimation(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatCount(Animation.REVERSE);

        binding.cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void updateQuestion() {

        if(currentQuestionIndex==20){
            currentQuestionIndex=0; // new view of Question or in next button
        }

        String question = questionsList.get(rand.get(currentQuestionIndex)).getAnswer();
        binding.questionTextview.setText(question);
        upadeCounter((ArrayList<Question>) questionsList);
        if(tempScore<0){
            tempScore=0;
        }
        binding.Score.setText("Score: " + tempScore);

//        String message = enterMessage.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences(ID_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (Integer.parseInt(max) < tempScore){
            max = ""+tempScore;
        }
        editor.putString("max", max);
        editor.apply();
    }

    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_anim);
        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.BLUE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
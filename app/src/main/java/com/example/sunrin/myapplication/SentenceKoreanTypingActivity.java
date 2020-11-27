package com.example.sunrin.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Constraints;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SentenceKoreanTypingActivity extends AppCompatActivity {

    private  android.support.v7.widget.Toolbar toolbar;
    private TextView showText;
    private TextView cpmText;
    private TextView timerText;
    private TextView accuracyText;
    private ProgressBar cpmGauge;
    private ProgressBar timerGauge;
    private ProgressBar accuracyGauge;
    private EditText typingText;
    private SpannableStringBuilder spanText;
    private CharSequence onTextSequence;
    private HashMap<Integer, ForegroundColorSpan> textColorMap = new HashMap<>();
    private ArrayList<String> textList = new ArrayList<>();
    private ArrayList<String> usedTextList = new ArrayList<>();
    private ArrayList<Integer> count2Phonologies = new ArrayList<>(Arrays.asList(
            0x116a, 0x116b, 0x116c, 0x118c, 0x116f, 0x1171, 0x1174, 0x11aa, 0x11ac,
            0x11ad, 0x11b0, 0x11b1, 0x11b2, 0x11b3, 0x11b5, 0x11b6, 0x11b9)); //2 카운트로 예외 처리할 음운들
    private CountDownTimer countDownTimer;
    private boolean isBack = false;
    private double cpm = 0;
    private double accuracy = 0;
    private int typeCount = 0;
    private int diffWordCount = 0;
    private int correctCount = 0;
    private int sumDiffCount = 0;
    private int sumCount = 0;
    private int randomIndex = 0;
    private long startTime = 0;
    private long activityStartTime = 0;
    private final int END_MILLI = 30 * 1000;
    private final int INTERVAL = 100;
    private final int TEXT_COUNT = 1269;
    private final int HAN_END = 0xD7AF;
    private final int HAN_START = 0xAC00;
    private final int JUNG_START = 0X1161;
    private final int JONG_START = 0x11A8;
    private BufferedReader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typing);

        toolbar = findViewById(R.id.app_toolbar);
        showText = findViewById(R.id.preview_text);
        typingText = findViewById(R.id.typing_text);
        cpmText = findViewById(R.id.cpm);
        timerText = findViewById(R.id.timer);
        accuracyText = findViewById(R.id.accuracy);
        cpmGauge = findViewById(R.id.cpm_gauge);
        timerGauge = findViewById(R.id.timer_gauge);
        accuracyGauge = findViewById(R.id.accuracy_gauge);
        cpmGauge.setVisibility(View.GONE);
        timerGauge.setVisibility(View.GONE);
        accuracyGauge.setVisibility(View.GONE);


        activityStartTime = System.currentTimeMillis();
        onTextSequence = "   ";

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String TAG = "SentenceTypingActivity";
        try{
            InputStreamReader is = new InputStreamReader(getResources().openRawResource(R.raw.korean_sentence), "utf-8");

            reader = new BufferedReader(is);
            CSVReader read = new CSVReader(reader);
            String[] line;

            while((line = read.readNext()) != null)
                textList.add(line[0]);
        } catch (IOException e){
            Log.e(TAG, e.toString());
        } finally{
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }

        randomIndex = (int)(Math.random() * TEXT_COUNT);
        showText.setText(textList.get(randomIndex));
        usedTextList.add(textList.get(randomIndex));

        ViewGroup.LayoutParams layoutParams = typingText.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        typingText.setLayoutParams(layoutParams);
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.timer, ConstraintSet.BOTTOM, R.id.preview_text, ConstraintSet.TOP, 0);
        constraintSet.applyTo(constraintLayout);

        spanText = new SpannableStringBuilder(showText.getText());

        countDownTimer = new CountDownTimer(END_MILLI, INTERVAL) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(millisUntilFinished / 1000 + " sec");

                startTime = System.currentTimeMillis();
                cpm = correctCount / ((double)(startTime - activityStartTime) / 1000 / 60);
                cpmText.setText("cpm: " + (int) cpm);

                if(typeCount > 0)
                    accuracy = 100.0 - 100.0 * ((double)(diffWordCount + sumDiffCount) /  (sumCount + typeCount));

                accuracyText.setText("accuracy: " + Math.round(accuracy * 10) / 10);
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent();
                intent.putExtra("cpm", String.valueOf(Math.round(cpm * 100) / 100));
                intent.putExtra("accuracy", String.valueOf(Math.round(accuracy * 100) / 100));
                setResult(1, intent);
                finish();
            }
        };
        countDownTimer.start();

        typingText.setCursorVisible(false);
        typingText.setClickable(false);
        typingText.setPrivateImeOptions("defaultInputmode=korean;");

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("dd");
                typingText.setFocusableInTouchMode(true);
                typingText.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(typingText,0);
            }
        }, 100);

        typingText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.length() > showText.length()){
                    typingText.setText(null);

                    while(usedTextList.contains(textList.get(randomIndex)))
                        randomIndex = (int)(Math.random() * TEXT_COUNT);
                    showText.setText(textList.get(randomIndex));
                    usedTextList.add(textList.get(randomIndex));
                    spanText.clear();
                    spanText.append(showText.getText().toString());
                    sumDiffCount += diffWordCount;
                    sumCount+=typeCount;
                    return;
                }

                if(charSequence.length() == 0){
                    for(int j = 0; j < showText.length(); j++){
                        if(textColorMap.containsKey(j)) {
                            spanText.removeSpan(textColorMap.get(j));
                            textColorMap.remove(j);
                        }
                    }
                    showText.setText(spanText);
                    return;
                }

                String compareText = onTextSequence.subSequence(0, onTextSequence.length() - 1).toString();

                isBack = compareText.equals(charSequence.toString());

                char changedChar = charSequence.charAt(charSequence.length() - 1);
                char compareChar = showText.getText().toString().charAt(charSequence.length() - 1);

                textColorMapping(showText.getText().toString(), charSequence.toString());

                if(!isBack && changedChar == compareChar){
                    int chCount = getCharCorrectCount(changedChar);
                    correctCount+=chCount;
                }

                typeCount = 0;
                for(char ch : charSequence.toString().toCharArray()){
                    int chCount = getCharCorrectCount(ch);
                    typeCount += chCount;
                }

                onTextSequence = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public int getCharCorrectCount(char ch){

        int inputCount = 1;

        if(ch >= HAN_START && ch <= HAN_END){
            int jungInt = ((ch - HAN_START) % (21 * 28)) / 28;
            int jongInt = (ch - HAN_START) % 28;

            inputCount += (count2Phonologies.contains(jungInt + JUNG_START) ? 2 : 1);
            if(jongInt > 0)
                inputCount += (count2Phonologies.contains(jongInt + JONG_START - 1) ? 2 : 1);
        }
        return inputCount;
    }

    public void textColorMapping(String show_text, String type_text){

        if(show_text.length() < type_text.length())
            return;

        diffWordCount = 0;
        for(int i = 0; i < type_text.length(); i++){

            if(show_text.charAt(i) == type_text.charAt(i)){
                if(textColorMap.containsKey(i)) {
                    spanText.removeSpan(textColorMap.get(i));
                    textColorMap.remove(i);
                }
                textColorMap.put(i, new ForegroundColorSpan(Color.GREEN));
                spanText.setSpan(textColorMap.get(i), i, i + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            else {
                if(textColorMap.containsKey(i)) {
                    spanText.removeSpan(textColorMap.get(i));
                    textColorMap.remove(i);
                }
                textColorMap.put(i, new ForegroundColorSpan(Color.RED));
                spanText.setSpan(textColorMap.get(i), i, i + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                diffWordCount+=getCharCorrectCount(type_text.charAt(i));
            }
        }
        for(int i = type_text.length(); i < show_text.length(); i++){
            if(textColorMap.containsKey(i)) {
                spanText.removeSpan(textColorMap.get(i));
                textColorMap.remove(i);
            }
        }

        showText.setText(spanText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//toolbar의 back키 눌렀을 때 동작

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("나가시겠습니까?");
            builder.setMessage("게임은 저장되지 않습니다.");

            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent();
                    setResult(0, intent);
                    finish();
                }
            });

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            builder.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("나가시겠습니까?");
        builder.setMessage("게임은 저장되지 않습니다.");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.show();
    }
}
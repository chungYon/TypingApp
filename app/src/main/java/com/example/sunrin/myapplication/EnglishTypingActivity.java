package com.example.sunrin.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class EnglishTypingActivity extends AppCompatActivity {

    private  android.support.v7.widget.Toolbar toolbar;
    private TextView showText;
    private TextView cpmText;
    private TextView timerText;
    private TextView accuracyText;
    private EditText typingText;
    private SpannableStringBuilder spanText;
    private CharSequence onTextSequence;
    private HashMap<Integer, ForegroundColorSpan> textColorMap = new HashMap<>();
    private ArrayList<String> textList = new ArrayList<>();
    private ArrayList<String> usedTextList = new ArrayList<>();
    private CountDownTimer countDownTimer;
    private boolean isBack = false;
    private double cpm = 0;
    private double accuracy = 100;
    private int typeCount = 0;
    private int correctCount = 0;
    private int diffWordCount = 0;
    private int sumDiffCount = 0;
    private int sumCount = 0;
    private int randomIndex = 0;
    private long startTime = 0;
    private long activityStartTime = 0;
    private final int END_MILLI = 30 * 1000;
    private final int INTERVAL = 100;
    private final int TEXT_COUNT = 5464;
    private final String TAG = "EnglishTypingActivity";
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

        activityStartTime = System.currentTimeMillis();
        onTextSequence = "   ";

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        try{
            InputStreamReader is = new InputStreamReader(getResources().openRawResource(R.raw.english));
            reader = new BufferedReader(is);
            CSVReader read = new CSVReader(reader);
            String[] line;

            while((line = read.readNext()) != null)
                textList.add(line[0].split(";")[1]);
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
        spanText = new SpannableStringBuilder(showText.getText());

        countDownTimer = new CountDownTimer(END_MILLI, INTERVAL) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.valueOf(millisUntilFinished / 1000) + " sec");
                startTime = System.currentTimeMillis();
                cpm = correctCount / ((double)(startTime - activityStartTime) / 1000 / 60);
                cpmText.setText("cpm: " + (int) cpm);
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

        typingText.post(new Runnable() {
            @Override
            public void run() {
                typingText.setFocusableInTouchMode(true);
                typingText.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(typingText,0);
            }
        });

        typingText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(typingText.length() > showText.length()){
                    while(usedTextList.contains(textList.get(randomIndex)))
                        randomIndex = (int)(Math.random() * TEXT_COUNT);
                    showText.setText(textList.get(randomIndex));
                    usedTextList.add(textList.get(randomIndex));
                    spanText.clear();
                    spanText.append(showText.getText().toString());
                    sumDiffCount += diffWordCount;
                    sumCount += typingText.length() - 1;
                    typingText.setText(null);
                    return;
                }

                if(typingText.length() == 0){
                    for(int j = 0; j < showText.length(); j++){
                        if(textColorMap.containsKey(j)) {
                            spanText.removeSpan(textColorMap.get(j));
                            textColorMap.remove(j);
                        }
                    }
                    showText.setText(spanText);
                    typeCount = 0;
                    return;
                }

                String compareText = onTextSequence.subSequence(0, onTextSequence.length() - 1).toString();

                isBack = compareText.equals(charSequence.toString());

                char changedChar = charSequence.charAt(charSequence.length() - 1);
                char compareChar = showText.getText().toString().charAt(charSequence.length() - 1);
                textColorMapping(showText.getText().toString(), charSequence.toString());

                if(!isBack && changedChar == compareChar)
                    correctCount++;
                else if (isBack)
                    typeCount-=2;

                typeCount++;

                if(typeCount > 0) {
                    accuracy = 100.0 - 100.0 * ((double)(diffWordCount + sumDiffCount) / (typeCount + sumCount));
                    accuracyText.setText("accuracy: " + Math.round(accuracy * 10) / 10);
                }else{
                    accuracyText.setText("accuracy: ");
                }

                onTextSequence = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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
                diffWordCount++;
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
}
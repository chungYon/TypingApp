package com.example.sunrin.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

public class EnglishTypingActivity extends AppCompatActivity {

    private  android.support.v7.widget.Toolbar toolbar;
    private TextView showText;
    private TextView timerText;
    private EditText typingText;
    private SpannableString spanText;
    private CharSequence onTextSequence;
    private HashMap<Integer, ForegroundColorSpan> textColorMap = new HashMap<>();
    private ArrayList<String> textList = new ArrayList<>();
    private boolean isEqual = true;
    private boolean isBack = false;
    private double accuracy = 100;
    private int typeCount = 0;
    //private final int MAX_TEXT_LENGTH = 6;
    private long startTime = 0;
    private long activityStartTime = 0;
    private final String TAG = "EnglishTypingActivity";
    private BufferedReader br;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_english_typing);

        toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showText = findViewById(R.id.preview_text);
        typingText = findViewById(R.id.typing_text);
        timerText = findViewById(R.id.timer);
        activityStartTime = System.currentTimeMillis();
        onTextSequence = "   ";
        br = null;
        //csv파일 읽어오는 코드, 확실하지 않으니 테스트
//        try{
//            InputStream is = this.getResources().openRawResource(R.raw.english_list);
//            br = Files.newBufferedReader(Paths.get("res\\values\\english_list.csv"));
//            String line = "";
//
//            while((line = br.readLine()) != null)
//                textList.add(line.replace(",", ""));
//        }catch (FileNotFoundException e){
//            e.printStackTrace();
//        }catch (IOException e){
//            e.printStackTrace();
//        }finally{
//            try{
//                if(br != null){
//                    br.close();
//                }
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//        }

        Log.d(TAG, String.valueOf(activityStartTime));

        spanText = new SpannableString(showText.getText());

        typingText.setCursorVisible(false);
        typingText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > showText.length())
                    return;
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

                if(compareText.equals(charSequence.toString()))
                    isBack = true;
                else
                    isBack = false;

                char changedChar = charSequence.charAt(charSequence.length() - 1);
                char compareChar = showText.getText().toString().charAt(charSequence.length() - 1);
                textColorMapping(showText.getText().toString(), charSequence.toString());

                if((changedChar >= 'a' && changedChar <= 'z' || changedChar >= 'A' && changedChar <= 'Z') && !isBack && changedChar == compareChar){
                    startTime = System.currentTimeMillis();

                    double cpm = typeCount / ((double)(startTime - activityStartTime) / 1000 / 60);
                    timerText.setText(String.valueOf(((int)cpm)));
                    typeCount++;
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

        for(int i = 0; i < type_text.length(); i++){

            if(show_text.charAt(i) == type_text.charAt(i)){
                if(textColorMap.containsKey(i)) {
                    spanText.removeSpan(textColorMap.get(i));
                    textColorMap.remove(i);
                }
            }
            else {
                if(!textColorMap.containsKey(i)) {
                    textColorMap.put(i, new ForegroundColorSpan(Color.RED));
                    spanText.setSpan(textColorMap.get(i), i, i + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }
        }
        for(int i = type_text.length(); i < show_text.length(); i++){
            if(textColorMap.containsKey(i)) {
                spanText.removeSpan(textColorMap.get(i));
                textColorMap.remove(i);
            }
        }

        if(spanText.length() > 0)
            showText.setText(spanText);
        else
            showText.setText("chungYon");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("나가시겠습니까?");
                builder.setMessage("게임은 저장되지 않습니다.");


                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
        }
        return super.onOptionsItemSelected(item);
    }
}
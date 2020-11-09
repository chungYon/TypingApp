package com.example.sunrin.myapplication;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.security.Key;

public class EnglishTypingActivity extends AppCompatActivity {

    private TextView showText;
    private TextView timerText;
    private EditText typingText;
    private long startTime = 0;
    private long endTime = 0;
    private int typeCount = 0;
    private final String TAG = "EnglishTypingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_english_typing);

        Toolbar toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showText = findViewById(R.id.preview_text);
        typingText = findViewById(R.id.typing_text);
        timerText = findViewById(R.id.timer);

        typingText.setCursorVisible(false);
        typingText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                showText.setText(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.length() == 0){
                    return;
                }

                char changedChar = charSequence.charAt(charSequence.length() - 1);

                if(changedChar >= 'a' && changedChar <= 'z' || changedChar >= 'A' && changedChar <= 'Z'){
                    long tmp;
                    if(typeCount % 2 == 0){
                        startTime = System.currentTimeMillis();
                        tmp = endTime;
                        endTime = startTime;
                        startTime = tmp;
                    }
                    else
                        endTime = System.currentTimeMillis();

                    if(startTime != 0)
                        timerText.setText(String.valueOf((endTime - startTime)));
                    typeCount++;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        typingText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                Log.d("EnglishTypingActivity","count : " + String.valueOf(typeCount));
//
//                if(i >= KeyEvent.KEYCODE_A && i <= KeyEvent.KEYCODE_Z){
//                    if(typeCount % 2 == 0)
//                        startTime = System.currentTimeMillis();
//                    else
//                        endTime = System.currentTimeMillis();
//
//                    if(startTime != 0 && endTime != 0)
//                        timerText.setText(String.valueOf((endTime - startTime) / 1000));
//                    typeCount++;
//
//                    return true;
//                }
//
//
//                return false;
//            }
//        });
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
package com.example.sunrin.myapplication;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        OptionFragment englishFragment = new OptionFragment();
        adapter.addItem(englishFragment);

        OptionFragment koreanFragment = new OptionFragment();
        adapter.addItem(koreanFragment);

        OptionFragment gameFragment = new OptionFragment();
        adapter.addItem(gameFragment);

        Bundle bundle = new Bundle();
        bundle.putString("category", "english");
        englishFragment.setArguments(bundle);

        Bundle bundle2 = new Bundle();
        bundle2.putString("category", "korean");
        koreanFragment.setArguments(bundle2);

        Bundle bundle3 = new Bundle();
        bundle3.putString("category", "sentence");
        gameFragment.setArguments(bundle3);

        pager.setAdapter(adapter);
    }
}

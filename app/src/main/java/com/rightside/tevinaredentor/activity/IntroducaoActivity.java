package com.rightside.tevinaredentor.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.rightside.tevinaredentor.R;

public class IntroducaoActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_introducao);

        addSlide(new FragmentSlide.Builder().background(android.R.color.holo_orange_dark).fragment(R.layout.intro_1).build());
        addSlide(new FragmentSlide.Builder().background(android.R.color.holo_orange_dark).fragment(R.layout.activity_login).build());
    }
}

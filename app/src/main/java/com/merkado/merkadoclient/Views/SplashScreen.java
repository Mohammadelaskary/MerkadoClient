package com.merkado.merkadoclient.Views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.merkado.merkadoclient.R;
import com.merkado.merkadoclient.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {
    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.getRoot());
        binding.logo.setVisibility(View.INVISIBLE);
        binding.slogan1.setVisibility(View.INVISIBLE);
        binding.sloganDot.setVisibility(View.INVISIBLE);
        binding.slogan2.setVisibility(View.INVISIBLE);
        View[] views = new View[]{binding.logo, binding.slogan1, binding.sloganDot, binding.slogan2};

// 100ms delay between Animations

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.putExtra("cart", "");
                intent.putExtra("depName","");
                startActivity(intent);
                finish();
            }
        }, 2500);
        long delayBetweenAnimations = 500L;

        for (int i = 0; i < views.length; i++) {
            final View view = views[i];

            // We calculate the delay for this Animation, each animation starts 100ms
            // after the previous one
            long delay = i * delayBetweenAnimations;

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.bounce);
                    view.startAnimation(animation);
                }
            }, delay);
        }
    }
}
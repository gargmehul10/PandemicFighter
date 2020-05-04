package com.mehul.pandemicfighter.Module2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mehul.pandemicfighter.R;

public class AboutUs extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    public void facebookPressed(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/PlanIt-122567759143775/"));
        startActivity(i);
    }
}
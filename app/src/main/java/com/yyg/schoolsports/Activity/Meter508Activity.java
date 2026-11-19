package com.yyg.schoolsports.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.PlayAudio;

import androidx.appcompat.app.AppCompatActivity;

public class Meter508Activity extends  AppCompatActivity{
    private Button m508testsetupBtn;
    private Button m508TestStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter508);

        m508TestStartBtn = (Button) this.findViewById(R.id.m508teststart_btn);
        m508TestStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Meter508Activity.this, ReadCardActivity.class);
                intent.putExtra("testitem","508M");
                startActivity(intent);
            }
        });

        m508testsetupBtn = (Button) findViewById(R.id.m508setup_btn);
        m508testsetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        PlayAudio.playaudio(1);
    }
}

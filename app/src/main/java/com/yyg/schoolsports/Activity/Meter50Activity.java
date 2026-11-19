package com.yyg.schoolsports.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.PlayAudio;

import androidx.appcompat.app.AppCompatActivity;

public class Meter50Activity extends AppCompatActivity {
    private Button m50testsetupBtn;
    private Button m50TestStartBtn;
    private Button m50testmodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter50);

        m50TestStartBtn = (Button) this.findViewById(R.id.m50teststart_btn);
        m50TestStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Meter50Activity.this, ReadCardActivity.class);
                intent.putExtra("testitem","50M");
                startActivity(intent);
            }
        });

        m50testsetupBtn = (Button) findViewById(R.id.m50setup_btn);
        m50testsetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        m50testmodeBtn = (Button) findViewById(R.id.m50testmode_btn);
        m50testmodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Meter50Activity.this, ReadCardActivity.class);
                intent.putExtra("testcardid","无卡号");
                intent.putExtra("TESTMODE", 2);  //测试模式
                startActivity(intent);
            }
        });

        PlayAudio.playaudio(1);
    }
}

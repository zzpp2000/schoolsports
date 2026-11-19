package com.yyg.schoolsports.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.PlayAudio;

import androidx.appcompat.app.AppCompatActivity;

public class SFBActivity extends AppCompatActivity{
    private Button sfbtestsetupBtn;
    private Button sfbTestStartBtn;
    private Button sfbtestmodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sfb);   //坐位体前屈

        sfbTestStartBtn = (Button) this.findViewById(R.id.sfbteststart_btn);
        sfbTestStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SFBActivity.this, ReadCardActivity.class);
                intent.putExtra("testitem","SFB");
                startActivity(intent);
            }
        });

        sfbtestsetupBtn = (Button) findViewById(R.id.sfbsetup_btn);
        sfbtestsetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sfbtestmodeBtn = (Button)findViewById(R.id.sfbtestmode_btn);
        sfbtestmodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SFBActivity.this, SFBTestActivity.class);
                intent.putExtra("testcardid","无卡号");
                intent.putExtra("TESTMODE", 2);  //测试模式
                startActivity(intent);
            }
        });

        PlayAudio.playaudio(1);
    }
}

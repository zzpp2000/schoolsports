package com.yyg.schoolsports.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.PlayAudio;

import androidx.appcompat.app.AppCompatActivity;

public class SuActivity extends AppCompatActivity {
    private Button sutestsetupBtn;
    private Button suTestStartBtn;
    private Button sutestmodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_su);       //仰卧起坐

        suTestStartBtn = (Button) this.findViewById(R.id.suteststart_btn);
        suTestStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuActivity.this, ReadCardActivity.class);
                intent.putExtra("testitem","SU");
                startActivity(intent);
            }
        });

        sutestsetupBtn = (Button) findViewById(R.id.susetup_btn);
        sutestsetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sutestmodeBtn = (Button) findViewById(R.id.sutestmode_btn);
        sutestmodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuActivity.this, SuTestActivity.class);
                intent.putExtra("testcardid","无卡号");
                intent.putExtra("TESTMODE", 2);  //测试模式
                startActivity(intent);
            }
        });

        PlayAudio.playaudio(1);
    }
}
